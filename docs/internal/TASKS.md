# 🛡️ PrivacyGuard — Complete Task List
### Melange On-Device AI Hackathon 2026
**500+ Unique Engineering, Design, and Submission Tasks**

> **Legend:** `[ ]` = Todo · `[x]` = Done · ⚡ = Critical Path · 🤖 = AI/Melange specific · 🔒 = Privacy/Security · 🎨 = UI/UX · 📋 = Submission

---

## PHASE 0 — ENVIRONMENT SETUP & PROJECT BOOTSTRAP
> **Goal:** Everything compiles and Melange SDK produces its first inference output.

### 0.1 — Accounts & Credentials
- [x] ⚡ Create account at melange.zetic.ai using your email address — this is required for your Personal Key
- [x] ⚡ Activate Melange Pro+ trial using coupon code `ZETICHACKATHON-BM26` from the hackathon brief
- [x] ⚡ Copy and securely store your Melange Personal Key — you will need it in BuildConfig
- [x] Create a Devpost account at devpost.com if you do not already have one
- [x] Register for the Melange On-Device AI Hackathon on the Devpost page before the deadline
- [x] Create a new public GitHub repository named `privacyguard` or `PrivacyGuard` with a brief description
- [x] Clone the ZETIC_Melange_apps repository locally for reference: `git clone https://github.com/zetic-ai/ZETIC_Melange_apps`
- [x] Browse the ZETIC_Melange_apps repository and identify the TextAnonymizer example to use as model reference
- [x] Read through https://docs.zetic.ai fully — focus on Android SDK setup section
- [x] Bookmark the Melange dashboard at https://melange.zetic.ai for model management

### 0.2 — Android Studio Project Setup
- [x] ⚡ Install Android Studio Iguana or newer if not already installed
- [x] ⚡ Create a new Android project: Empty Activity, Kotlin, minimum SDK API 26 (Android 8.0 Oreo)
- [x] ⚡ Set package name to `com.privacyguard` or your preferred namespace
- [x] ⚡ Enable Jetpack Compose in the new project wizard (use Compose for all UI)
- [x] Set `compileSdk` to 34 and `targetSdk` to 34 in build.gradle
- [x] Confirm the project builds and the default Compose Hello World screen renders on a device or emulator
- [x] Set up an Android Virtual Device (AVD) running API 33 for testing if no physical device available
- [x] Enable USB debugging on your physical Android device if using one
- [x] Run the default project on your test device/emulator to confirm the development pipeline works end to end
- [x] Initialise git in the project directory and push the initial commit to GitHub

### 0.3 — Dependency Configuration
- [x] ⚡ Add Melange SDK dependency to `app/build.gradle`: `implementation("com.zeticai.mlange:mlange:1.2.2")`
- [x] ⚡ Add `jniLibs { useLegacyPackaging = true }` inside the `packaging` block in build.gradle
- [x] Add Kotlin Coroutines dependency: `implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")`
- [x] Add AndroidX Lifecycle ViewModel: `implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")`
- [x] Add AndroidX Security Crypto for EncryptedSharedPreferences: `implementation("androidx.security:security-crypto:1.1.0-alpha06")`
- [x] Add Gson for JSON serialisation of detection events: `implementation("com.google.code.gson:gson:2.10.1")`
- [x] Add Compose Material3: `implementation("androidx.compose.material3:material3:1.2.1")`
- [x] Add Accompanist Permissions library for runtime permission requests in Compose
- [x] Sync Gradle and confirm all dependencies resolve without errors
- [x] Run `./gradlew dependencies` to verify no version conflicts in the dependency tree

### 0.4 — Melange SDK Verification
- [x] ⚡ Create a `MelangeTest.kt` scratch file and initialise a `ZeticMLangeModel` instance with your Personal Key
- [x] ⚡ Run a minimal inference call with a dummy input to verify the SDK is correctly integrated
- [x] Confirm the model downloads or loads from local assets without throwing an exception
- [x] Log inference output to Logcat and confirm you receive non-null output buffers
- [x] Measure and log the cold-start latency of the first inference call
- [x] Measure and log the warm inference latency on subsequent calls
- [x] Confirm the SDK correctly identifies and uses the NPU if available on your device
- [x] Delete the scratch test file after verification — do not leave test code in the production codebase

### 0.5 — Project Structure
- [x] Create package directory `service/` under `com.privacyguard` for background services
- [x] Create package directory `ml/` for all Melange/AI model related classes
- [x] Create package directory `ui/` with sub-packages `alert/`, `dashboard/`, `history/`, `whitelist/`, `settings/`, `onboarding/`
- [x] Create package directory `data/` for repositories and data models
- [x] Create package directory `util/` for utility classes
- [x] Create a `BuildConfig.kt` or `local.properties` entry for your Melange Personal Key — never hardcode it in source
- [x] Add `local.properties` and `*.jks` to `.gitignore` to prevent credential leakage
- [x] Create a `README.md` in the root with placeholder sections: Project Name, Problem, Melange Usage, Model Used, Setup Instructions
- [x] Create an `ARCHITECTURE.md` with placeholder for the data flow diagram
- [x] Set up GitHub Actions workflow file `.github/workflows/build.yml` for automated build verification (optional but impressive)

---

## PHASE 1 — AI MODEL CORE
> **Goal:** Text goes in, PII entities come out with type and confidence score.

### 1.1 — Model Selection & Acquisition
- [x] ⚡ 🤖 Log into the Melange dashboard at https://melange.zetic.ai
- [x] ⚡ 🤖 Locate the `Team_ZETIC/TextAnonymizer` model in the public model library
- [x] 🤖 Review the TextAnonymizer model's input/output specification — document expected token format and output label set
- [x] 🤖 Confirm the model supports the following entity types: credit card, SSN, email, phone, password, API key
- [x] 🤖 Note the model's maximum sequence length (typically 512 tokens) for input clamping
- [x] 🤖 Note the model's label mapping: map integer class indices to entity type strings
- [x] 🤖 Download or reference the model key/ID that will be passed to `ZeticMLangeModel`
- [x] 🤖 Check if a quantised version of the model is available for NPU acceleration — prefer quantised

