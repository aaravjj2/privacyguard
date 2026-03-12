# PrivacyGuard

> On-Device PII Protection Powered by Melange AI

```
    +------------------+
    |   PRIVACYGUARD   |
    |                  |
    |    [ SHIELD ]    |
    |                  |
    |  On-Device PII   |
    |   Protection     |
    +------------------+
```

Real-time, fully on-device privacy protection for Android that detects and blocks accidental sharing of sensitive personal information (PII) before it leaves your device.

**Hackathon Submission:** Melange On-Device AI Hackathon 2026

---

## The Problem

Every day, users unknowingly copy and paste sensitive personal information -- credit card numbers, Social Security numbers, email addresses, phone numbers, and more -- through their device clipboard. This data silently sits in the clipboard buffer, accessible to any app with clipboard read permissions. A single careless paste into a chat window, search bar, or web form can expose critical PII to unintended recipients or malicious applications harvesting clipboard contents.

Traditional solutions rely on cloud-based scanning, which ironically introduces another privacy risk: your sensitive data must be transmitted over the network to a remote server for analysis. This defeats the very purpose of protecting user privacy.

The clipboard is one of the most overlooked attack surfaces on mobile devices, and users have virtually zero visibility into what sensitive data flows through it.

## The Solution

**PrivacyGuard** is an Android application that provides real-time, fully on-device PII detection using the **Melange AI** runtime. It monitors clipboard activity and text fields across applications, instantly identifying sensitive information such as credit card numbers, SSNs, phone numbers, email addresses, and other PII entities.

Key principles:

- **No internet required.** The Melange AI model runs entirely on-device using NPU acceleration. There are zero network calls during inference -- or ever.
- **No cloud dependency.** All detection, alerting, and logging happens locally on the device.
- **Instant detection.** Sub-50ms inference latency means alerts appear in real time, before the user can paste sensitive data into an unintended destination.
- **User empowerment.** Users receive clear, actionable alerts with the ability to whitelist trusted apps, review detection history, and maintain full control over their privacy.

## How Melange Is Used

PrivacyGuard leverages the Melange on-device AI platform as its core inference engine:

| Aspect | Detail |
|---|---|
| **Model** | `Team_ZETIC/TextAnonymizer` -- a Named Entity Recognition (NER) model fine-tuned for PII detection |
| **SDK Version** | Melange SDK 1.2.2 |
| **Runtime** | On-device inference with NPU acceleration via the Melange runtime |
| **Network** | Zero network calls during inference -- the model is bundled and executed locally |
| **Pipeline** | Text is tokenized, fed into the Melange model as ByteBuffer input, and output logits are decoded into entity labels (PER, LOC, ORG, CREDIT_CARD, SSN, etc.) |

The Melange SDK provides the critical capability of running a production-quality NER model directly on the device hardware, enabling real-time PII detection without any cloud dependency.

## Architecture

```
+-------------------------------------------------------------------+
|                        ANDROID SYSTEM                              |
|                                                                    |
|  +------------------+    +--------------------+                    |
|  | ClipboardManager |    | AccessibilityService|                   |
|  | (System Service) |    | (Text Field Events) |                   |
|  +--------+---------+    +---------+----------+                    |
|           |                        |                               |
+-----------+------------------------+-------------------------------+
            |                        |
            v                        v
+-------------------------------------------------------------------+
|                     PRIVACYGUARD APP                               |
|                                                                    |
|  +------------------------------------------------------------+   |
|  |                    INPUT LAYER                              |   |
|  |                                                             |   |
|  |  +-------------------+    +-------------------------+       |   |
|  |  | ClipboardMonitor  |    | AccessibilityScanner    |       |   |
|  |  | (Foreground Svc)  |    | (AccessibilityService)  |       |   |
|  |  +--------+----------+    +------------+------------+       |   |
|  |           |                            |                    |   |
|  |           +------------+---------------+                    |   |
|  |                        |                                    |   |
|  |                        v                                    |   |
|  |              +---------+----------+                         |   |
|  |              |     Debouncer      |                         |   |
|  |              | (300ms cooldown)   |                         |   |
|  |              +---------+----------+                         |   |
|  +------------------------------------------------------------+   |
|                           |                                        |
|  +------------------------------------------------------------+   |
|  |                  PRE-SCREENING LAYER                        |   |
|  |                                                             |   |
|  |              +---------v----------+                         |   |
|  |              |   RegexPreScreener |                         |   |
|  |              | (Pattern matching  |                         |   |
|  |              |  + Luhn validation)|                         |   |
|  |              +---------+----------+                         |   |
|  +------------------------------------------------------------+   |
|                           |                                        |
|  +------------------------------------------------------------+   |
|  |                  ML INFERENCE LAYER                         |   |
|  |                                                             |   |
|  |              +---------v----------+                         |   |
|  |              |     Tokenizer      |                         |   |
|  |              | (Text -> Token IDs)|                         |   |
|  |              +---------+----------+                         |   |
|  |                        |                                    |   |
|  |              +---------v----------+                         |   |
|  |              |   Melange Model    |                         |   |
|  |              | (ZeticMLangeModel) |                         |   |
|  |              | Team_ZETIC/        |                         |   |
|  |              |  TextAnonymizer    |                         |   |
|  |              +---------+----------+                         |   |
|  |                        |                                    |   |
|  |              +---------v----------+                         |   |
|  |              |   OutputDecoder    |                         |   |
|  |              | (Logits -> Entity  |                         |   |
|  |              |  labels + spans)   |                         |   |
|  |              +---------+----------+                         |   |
|  +------------------------------------------------------------+   |
|                           |                                        |
|  +------------------------------------------------------------+   |
|  |                   OUTPUT LAYER                              |   |
|  |                                                             |   |
|  |              +---------v----------+                         |   |
|  |              |    AlertManager    |                         |   |
|  |              | (Critical/High/    |                         |   |
|  |              |  Medium severity)  |                         |   |
|  |              +---------+----------+                         |   |
|  |                        |                                    |   |
|  |              +---------v----------+                         |   |
|  |              | EncryptedLog       |                         |   |
|  |              |   Repository       |                         |   |
|  |              | (AES-256-GCM)      |                         |   |
|  |              +--------------------+                         |   |
|  +------------------------------------------------------------+   |
+-------------------------------------------------------------------+
```

