# Contributing to PrivacyGuard

Thank you for your interest in contributing to PrivacyGuard! This document outlines the process for contributing to this project.

## Getting Started

1. Fork the repository at https://github.com/aaravjj2/privacyguard
2. Clone your fork locally
3. Create a new branch for your feature or fix: `git checkout -b feature/my-feature`
4. Copy `local.properties.example` to `local.properties` and add your Melange API key

## Development Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Melange SDK personal key (from https://melange.ai)

### Building
```bash
./gradlew assembleDebug
```

### Running Tests
```bash
./gradlew test
```

### Running Lint
```bash
./gradlew lint
```

## Privacy Requirements

PrivacyGuard is an **on-device-only** privacy tool. All contributions must adhere to:

- **No network calls**: The app has `INTERNET` permission explicitly removed. Do not add any network code.
- **No third-party analytics**: No Firebase, no Crashlytics, no tracking SDKs.
- **On-device ML only**: PII detection runs entirely on-device using the Melange SDK.
- **No data exfiltration**: Any code that could send user data off-device will be rejected.

## Code Style

- Follow standard Kotlin coding conventions
- Use `ktlint` formatting (enforced by `./gradlew ktlintCheck`)
- Keep functions small and focused
- Write unit tests for all new validation logic
- Target 100% branch coverage for validator classes

## Pull Request Process

1. Ensure all tests pass: `./gradlew test`
2. Ensure lint passes: `./gradlew lint`
3. Update relevant documentation if changing public APIs
4. Fill out the PR template completely
5. One PR per logical change — do not bundle unrelated fixes

## Submitting Issues

When reporting a bug, please include:
- Android version and device model
- Steps to reproduce
- Expected vs actual behavior
- Any relevant logcat output (with PII redacted)

Do **not** include any real personal data (SSNs, credit cards, etc.) in issue reports.

## Security Issues

Please do **not** file public issues for security vulnerabilities. See [SECURITY.md](SECURITY.md) for the responsible disclosure process.

## License

By contributing to PrivacyGuard, you agree that your contributions will be licensed under the MIT License.