### 1.2 — PIIEntity Data Model
- [x] Create `PIIEntity.kt` data class with fields: `entityType: EntityType`, `confidence: Float`, `startIndex: Int`, `endIndex: Int`, `rawText: String` (for internal use only, not persisted)
- [x] Create `EntityType.kt` enum with values: `CREDIT_CARD`, `SSN`, `PASSWORD`, `API_KEY`, `EMAIL`, `PHONE`, `PERSON_NAME`, `ADDRESS`, `DATE_OF_BIRTH`, `MEDICAL_ID`, `UNKNOWN`
- [x] Add a `severity: Severity` computed property to `EntityType` returning `CRITICAL`, `HIGH`, or `MEDIUM`
- [x] Create `Severity.kt` enum with values `CRITICAL`, `HIGH`, `MEDIUM` and a `color: Color` property for each
- [x] Write unit tests for `EntityType.severity` to confirm each entity maps to the correct severity level
- [x] Create `PIIAnalysisResult.kt` data class wrapping `List<PIIEntity>` with a computed `hasSensitiveData()` boolean
- [x] Add a computed `highestSeverity` property to `PIIAnalysisResult` returning the max severity present
- [x] Write unit tests for `PIIAnalysisResult` that verify `hasSensitiveData()` returns false for empty entity lists

### 1.3 — Tokenizer
- [x] Create `PIITokenizer.kt` class responsible for converting raw text to ByteBuffer inputs for the Melange model
- [x] Implement WordPiece tokenisation matching the vocabulary of the TextAnonymizer model
- [x] Load the model vocabulary file from app assets — add `vocab.txt` to `assets/` directory
- [x] Implement `encode(text: String): TokenizerOutput` returning token IDs and attention mask as IntArrays
- [x] Handle input texts longer than the model's maximum sequence length by truncating and logging a warning
- [x] Handle empty string input gracefully — return empty output without calling the model
- [x] Handle null input gracefully — return empty output without throwing a NullPointerException
- [x] Convert IntArray token IDs to ByteBuffer in the correct format expected by Melange (int32, little-endian)
- [x] Convert IntArray attention mask to ByteBuffer in the same format
- [x] Write unit tests for the tokenizer: verify tokenisation of a known string matches expected token IDs
- [x] Test tokenizer with a credit card number string to confirm it tokenises correctly
- [x] Test tokenizer with a sentence containing an email address
- [x] Test tokenizer with special characters, emojis, and unicode text to ensure no crashes

### 1.4 — PrivacyModel (Melange Inference Wrapper)
- [x] ⚡ 🤖 Create `PrivacyModel.kt` as a singleton object or class with lifecycle management
- [x] ⚡ 🤖 Initialise `ZeticMLangeModel` with context, Personal Key, and model name — handle initialisation errors with a sealed Result type
- [x] 🤖 Implement `analyzeText(text: String): PIIAnalysisResult` as a suspend function running in a background coroutine
- [x] 🤖 Call `privacyModel.run(arrayOf(inputIdBuffer, attentionMaskBuffer))` with properly prepared ByteBuffers
- [x] 🤖 Read `privacyModel.outputBuffers[0]` after inference to get raw logit output
- [x] Implement `OutputDecoder.kt` that converts raw logit ByteBuffer to a list of `PIIEntity` objects
- [x] In OutputDecoder: apply softmax or argmax over the NER label dimension to get per-token predictions
- [x] In OutputDecoder: group consecutive tokens with the same entity type into entity spans
- [x] In OutputDecoder: map integer class indices back to `EntityType` enum values using the label map
- [x] In OutputDecoder: compute confidence score as the max softmax probability for the predicted class
- [x] In OutputDecoder: filter out entities below the confidence threshold (default 0.85)
- [x] 🤖 Implement `close()` method that calls `privacyModel.close()` — wire to ViewModel/Service lifecycle
- [x] Implement a `ModelState` sealed class: `Initialising`, `Ready`, `Running`, `Error(message)` for UI state binding
- [x] Expose a `modelState: StateFlow<ModelState>` that UI components can observe
- [x] Log inference latency in milliseconds using `System.currentTimeMillis()` before and after `run()` call
- [x] Expose `lastInferenceLatencyMs: Long` as a LiveData/StateFlow for the dashboard performance indicator
- [x] Handle the case where the model is called before initialisation is complete — queue the request
- [x] Implement model warm-up: run a single inference on a dummy input at startup to prime the NPU cache
- [x] Add a `runMode` configuration option supporting `QUANTIZED` (NPU) and `STANDARD` (CPU) modes

### 1.5 — Confidence Thresholds
- [x] Create `ConfidenceThresholds.kt` with per-entity-type default thresholds
- [x] Set `CREDIT_CARD` threshold to 0.90 — high precision critical for avoiding false positives on card-like numbers
- [x] Set `SSN` threshold to 0.92 — SSN patterns have a distinct format, high precision achievable
- [x] Set `PASSWORD` threshold to 0.80 — contextual entity, slightly lower threshold acceptable
- [x] Set `API_KEY` threshold to 0.85 — token-like strings need decent confidence to avoid flagging random hashes
- [x] Set `EMAIL` threshold to 0.95 — email format is very precise, false positives should be near zero
- [x] Set `PHONE` threshold to 0.88 — phone numbers can match non-phone digit sequences
- [x] Set `PERSON_NAME` threshold to 0.75 — names are inherently ambiguous, lower threshold acceptable
- [x] Make thresholds user-configurable via the Settings screen with a per-type slider
- [x] Persist threshold overrides to EncryptedSharedPreferences so they survive app restarts

### 1.6 — Model Validation
- [x] Create a `ModelValidation.kt` test class with a corpus of known PII strings
- [x] Add test case: `"My credit card is 4532 1234 5678 9012"` should detect CREDIT_CARD
- [x] Add test case: `"SSN: 123-45-6789"` should detect SSN
- [x] Add test case: `"Email me at user@example.com"` should detect EMAIL
- [x] Add test case: `"Call me at (555) 867-5309"` should detect PHONE
- [x] Add test case: `"My API key is sk-abc123xyz789"` should detect API_KEY
- [x] Add test case: `"Hello, how are you?"` should return EMPTY result — no false positive
- [x] Add test case: `"The invoice total is $4,532.00"` should NOT detect as credit card — common false positive pattern
- [x] Add test case: Unicode and emoji mixed text should not crash the model
- [x] Add test case: Very long text (1000+ characters) should be handled gracefully via truncation
- [x] Run validation suite and record true positive rate — target >95% on PII test cases
- [x] Run validation suite and record false positive rate — target <5% on benign text corpus

---

## PHASE 2 — MONITORING PIPELINE
> **Goal:** Real clipboard and text field events reach the model and produce PIIAnalysisResults.

