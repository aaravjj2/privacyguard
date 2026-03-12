# PrivacyGuard Security Model

This document describes the security architecture of PrivacyGuard, covering the zero-network enforcement, data encryption, PII handling policies, accessibility service scoping, and release build hardening.

---

## Table of Contents

1. [Zero-Network Architecture](#zero-network-architecture)
2. [INTERNET Permission Removal](#internet-permission-removal)
3. [Android Keystore Usage](#android-keystore-usage)
4. [EncryptedSharedPreferences](#encryptedsharedpreferences)
5. [No Raw PII Storage](#no-raw-pii-storage)
6. [Accessibility Service Scoping](#accessibility-service-scoping)
7. [ProGuard Rules for Release](#proguard-rules-for-release)

---

## Zero-Network Architecture

PrivacyGuard is built on a **zero-network architecture**. This is not merely a design choice -- it is a hard technical enforcement at the Android operating system level.

### Design Principles

1. **No outbound connections:** The application cannot make HTTP requests, open sockets, or transmit data over any network protocol (Wi-Fi, cellular, Bluetooth networking).
2. **No inbound connections:** The application does not expose any services, content providers, or broadcast receivers that could be reached over a network.
3. **No DNS resolution:** Without the INTERNET permission, the application cannot resolve domain names.
4. **No dependency on connectivity:** The application provides identical functionality whether the device is online, offline, or in airplane mode.

### Why Zero-Network?

PrivacyGuard processes the most sensitive data a user has -- credit card numbers, Social Security numbers, personal names, addresses, and more. Any network capability, even if unused, represents a potential exfiltration vector:

- A compromised dependency could silently transmit data.
- A future code change could inadvertently introduce network calls.
- A supply chain attack could inject data collection into a library update.

By removing the INTERNET permission at the manifest level, all of these attack vectors are eliminated by the Android operating system itself. Even if the application code is compromised, the OS will deny any network access attempt.

### Verification

Users and auditors can verify the zero-network guarantee by:

1. Inspecting the merged `AndroidManifest.xml` in the built APK to confirm the INTERNET permission is absent.
2. Running the app while monitoring network traffic with a tool like `tcpdump` or a network proxy -- zero traffic will be observed.
3. Reviewing the `uses-permission` declarations with the `tools:node="remove"` directive in the source manifest.

---

## INTERNET Permission Removal

### Manifest Declaration

In `AndroidManifest.xml`, the INTERNET permission is explicitly removed:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.privacyguard">

    <!-- EXPLICITLY REMOVE INTERNET PERMISSION -->
    <!-- This ensures no transitive dependency can add it back -->
    <uses-permission
        android:name="android.permission.INTERNET"
        tools:node="remove" />

    <!-- ... other permissions ... -->
</manifest>
```

### How `tools:node="remove"` Works

The Android build system merges manifests from the app, all library dependencies, and the platform. The `tools:node="remove"` directive instructs the manifest merger to:

1. Remove any `<uses-permission android:name="android.permission.INTERNET">` declaration from the final merged manifest.
2. This applies regardless of where the permission was declared -- even if a third-party library's manifest includes it.
3. The removal is enforced at build time, so the resulting APK will never contain the INTERNET permission.

### Runtime Enforcement

When an app lacks the INTERNET permission:

- Any call to `java.net.Socket`, `java.net.URL.openConnection()`, `HttpURLConnection`, `OkHttp`, `Retrofit`, or any other networking API will throw a `SecurityException`.
- The Android kernel-level network policy (`netd`) blocks the app's UID from accessing the network stack entirely.
- This enforcement cannot be bypassed by application code -- it is enforced by the operating system kernel.

### Dependency Audit

All dependencies should be periodically audited to ensure none attempt network calls that would fail at runtime. Current dependencies and their network status:

| Dependency | Network Usage |
|---|---|
| Melange SDK 1.2.2 | Model inference is fully local; SDK does not make network calls during inference |
| AndroidX Security Crypto | Local encryption only, no network |
| Jetpack Compose / Material3 | UI framework, no network |
| Gson | JSON serialization, no network |
| Kotlin Coroutines | Concurrency framework, no network |

---

## Android Keystore Usage

### Overview

PrivacyGuard uses the Android Keystore system to generate and store cryptographic keys for encrypting detection logs and user preferences. The Android Keystore provides:

- **Hardware-backed storage:** On devices with a Trusted Execution Environment (TEE) or Secure Element (SE), keys are stored in dedicated security hardware and never exposed to the application processor.
- **Key isolation:** Keys stored in the Keystore cannot be extracted -- cryptographic operations are performed within the Keystore itself.
- **Access control:** Keys are scoped to the application's UID and cannot be accessed by other applications.

### Key Specification

PrivacyGuard uses the following key configuration:

```kotlin
private fun getOrCreateMasterKey(): MasterKey {
    return MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
}
```

| Parameter | Value | Description |
|---|---|---|
| Algorithm | AES | Advanced Encryption Standard |
| Key size | 256 bits | Maximum AES key length |
| Block mode | GCM | Galois/Counter Mode (authenticated encryption) |
| Key scheme | `AES256_GCM` | AndroidX Security MasterKey scheme |
| Key storage | Android Keystore | Hardware-backed when available |
| Key extractable | No | Keys cannot be exported from the Keystore |

### Key Lifecycle

1. **Generation:** The master key is generated on first app launch when `EncryptedSharedPreferences` is first accessed. If a key with the same alias already exists, it is reused.
2. **Usage:** The key is used transparently by `EncryptedSharedPreferences` for all read/write operations.
3. **Persistence:** The key persists across app restarts and device reboots. It is stored in the Android Keystore, not in the app's data directory.
4. **Deletion:** The key is deleted only if the user clears the app's data or uninstalls the app. There is no remote key revocation mechanism (consistent with the zero-network architecture).

### Security Considerations

- On devices with StrongBox (dedicated secure element), the key is stored in hardware that is isolated from the main processor.
- On devices with TEE but no StrongBox, the key is stored in the Trusted Execution Environment.
- On devices without hardware-backed Keystore (rare on modern devices), the key is stored in a software-only Keystore with reduced security guarantees.
- The key is never transmitted, backed up to cloud services, or shared with other applications.

---

## EncryptedSharedPreferences

### Overview

PrivacyGuard uses `EncryptedSharedPreferences` from the AndroidX Security Crypto library to encrypt all persistent data at rest. This provides transparent encryption and decryption through the standard `SharedPreferences` API.

### Implementation

```kotlin
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedLogRepository(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "privacyguard_detection_logs",       // File name
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    /**
     * Store a detection record.
     * Only metadata is stored -- never the raw PII text.
     */
    fun storeDetection(detection: DetectionRecord) {
        val json = gson.toJson(detection)
        encryptedPrefs.edit()
            .putString(detection.id, json)
            .apply()
    }

    /**
     * Retrieve all detection records.
     */
    fun getAllDetections(): List<DetectionRecord> {
        return encryptedPrefs.all.values
            .filterIsInstance<String>()
            .map { gson.fromJson(it, DetectionRecord::class.java) }
            .sortedByDescending { it.timestamp }
    }

    /**
     * Delete a specific detection record.
     */
    fun deleteDetection(id: String) {
        encryptedPrefs.edit().remove(id).apply()
    }

    /**
     * Delete all detection records.
     */
    fun clearAllDetections() {
        encryptedPrefs.edit().clear().apply()
    }
}
```

### Encryption Details

| Aspect | Scheme | Description |
|---|---|---|
| **Key encryption** | AES256-SIV | Deterministic AEAD -- encrypts the SharedPreferences key names so that even the keys (detection IDs) are not readable without decryption. |
| **Value encryption** | AES256-GCM | Probabilistic AEAD -- encrypts the SharedPreferences values (detection metadata JSON) with authentication to prevent tampering. |
| **Master key** | AES256-GCM (Keystore) | The master key that encrypts the data encryption keys, stored in Android Keystore. |

### What the Encrypted File Looks Like

The encrypted SharedPreferences file on disk contains:

```xml
<!-- Actual file on disk (values are encrypted and base64-encoded) -->
<map>
    <string name="AX8f2k...">EjRWeJq8Bnh7G1b+kTz3...</string>
    <string name="Bk9dLm...">Fp4XhRs9Coi8H2c+lUa4...</string>
</map>
```

Both the key names and values are encrypted. An attacker with file system access would see only opaque encrypted blobs with no indication of what data is stored.

---

## No Raw PII Storage

### Policy

PrivacyGuard enforces a strict policy: **raw PII text is never persisted to any storage medium.** This includes:

- SharedPreferences (encrypted or otherwise)
- SQLite databases
- Files on internal or external storage
- Android logs (in release builds)

### What Is Stored

Only structured detection metadata is persisted:

```kotlin
/**
 * Detection record stored in encrypted preferences.
 * NOTE: This class deliberately does not contain the raw PII text.
 */
data class DetectionRecord(
    val id: String,              // Unique detection UUID
    val entityType: String,      // e.g., "CREDIT_CARD", "SSN", "EMAIL"
    val severity: String,        // "CRITICAL", "HIGH", or "MEDIUM"
    val timestamp: Long,         // Unix timestamp in milliseconds
    val sourceApp: String,       // Package name of the source app
    val source: String           // "CLIPBOARD" or "ACCESSIBILITY"
)
```

### What Is NOT Stored

| Data | Stored? | Reason |
|---|---|---|
| Credit card number | No | Raw PII -- never persisted |
| SSN digits | No | Raw PII -- never persisted |
| Email address | No | Raw PII -- never persisted |
| Phone number | No | Raw PII -- never persisted |
| Person name | No | Raw PII -- never persisted |
| Full clipboard text | No | May contain PII |
| Full text field content | No | May contain PII |
| Entity character offsets | No | Could be used to reconstruct PII from context |

### Memory Handling

During the detection pipeline, PII text exists temporarily in memory:

1. Text is received from the clipboard listener or accessibility service.
2. Text is tokenized and processed through the ML model.
3. Detected entities are created with type and severity metadata only.
4. The original text string reference is not retained -- it becomes eligible for garbage collection as soon as the detection pipeline completes.
5. In release builds, no PII text is written to Android log buffers (all `Log.d` calls containing text are stripped by ProGuard).

---

## Accessibility Service Scoping

### Configuration

The accessibility service is configured with minimal scope to reduce its access footprint:

```xml
<!-- res/xml/accessibility_service_config.xml -->
<accessibility-service
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeViewTextChanged|typeViewFocused"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:notificationTimeout="300"
    android:canRetrieveWindowContent="false"
    android:canRequestFilterKeyEvents="false"
    android:canPerformGestures="false"
    android:canRequestTouchExplorationMode="false"
    android:settingsActivity="com.privacyguard.ui.SettingsActivity"
    android:description="@string/accessibility_service_description" />
```

### Scoping Details

| Capability | Value | Justification |
|---|---|---|
| `accessibilityEventTypes` | `typeViewTextChanged \| typeViewFocused` | Only listens for text input events. Does not listen for window changes, scroll events, gestures, or notifications. |
| `canRetrieveWindowContent` | `false` | Cannot read the full content of any window. Only receives the text in the event payload. |
| `canRequestFilterKeyEvents` | `false` | Cannot intercept keystrokes. This is not a keylogger. |
| `canPerformGestures` | `false` | Cannot inject taps, swipes, or other gestures. |
| `canRequestTouchExplorationMode` | `false` | Does not interact with touch exploration (accessibility feature for visually impaired users). |
| `notificationTimeout` | `300` | Minimum 300ms between event deliveries, providing implicit debouncing. |

### What the Service Can and Cannot Do

**Can:**
- Receive notifications when text changes in a focused text field.
- Read the new text content from the accessibility event.
- Identify the source application by package name.

**Cannot:**
- Read screen content outside of text fields.
- Capture screenshots or visual content.
- Intercept keystrokes or gestures.
- Interact with or modify other applications.
- Access password fields (Android masks these from accessibility events).
- Read content from windows other than the currently focused one.

### User Transparency

- The service includes a user-visible description (`@string/accessibility_service_description`) that clearly explains its purpose.
- The service can be disabled at any time through Android Settings > Accessibility.
- When disabled, PrivacyGuard falls back to clipboard-only monitoring.

---

## ProGuard Rules for Release

### Overview

Release builds use R8 (the Android ProGuard replacement) for code shrinking, optimization, and obfuscation. PrivacyGuard includes specific ProGuard rules to enhance security and performance.

### ProGuard Configuration

```proguard
# proguard-rules.pro

# ============================================
# SECURITY: Strip debug logging in release
# ============================================

# Remove all Log.d (debug) and Log.v (verbose) calls
# This prevents PII text from appearing in logcat in release builds
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# ============================================
# MELANGE SDK: Keep model interface classes
# ============================================

# Keep Melange SDK classes needed for reflection
-keep class ai.zetic.product.mlange.android.** { *; }
-keepclassmembers class ai.zetic.product.mlange.android.** { *; }

# ============================================
# DATA CLASSES: Keep for Gson serialization
# ============================================

# Keep detection record data class for JSON serialization
-keep class com.privacyguard.model.DetectionRecord { *; }
-keepclassmembers class com.privacyguard.model.DetectionRecord { *; }

# Keep entity classes
-keep class com.privacyguard.model.DetectedEntity { *; }
-keepclassmembers class com.privacyguard.model.DetectedEntity { *; }

# ============================================
# SECURITY: Obfuscation settings
# ============================================

# Enable aggressive obfuscation for all non-kept classes
-repackageclasses ''
-allowaccessmodification

# Obfuscate class member names
-obfuscationdictionary obfuscation-dictionary.txt
-classobfuscationdictionary obfuscation-dictionary.txt
-packageobfuscationdictionary obfuscation-dictionary.txt

# ============================================
# ENCRYPTED SHARED PREFERENCES
# ============================================

# Keep AndroidX Security Crypto classes
-keep class androidx.security.crypto.** { *; }

# ============================================
# ACCESSIBILITY SERVICE
# ============================================

# Keep accessibility service class (referenced in manifest)
-keep class com.privacyguard.service.PiiAccessibilityService { *; }
```

### Security Benefits of ProGuard

| Benefit | Description |
|---|---|
| **Log stripping** | `Log.d()` and `Log.v()` calls are completely removed from release bytecode. Any PII text that might be logged during development is guaranteed absent from release builds. |
| **Code obfuscation** | Class names, method names, and field names are renamed to short meaningless identifiers, making reverse engineering more difficult. |
| **Dead code removal** | Unused code paths are eliminated, reducing the attack surface. |
| **String encryption** | Combined with R8's optimization, string constants are harder to extract from the APK. |

### Build Configuration

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Verification

After building a release APK, verify the security configuration:

1. **Check merged manifest:** Confirm INTERNET permission is absent.
   ```bash
   aapt dump permissions app-release.apk
   ```

2. **Check for log calls:** Decompile and verify no `Log.d` or `Log.v` calls remain.
   ```bash
   apktool d app-release.apk -o decompiled
   grep -r "Log.d\|Log.v" decompiled/smali/
   ```

3. **Check obfuscation:** Verify class names are obfuscated in the decompiled output.

4. **Network test:** Install the release APK and monitor with a network proxy to confirm zero network traffic.