## Features

- **Real-time clipboard monitoring** -- Detects PII the instant it is copied to the clipboard via a persistent foreground service.
- **Accessibility service for text field monitoring** -- Scans text entered into fields across all apps using Android's AccessibilityService framework.
- **On-device ML inference (sub-50ms)** -- The Melange runtime delivers fast NER inference with NPU acceleration, ensuring alerts appear before the user can act on the copied data.
- **Three-tier alert system** -- Alerts are categorized by severity:
  - **Critical:** SSN, credit card numbers (Luhn-validated)
  - **High:** Phone numbers, email addresses, passport numbers
  - **Medium:** Names, addresses, dates of birth
- **Encrypted detection history** -- All detection logs are encrypted with AES-256-GCM and stored locally. Raw PII text is never persisted.
- **App whitelist management** -- Users can whitelist trusted apps (e.g., password managers) to suppress alerts for specific applications.
- **Regex pre-screener with Luhn validation** -- A fast regex layer pre-screens text before ML inference, catching obvious patterns and validating credit card numbers with the Luhn algorithm.
- **Works in airplane mode** -- The entire system operates with zero network connectivity. The INTERNET permission is explicitly removed from the manifest.

## Privacy Guarantee

PrivacyGuard is built on a zero-trust, zero-network architecture:

| Guarantee | Implementation |
|---|---|
| **No internet access** | The `INTERNET` permission is explicitly removed in `AndroidManifest.xml` using `tools:node="remove"`. The app cannot make network calls even if compromised. |
| **All data processed on-device** | Melange model inference, regex screening, alerting, and logging all execute locally. No data ever leaves the device. |
| **Detection history encrypted** | All stored detection records are encrypted with AES-256-GCM using keys managed by Android Keystore. |
| **Raw PII never persisted** | Only detection metadata (entity type, severity, timestamp, source app) is stored. The actual PII text is never written to disk. |
| **No analytics or telemetry** | There are no analytics SDKs, crash reporters, or telemetry of any kind. Zero third-party data collection. |

## Build & Run

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 34+
- A physical device or emulator running Android 8.0+ (API 26+)
- A Melange Personal Key (obtain from [Melange platform](https://melange.ai))

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/privacyguard.git
   cd privacyguard
   ```

2. **Add your Melange Personal Key to `local.properties`:**
   ```properties
   melange.personal.key=YOUR_KEY
   ```

3. **Open in Android Studio:**
   Open the project root directory in Android Studio and let Gradle sync complete.

4. **Build and run:**
   Select your target device (API 26+) and click Run, or use the command line:
   ```bash
   ./gradlew installDebug
   ```

5. **Grant permissions:**
   On first launch, follow the setup wizard to grant the required permissions (Accessibility Service, Overlay, Notifications).

## Permissions Explained

| Permission | Purpose |
|---|---|
| `FOREGROUND_SERVICE` | Runs the clipboard monitoring service persistently in the background with a visible notification. |
| `SYSTEM_ALERT_WINDOW` | Displays real-time PII alert overlays on top of other apps when sensitive data is detected. |
| `ACCESSIBILITY_SERVICE` | Monitors text fields across all applications to detect PII being entered (not just clipboard). |
| `RECEIVE_BOOT_COMPLETED` | Automatically restarts the monitoring service after the device reboots. |
| `POST_NOTIFICATIONS` | Displays alert notifications when PII is detected (required for Android 13+). |

> **INTERNET permission is ABSENT.** This is intentional and is the core privacy guarantee of PrivacyGuard. The app physically cannot transmit data over the network. The permission is explicitly removed in the manifest with `tools:node="remove"`.

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose with Material3 |
| AI/ML Runtime | Melange SDK 1.2.2 (on-device NER inference) |
| Concurrency | Kotlin Coroutines + Flow |
| Encryption | AndroidX Security Crypto (AES-256-GCM) |
| Serialization | Gson |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 (Android 14) |

## Demo Video

[Placeholder -- Demo video link will be added here]

## License

```
MIT License

Copyright (c) 2026 PrivacyGuard

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