### 2.1 — ClipboardMonitorService
- [x] ⚡ Create `ClipboardMonitorService.kt` extending `Service`
- [x] ⚡ Override `onStartCommand()` to start the service as a foreground service with a persistent notification
- [x] Create a `NotificationChannel` for `PrivacyGuard Monitoring` channel with low importance (silent)
- [x] Build the persistent notification: icon, title "PrivacyGuard Active", action to open the dashboard
- [x] Call `startForeground(NOTIFICATION_ID, notification)` immediately in `onStartCommand()` before any other work
- [x] ⚡ Get a reference to `ClipboardManager` via `getSystemService(CLIPBOARD_SERVICE)`
- [x] ⚡ Call `clipboardManager.addPrimaryClipChangedListener(listener)` to register for clipboard events
- [x] In the listener: extract text from `clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()`
- [x] Guard the listener against null clipboard data — return early if text is null or blank
- [x] Guard the listener against very short strings (< 5 characters) — too short to contain meaningful PII
- [x] Guard the listener against very long strings (> 10,000 characters) — truncate to avoid memory issues
- [x] Pass extracted text to the debounced analysis pipeline via a coroutine
- [x] Track the source app that wrote to the clipboard using `ClipData.description` if available
- [x] Override `onDestroy()` to remove the clipboard listener and release resources
- [x] Handle the case where the service is destroyed and restarted — re-register the listener properly
- [x] Add `START_STICKY` as the return value from `onStartCommand()` so the service restarts if killed
- [x] Register the service in `AndroidManifest.xml` with `android:foregroundServiceType="dataSync"`
- [x] Add `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_DATA_SYNC` permissions to the manifest
- [x] Add `RECEIVE_BOOT_COMPLETED` permission so a BroadcastReceiver can restart the service after reboot
- [x] Create `BootReceiver.kt` extending `BroadcastReceiver` that starts `ClipboardMonitorService` on boot
- [x] Register `BootReceiver` in the manifest with `android.intent.action.BOOT_COMPLETED` intent filter

### 2.2 — PrivacyAccessibilityService
- [x] ⚡ Create `PrivacyAccessibilityService.kt` extending `AccessibilityService`
- [x] ⚡ Create `res/xml/accessibility_service_config.xml` configuring the service for `typeViewTextChanged` events
- [x] In the config XML, set `android:description` to a clear, honest explanation of what the service monitors
- [x] In the config XML, set `android:settingsActivity` to the app's Settings screen
- [x] ⚡ Override `onAccessibilityEvent()` to handle `TYPE_VIEW_TEXT_CHANGED` events
- [x] In `onAccessibilityEvent()`: read the changed text from `event.text.joinToString()`
- [x] In `onAccessibilityEvent()`: read the source app package name from `event.packageName`
- [x] Check the whitelist before any analysis — if source app is whitelisted, return immediately
- [x] Skip analysis if the source is PrivacyGuard itself — prevent recursive event loops
- [x] Detect password field input type: if `event.source?.inputType` has `TYPE_TEXT_VARIATION_PASSWORD`, skip analysis
- [x] Detect web password fields by checking `event.source?.hint` for password-related strings
- [x] Pass the text to the debounced analysis pipeline — do not call inference inline
- [x] Override `onInterrupt()` with an empty implementation (required by the abstract class)
- [x] Register the service in `AndroidManifest.xml` with `android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"`
- [x] Add `<meta-data>` referencing `accessibility_service_config.xml` in the service declaration
- [x] Test that the service receives events when typing in the built-in Notes app
- [x] Test that the service does NOT receive events when typing in a password field
- [x] Test that the service does NOT receive events for whitelisted apps

### 2.3 — Debouncer
- [x] Create `Debouncer.kt` utility class using Kotlin coroutines for debouncing rapid-fire events
- [x] Implement `debounce(delayMs: Long, action: suspend () -> Unit)` that cancels pending calls within the delay window
- [x] Use `CoroutineScope` with `Dispatchers.Default` for background execution
- [x] Set default debounce delay to 800ms — balances responsiveness with avoiding excessive inference calls
- [x] Make the debounce delay configurable in Settings
- [x] Ensure the debouncer properly cancels pending jobs when the service is destroyed
- [x] Write unit tests for the debouncer verifying it only fires once per burst of events within the window
- [x] Test the debouncer with rapid clipboard changes (simulated copy spam) to confirm no memory leak

### 2.4 — Analysis Pipeline
- [x] ⚡ Create `AnalysisPipeline.kt` that ties together Debouncer, PrivacyModel, AlertManager, and EncryptedLogRepository
- [x] Implement `processText(text: String, sourceApp: String?)` as the single entry point for all monitoring events
- [x] In `processText()`: check whitelist first — return immediately if whitelisted
- [x] In `processText()`: check minimum text length — return if below threshold
- [x] In `processText()`: run through Debouncer before calling inference
- [x] In `processText()`: call `PrivacyModel.analyzeText(text)` and await the result
- [x] In `processText()`: if result has sensitive data, call `AlertManager.show(result, sourceApp)`
- [x] In `processText()`: call `EncryptedLogRepository.record(result, sourceApp)` regardless of severity
- [x] Emit metrics (latency, entity types found) to the dashboard ViewModel via a SharedFlow
- [x] Handle coroutine cancellation gracefully — use `try/finally` to ensure cleanup on cancellation
- [x] Handle model inference exceptions — log the error and fail silently rather than crashing
- [x] Implement a circuit breaker: if inference fails 5 times in a row, pause the pipeline for 60 seconds

### 2.5 — Whitelist Integration in Pipeline
- [x] Query `WhitelistManager.isWhitelisted(packageName: String)` at the start of `processText()`
- [x] Ensure whitelist check is O(1) using an in-memory Set rather than a database query
- [x] Update the in-memory whitelist Set whenever `WhitelistManager` is modified
- [x] Add PrivacyGuard's own package name to a hard-coded exclusion list — never analyse our own UI

---

## PHASE 3 — ALERT SYSTEM & UI
> **Goal:** The right alert appears at the right time with the right information.

### 3.1 — AlertManager
- [x] Create `AlertManager.kt` as a singleton coordinating all alert display logic
- [x] Implement `show(result: PIIAnalysisResult, sourceApp: String?)` that selects the correct alert type by severity
- [x] For `CRITICAL` severity: call `showCriticalOverlay(result, sourceApp)`
- [x] For `HIGH` severity: call `showHighBanner(result, sourceApp)`
- [x] For `MEDIUM` severity: call `showMediumToast(result, sourceApp)`
- [x] Implement an alert queue to prevent multiple overlapping alerts — only one alert at a time
- [x] Implement cooldown logic: the same entity type from the same source app cannot re-alert within 30 seconds
- [x] Expose `currentAlert: StateFlow<AlertState?>` for UI components to observe
- [x] Create `AlertState.kt` sealed class: `Critical(result, sourceApp)`, `High(result, sourceApp)`, `Medium(result, sourceApp)`, `None`

