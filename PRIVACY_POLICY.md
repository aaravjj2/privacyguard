# PrivacyGuard Privacy Policy

**Effective Date:** March 2026
**Last Updated:** March 2026

---

## Overview

PrivacyGuard is an on-device PII (Personally Identifiable Information) detection application for Android. This privacy policy explains how PrivacyGuard handles data. The short version: **PrivacyGuard collects no data, transmits no data, and processes everything locally on your device.**

---

## No Data Collection

PrivacyGuard does not collect any user data. There are:

- No user accounts or registration
- No analytics or usage tracking
- No crash reporting or diagnostics
- No advertising or ad identifiers
- No telemetry of any kind
- No third-party SDKs that collect data

PrivacyGuard has no concept of "users" -- it is a purely local tool that runs on your device.

---

## No Network Transmission

PrivacyGuard **cannot** transmit data over the network. This is enforced at the system level:

- The Android `INTERNET` permission is explicitly removed from the application manifest using `tools:node="remove"`.
- This means the Android operating system will block any attempt by the app to open a network socket, make an HTTP request, or communicate over the network in any way.
- This protection applies even if a dependency or library within the app attempts to make a network call -- the operating system will deny it.
- The app functions identically with or without an active internet connection, including in airplane mode.

This is not a policy decision -- it is a technical enforcement. The app is architecturally incapable of transmitting data.

---

## All Processing On-Device

All of PrivacyGuard's functionality executes locally on your device:

- **PII detection:** The Melange AI model (`Team_ZETIC/TextAnonymizer`) runs entirely on-device using NPU acceleration. No text is sent to any server for analysis.
- **Regex pre-screening:** Pattern matching and Luhn validation are performed locally using standard library functions.
- **Alert generation:** All alerts (overlay windows and notifications) are generated and displayed locally.
- **Detection logging:** All detection records are stored locally on the device in encrypted storage.
- **User preferences:** Settings, whitelists, and configuration are stored locally on the device.

There is no server, no API, no cloud service, and no backend of any kind associated with PrivacyGuard.

---

## What Data Is Stored

PrivacyGuard stores a minimal set of **detection metadata** on your device. This metadata helps you review your detection history and understand patterns.

### Stored Data (Detection Metadata Only)

For each PII detection event, the following metadata is stored:

| Field | Example | Purpose |
|---|---|---|
| Entity type | `CREDIT_CARD`, `EMAIL`, `SSN` | Identifies what type of PII was detected |
| Severity level | `CRITICAL`, `HIGH`, `MEDIUM` | Indicates the sensitivity of the detected PII |
| Timestamp | `2026-03-11T14:30:00Z` | Records when the detection occurred |
| Source app | `com.example.messenger` | Identifies which app the PII was detected in |
| Detection ID | `uuid-a1b2c3d4...` | Unique identifier for the detection record |

### Data That Is Never Stored

The following data is **never** written to disk or persisted in any form:

- **Raw PII text** -- The actual sensitive content (credit card numbers, SSNs, email addresses, etc.) is never stored. It exists only in device memory during the brief detection pipeline execution and is immediately eligible for garbage collection afterward.
- **Full clipboard contents** -- The complete text that was copied is not stored.
- **Text field contents** -- Text captured by the accessibility service is not stored.
- **Screenshots or screen content** -- PrivacyGuard does not capture or store screen content.

---

## How Data Is Encrypted

All stored data is encrypted at rest using industry-standard encryption:

- **Algorithm:** AES-256-GCM (Advanced Encryption Standard with 256-bit keys in Galois/Counter Mode)
- **Key management:** Encryption keys are generated and stored in the **Android Keystore**, which provides hardware-backed key storage on supported devices. Keys never leave the secure hardware enclave.
- **Implementation:** PrivacyGuard uses `EncryptedSharedPreferences` from the AndroidX Security Crypto library, which provides transparent encryption and decryption of stored data.
- **Scope:** All detection metadata and user preferences are encrypted. There is no unencrypted data stored by the application.

If the device is compromised, an attacker would need to bypass the Android Keystore (which is hardware-backed on most modern devices) to decrypt the stored metadata. Even then, they would find only detection metadata -- never the actual PII content.

---

## User Controls

PrivacyGuard provides full user control over stored data and application behavior:

### Delete Detection History

- Users can delete individual detection records from the history screen.
- Users can delete all detection history at once using the "Clear All History" option in Settings.
- Deleted data is permanently removed from encrypted storage and cannot be recovered.

### App Whitelist Management

- Users can add apps to a whitelist to suppress PII detection alerts for those specific applications.
- Common use case: whitelisting a password manager so that copying passwords does not trigger alerts.
- The whitelist is stored locally in encrypted storage.
- Apps can be added or removed from the whitelist at any time.

### Service Controls

- Users can enable or disable clipboard monitoring at any time.
- Users can enable or disable the accessibility service through Android system settings.
- When services are disabled, no text is captured or analyzed.

### Alert Preferences

- Users can adjust alert sensitivity levels.
- Users can choose between overlay alerts, notifications, or both.

---

## Accessibility Service

PrivacyGuard includes an optional Android AccessibilityService that monitors text fields across applications. This service:

- **Only** listens for text change and focus events in text input fields.
- **Does not** capture screen content, gestures, or any other accessibility data.
- **Does not** store the text it captures -- text is analyzed in memory and immediately discarded after detection.
- **Can be disabled** at any time through Android's accessibility settings.
- Is scoped to the minimum required event types to function.

The accessibility service is used solely for the purpose of detecting PII in text fields before it can be submitted or shared.

---

## Children's Privacy

PrivacyGuard does not collect any data from anyone, including children. There are no accounts, no tracking, and no data transmission. The app functions identically for all users regardless of age.

---

## Changes to This Policy

If this privacy policy is updated, the changes will be reflected in the `PRIVACY_POLICY.md` file in the project repository. Since PrivacyGuard has no network capability and no user accounts, there is no mechanism to push policy notifications -- users should review this file when updating the application.

---

## Contact

For questions about this privacy policy or PrivacyGuard's data handling practices, please open an issue in the project repository.

---

## Summary

| Question | Answer |
|---|---|
| Does PrivacyGuard collect data? | **No.** |
| Does PrivacyGuard transmit data over the network? | **No. It cannot -- the INTERNET permission is removed.** |
| Is PII text stored on the device? | **No. Only detection metadata (type, severity, timestamp) is stored.** |
| Is stored data encrypted? | **Yes. AES-256-GCM with Android Keystore-managed keys.** |
| Can users delete their data? | **Yes. Individual records or all history can be deleted.** |
| Are there analytics or telemetry? | **No. None whatsoever.** |
| Does it work offline? | **Yes. It works exclusively offline.** |
