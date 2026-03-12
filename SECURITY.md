# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.x     | :white_check_mark: |

## Reporting a Vulnerability

PrivacyGuard takes security seriously. If you discover a security vulnerability, please follow responsible disclosure practices.

### How to Report

**Do not file a public GitHub issue for security vulnerabilities.**

Instead, please report security issues by emailing the maintainer directly. Include:

- Description of the vulnerability
- Steps to reproduce
- Potential impact assessment
- Suggested fix (if you have one)

You will receive a response within 48 hours acknowledging your report. We aim to patch confirmed vulnerabilities within 7 days.

### What to Report

Please report security issues including but not limited to:

- PII leakage through logs, analytics, or network calls
- Keystore or encryption implementation weaknesses
- Accessibility service privilege escalation
- Insecure data storage (unencrypted PII on disk)
- Bypass of redaction or masking logic
- Vulnerabilities in the WordPiece tokenizer or ML inference pipeline

### Out of Scope

The following are **not** in scope for security reports:

- Theoretical attacks requiring physical device access with root
- Issues that require the user to have already installed malware
- Missing security headers (not applicable for Android apps)
- Denial of service via crafted input to validators (non-exploitable outside app)

## Security Architecture

PrivacyGuard is designed with privacy-by-default:

- **No INTERNET permission**: The `android.permission.INTERNET` permission is explicitly removed via `tools:node="remove"` in the manifest. The app cannot make network requests.
- **AES-256-GCM encryption**: Sensitive settings are stored using `EncryptedSharedPreferences` backed by the Android Keystore.
- **On-device ML only**: The `Team_ZETIC/TextAnonymizer` NER model runs entirely on-device via the Melange SDK. No text is ever sent to a server.
- **Foreground service**: The clipboard monitoring service runs as a foreground service with a persistent notification, ensuring user visibility and control.
- **Minimal permissions**: Only `FOREGROUND_SERVICE`, `RECEIVE_BOOT_COMPLETED`, and `ACCESSIBILITY_SERVICE` permissions are requested.

## Disclosure Policy

After a fix is developed and released:

1. The reporter will be credited in the release notes (unless they prefer anonymity).
2. A security advisory will be published on GitHub.
3. The vulnerability will be documented in this file under a new "Known Fixed Vulnerabilities" section.