### 3.2 — Critical Alert Overlay
- [x] ⚡ Request `SYSTEM_ALERT_WINDOW` permission during onboarding — this is required for the full-screen overlay
- [x] ⚡ Create `AlertOverlayService.kt` that uses `WindowManager` to display a Compose-based overlay view
- [x] Implement the overlay as a `ComposeView` inflated and added to `WindowManager` with `TYPE_APPLICATION_OVERLAY`
- [x] Set overlay layout params: `MATCH_PARENT` width, `WRAP_CONTENT` height, `FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN`
- [x] Design the overlay UI: red background gradient, shield icon, entity type label, source app name, action buttons
- [x] Add `[Clear Clipboard]` button that calls `ClipboardManager.clearPrimaryClip()` and dismisses the overlay
- [x] Add `[Dismiss]` button that dismisses the overlay without taking action
- [x] Add `[Add to Whitelist]` button that opens a confirmation dialog and adds the source app to whitelist
- [x] Add a countdown timer (10 seconds) after which the overlay auto-dismisses if the user takes no action
- [x] Animate the overlay entrance with a slide-down animation from top using Compose `AnimatedVisibility`
- [x] Ensure the overlay is dismissed when the user presses the back button via `OnBackPressedCallback`
- [x] Handle screen rotation while overlay is showing — dismiss and re-show in correct orientation

### 3.3 — High Severity Banner
- [x] Create `AlertBannerComposable.kt` as a Compose bottom sheet component for HIGH severity alerts
- [x] Design: orange accent, PII type icon, description text, source app, two action buttons
- [x] Show via `ModalBottomSheet` in Jetpack Compose Material3
- [x] Include `[Clear Clipboard]` and `[Dismiss]` buttons with appropriate styling
- [x] Animate entrance with the standard Material3 bottom sheet drag handle and slide-up animation
- [x] Dismiss on swipe-down gesture — standard ModalBottomSheet behaviour
- [x] Display the number of entities found if multiple are detected in the same text

### 3.4 — Medium Severity Toast
- [x] Show a Compose `Snackbar` at the bottom of the screen for MEDIUM severity events
- [x] Include entity type label and source app name in the Snackbar message
- [x] Add a single `[Details]` action button that opens the detection history screen
- [x] Auto-dismiss after 4 seconds — medium severity should not block the user

### 3.5 — Main Dashboard Screen
- [x] ⚡ 🎨 Create `DashboardScreen.kt` as the app's home screen in Jetpack Compose
- [x] Show a circular `ProtectionScore` indicator (0-100) that increases with uptime and decreases when PII is detected
- [x] Show the monitoring status as a toggle with `ON`/`OFF` state and a clear visual indicator
- [x] Show real-time stats: detections today, detections this week, most common entity type
- [x] Show a live feed of the last 5 detection events as cards — entity type, time, source app
- [x] Show the current inference latency in milliseconds in a small metrics row
- [x] Include a `[View All History]` button that navigates to the History screen
- [x] Include a `[Manage Whitelist]` button that navigates to the Whitelist screen
- [x] Animate the protection score indicator with a smooth sweep animation on first load
- [x] Implement `DashboardViewModel.kt` with `StateFlow<DashboardUiState>` exposing all reactive state
- [x] Handle the case where Accessibility Service is not enabled — show a prominent permission prompt card
- [x] Handle the case where Notification permission is not granted — show a prompt card
- [x] Handle the case where SYSTEM_ALERT_WINDOW is not granted — show a prompt card

### 3.6 — Detection History Screen
- [x] 🎨 Create `HistoryScreen.kt` in Jetpack Compose showing a scrollable chronological list of all detection events
- [x] Each history item card should show: severity indicator dot, entity type, source app, timestamp (relative)
- [x] Implement filter chips at the top for filtering by severity: All / Critical / High / Medium
- [x] Implement swipe-to-delete on individual history items
- [x] Implement a `[Clear All History]` button with a confirmation dialog
- [x] Show an empty state illustration and message when no history exists
- [x] Implement `HistoryViewModel.kt` with a `Flow<List<DetectionEvent>>` from `EncryptedLogRepository`
- [x] Paginate the history list using Compose `LazyColumn` with pagination — avoid loading thousands of items at once
- [x] Add a search bar to filter history by entity type or source app name

### 3.7 — Whitelist Manager Screen
- [x] 🎨 Create `WhitelistScreen.kt` in Jetpack Compose
- [x] Load the list of all installed apps using `PackageManager.getInstalledApplications()`
- [x] Display apps in a searchable `LazyColumn` with app icon, app name, and a toggle switch
- [x] Show a `[Trusted Apps]` header section with pre-populated suggestions (1Password, Bitwarden, etc.)
- [x] Sort the list: whitelisted apps first, then alphabetically
- [x] Implement search field that filters by app name in real time
- [x] Persist whitelist changes immediately via `WhitelistManager` on toggle change
- [x] Show a count badge on the navigation item showing how many apps are whitelisted
- [x] Implement `WhitelistViewModel.kt` managing the list state and toggle logic

### 3.8 — Settings Screen
- [x] 🎨 Create `SettingsScreen.kt` in Jetpack Compose
- [x] Add `Enable Clipboard Monitoring` toggle switch with explanation text
- [x] Add `Enable Text Field Monitoring` toggle switch with explanation text
- [x] Add `Debounce Delay` slider (range 200ms to 2000ms, default 800ms) with current value display
- [x] Add per-entity-type confidence threshold sliders in a collapsible `Expandable` section
- [x] Add `Notification Channel Settings` shortcut that deep-links to system notification settings
- [x] Add `[Reset All Settings to Default]` button with confirmation dialog
- [x] Add `Enable Performance Metrics` toggle that shows latency in the dashboard
- [x] Add `Run Mode` option: NPU (Quantised) / CPU (Standard) — with explanation of tradeoff
- [x] Persist all settings via `EncryptedSharedPreferences` immediately on change
- [x] Read all settings back correctly on app restart — write a round-trip test

### 3.9 — Onboarding Flow
- [x] 🎨 Create a 3-screen onboarding flow shown only on first app launch
- [x] Onboarding Screen 1: Title "Your Clipboard Is Exposed" — explain the problem with a visual illustration
- [x] Onboarding Screen 2: "How PrivacyGuard Protects You" — explain on-device AI with a flow diagram
- [x] Onboarding Screen 3: "Grant Permissions" — request Accessibility Service, Notification, SYSTEM_ALERT_WINDOW in sequence
- [x] For each permission: show an explanation dialog before opening the system permission screen
- [x] Handle the case where the user denies a permission — explain the consequence and offer to open Settings
- [x] Mark onboarding as complete in SharedPreferences once all permissions are granted
- [x] Do NOT show onboarding again if it has been completed — check the flag in `MainActivity.onCreate()`
- [x] Add a `[Skip]` button that bypasses permissions but shows a persistent in-app warning banner

---

## PHASE 4 — DATA LAYER & PRIVACY HARDENING
> **Goal:** Everything persists correctly, nothing phones home, all data is encrypted.

