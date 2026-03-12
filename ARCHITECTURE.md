# PrivacyGuard Architecture

This document describes the layered architecture of PrivacyGuard, its component interactions, data flow, Melange SDK integration, security model, and performance targets.

---

## Layered Architecture Overview

PrivacyGuard follows a strict layered architecture where data flows downward through well-defined boundaries. Each layer has a single responsibility and communicates only with its immediate neighbors.

```
+===================================================================+
|                                                                    |
|                      PRESENTATION LAYER                            |
|                                                                    |
|  +------------------+  +------------------+  +-----------------+   |
|  |   MainActivity   |  |  DashboardScreen |  | SettingsScreen  |   |
|  | (Jetpack Compose)|  | (Detection List) |  | (Whitelist/Cfg) |   |
|  +------------------+  +------------------+  +-----------------+   |
|                                                                    |
+============================+=======================================+
                             |
+============================v=======================================+
|                                                                    |
|                       SERVICE LAYER                                |
|                                                                    |
|  +------------------------+  +-------------------------------+     |
|  | ClipboardMonitorService|  | PiiAccessibilityService       |     |
|  | (Foreground Service)   |  | (AccessibilityService)        |     |
|  |                        |  |                                |     |
|  | - Clipboard listener   |  | - onAccessibilityEvent()      |     |
|  | - Persistent notif.    |  | - Text field content capture  |     |
|  +----------+-------------+  +---------------+---------------+     |
|             |                                |                     |
+============================+=======================================+
                             |
+============================v=======================================+
|                                                                    |
|                     DETECTION LAYER                                |
|                                                                    |
|  +------------------------------------------------------------+   |
|  |                     DetectionPipeline                       |   |
|  |                                                             |   |
|  |  +-----------+    +----------------+    +----------------+  |   |
|  |  | Debouncer |    | RegexPre-      |    | MelangeNer     |  |   |
|  |  | (300ms)   +--->| Screener       +--->| Classifier     |  |   |
|  |  |           |    | (Luhn, regex)  |    | (ML inference) |  |   |
|  |  +-----------+    +----------------+    +-------+--------+  |   |
|  |                                                 |           |   |
|  |                                    +------------v--------+  |   |
|  |                                    |   OutputDecoder     |  |   |
|  |                                    |   (Logits->Entities)|  |   |
|  |                                    +------------+--------+  |   |
|  +------------------------------------------------------------+   |
|                                        |                          |
+========================================+==========================+
                                         |
+========================================v==========================+
|                                                                    |
|                      RESPONSE LAYER                                |
|                                                                    |
|  +---------------------+    +----------------------------------+   |
|  |    AlertManager      |    |    EncryptedLogRepository       |   |
|  |                      |    |                                  |   |
|  | - Severity scoring   |    | - AES-256-GCM encryption        |   |
|  | - Overlay alerts     |    | - Metadata-only storage          |   |
|  | - Notifications      |    | - EncryptedSharedPreferences     |   |
|  | - Whitelist check    |    | - Android Keystore keys          |   |
|  +---------------------+    +----------------------------------+   |
|                                                                    |
+====================================================================+
```

---

## Component Descriptions

### Presentation Layer

The user-facing layer built with Jetpack Compose and Material3.

| Component | Description |
|---|---|
| **MainActivity** | Entry point and navigation host. Manages the Compose NavGraph and handles permission request flows during initial setup. |
| **DashboardScreen** | Displays real-time detection statistics, recent alerts, and monitoring status. Shows a live feed of detected PII events with severity indicators. |
| **SettingsScreen** | Provides user controls including app whitelist management, alert sensitivity tuning, detection history management (view/delete), and service enable/disable toggles. |

### Service Layer

Long-running Android services that capture text input events.

| Component | Description |
|---|---|
| **ClipboardMonitorService** | A foreground service that registers a `ClipboardManager.OnPrimaryClipChangedListener`. Runs persistently with a status notification. Captures clipboard content changes and forwards text to the Detection Layer. Survives app backgrounding. |
| **PiiAccessibilityService** | An Android `AccessibilityService` that listens for `TYPE_VIEW_TEXT_CHANGED` and `TYPE_VIEW_FOCUSED` events. Captures text being entered into text fields across all applications. Scoped to minimize system impact. |

### Detection Layer

The core intelligence of PrivacyGuard, responsible for identifying PII in text.

| Component | Description |
|---|---|
| **Debouncer** | Rate-limits incoming text events with a 300ms cooldown window. Prevents duplicate processing when the user is actively typing or rapidly copying. Implemented with Kotlin coroutines (`debounce` operator on Flow). |
| **RegexPreScreener** | A fast first-pass filter using compiled regex patterns. Detects obvious PII patterns (credit card numbers, SSNs, email addresses, phone numbers) before invoking ML inference. Includes Luhn algorithm validation for credit card number candidates. This layer is computationally cheap and reduces unnecessary ML invocations. |
| **MelangeNerClassifier** | The core ML component. Wraps the `ZeticMLangeModel` instance loaded with the `Team_ZETIC/TextAnonymizer` model. Tokenizes input text, prepares `ByteBuffer` input tensors, runs inference through the Melange runtime (with NPU acceleration), and produces raw logit outputs. |
| **OutputDecoder** | Converts raw model output logits into structured entity predictions. Applies argmax over the label dimension, maps token-level predictions back to character spans in the original text, merges B-/I- tagged sequences into complete entity spans, and assigns entity type labels (PER, LOC, ORG, CREDIT_CARD, SSN, PHONE, EMAIL, etc.). |