### 4.1 — DetectionEvent Data Model
- [x] Create `DetectionEvent.kt` data class with: `id: UUID`, `timestamp: Long`, `entityType: EntityType`, `severity: Severity`, `sourceApp: String?`, `actionTaken: UserAction`
- [x] Create `UserAction.kt` enum: `CLIPBOARD_CLEARED`, `DISMISSED`, `WHITELISTED_APP`, `AUTO_DISMISSED`, `NO_ACTION`
- [x] Explicitly DO NOT include `rawText` in `DetectionEvent` — the sensitive data is never persisted
- [x] Implement `DetectionEvent.toJson()` and `DetectionEvent.fromJson()` using Gson for serialisation
- [x] Write unit tests for serialisation round-trip — ensure no fields are lost or corrupted

### 4.2 — EncryptedLogRepository
- [x] ⚡ 🔒 Create `EncryptedLogRepository.kt` using `EncryptedSharedPreferences` for all storage
- [x] ⚡ 🔒 Initialise `EncryptedSharedPreferences` with Android Keystore master key: `MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()`
- [x] Implement `record(event: DetectionEvent)` that serialises the event and stores it under key `event_${event.id}`
- [x] Implement `getAllEvents(): Flow<List<DetectionEvent>>` as a reactive stream that emits on every change
- [x] Implement `deleteEvent(id: UUID)` for swipe-to-delete functionality
- [x] Implement `deleteAllEvents()` for the clear history action
- [x] Implement `getRecentEvents(count: Int): List<DetectionEvent>` for the dashboard preview
- [x] Implement `getEventCountByType(): Map<EntityType, Int>` for dashboard statistics
- [x] Limit total stored events to 1000 — implement LRU eviction when limit is exceeded
- [x] Write unit tests for all repository operations using Robolectric

### 4.3 — WhitelistManager
- [x] Create `WhitelistManager.kt` using `EncryptedSharedPreferences` for storage
- [x] Implement `addToWhitelist(packageName: String)` that stores the package name in the encrypted set
- [x] Implement `removeFromWhitelist(packageName: String)`
- [x] Implement `isWhitelisted(packageName: String): Boolean` backed by an in-memory `HashSet` for O(1) lookup
- [x] Implement `getAllWhitelistedApps(): Set<String>` for the UI list
- [x] Implement `prePopulateDefaults()` called on first run with known password manager package names
- [x] Add to defaults: `com.agilebits.onepassword`, `com.bitwarden.mobile`, `org.keepassdroid`, `com.lastpass.lpandroid`
- [x] Expose a `Flow<Set<String>>` for the whitelist screen to observe changes reactively
- [x] Write unit tests for add/remove/isWhitelisted operations

### 4.4 — Privacy Hardening
- [x] ⚡ 🔒 Remove the `INTERNET` permission from `AndroidManifest.xml` — confirm it is NOT present
- [x] 🔒 Verify no INTERNET permission by running `aapt dump permissions app-debug.apk | grep INTERNET` — expect no output
- [x] 🔒 Add `android:allowBackup="false"` to the `<application>` tag — prevents data in cloud backups
- [x] 🔒 Add `android:fullBackupContent="@xml/backup_rules"` with a `backup_rules.xml` that excludes all preferences
- [x] 🔒 Remove Firebase, Analytics, Crashlytics, or any other telemetry dependency if accidentally included
- [x] 🔒 Audit all third-party SDKs for network calls — confirm Melange SDK itself only communicates during model download, not during inference
- [x] 🔒 Add ProGuard rules to strip any reflection-based network access attempts from third-party libraries
- [x] 🔒 Set `android:networkSecurityConfig` to a custom config that blocks all cleartext traffic
- [x] 🔒 Add `android:usesCleartextTraffic="false"` to the `<application>` tag as an extra safeguard
- [x] 🔒 Enable full-disk encryption via `android:directBootAware="false"` for services that don't need to run pre-unlock

### 4.5 — Keystore & Key Management
- [x] 🔒 Create `KeystoreManager.kt` managing the app's Android Keystore keys
- [x] 🔒 Generate a `MasterKey` using `AES256_GCM` scheme for all EncryptedSharedPreferences instances
- [x] 🔒 Verify the master key exists before each sensitive operation — regenerate if corrupted (handles factory reset)
- [x] 🔒 Add a `strongBoxBacked = true` flag if the device has StrongBox hardware security module
- [x] 🔒 Add a `BiometricManager` check to optionally require biometric authentication before viewing history

---

## PHASE 5 — POLISH, TESTING & DEMO PREP
> **Goal:** The app is stable, fast, and looks great on camera.

### 5.1 — Performance Optimization
- [x] Profile the app with Android Studio CPU Profiler during a clipboard monitor cycle — identify bottlenecks
- [x] Ensure model inference is always on `Dispatchers.Default` — never on the main thread
- [x] Ensure all UI updates are always on `Dispatchers.Main` via `withContext` or `LiveData.postValue()`
- [x] Verify the foreground service does not cause ANR (Application Not Responding) — watchdog timeout is 5 seconds
- [x] Measure battery drain over 1 hour of active monitoring — should be under 2%
- [x] Reduce `ClipboardMonitorService` memory footprint by reusing ByteBuffer instances between inference calls
- [x] Implement model instance pooling if concurrency is needed (unlikely in hackathon but good practice)
- [x] Test with a large clipboard payload (5,000 characters) to confirm no UI freeze or crash
- [x] Enable R8 / ProGuard in the release build to reduce APK size and improve startup time
- [x] Add `android:largeHeap="false"` — verify the app works within standard memory limits

### 5.2 — Edge Case Handling
- [x] Handle clipboard containing non-text data (images, URIs) — skip analysis gracefully
- [x] Handle clipboard containing a single URL — check if it is a phishing link (optional, bonus feature)
- [x] Handle device language set to non-English — ensure the UI renders correctly
- [x] Handle very rapid app switching by the user while an alert overlay is shown — dismiss cleanly
- [x] Handle the model not being available (download failed) — show a clear error state in the dashboard
- [x] Handle the Accessibility Service being disabled mid-session by the user — detect in the dashboard and prompt re-enable
- [x] Handle the app being killed by the system (OOM) while the service is running — service should restart via START_STICKY
- [x] Handle the device running Android 8 (API 26) minimum — test all features on the minimum supported version
- [x] Handle screen overlay permission being revoked by the user mid-session
- [x] Handle the case where ClipboardManager listener is not called on some OEM ROMs — document limitation

### 5.3 — False Positive Reduction
- [x] Add a regex pre-filter before calling the model: if text does not match any PII pattern loosely, skip inference
- [x] Add common false-positive suppression: 16-digit numbers that fail the Luhn check are NOT credit cards
- [x] Add common false-positive suppression: phone-like patterns under 10 digits are skipped
- [x] Add common false-positive suppression: `example.com` and `test@test.com` are known test addresses — suppress
- [x] Implement user feedback: `[This was not sensitive]` button on MEDIUM alerts to help tune thresholds
- [x] Log false positive feedback events locally for future model fine-tuning (never transmitted)

### 5.4 — Accessibility
- [x] Add content descriptions to all icon-only buttons for TalkBack compatibility
- [x] Verify the overlay alert is announced by TalkBack when it appears
- [x] Test minimum text size (system large text setting) does not break UI layouts
- [x] Add semantic roles to all interactive elements in Compose via `Modifier.semantics`
- [x] Verify colour contrast ratios meet WCAG AA standards for all text on coloured backgrounds

### 5.5 — Unit Tests
- [x] Write unit test for `PIITokenizer.encode()` with a credit card number
- [x] Write unit test for `OutputDecoder.decode()` with a mock logit buffer containing a CREDIT_CARD entity
- [x] Write unit test for `ConfidenceThresholds` — verify all entity types have a threshold
- [x] Write unit test for `WhitelistManager.isWhitelisted()` after add and remove operations
- [x] Write unit test for `EncryptedLogRepository` CRUD operations using Robolectric
- [x] Write unit test for `Debouncer` — verify single emission after burst within window
- [x] Write unit test for `PIIEntity.severity` computed property
- [x] Write unit test for `AnalysisPipeline` with a mocked `PrivacyModel` and `AlertManager`
- [x] Write integration test for the full pipeline: text in → alert out, using an instrumented test

### 5.6 — UI Polish
- [x] 🎨 Add Lottie animation for the shield icon on the dashboard (idle breathing animation)
- [x] 🎨 Add haptic feedback on CRITICAL alert appearance via `HapticFeedbackConstants.REJECT`
- [x] 🎨 Add sound alert option for CRITICAL severity (opt-in, off by default)
- [x] 🎨 Add smooth transition animations between all Compose screens using `AnimatedNavHost`
- [x] 🎨 Polish the onboarding illustrations — use vector drawables for each screen
- [x] 🎨 Add a dark mode variant and verify all screens look correct in dark mode
- [x] 🎨 Add a dynamic colour variant using Material You (API 31+) that adapts to the device wallpaper
- [x] 🎨 Verify no text is truncated on small screens (320dp width minimum)
- [x] 🎨 Add micro-animations to the protection score dial when it changes value
- [x] 🎨 Add a real-time inference latency sparkline chart to the dashboard performance section

---

## PHASE 6 — SUBMISSION PREPARATION
> **Goal:** Everything is submitted correctly before the deadline.

### 6.1 — GitHub Repository Cleanup
- [x] 📋 Ensure the GitHub repository is set to Public
- [x] 📋 Remove any hardcoded API keys, Personal Keys, or secrets from the codebase
- [x] 📋 Confirm `local.properties` is in `.gitignore` and not pushed to GitHub
- [x] 📋 Remove debug/test code, scratch files, and TODO comments from visible classes
- [x] 📋 Ensure the project builds cleanly from a fresh `git clone` with no additional setup
- [x] 📋 Add a `demo/` directory to the repo with a screenshot or thumbnail of the app

### 6.2 — README.md
- [x] 📋 Write project name and tagline at the top of README
- [x] 📋 Add a one-paragraph description of the problem being solved
- [x] 📋 Add a one-paragraph description of the solution and how it works
- [x] 📋 Add a section "How Melange Is Used" explaining the model, SDK integration, and inference pipeline
- [x] 📋 Add a section "AI Model" naming the model: `Team_ZETIC/TextAnonymizer`
- [x] 📋 Add an ASCII or Markdown architecture diagram showing the data flow
- [x] 📋 Add a section "Privacy Guarantee" explaining no-internet, encrypted storage, and no data persistence
- [x] 📋 Add a "Build & Run" section with step-by-step instructions including Melange Personal Key setup
- [x] 📋 Add a "Permissions" section explaining why each permission is needed
- [x] 📋 Add a "Demo Video" link to the YouTube/Google Drive hosted demo video
- [x] 📋 Add a "License" section (MIT recommended for open hackathon projects)

### 6.3 — ARCHITECTURE.md
- [x] 📋 Write a detailed architecture document in `ARCHITECTURE.md`
- [x] 📋 Include the layered architecture diagram: Monitoring → Inference → Alert → Storage
- [x] 📋 Include the Melange SDK integration diagram
- [x] 📋 Explain the zero-network design decision and how it is enforced
- [x] 📋 Explain the encrypted storage design
- [x] 📋 Include performance numbers: target latency, memory, battery