### Response Layer

Handles alerting the user and persisting detection records.

| Component | Description |
|---|---|
| **AlertManager** | Receives detected entities and determines the appropriate response. Scores severity (Critical/High/Medium), checks the source app against the user's whitelist, and dispatches alerts via overlay windows (`SYSTEM_ALERT_WINDOW`) and/or notifications. Implements a suppression window to avoid alert fatigue. |
| **EncryptedLogRepository** | Persists detection metadata using `EncryptedSharedPreferences` backed by Android Keystore. Stores only metadata (entity type, severity level, timestamp, source app package name). Raw PII text is never written to storage. Provides query APIs for the Dashboard screen. |

---

## Data Flow: Clipboard Event to Alert

The following describes the complete data flow when a user copies text containing a credit card number:

```
Step 1: USER COPIES TEXT
        "My card is 4532015112830366"
                    |
                    v
Step 2: CLIPBOARD EVENT
        ClipboardManager fires OnPrimaryClipChangedListener
        ClipboardMonitorService captures the text
                    |
                    v
Step 3: DEBOUNCING
        Debouncer checks: has 300ms elapsed since last event?
        YES -> forward text to pipeline
        NO  -> drop (superseded by newer event)
                    |
                    v
Step 4: REGEX PRE-SCREENING
        RegexPreScreener runs pattern matching:
        - Credit card regex matches "4532015112830366"
        - Luhn validation: checksum passes -> valid CC number
        - Result: CREDIT_CARD candidate found
        (Text still forwarded to ML for comprehensive scan)
                    |
                    v
Step 5: TOKENIZATION
        Tokenizer converts text to token IDs:
        "My card is 4532015112830366"
        -> [101, 2026, 4003, 2003, 8745, 1014, ..., 102]
        Token IDs packed into ByteBuffer
                    |
                    v
Step 6: MELANGE INFERENCE
        ZeticMLangeModel.run(inputBuffer) -> outputBuffer
        Model: Team_ZETIC/TextAnonymizer
        Runtime: NPU-accelerated via Melange
        Latency: ~30-45ms typical
                    |
                    v
Step 7: OUTPUT DECODING
        OutputDecoder processes logits:
        - argmax per token position
        - Map to BIO labels: O, O, O, B-CREDIT_CARD, I-CREDIT_CARD, ...
        - Merge into entity spans
        - Result: Entity(type=CREDIT_CARD, span="4532015112830366",
                         start=14, end=30, confidence=0.97)
                    |
                    v
Step 8: SEVERITY SCORING
        AlertManager scores the detection:
        - CREDIT_CARD -> Severity.CRITICAL
        - Check whitelist: source app not whitelisted
        - Check suppression window: no recent duplicate
                    |
                    v
Step 9: ALERT DISPATCH
        AlertManager dispatches:
        - Overlay alert (SYSTEM_ALERT_WINDOW) with warning
        - Notification with "Critical PII detected" message
        - User can dismiss, whitelist the app, or view details
                    |
                    v
Step 10: ENCRYPTED LOGGING
         EncryptedLogRepository stores:
         {
           "entityType": "CREDIT_CARD",
           "severity": "CRITICAL",
           "timestamp": 1741689600000,
           "sourceApp": "com.example.messenger",
           "id": "uuid-..."
         }
         NOTE: The actual card number "4532015112830366" is NOT stored.
```

---

## Melange SDK Integration Details

### Model Specification

- **Model identifier:** `Team_ZETIC/TextAnonymizer`
- **Task:** Named Entity Recognition (NER) for PII detection
- **SDK version:** Melange SDK 1.2.2
- **Acceleration:** NPU via Melange runtime (falls back to CPU if NPU unavailable)

### Integration Architecture

```
+---------------------------------------------------------------+
|                    MelangeNerClassifier                        |
|                                                                |
|  +------------------+    +------------------------------+      |
|  |   Tokenizer      |    |   ZeticMLangeModel           |      |
|  |                   |    |                              |      |
|  | - Vocab loading   |    | - Model initialization      |      |
|  | - Text -> IDs     |    | - Input buffer allocation   |      |
|  | - Padding/trunk.  |    | - NPU inference execution   |      |
|  | - Attention mask   |    | - Output buffer retrieval   |      |
|  +--------+----------+    +--+---------------------------+      |
|           |                  ^           |                      |
|           |  ByteBuffer      |           |  ByteBuffer          |
|           +------------------+           |                      |
|                                          v                      |
|                              +-----------+------------+         |
|                              |   OutputDecoder        |         |
|                              |                        |         |
|                              | - Logit extraction     |         |
|                              | - Argmax computation   |         |
|                              | - BIO tag merging      |         |
|                              | - Entity span mapping  |         |
|                              +------------------------+         |
+---------------------------------------------------------------+
```