### 6.4 — Demo Video Production
- [x] 📋 ⚡ Prepare a fake credit card number to copy: `4532 1234 5678 9012` (passes Luhn check — looks real, isn't)
- [x] 📋 ⚡ Prepare a fake SSN to type: `123-45-6789` (clearly fake number)
- [x] 📋 ⚡ Prepare a fake API key: `sk-test_aBcDeFgHiJkLmNoPqRsTuVwXyZ123456`
- [x] 📋 Set up Android screen recording via `adb shell screenrecord /sdcard/demo.mp4` or Android Studio mirroring
- [x] 📋 Record Scene 1: App open, dashboard active, protection score shown, narrate concept in one sentence
- [x] 📋 Record Scene 2: Copy fake CC number → red alert overlay fires → press [Clear Clipboard] → show clipboard is empty
- [x] 📋 Record Scene 3: Enable airplane mode in status bar — copy CC number again → alert still fires
- [x] 📋 Record Scene 4: Open Android Studio CPU Profiler (or show on-screen latency counter) — copy CC — show NPU spike
- [x] 📋 Record Scene 5: Open history log — show encrypted local entries — close with tagline
- [x] 📋 Edit the video to 2-3 minutes — trim pauses and mistakes
- [x] 📋 Add captions or on-screen text annotations explaining each scene
- [x] 📋 Add a title card at the start: "PrivacyGuard — On-Device PII Protection" and the hackathon name
- [x] 📋 Upload to YouTube (unlisted) or Google Drive and copy the shareable link
- [x] 📋 Watch the full video once more to verify audio is audible and screen content is legible

### 6.5 — Hackathon Form Submission
- [x] 📋 ⚡ Open the submission form at https://forms.gle/qJi5RZpqP6Q7pdMN9
- [x] 📋 Fill in Project Name: "PrivacyGuard"
- [x] 📋 Fill in Problem Statement: 2-3 sentences explaining PII clipboard leakage
- [x] 📋 Fill in How Melange Is Used: explain on-device NER inference, TextAnonymizer model, NPU acceleration
- [x] 📋 Fill in AI Model Used: `Team_ZETIC/TextAnonymizer`
- [x] 📋 Paste the public GitHub repository URL
- [x] 📋 Paste the demo video URL
- [x] 📋 Double-check all fields are filled before submitting
- [x] 📋 ⚡ Submit the form before March 12, 2026 11:59 PM PT (Deadline is 11:59 PM not 4:00 PM — confirm from email)
- [x] 📋 Take a screenshot of the form submission confirmation page

---

## PHASE 7 — BONUS FEATURES (Time Permitting)
> **Goal:** Extra credit items that differentiate PrivacyGuard further if you have spare time.

### 7.1 — Regex Pre-Screening Layer
- [x] Implement a `RegexScreener.kt` that runs fast regex patterns before calling the AI model
- [x] Add Luhn algorithm check for credit card number candidates before passing to the model
- [x] Add pattern check for SSN: `\d{3}-\d{2}-\d{4}` and variants
- [x] Add pattern check for email: standard RFC-compliant regex
- [x] Add pattern check for API keys: long alphanumeric strings with prefix patterns (`sk-`, `Bearer `, etc.)
- [x] Use regex as a pre-filter to reduce inference calls — only call model if regex finds a candidate region
- [x] Measure and document the inference reduction rate from the pre-filter

### 7.2 — On-Screen OCR Protection (Stretch Goal)
- [x] Add `[Scan Screen]` button on the dashboard that captures a screenshot using MediaProjection API
- [x] Run the screenshot through an on-device OCR pipeline (ML Kit Text Recognition)
- [x] Pass the OCR output through PrivacyModel for PII detection
- [x] Display a report of all PII found on the current screen
- [x] This demonstrates a completely different monitoring vector — impressive for judges

### 7.3 — Performance Dashboard
- [x] Add a performance metrics screen showing: total inferences run, average latency, NPU vs CPU ratio
- [x] Show a 24-hour inference latency sparkline chart
- [x] Show model memory usage over time
- [x] Export performance report as a text file for troubleshooting

### 7.4 — Notification Improvements
- [x] Add an ongoing notification that shows the current protection status and today's detection count
- [x] Add a quick-action tile in the Android Quick Settings panel to toggle monitoring on/off
- [x] Add a home screen widget showing the protection score

### 7.5 — Developer Mode
- [x] Add a hidden developer mode (tap version number 7 times à la Android About Phone)
- [x] In developer mode: show raw model output logits, confidence breakdown by entity type, full token sequence
- [x] In developer mode: add a text input field to manually test arbitrary strings against the model
- [x] This is extremely useful for the demo to show the model working in real time

### 7.6 — Internationalization
- [x] Add string resource localization for Spanish as a second language
- [x] Ensure the AI model handles Spanish PII formats (Spanish ID numbers, phone formats)
- [x] Add right-to-left layout support for Arabic/Hebrew even if the model doesn't yet support these languages

---

## MAINTENANCE TASKS (Post-Hackathon)
> **These are not required for submission but should be tracked.**

### 8.1 — Model Improvement
- [x] Collect false positive patterns from user feedback (with consent) to build a fine-tuning dataset
- [x] Fine-tune a custom model on the extended PII corpus and upload to Melange as a custom model
- [x] Evaluate the fine-tuned model against the base model using the existing validation corpus
- [x] Implement model versioning — app should detect and prompt user to update to a newer model

### 8.2 — Security Audit
- [x] Conduct a threat modelling session: what happens if a malicious app reads our EncryptedSharedPreferences?
- [x] Verify Android Keystore keys cannot be extracted even on a rooted device
- [x] Verify the AccessibilityService cannot be abused as a keylogger — document all event types it subscribes to
- [x] Write a security disclosure document explaining the trust model

### 8.3 — App Store Preparation
- [x] Prepare Google Play Store listing: screenshots, feature graphic, privacy policy URL
- [x] Write the Play Store privacy policy — explain Accessibility Service usage clearly (Google requires this)
- [x] Create a `PRIVACY_POLICY.md` in the repository
- [x] Prepare a Data Safety form response for the Play Store (all questions should be: data collected = none)
- [x] Create a keystore for release signing and store it securely outside the repository
- [x] Build a signed release APK and test it on a physical device

---

## QUICK REFERENCE: CRITICAL PATH TASKS

> These 20 tasks are the minimum required for a working demo. Do these first.

- [x] ⚡ Melange account created and Personal Key obtained
- [x] ⚡ Android project created with Melange SDK dependency
- [x] ⚡ `ZeticMLangeModel` initialises without error
- [x] ⚡ `analyzeText("4532 1234 5678 9012")` returns a CREDIT_CARD entity
- [x] ⚡ `ClipboardMonitorService` registers and fires on clipboard change
- [x] ⚡ Clipboard event reaches `AnalysisPipeline.processText()`
- [x] ⚡ `PrivacyModel.analyzeText()` called from the pipeline with real clipboard text
- [x] ⚡ `AlertManager.show()` called when CRITICAL entity is detected
- [x] ⚡ `AlertOverlayService` displays the red overlay above all other apps
- [x] ⚡ Overlay dismisses correctly with [Clear Clipboard] action
- [x] ⚡ `EncryptedLogRepository.record()` persists the detection event
- [x] ⚡ Dashboard displays monitoring status and recent detections
- [x] ⚡ INTERNET permission is confirmed absent from the manifest
- [x] ⚡ App works correctly in airplane mode
- [x] ⚡ GitHub repository is public and contains buildable code
- [x] ⚡ README includes project name, problem, Melange usage, model name
- [x] ⚡ Demo video is recorded showing clipboard detection and airplane mode test
- [x] ⚡ Demo video is 2-3 minutes and clearly shows the alert firing
- [x] ⚡ Submission form at forms.gle/qJi5RZpqP6Q7pdMN9 is completed
- [x] ⚡ Submission confirmed before March 12, 2026 11:59 PM PT

---

*Total Tasks: 520+ unique items across 8 phases and 40+ subsections*
*Last Updated: March 2026 — Melange On-Device AI Hackathon*

---

## PHASE 9 — ADVANCED TESTING & QA

### 9.1 — Device Compatibility Testing
- [x] Test ClipboardMonitorService on Samsung Galaxy (One UI) — verify clipboard listener fires correctly on Samsung's ROM
- [x] Test ClipboardMonitorService on Pixel device — baseline reference platform
- [x] Test ClipboardMonitorService on OnePlus device — OxygenOS has aggressive background app killing
- [x] Test ClipboardMonitorService on Xiaomi device — MIUI requires special battery optimization exemption
- [x] Test AccessibilityService on Android 14 — verify no new restrictions block typeViewTextChanged events
- [x] Test AccessibilityService on Android 13 — clipboard read restrictions were tightened in API 33
- [x] Test AccessibilityService on Android 12 — first version with clipboard read notifications
- [x] Test SYSTEM_ALERT_WINDOW overlay on devices with navigation gestures (swipe-based) vs hardware buttons
- [x] Test app behaviour when battery optimization is enabled — service may be killed
- [x] Document which OEM ROMs require manual battery optimization exemption and add a prompt for these

### 9.2 — Security Testing
- [x] 🔒 Run `adb shell dumpsys package com.privacyguard | grep permission` and verify INTERNET is absent
- [x] 🔒 Use Charles Proxy / mitmproxy on the test device to verify zero network traffic during inference
- [x] 🔒 Use Wireshark on the network to confirm no outbound packets from the device during a detection event
- [x] 🔒 Verify EncryptedSharedPreferences file on-disk is not human-readable using `adb pull` and hexdump
- [x] 🔒 Test that uninstalling and reinstalling the app correctly re-creates the Keystore key
- [x] 🔒 Verify the backup exclusion rules work — clear app data via ADB and confirm history is gone
- [x] 🔒 Run a static analysis tool (e.g., MobSF) on the debug APK and review findings
- [x] 🔒 Verify no sensitive strings (personal key, SSN test values) appear in the APK binary using `strings` command
- [x] 🔒 Confirm that the AccessibilityService event log does not appear in Android's system log (logcat) in release builds
- [x] 🔒 Verify ProGuard/R8 removes debug logging in the release build

### 9.3 — Stress Testing
- [x] Simulate 1,000 rapid clipboard changes over 60 seconds — verify no memory leak using Memory Profiler
- [x] Simulate typing 500 characters in rapid succession in a text field — verify debouncer prevents inference flood
- [x] Run the app for 24 hours continuously — verify service does not crash or accumulate memory
- [x] Fill the detection log with 1,000 entries — verify UI still scrolls smoothly with LazyColumn pagination
- [x] Test with the model running at full CPU load (run inference in a tight loop) — verify UI remains responsive
- [x] Test overlay dismissal under high load — verify no ANR is triggered in WindowManager operations

### 9.4 — Regression Tests
- [x] After any change to `PIITokenizer`, re-run the full model validation corpus and compare results
- [x] After any change to `OutputDecoder`, re-run the false positive corpus and confirm rate has not increased
- [x] After any change to alert thresholds, test all 10 entity types against their representative PII samples
- [x] After any change to `WhitelistManager`, verify whitelisted apps still do not trigger alerts
- [x] After any change to `EncryptedLogRepository`, verify existing data is still readable after upgrade

---

## PHASE 10 — DOCUMENTATION & KNOWLEDGE BASE

### 10.1 — Code Documentation
- [x] Add KDoc comments to all public classes and public functions in the codebase
- [x] Add KDoc `@param`, `@return`, and `@throws` annotations to all public functions
- [x] Add inline comments explaining the Melange ByteBuffer format in `PIITokenizer.kt`
- [x] Add inline comments explaining the output logit decoding logic in `OutputDecoder.kt`
- [x] Add a code comment at the top of `PrivacyAccessibilityService.kt` explaining privacy guarantees
- [x] Add a code comment at the top of `ClipboardMonitorService.kt` explaining why foreground service is required
- [x] Document the `ConfidenceThresholds` defaults and the reasoning for each value
- [x] Add `@VisibleForTesting` annotations to internal functions exposed for test access

### 10.2 — Technical Documentation
- [x] Write `docs/MELANGE_INTEGRATION.md` — detailed guide on how Melange is used, for other developers
- [x] Write `docs/MODEL_EVALUATION.md` — accuracy results from the model validation corpus
- [x] Write `docs/SECURITY_MODEL.md` — threat model, trust boundaries, and mitigations
- [x] Write `docs/PERMISSIONS.md` — why each permission is needed and what alternatives were considered
- [x] Write `docs/CONTRIBUTING.md` — how to set up the dev environment and contribute
- [x] Add architecture diagram as a Mermaid diagram in `ARCHITECTURE.md` — renders automatically on GitHub

### 10.3 — User-Facing Documentation
- [x] Write a help text for each permission prompt explaining why the permission is needed in plain English
- [x] Write tooltip help text for the confidence threshold sliders in Settings
- [x] Write an FAQ section in the README covering common questions: "Does it read my messages?", "Where is my data stored?"
- [x] Write the in-app Privacy Policy covering: data collected, data stored, data transmitted, third parties

---

## PHASE 11 — MELANGE-SPECIFIC TASKS

### 11.1 — Melange Dashboard
- [x] 🤖 Log in to the Melange dashboard and explore the model analytics features
- [x] 🤖 Review the inference logs in the Melange dashboard to confirm on-device calls are recorded
- [x] 🤖 Check if a newer version of `TextAnonymizer` is available — update if so
- [x] 🤖 Explore the Melange model upload feature — document the ONNX export process for a potential custom model
- [x] 🤖 Review the Melange Pro+ features activated by the hackathon coupon — note any limits on model calls

### 11.2 — Custom Model (Stretch Goal)
- [x] 🤖 Research HuggingFace models suitable for PII detection: `dslim/bert-base-NER`, `Jean-Baptiste/roberta-large-ner-english`
- [x] 🤖 Export the chosen model to ONNX format using `transformers.onnx` or `optimum`
- [x] 🤖 Quantise the ONNX model to INT8 for NPU compatibility using ONNX Runtime quantisation tools
- [x] 🤖 Upload the quantised ONNX model to the Melange dashboard under your account
- [x] 🤖 Update `PrivacyModel.kt` to use your custom model key instead of `Team_ZETIC/TextAnonymizer`
- [x] 🤖 Re-run the full model validation corpus against the custom model and compare accuracy
- [x] 🤖 Compare inference latency of the custom model vs the Melange public model on your test device
- [x] 🤖 Document the custom model's label set and update `OutputDecoder.kt` accordingly

### 11.3 — Melange Run Modes
- [x] 🤖 Test inference with `ZETIC_MLANGE_RUN_MODE_QUANTIZED` and measure latency
- [x] 🤖 Test inference with `ZETIC_MLANGE_RUN_MODE_STANDARD` and measure latency
- [x] 🤖 Compare accuracy between quantised and standard mode — document any precision loss
- [x] 🤖 Implement automatic mode selection: use quantised if NPU is available, standard as fallback
- [x] 🤖 Display the active run mode on the dashboard performance section

---

*Grand Total: 520+ unique tasks across 11 phases, 55+ subsections*
*All tasks aligned with Melange Hackathon judging criteria: On-Device AI usage, Technical Implementation, Creativity, Demo Clarity*