### Lifecycle Management

1. **Initialization:** `ZeticMLangeModel` is instantiated during `ClipboardMonitorService.onCreate()` with the model key and personal key. The model is loaded into memory and prepared for inference.
2. **Warm-up:** A single warm-up inference is run with dummy input during initialization to ensure the NPU pipeline is primed and first-inference latency is minimized.
3. **Active inference:** During normal operation, the model instance is reused for all inference calls. Input/output `ByteBuffer` instances are pre-allocated and reused to minimize GC pressure.
4. **Suspension:** When the monitoring service is paused by the user, the model is retained in memory but no inference calls are made.
5. **Teardown:** `ZeticMLangeModel.deinitModel()` is called during `ClipboardMonitorService.onDestroy()` to release NPU resources and free memory.

---

## Security Model

### Zero-Network Architecture

PrivacyGuard enforces a strict zero-network policy at the Android manifest level:

```xml
<uses-permission android:name="android.permission.INTERNET"
    tools:node="remove" />
```

This declaration instructs the Android build system to remove the INTERNET permission from the final merged manifest, even if a transitive dependency attempts to add it. The app physically cannot open network sockets.

### Data Encryption

All persistent data is encrypted using AES-256-GCM:

- **Key management:** Encryption keys are generated and stored in the Android Keystore, which provides hardware-backed key storage on supported devices.
- **Storage:** `EncryptedSharedPreferences` from the AndroidX Security Crypto library wraps standard SharedPreferences with transparent encryption/decryption.
- **Scope:** All detection log entries and user preferences are encrypted at rest.

### No Raw PII Persistence

The system enforces a strict policy: raw PII text detected by the model is never written to disk. Only structured metadata is persisted:

- Entity type (e.g., `CREDIT_CARD`, `SSN`, `EMAIL`)
- Severity level (`CRITICAL`, `HIGH`, `MEDIUM`)
- Detection timestamp
- Source application package name
- Unique detection ID

The original text containing PII exists only in memory during the detection pipeline and is eligible for garbage collection immediately after alert dispatch.

### Accessibility Service Scoping

The `PiiAccessibilityService` is configured with minimal event types and feedback:

- Listens only for `TYPE_VIEW_TEXT_CHANGED` and `TYPE_VIEW_FOCUSED` events
- Does not request `FLAG_RETRIEVE_INTERACTIVE_WINDOWS`
- Does not capture screen content or gestures
- Applies debouncing to minimize processing frequency

---

## Performance Targets

| Metric | Target | Notes |
|---|---|---|
| **ML inference latency** | < 50ms | Measured end-to-end from tokenization through output decoding. Typical latency is 30-45ms with NPU acceleration. |
| **Regex pre-screening** | < 5ms | Compiled regex patterns with Luhn validation. |
| **Total detection pipeline** | < 100ms | From clipboard event to alert dispatch. |
| **Battery impact** | < 2% per hour | Measured during active monitoring with typical clipboard usage patterns. Achieved through debouncing, pre-screening (avoiding unnecessary ML calls), and NPU efficiency. |
| **Memory footprint** | < 80MB | Model loaded in memory plus runtime buffers. |
| **Cold start (model load)** | < 3 seconds | One-time cost during service initialization. |
| **Alert display latency** | < 200ms | From PII detection to overlay/notification visible to user. |

### Performance Optimization Strategies

1. **Debouncing:** 300ms cooldown prevents redundant processing during rapid clipboard changes or active typing.
2. **Regex pre-screening:** Cheap regex checks identify obvious PII patterns and can short-circuit the pipeline without invoking ML inference for clearly non-PII text.
3. **Buffer reuse:** Input and output `ByteBuffer` instances are pre-allocated and reused across inference calls to minimize garbage collection.
4. **NPU acceleration:** The Melange runtime automatically leverages device NPU hardware when available, significantly reducing inference latency and power consumption compared to CPU-only execution.
5. **Coroutine dispatchers:** Detection pipeline runs on `Dispatchers.Default` to avoid blocking the main thread, with alert dispatch switching to `Dispatchers.Main` only for UI operations.

---

## Module Structure

```
app/
  src/
    main/
      java/com/privacyguard/
        ui/              # Presentation Layer (Compose screens)
        service/         # Service Layer (Clipboard, Accessibility)
        detection/       # Detection Layer (Pipeline, ML, Regex)
        alert/           # Response Layer (AlertManager)
        data/            # Response Layer (EncryptedLogRepository)
        model/           # Data classes (Entity, Detection, Severity)
        util/            # Shared utilities (Debouncer, Extensions)
      res/
        xml/             # Accessibility service config
        values/          # Strings, themes
```
