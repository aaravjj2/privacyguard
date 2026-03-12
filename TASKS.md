# 🛡️ PrivacyGuard — Complete Task List
### Melange On-Device AI Hackathon 2026
**500+ Unique Engineering, Design, and Submission Tasks**

> **Legend:** `[ ]` = Todo · `[x]` = Done · ⚡ = Critical Path · 🤖 = AI/Melange specific · 🔒 = Privacy/Security · 🎨 = UI/UX · 📋 = Submission

---

## PHASE 0 — ENVIRONMENT SETUP & PROJECT BOOTSTRAP
> **Goal:** Everything compiles and Melange SDK produces its first inference output.

### 0.1 — Accounts & Credentials
- [ ] ⚡ Create account at melange.zetic.ai using your email address — this is required for your Personal Key
- [ ] ⚡ Activate Melange Pro+ trial using coupon code `ZETICHACKATHON-BM26` from the hackathon brief
- [ ] ⚡ Copy and securely store your Melange Personal Key — you will need it in BuildConfig
- [ ] Create a Devpost account at devpost.com if you do not already have one
- [ ] Register for the Melange On-Device AI Hackathon on the Devpost page before the deadline
- [ ] Create a new public GitHub repository named `privacyguard` or `PrivacyGuard` with a brief description
- [ ] Clone the ZETIC_Melange_apps repository locally for reference: `git clone https://github.com/zetic-ai/ZETIC_Melange_apps`
- [ ] Browse the ZETIC_Melange_apps repository and identify the TextAnonymizer example to use as model reference
- [ ] Read through https://docs.zetic.ai fully — focus on Android SDK setup section
- [ ] Bookmark the Melange dashboard at https://melange.zetic.ai for model management

### 0.2 — Android Studio Project Setup
- [ ] ⚡ Install Android Studio Iguana or newer if not already installed
- [ ] ⚡ Create a new Android project: Empty Activity, Kotlin, minimum SDK API 26 (Android 8.0 Oreo)
- [ ] ⚡ Set package name to `com.privacyguard` or your preferred namespace
- [ ] ⚡ Enable Jetpack Compose in the new project wizard (use Compose for all UI)
- [ ] Set `compileSdk` to 34 and `targetSdk` to 34 in build.gradle
- [ ] Confirm the project builds and the default Compose Hello World screen renders on a device or emulator
- [ ] Set up an Android Virtual Device (AVD) running API 33 for testing if no physical device available
- [ ] Enable USB debugging on your physical Android device if using one
- [ ] Run the default project on your test device/emulator to confirm the development pipeline works end to end
- [ ] Initialise git in the project directory and push the initial commit to GitHub

### 0.3 — Dependency Configuration
- [ ] ⚡ Add Melange SDK dependency to `app/build.gradle`: `implementation("com.zeticai.mlange:mlange:1.2.2")`
- [ ] ⚡ Add `jniLibs { useLegacyPackaging = true }` inside the `packaging` block in build.gradle
- [ ] Add Kotlin Coroutines dependency: `implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")`
- [ ] Add AndroidX Lifecycle ViewModel: `implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")`
- [ ] Add AndroidX Security Crypto for EncryptedSharedPreferences: `implementation("androidx.security:security-crypto:1.1.0-alpha06")`
- [ ] Add Gson for JSON serialisation of detection events: `implementation("com.google.code.gson:gson:2.10.1")`
- [ ] Add Compose Material3: `implementation("androidx.compose.material3:material3:1.2.1")`
- [ ] Add Accompanist Permissions library for runtime permission requests in Compose
- [ ] Sync Gradle and confirm all dependencies resolve without errors
- [ ] Run `./gradlew dependencies` to verify no version conflicts in the dependency tree

### 0.4 — Melange SDK Verification
- [ ] ⚡ Create a `MelangeTest.kt` scratch file and initialise a `ZeticMLangeModel` instance with your Personal Key
- [ ] ⚡ Run a minimal inference call with a dummy input to verify the SDK is correctly integrated
- [ ] Confirm the model downloads or loads from local assets without throwing an exception
- [ ] Log inference output to Logcat and confirm you receive non-null output buffers
- [ ] Measure and log the cold-start latency of the first inference call
- [ ] Measure and log the warm inference latency on subsequent calls
- [ ] Confirm the SDK correctly identifies and uses the NPU if available on your device
- [ ] Delete the scratch test file after verification — do not leave test code in the production codebase

### 0.5 — Project Structure
- [ ] Create package directory `service/` under `com.privacyguard` for background services
- [ ] Create package directory `ml/` for all Melange/AI model related classes
- [ ] Create package directory `ui/` with sub-packages `alert/`, `dashboard/`, `history/`, `whitelist/`, `settings/`, `onboarding/`
- [ ] Create package directory `data/` for repositories and data models
- [ ] Create package directory `util/` for utility classes
- [ ] Create a `BuildConfig.kt` or `local.properties` entry for your Melange Personal Key — never hardcode it in source
- [ ] Add `local.properties` and `*.jks` to `.gitignore` to prevent credential leakage
- [ ] Create a `README.md` in the root with placeholder sections: Project Name, Problem, Melange Usage, Model Used, Setup Instructions
- [ ] Create an `ARCHITECTURE.md` with placeholder for the data flow diagram
- [ ] Set up GitHub Actions workflow file `.github/workflows/build.yml` for automated build verification (optional but impressive)

---

## PHASE 1 — AI MODEL CORE
> **Goal:** Text goes in, PII entities come out with type and confidence score.

### 1.1 — Model Selection & Acquisition
- [ ] ⚡ 🤖 Log into the Melange dashboard at https://melange.zetic.ai
- [ ] ⚡ 🤖 Locate the `Team_ZETIC/TextAnonymizer` model in the public model library
- [ ] 🤖 Review the TextAnonymizer model's input/output specification — document expected token format and output label set
- [ ] 🤖 Confirm the model supports the following entity types: credit card, SSN, email, phone, password, API key
- [ ] 🤖 Note the model's maximum sequence length (typically 512 tokens) for input clamping
- [ ] 🤖 Note the model's label mapping: map integer class indices to entity type strings
- [ ] 🤖 Download or reference the model key/ID that will be passed to `ZeticMLangeModel`
- [ ] 🤖 Check if a quantised version of the model is available for NPU acceleration — prefer quantised

### 1.2 — PIIEntity Data Model
- [ ] Create `PIIEntity.kt` data class with fields: `entityType: EntityType`, `confidence: Float`, `startIndex: Int`, `endIndex: Int`, `rawText: String` (for internal use only, not persisted)
- [ ] Create `EntityType.kt` enum with values: `CREDIT_CARD`, `SSN`, `PASSWORD`, `API_KEY`, `EMAIL`, `PHONE`, `PERSON_NAME`, `ADDRESS`, `DATE_OF_BIRTH`, `MEDICAL_ID`, `UNKNOWN`
- [ ] Add a `severity: Severity` computed property to `EntityType` returning `CRITICAL`, `HIGH`, or `MEDIUM`
- [ ] Create `Severity.kt` enum with values `CRITICAL`, `HIGH`, `MEDIUM` and a `color: Color` property for each
- [ ] Write unit tests for `EntityType.severity` to confirm each entity maps to the correct severity level
- [ ] Create `PIIAnalysisResult.kt` data class wrapping `List<PIIEntity>` with a computed `hasSensitiveData()` boolean
- [ ] Add a computed `highestSeverity` property to `PIIAnalysisResult` returning the max severity present
- [ ] Write unit tests for `PIIAnalysisResult` that verify `hasSensitiveData()` returns false for empty entity lists

### 1.3 — Tokenizer
- [ ] Create `PIITokenizer.kt` class responsible for converting raw text to ByteBuffer inputs for the Melange model
- [ ] Implement WordPiece tokenisation matching the vocabulary of the TextAnonymizer model
- [ ] Load the model vocabulary file from app assets — add `vocab.txt` to `assets/` directory
- [ ] Implement `encode(text: String): TokenizerOutput` returning token IDs and attention mask as IntArrays
- [ ] Handle input texts longer than the model's maximum sequence length by truncating and logging a warning
- [ ] Handle empty string input gracefully — return empty output without calling the model
- [ ] Handle null input gracefully — return empty output without throwing a NullPointerException
- [ ] Convert IntArray token IDs to ByteBuffer in the correct format expected by Melange (int32, little-endian)
- [ ] Convert IntArray attention mask to ByteBuffer in the same format
- [ ] Write unit tests for the tokenizer: verify tokenisation of a known string matches expected token IDs
- [ ] Test tokenizer with a credit card number string to confirm it tokenises correctly
- [ ] Test tokenizer with a sentence containing an email address
- [ ] Test tokenizer with special characters, emojis, and unicode text to ensure no crashes

### 1.4 — PrivacyModel (Melange Inference Wrapper)
- [ ] ⚡ 🤖 Create `PrivacyModel.kt` as a singleton object or class with lifecycle management
- [ ] ⚡ 🤖 Initialise `ZeticMLangeModel` with context, Personal Key, and model name — handle initialisation errors with a sealed Result type
- [ ] 🤖 Implement `analyzeText(text: String): PIIAnalysisResult` as a suspend function running in a background coroutine
- [ ] 🤖 Call `privacyModel.run(arrayOf(inputIdBuffer, attentionMaskBuffer))` with properly prepared ByteBuffers
- [ ] 🤖 Read `privacyModel.outputBuffers[0]` after inference to get raw logit output
- [ ] Implement `OutputDecoder.kt` that converts raw logit ByteBuffer to a list of `PIIEntity` objects
- [ ] In OutputDecoder: apply softmax or argmax over the NER label dimension to get per-token predictions
- [ ] In OutputDecoder: group consecutive tokens with the same entity type into entity spans
- [ ] In OutputDecoder: map integer class indices back to `EntityType` enum values using the label map
- [ ] In OutputDecoder: compute confidence score as the max softmax probability for the predicted class
- [ ] In OutputDecoder: filter out entities below the confidence threshold (default 0.85)
- [ ] 🤖 Implement `close()` method that calls `privacyModel.close()` — wire to ViewModel/Service lifecycle
- [ ] Implement a `ModelState` sealed class: `Initialising`, `Ready`, `Running`, `Error(message)` for UI state binding
- [ ] Expose a `modelState: StateFlow<ModelState>` that UI components can observe
- [ ] Log inference latency in milliseconds using `System.currentTimeMillis()` before and after `run()` call
- [ ] Expose `lastInferenceLatencyMs: Long` as a LiveData/StateFlow for the dashboard performance indicator
- [ ] Handle the case where the model is called before initialisation is complete — queue the request
- [ ] Implement model warm-up: run a single inference on a dummy input at startup to prime the NPU cache
- [ ] Add a `runMode` configuration option supporting `QUANTIZED` (NPU) and `STANDARD` (CPU) modes

### 1.5 — Confidence Thresholds
- [ ] Create `ConfidenceThresholds.kt` with per-entity-type default thresholds
- [ ] Set `CREDIT_CARD` threshold to 0.90 — high precision critical for avoiding false positives on card-like numbers
- [ ] Set `SSN` threshold to 0.92 — SSN patterns have a distinct format, high precision achievable
- [ ] Set `PASSWORD` threshold to 0.80 — contextual entity, slightly lower threshold acceptable
- [ ] Set `API_KEY` threshold to 0.85 — token-like strings need decent confidence to avoid flagging random hashes
- [ ] Set `EMAIL` threshold to 0.95 — email format is very precise, false positives should be near zero
- [ ] Set `PHONE` threshold to 0.88 — phone numbers can match non-phone digit sequences
- [ ] Set `PERSON_NAME` threshold to 0.75 — names are inherently ambiguous, lower threshold acceptable
- [ ] Make thresholds user-configurable via the Settings screen with a per-type slider
- [ ] Persist threshold overrides to EncryptedSharedPreferences so they survive app restarts

### 1.6 — Model Validation
- [ ] Create a `ModelValidation.kt` test class with a corpus of known PII strings
- [ ] Add test case: `"My credit card is 4532 1234 5678 9012"` should detect CREDIT_CARD
- [ ] Add test case: `"SSN: 123-45-6789"` should detect SSN
- [ ] Add test case: `"Email me at user@example.com"` should detect EMAIL
- [ ] Add test case: `"Call me at (555) 867-5309"` should detect PHONE
- [ ] Add test case: `"My API key is sk-abc123xyz789"` should detect API_KEY
- [ ] Add test case: `"Hello, how are you?"` should return EMPTY result — no false positive
- [ ] Add test case: `"The invoice total is $4,532.00"` should NOT detect as credit card — common false positive pattern
- [ ] Add test case: Unicode and emoji mixed text should not crash the model
- [ ] Add test case: Very long text (1000+ characters) should be handled gracefully via truncation
- [ ] Run validation suite and record true positive rate — target >95% on PII test cases
- [ ] Run validation suite and record false positive rate — target <5% on benign text corpus

---

## PHASE 2 — MONITORING PIPELINE
> **Goal:** Real clipboard and text field events reach the model and produce PIIAnalysisResults.

### 2.1 — ClipboardMonitorService
- [ ] ⚡ Create `ClipboardMonitorService.kt` extending `Service`
- [ ] ⚡ Override `onStartCommand()` to start the service as a foreground service with a persistent notification
- [ ] Create a `NotificationChannel` for `PrivacyGuard Monitoring` channel with low importance (silent)
- [ ] Build the persistent notification: icon, title "PrivacyGuard Active", action to open the dashboard
- [ ] Call `startForeground(NOTIFICATION_ID, notification)` immediately in `onStartCommand()` before any other work
- [ ] ⚡ Get a reference to `ClipboardManager` via `getSystemService(CLIPBOARD_SERVICE)`
- [ ] ⚡ Call `clipboardManager.addPrimaryClipChangedListener(listener)` to register for clipboard events
- [ ] In the listener: extract text from `clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()`
- [ ] Guard the listener against null clipboard data — return early if text is null or blank
- [ ] Guard the listener against very short strings (< 5 characters) — too short to contain meaningful PII
- [ ] Guard the listener against very long strings (> 10,000 characters) — truncate to avoid memory issues
- [ ] Pass extracted text to the debounced analysis pipeline via a coroutine
- [ ] Track the source app that wrote to the clipboard using `ClipData.description` if available
- [ ] Override `onDestroy()` to remove the clipboard listener and release resources
- [ ] Handle the case where the service is destroyed and restarted — re-register the listener properly
- [ ] Add `START_STICKY` as the return value from `onStartCommand()` so the service restarts if killed
- [ ] Register the service in `AndroidManifest.xml` with `android:foregroundServiceType="dataSync"`
- [ ] Add `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_DATA_SYNC` permissions to the manifest
- [ ] Add `RECEIVE_BOOT_COMPLETED` permission so a BroadcastReceiver can restart the service after reboot
- [ ] Create `BootReceiver.kt` extending `BroadcastReceiver` that starts `ClipboardMonitorService` on boot
- [ ] Register `BootReceiver` in the manifest with `android.intent.action.BOOT_COMPLETED` intent filter

### 2.2 — PrivacyAccessibilityService
- [ ] ⚡ Create `PrivacyAccessibilityService.kt` extending `AccessibilityService`
- [ ] ⚡ Create `res/xml/accessibility_service_config.xml` configuring the service for `typeViewTextChanged` events
- [ ] In the config XML, set `android:description` to a clear, honest explanation of what the service monitors
- [ ] In the config XML, set `android:settingsActivity` to the app's Settings screen
- [ ] ⚡ Override `onAccessibilityEvent()` to handle `TYPE_VIEW_TEXT_CHANGED` events
- [ ] In `onAccessibilityEvent()`: read the changed text from `event.text.joinToString()`
- [ ] In `onAccessibilityEvent()`: read the source app package name from `event.packageName`
- [ ] Check the whitelist before any analysis — if source app is whitelisted, return immediately
- [ ] Skip analysis if the source is PrivacyGuard itself — prevent recursive event loops
- [ ] Detect password field input type: if `event.source?.inputType` has `TYPE_TEXT_VARIATION_PASSWORD`, skip analysis
- [ ] Detect web password fields by checking `event.source?.hint` for password-related strings
- [ ] Pass the text to the debounced analysis pipeline — do not call inference inline
- [ ] Override `onInterrupt()` with an empty implementation (required by the abstract class)
- [ ] Register the service in `AndroidManifest.xml` with `android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"`
- [ ] Add `<meta-data>` referencing `accessibility_service_config.xml` in the service declaration
- [ ] Test that the service receives events when typing in the built-in Notes app
- [ ] Test that the service does NOT receive events when typing in a password field
- [ ] Test that the service does NOT receive events for whitelisted apps

### 2.3 — Debouncer
- [ ] Create `Debouncer.kt` utility class using Kotlin coroutines for debouncing rapid-fire events
- [ ] Implement `debounce(delayMs: Long, action: suspend () -> Unit)` that cancels pending calls within the delay window
- [ ] Use `CoroutineScope` with `Dispatchers.Default` for background execution
- [ ] Set default debounce delay to 800ms — balances responsiveness with avoiding excessive inference calls
- [ ] Make the debounce delay configurable in Settings
- [ ] Ensure the debouncer properly cancels pending jobs when the service is destroyed
- [ ] Write unit tests for the debouncer verifying it only fires once per burst of events within the window
- [ ] Test the debouncer with rapid clipboard changes (simulated copy spam) to confirm no memory leak

### 2.4 — Analysis Pipeline
- [ ] ⚡ Create `AnalysisPipeline.kt` that ties together Debouncer, PrivacyModel, AlertManager, and EncryptedLogRepository
- [ ] Implement `processText(text: String, sourceApp: String?)` as the single entry point for all monitoring events
- [ ] In `processText()`: check whitelist first — return immediately if whitelisted
- [ ] In `processText()`: check minimum text length — return if below threshold
- [ ] In `processText()`: run through Debouncer before calling inference
- [ ] In `processText()`: call `PrivacyModel.analyzeText(text)` and await the result
- [ ] In `processText()`: if result has sensitive data, call `AlertManager.show(result, sourceApp)`
- [ ] In `processText()`: call `EncryptedLogRepository.record(result, sourceApp)` regardless of severity
- [ ] Emit metrics (latency, entity types found) to the dashboard ViewModel via a SharedFlow
- [ ] Handle coroutine cancellation gracefully — use `try/finally` to ensure cleanup on cancellation
- [ ] Handle model inference exceptions — log the error and fail silently rather than crashing
- [ ] Implement a circuit breaker: if inference fails 5 times in a row, pause the pipeline for 60 seconds

### 2.5 — Whitelist Integration in Pipeline
- [ ] Query `WhitelistManager.isWhitelisted(packageName: String)` at the start of `processText()`
- [ ] Ensure whitelist check is O(1) using an in-memory Set rather than a database query
- [ ] Update the in-memory whitelist Set whenever `WhitelistManager` is modified
- [ ] Add PrivacyGuard's own package name to a hard-coded exclusion list — never analyse our own UI

---

## PHASE 3 — ALERT SYSTEM & UI
> **Goal:** The right alert appears at the right time with the right information.

### 3.1 — AlertManager
- [ ] Create `AlertManager.kt` as a singleton coordinating all alert display logic
- [ ] Implement `show(result: PIIAnalysisResult, sourceApp: String?)` that selects the correct alert type by severity
- [ ] For `CRITICAL` severity: call `showCriticalOverlay(result, sourceApp)`
- [ ] For `HIGH` severity: call `showHighBanner(result, sourceApp)`
- [ ] For `MEDIUM` severity: call `showMediumToast(result, sourceApp)`
- [ ] Implement an alert queue to prevent multiple overlapping alerts — only one alert at a time
- [ ] Implement cooldown logic: the same entity type from the same source app cannot re-alert within 30 seconds
- [ ] Expose `currentAlert: StateFlow<AlertState?>` for UI components to observe
- [ ] Create `AlertState.kt` sealed class: `Critical(result, sourceApp)`, `High(result, sourceApp)`, `Medium(result, sourceApp)`, `None`

### 3.2 — Critical Alert Overlay
- [ ] ⚡ Request `SYSTEM_ALERT_WINDOW` permission during onboarding — this is required for the full-screen overlay
- [ ] ⚡ Create `AlertOverlayService.kt` that uses `WindowManager` to display a Compose-based overlay view
- [ ] Implement the overlay as a `ComposeView` inflated and added to `WindowManager` with `TYPE_APPLICATION_OVERLAY`
- [ ] Set overlay layout params: `MATCH_PARENT` width, `WRAP_CONTENT` height, `FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN`
- [ ] Design the overlay UI: red background gradient, shield icon, entity type label, source app name, action buttons
- [ ] Add `[Clear Clipboard]` button that calls `ClipboardManager.clearPrimaryClip()` and dismisses the overlay
- [ ] Add `[Dismiss]` button that dismisses the overlay without taking action
- [ ] Add `[Add to Whitelist]` button that opens a confirmation dialog and adds the source app to whitelist
- [ ] Add a countdown timer (10 seconds) after which the overlay auto-dismisses if the user takes no action
- [ ] Animate the overlay entrance with a slide-down animation from top using Compose `AnimatedVisibility`
- [ ] Ensure the overlay is dismissed when the user presses the back button via `OnBackPressedCallback`
- [ ] Handle screen rotation while overlay is showing — dismiss and re-show in correct orientation

### 3.3 — High Severity Banner
- [ ] Create `AlertBannerComposable.kt` as a Compose bottom sheet component for HIGH severity alerts
- [ ] Design: orange accent, PII type icon, description text, source app, two action buttons
- [ ] Show via `ModalBottomSheet` in Jetpack Compose Material3
- [ ] Include `[Clear Clipboard]` and `[Dismiss]` buttons with appropriate styling
- [ ] Animate entrance with the standard Material3 bottom sheet drag handle and slide-up animation
- [ ] Dismiss on swipe-down gesture — standard ModalBottomSheet behaviour
- [ ] Display the number of entities found if multiple are detected in the same text

### 3.4 — Medium Severity Toast
- [ ] Show a Compose `Snackbar` at the bottom of the screen for MEDIUM severity events
- [ ] Include entity type label and source app name in the Snackbar message
- [ ] Add a single `[Details]` action button that opens the detection history screen
- [ ] Auto-dismiss after 4 seconds — medium severity should not block the user

### 3.5 — Main Dashboard Screen
- [ ] ⚡ 🎨 Create `DashboardScreen.kt` as the app's home screen in Jetpack Compose
- [ ] Show a circular `ProtectionScore` indicator (0-100) that increases with uptime and decreases when PII is detected
- [ ] Show the monitoring status as a toggle with `ON`/`OFF` state and a clear visual indicator
- [ ] Show real-time stats: detections today, detections this week, most common entity type
- [ ] Show a live feed of the last 5 detection events as cards — entity type, time, source app
- [ ] Show the current inference latency in milliseconds in a small metrics row
- [ ] Include a `[View All History]` button that navigates to the History screen
- [ ] Include a `[Manage Whitelist]` button that navigates to the Whitelist screen
- [ ] Animate the protection score indicator with a smooth sweep animation on first load
- [ ] Implement `DashboardViewModel.kt` with `StateFlow<DashboardUiState>` exposing all reactive state
- [ ] Handle the case where Accessibility Service is not enabled — show a prominent permission prompt card
- [ ] Handle the case where Notification permission is not granted — show a prompt card
- [ ] Handle the case where SYSTEM_ALERT_WINDOW is not granted — show a prompt card

### 3.6 — Detection History Screen
- [ ] 🎨 Create `HistoryScreen.kt` in Jetpack Compose showing a scrollable chronological list of all detection events
- [ ] Each history item card should show: severity indicator dot, entity type, source app, timestamp (relative)
- [ ] Implement filter chips at the top for filtering by severity: All / Critical / High / Medium
- [ ] Implement swipe-to-delete on individual history items
- [ ] Implement a `[Clear All History]` button with a confirmation dialog
- [ ] Show an empty state illustration and message when no history exists
- [ ] Implement `HistoryViewModel.kt` with a `Flow<List<DetectionEvent>>` from `EncryptedLogRepository`
- [ ] Paginate the history list using Compose `LazyColumn` with pagination — avoid loading thousands of items at once
- [ ] Add a search bar to filter history by entity type or source app name

### 3.7 — Whitelist Manager Screen
- [ ] 🎨 Create `WhitelistScreen.kt` in Jetpack Compose
- [ ] Load the list of all installed apps using `PackageManager.getInstalledApplications()`
- [ ] Display apps in a searchable `LazyColumn` with app icon, app name, and a toggle switch
- [ ] Show a `[Trusted Apps]` header section with pre-populated suggestions (1Password, Bitwarden, etc.)
- [ ] Sort the list: whitelisted apps first, then alphabetically
- [ ] Implement search field that filters by app name in real time
- [ ] Persist whitelist changes immediately via `WhitelistManager` on toggle change
- [ ] Show a count badge on the navigation item showing how many apps are whitelisted
- [ ] Implement `WhitelistViewModel.kt` managing the list state and toggle logic

### 3.8 — Settings Screen
- [ ] 🎨 Create `SettingsScreen.kt` in Jetpack Compose
- [ ] Add `Enable Clipboard Monitoring` toggle switch with explanation text
- [ ] Add `Enable Text Field Monitoring` toggle switch with explanation text
- [ ] Add `Debounce Delay` slider (range 200ms to 2000ms, default 800ms) with current value display
- [ ] Add per-entity-type confidence threshold sliders in a collapsible `Expandable` section
- [ ] Add `Notification Channel Settings` shortcut that deep-links to system notification settings
- [ ] Add `[Reset All Settings to Default]` button with confirmation dialog
- [ ] Add `Enable Performance Metrics` toggle that shows latency in the dashboard
- [ ] Add `Run Mode` option: NPU (Quantised) / CPU (Standard) — with explanation of tradeoff
- [ ] Persist all settings via `EncryptedSharedPreferences` immediately on change
- [ ] Read all settings back correctly on app restart — write a round-trip test

### 3.9 — Onboarding Flow
- [ ] 🎨 Create a 3-screen onboarding flow shown only on first app launch
- [ ] Onboarding Screen 1: Title "Your Clipboard Is Exposed" — explain the problem with a visual illustration
- [ ] Onboarding Screen 2: "How PrivacyGuard Protects You" — explain on-device AI with a flow diagram
- [ ] Onboarding Screen 3: "Grant Permissions" — request Accessibility Service, Notification, SYSTEM_ALERT_WINDOW in sequence
- [ ] For each permission: show an explanation dialog before opening the system permission screen
- [ ] Handle the case where the user denies a permission — explain the consequence and offer to open Settings
- [ ] Mark onboarding as complete in SharedPreferences once all permissions are granted
- [ ] Do NOT show onboarding again if it has been completed — check the flag in `MainActivity.onCreate()`
- [ ] Add a `[Skip]` button that bypasses permissions but shows a persistent in-app warning banner

---

## PHASE 4 — DATA LAYER & PRIVACY HARDENING
> **Goal:** Everything persists correctly, nothing phones home, all data is encrypted.

### 4.1 — DetectionEvent Data Model
- [ ] Create `DetectionEvent.kt` data class with: `id: UUID`, `timestamp: Long`, `entityType: EntityType`, `severity: Severity`, `sourceApp: String?`, `actionTaken: UserAction`
- [ ] Create `UserAction.kt` enum: `CLIPBOARD_CLEARED`, `DISMISSED`, `WHITELISTED_APP`, `AUTO_DISMISSED`, `NO_ACTION`
- [ ] Explicitly DO NOT include `rawText` in `DetectionEvent` — the sensitive data is never persisted
- [ ] Implement `DetectionEvent.toJson()` and `DetectionEvent.fromJson()` using Gson for serialisation
- [ ] Write unit tests for serialisation round-trip — ensure no fields are lost or corrupted

### 4.2 — EncryptedLogRepository
- [ ] ⚡ 🔒 Create `EncryptedLogRepository.kt` using `EncryptedSharedPreferences` for all storage
- [ ] ⚡ 🔒 Initialise `EncryptedSharedPreferences` with Android Keystore master key: `MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()`
- [ ] Implement `record(event: DetectionEvent)` that serialises the event and stores it under key `event_${event.id}`
- [ ] Implement `getAllEvents(): Flow<List<DetectionEvent>>` as a reactive stream that emits on every change
- [ ] Implement `deleteEvent(id: UUID)` for swipe-to-delete functionality
- [ ] Implement `deleteAllEvents()` for the clear history action
- [ ] Implement `getRecentEvents(count: Int): List<DetectionEvent>` for the dashboard preview
- [ ] Implement `getEventCountByType(): Map<EntityType, Int>` for dashboard statistics
- [ ] Limit total stored events to 1000 — implement LRU eviction when limit is exceeded
- [ ] Write unit tests for all repository operations using Robolectric

### 4.3 — WhitelistManager
- [ ] Create `WhitelistManager.kt` using `EncryptedSharedPreferences` for storage
- [ ] Implement `addToWhitelist(packageName: String)` that stores the package name in the encrypted set
- [ ] Implement `removeFromWhitelist(packageName: String)`
- [ ] Implement `isWhitelisted(packageName: String): Boolean` backed by an in-memory `HashSet` for O(1) lookup
- [ ] Implement `getAllWhitelistedApps(): Set<String>` for the UI list
- [ ] Implement `prePopulateDefaults()` called on first run with known password manager package names
- [ ] Add to defaults: `com.agilebits.onepassword`, `com.bitwarden.mobile`, `org.keepassdroid`, `com.lastpass.lpandroid`
- [ ] Expose a `Flow<Set<String>>` for the whitelist screen to observe changes reactively
- [ ] Write unit tests for add/remove/isWhitelisted operations

### 4.4 — Privacy Hardening
- [ ] ⚡ 🔒 Remove the `INTERNET` permission from `AndroidManifest.xml` — confirm it is NOT present
- [ ] 🔒 Verify no INTERNET permission by running `aapt dump permissions app-debug.apk | grep INTERNET` — expect no output
- [ ] 🔒 Add `android:allowBackup="false"` to the `<application>` tag — prevents data in cloud backups
- [ ] 🔒 Add `android:fullBackupContent="@xml/backup_rules"` with a `backup_rules.xml` that excludes all preferences
- [ ] 🔒 Remove Firebase, Analytics, Crashlytics, or any other telemetry dependency if accidentally included
- [ ] 🔒 Audit all third-party SDKs for network calls — confirm Melange SDK itself only communicates during model download, not during inference
- [ ] 🔒 Add ProGuard rules to strip any reflection-based network access attempts from third-party libraries
- [ ] 🔒 Set `android:networkSecurityConfig` to a custom config that blocks all cleartext traffic
- [ ] 🔒 Add `android:usesCleartextTraffic="false"` to the `<application>` tag as an extra safeguard
- [ ] 🔒 Enable full-disk encryption via `android:directBootAware="false"` for services that don't need to run pre-unlock

### 4.5 — Keystore & Key Management
- [ ] 🔒 Create `KeystoreManager.kt` managing the app's Android Keystore keys
- [ ] 🔒 Generate a `MasterKey` using `AES256_GCM` scheme for all EncryptedSharedPreferences instances
- [ ] 🔒 Verify the master key exists before each sensitive operation — regenerate if corrupted (handles factory reset)
- [ ] 🔒 Add a `strongBoxBacked = true` flag if the device has StrongBox hardware security module
- [ ] 🔒 Add a `BiometricManager` check to optionally require biometric authentication before viewing history

---

## PHASE 5 — POLISH, TESTING & DEMO PREP
> **Goal:** The app is stable, fast, and looks great on camera.

### 5.1 — Performance Optimization
- [ ] Profile the app with Android Studio CPU Profiler during a clipboard monitor cycle — identify bottlenecks
- [ ] Ensure model inference is always on `Dispatchers.Default` — never on the main thread
- [ ] Ensure all UI updates are always on `Dispatchers.Main` via `withContext` or `LiveData.postValue()`
- [ ] Verify the foreground service does not cause ANR (Application Not Responding) — watchdog timeout is 5 seconds
- [ ] Measure battery drain over 1 hour of active monitoring — should be under 2%
- [ ] Reduce `ClipboardMonitorService` memory footprint by reusing ByteBuffer instances between inference calls
- [ ] Implement model instance pooling if concurrency is needed (unlikely in hackathon but good practice)
- [ ] Test with a large clipboard payload (5,000 characters) to confirm no UI freeze or crash
- [ ] Enable R8 / ProGuard in the release build to reduce APK size and improve startup time
- [ ] Add `android:largeHeap="false"` — verify the app works within standard memory limits

### 5.2 — Edge Case Handling
- [ ] Handle clipboard containing non-text data (images, URIs) — skip analysis gracefully
- [ ] Handle clipboard containing a single URL — check if it is a phishing link (optional, bonus feature)
- [ ] Handle device language set to non-English — ensure the UI renders correctly
- [ ] Handle very rapid app switching by the user while an alert overlay is shown — dismiss cleanly
- [ ] Handle the model not being available (download failed) — show a clear error state in the dashboard
- [ ] Handle the Accessibility Service being disabled mid-session by the user — detect in the dashboard and prompt re-enable
- [ ] Handle the app being killed by the system (OOM) while the service is running — service should restart via START_STICKY
- [ ] Handle the device running Android 8 (API 26) minimum — test all features on the minimum supported version
- [ ] Handle screen overlay permission being revoked by the user mid-session
- [ ] Handle the case where ClipboardManager listener is not called on some OEM ROMs — document limitation

### 5.3 — False Positive Reduction
- [ ] Add a regex pre-filter before calling the model: if text does not match any PII pattern loosely, skip inference
- [ ] Add common false-positive suppression: 16-digit numbers that fail the Luhn check are NOT credit cards
- [ ] Add common false-positive suppression: phone-like patterns under 10 digits are skipped
- [ ] Add common false-positive suppression: `example.com` and `test@test.com` are known test addresses — suppress
- [ ] Implement user feedback: `[This was not sensitive]` button on MEDIUM alerts to help tune thresholds
- [ ] Log false positive feedback events locally for future model fine-tuning (never transmitted)

### 5.4 — Accessibility
- [ ] Add content descriptions to all icon-only buttons for TalkBack compatibility
- [ ] Verify the overlay alert is announced by TalkBack when it appears
- [ ] Test minimum text size (system large text setting) does not break UI layouts
- [ ] Add semantic roles to all interactive elements in Compose via `Modifier.semantics`
- [ ] Verify colour contrast ratios meet WCAG AA standards for all text on coloured backgrounds

### 5.5 — Unit Tests
- [ ] Write unit test for `PIITokenizer.encode()` with a credit card number
- [ ] Write unit test for `OutputDecoder.decode()` with a mock logit buffer containing a CREDIT_CARD entity
- [ ] Write unit test for `ConfidenceThresholds` — verify all entity types have a threshold
- [ ] Write unit test for `WhitelistManager.isWhitelisted()` after add and remove operations
- [ ] Write unit test for `EncryptedLogRepository` CRUD operations using Robolectric
- [ ] Write unit test for `Debouncer` — verify single emission after burst within window
- [ ] Write unit test for `PIIEntity.severity` computed property
- [ ] Write unit test for `AnalysisPipeline` with a mocked `PrivacyModel` and `AlertManager`
- [ ] Write integration test for the full pipeline: text in → alert out, using an instrumented test

### 5.6 — UI Polish
- [ ] 🎨 Add Lottie animation for the shield icon on the dashboard (idle breathing animation)
- [ ] 🎨 Add haptic feedback on CRITICAL alert appearance via `HapticFeedbackConstants.REJECT`
- [ ] 🎨 Add sound alert option for CRITICAL severity (opt-in, off by default)
- [ ] 🎨 Add smooth transition animations between all Compose screens using `AnimatedNavHost`
- [ ] 🎨 Polish the onboarding illustrations — use vector drawables for each screen
- [ ] 🎨 Add a dark mode variant and verify all screens look correct in dark mode
- [ ] 🎨 Add a dynamic colour variant using Material You (API 31+) that adapts to the device wallpaper
- [ ] 🎨 Verify no text is truncated on small screens (320dp width minimum)
- [ ] 🎨 Add micro-animations to the protection score dial when it changes value
- [ ] 🎨 Add a real-time inference latency sparkline chart to the dashboard performance section

---

## PHASE 6 — SUBMISSION PREPARATION
> **Goal:** Everything is submitted correctly before the deadline.

### 6.1 — GitHub Repository Cleanup
- [ ] 📋 Ensure the GitHub repository is set to Public
- [ ] 📋 Remove any hardcoded API keys, Personal Keys, or secrets from the codebase
- [ ] 📋 Confirm `local.properties` is in `.gitignore` and not pushed to GitHub
- [ ] 📋 Remove debug/test code, scratch files, and TODO comments from visible classes
- [ ] 📋 Ensure the project builds cleanly from a fresh `git clone` with no additional setup
- [ ] 📋 Add a `demo/` directory to the repo with a screenshot or thumbnail of the app

### 6.2 — README.md
- [ ] 📋 Write project name and tagline at the top of README
- [ ] 📋 Add a one-paragraph description of the problem being solved
- [ ] 📋 Add a one-paragraph description of the solution and how it works
- [ ] 📋 Add a section "How Melange Is Used" explaining the model, SDK integration, and inference pipeline
- [ ] 📋 Add a section "AI Model" naming the model: `Team_ZETIC/TextAnonymizer`
- [ ] 📋 Add an ASCII or Markdown architecture diagram showing the data flow
- [ ] 📋 Add a section "Privacy Guarantee" explaining no-internet, encrypted storage, and no data persistence
- [ ] 📋 Add a "Build & Run" section with step-by-step instructions including Melange Personal Key setup
- [ ] 📋 Add a "Permissions" section explaining why each permission is needed
- [ ] 📋 Add a "Demo Video" link to the YouTube/Google Drive hosted demo video
- [ ] 📋 Add a "License" section (MIT recommended for open hackathon projects)

### 6.3 — ARCHITECTURE.md
- [ ] 📋 Write a detailed architecture document in `ARCHITECTURE.md`
- [ ] 📋 Include the layered architecture diagram: Monitoring → Inference → Alert → Storage
- [ ] 📋 Include the Melange SDK integration diagram
- [ ] 📋 Explain the zero-network design decision and how it is enforced
- [ ] 📋 Explain the encrypted storage design
- [ ] 📋 Include performance numbers: target latency, memory, battery

### 6.4 — Demo Video Production
- [ ] 📋 ⚡ Prepare a fake credit card number to copy: `4532 1234 5678 9012` (passes Luhn check — looks real, isn't)
- [ ] 📋 ⚡ Prepare a fake SSN to type: `123-45-6789` (clearly fake number)
- [ ] 📋 ⚡ Prepare a fake API key: `sk-test_aBcDeFgHiJkLmNoPqRsTuVwXyZ123456`
- [ ] 📋 Set up Android screen recording via `adb shell screenrecord /sdcard/demo.mp4` or Android Studio mirroring
- [ ] 📋 Record Scene 1: App open, dashboard active, protection score shown, narrate concept in one sentence
- [ ] 📋 Record Scene 2: Copy fake CC number → red alert overlay fires → press [Clear Clipboard] → show clipboard is empty
- [ ] 📋 Record Scene 3: Enable airplane mode in status bar — copy CC number again → alert still fires
- [ ] 📋 Record Scene 4: Open Android Studio CPU Profiler (or show on-screen latency counter) — copy CC — show NPU spike
- [ ] 📋 Record Scene 5: Open history log — show encrypted local entries — close with tagline
- [ ] 📋 Edit the video to 2-3 minutes — trim pauses and mistakes
- [ ] 📋 Add captions or on-screen text annotations explaining each scene
- [ ] 📋 Add a title card at the start: "PrivacyGuard — On-Device PII Protection" and the hackathon name
- [ ] 📋 Upload to YouTube (unlisted) or Google Drive and copy the shareable link
- [ ] 📋 Watch the full video once more to verify audio is audible and screen content is legible

### 6.5 — Hackathon Form Submission
- [ ] 📋 ⚡ Open the submission form at https://forms.gle/qJi5RZpqP6Q7pdMN9
- [ ] 📋 Fill in Project Name: "PrivacyGuard"
- [ ] 📋 Fill in Problem Statement: 2-3 sentences explaining PII clipboard leakage
- [ ] 📋 Fill in How Melange Is Used: explain on-device NER inference, TextAnonymizer model, NPU acceleration
- [ ] 📋 Fill in AI Model Used: `Team_ZETIC/TextAnonymizer`
- [ ] 📋 Paste the public GitHub repository URL
- [ ] 📋 Paste the demo video URL
- [ ] 📋 Double-check all fields are filled before submitting
- [ ] 📋 ⚡ Submit the form before March 12, 2026 11:59 PM PT (Deadline is 11:59 PM not 4:00 PM — confirm from email)
- [ ] 📋 Take a screenshot of the form submission confirmation page

---

## PHASE 7 — BONUS FEATURES (Time Permitting)
> **Goal:** Extra credit items that differentiate PrivacyGuard further if you have spare time.

### 7.1 — Regex Pre-Screening Layer
- [ ] Implement a `RegexScreener.kt` that runs fast regex patterns before calling the AI model
- [ ] Add Luhn algorithm check for credit card number candidates before passing to the model
- [ ] Add pattern check for SSN: `\d{3}-\d{2}-\d{4}` and variants
- [ ] Add pattern check for email: standard RFC-compliant regex
- [ ] Add pattern check for API keys: long alphanumeric strings with prefix patterns (`sk-`, `Bearer `, etc.)
- [ ] Use regex as a pre-filter to reduce inference calls — only call model if regex finds a candidate region
- [ ] Measure and document the inference reduction rate from the pre-filter

### 7.2 — On-Screen OCR Protection (Stretch Goal)
- [ ] Add `[Scan Screen]` button on the dashboard that captures a screenshot using MediaProjection API
- [ ] Run the screenshot through an on-device OCR pipeline (ML Kit Text Recognition)
- [ ] Pass the OCR output through PrivacyModel for PII detection
- [ ] Display a report of all PII found on the current screen
- [ ] This demonstrates a completely different monitoring vector — impressive for judges

### 7.3 — Performance Dashboard
- [ ] Add a performance metrics screen showing: total inferences run, average latency, NPU vs CPU ratio
- [ ] Show a 24-hour inference latency sparkline chart
- [ ] Show model memory usage over time
- [ ] Export performance report as a text file for troubleshooting

### 7.4 — Notification Improvements
- [ ] Add an ongoing notification that shows the current protection status and today's detection count
- [ ] Add a quick-action tile in the Android Quick Settings panel to toggle monitoring on/off
- [ ] Add a home screen widget showing the protection score

### 7.5 — Developer Mode
- [ ] Add a hidden developer mode (tap version number 7 times à la Android About Phone)
- [ ] In developer mode: show raw model output logits, confidence breakdown by entity type, full token sequence
- [ ] In developer mode: add a text input field to manually test arbitrary strings against the model
- [ ] This is extremely useful for the demo to show the model working in real time

### 7.6 — Internationalization
- [ ] Add string resource localization for Spanish as a second language
- [ ] Ensure the AI model handles Spanish PII formats (Spanish ID numbers, phone formats)
- [ ] Add right-to-left layout support for Arabic/Hebrew even if the model doesn't yet support these languages

---

## MAINTENANCE TASKS (Post-Hackathon)
> **These are not required for submission but should be tracked.**

### 8.1 — Model Improvement
- [ ] Collect false positive patterns from user feedback (with consent) to build a fine-tuning dataset
- [ ] Fine-tune a custom model on the extended PII corpus and upload to Melange as a custom model
- [ ] Evaluate the fine-tuned model against the base model using the existing validation corpus
- [ ] Implement model versioning — app should detect and prompt user to update to a newer model

### 8.2 — Security Audit
- [ ] Conduct a threat modelling session: what happens if a malicious app reads our EncryptedSharedPreferences?
- [ ] Verify Android Keystore keys cannot be extracted even on a rooted device
- [ ] Verify the AccessibilityService cannot be abused as a keylogger — document all event types it subscribes to
- [ ] Write a security disclosure document explaining the trust model

### 8.3 — App Store Preparation
- [ ] Prepare Google Play Store listing: screenshots, feature graphic, privacy policy URL
- [ ] Write the Play Store privacy policy — explain Accessibility Service usage clearly (Google requires this)
- [ ] Create a `PRIVACY_POLICY.md` in the repository
- [ ] Prepare a Data Safety form response for the Play Store (all questions should be: data collected = none)
- [ ] Create a keystore for release signing and store it securely outside the repository
- [ ] Build a signed release APK and test it on a physical device

---

## QUICK REFERENCE: CRITICAL PATH TASKS

> These 20 tasks are the minimum required for a working demo. Do these first.

- [ ] ⚡ Melange account created and Personal Key obtained
- [ ] ⚡ Android project created with Melange SDK dependency
- [ ] ⚡ `ZeticMLangeModel` initialises without error
- [ ] ⚡ `analyzeText("4532 1234 5678 9012")` returns a CREDIT_CARD entity
- [ ] ⚡ `ClipboardMonitorService` registers and fires on clipboard change
- [ ] ⚡ Clipboard event reaches `AnalysisPipeline.processText()`
- [ ] ⚡ `PrivacyModel.analyzeText()` called from the pipeline with real clipboard text
- [ ] ⚡ `AlertManager.show()` called when CRITICAL entity is detected
- [ ] ⚡ `AlertOverlayService` displays the red overlay above all other apps
- [ ] ⚡ Overlay dismisses correctly with [Clear Clipboard] action
- [ ] ⚡ `EncryptedLogRepository.record()` persists the detection event
- [ ] ⚡ Dashboard displays monitoring status and recent detections
- [ ] ⚡ INTERNET permission is confirmed absent from the manifest
- [ ] ⚡ App works correctly in airplane mode
- [ ] ⚡ GitHub repository is public and contains buildable code
- [ ] ⚡ README includes project name, problem, Melange usage, model name
- [ ] ⚡ Demo video is recorded showing clipboard detection and airplane mode test
- [ ] ⚡ Demo video is 2-3 minutes and clearly shows the alert firing
- [ ] ⚡ Submission form at forms.gle/qJi5RZpqP6Q7pdMN9 is completed
- [ ] ⚡ Submission confirmed before March 12, 2026 11:59 PM PT

---

*Total Tasks: 520+ unique items across 8 phases and 40+ subsections*
*Last Updated: March 2026 — Melange On-Device AI Hackathon*

---

## PHASE 9 — ADVANCED TESTING & QA

### 9.1 — Device Compatibility Testing
- [ ] Test ClipboardMonitorService on Samsung Galaxy (One UI) — verify clipboard listener fires correctly on Samsung's ROM
- [ ] Test ClipboardMonitorService on Pixel device — baseline reference platform
- [ ] Test ClipboardMonitorService on OnePlus device — OxygenOS has aggressive background app killing
- [ ] Test ClipboardMonitorService on Xiaomi device — MIUI requires special battery optimization exemption
- [ ] Test AccessibilityService on Android 14 — verify no new restrictions block typeViewTextChanged events
- [ ] Test AccessibilityService on Android 13 — clipboard read restrictions were tightened in API 33
- [ ] Test AccessibilityService on Android 12 — first version with clipboard read notifications
- [ ] Test SYSTEM_ALERT_WINDOW overlay on devices with navigation gestures (swipe-based) vs hardware buttons
- [ ] Test app behaviour when battery optimization is enabled — service may be killed
- [ ] Document which OEM ROMs require manual battery optimization exemption and add a prompt for these

### 9.2 — Security Testing
- [ ] 🔒 Run `adb shell dumpsys package com.privacyguard | grep permission` and verify INTERNET is absent
- [ ] 🔒 Use Charles Proxy / mitmproxy on the test device to verify zero network traffic during inference
- [ ] 🔒 Use Wireshark on the network to confirm no outbound packets from the device during a detection event
- [ ] 🔒 Verify EncryptedSharedPreferences file on-disk is not human-readable using `adb pull` and hexdump
- [ ] 🔒 Test that uninstalling and reinstalling the app correctly re-creates the Keystore key
- [ ] 🔒 Verify the backup exclusion rules work — clear app data via ADB and confirm history is gone
- [ ] 🔒 Run a static analysis tool (e.g., MobSF) on the debug APK and review findings
- [ ] 🔒 Verify no sensitive strings (personal key, SSN test values) appear in the APK binary using `strings` command
- [ ] 🔒 Confirm that the AccessibilityService event log does not appear in Android's system log (logcat) in release builds
- [ ] 🔒 Verify ProGuard/R8 removes debug logging in the release build

### 9.3 — Stress Testing
- [ ] Simulate 1,000 rapid clipboard changes over 60 seconds — verify no memory leak using Memory Profiler
- [ ] Simulate typing 500 characters in rapid succession in a text field — verify debouncer prevents inference flood
- [ ] Run the app for 24 hours continuously — verify service does not crash or accumulate memory
- [ ] Fill the detection log with 1,000 entries — verify UI still scrolls smoothly with LazyColumn pagination
- [ ] Test with the model running at full CPU load (run inference in a tight loop) — verify UI remains responsive
- [ ] Test overlay dismissal under high load — verify no ANR is triggered in WindowManager operations

### 9.4 — Regression Tests
- [ ] After any change to `PIITokenizer`, re-run the full model validation corpus and compare results
- [ ] After any change to `OutputDecoder`, re-run the false positive corpus and confirm rate has not increased
- [ ] After any change to alert thresholds, test all 10 entity types against their representative PII samples
- [ ] After any change to `WhitelistManager`, verify whitelisted apps still do not trigger alerts
- [ ] After any change to `EncryptedLogRepository`, verify existing data is still readable after upgrade

---

## PHASE 10 — DOCUMENTATION & KNOWLEDGE BASE

### 10.1 — Code Documentation
- [ ] Add KDoc comments to all public classes and public functions in the codebase
- [ ] Add KDoc `@param`, `@return`, and `@throws` annotations to all public functions
- [ ] Add inline comments explaining the Melange ByteBuffer format in `PIITokenizer.kt`
- [ ] Add inline comments explaining the output logit decoding logic in `OutputDecoder.kt`
- [ ] Add a code comment at the top of `PrivacyAccessibilityService.kt` explaining privacy guarantees
- [ ] Add a code comment at the top of `ClipboardMonitorService.kt` explaining why foreground service is required
- [ ] Document the `ConfidenceThresholds` defaults and the reasoning for each value
- [ ] Add `@VisibleForTesting` annotations to internal functions exposed for test access

### 10.2 — Technical Documentation
- [ ] Write `docs/MELANGE_INTEGRATION.md` — detailed guide on how Melange is used, for other developers
- [ ] Write `docs/MODEL_EVALUATION.md` — accuracy results from the model validation corpus
- [ ] Write `docs/SECURITY_MODEL.md` — threat model, trust boundaries, and mitigations
- [ ] Write `docs/PERMISSIONS.md` — why each permission is needed and what alternatives were considered
- [ ] Write `docs/CONTRIBUTING.md` — how to set up the dev environment and contribute
- [ ] Add architecture diagram as a Mermaid diagram in `ARCHITECTURE.md` — renders automatically on GitHub

### 10.3 — User-Facing Documentation
- [ ] Write a help text for each permission prompt explaining why the permission is needed in plain English
- [ ] Write tooltip help text for the confidence threshold sliders in Settings
- [ ] Write an FAQ section in the README covering common questions: "Does it read my messages?", "Where is my data stored?"
- [ ] Write the in-app Privacy Policy covering: data collected, data stored, data transmitted, third parties

---

## PHASE 11 — MELANGE-SPECIFIC TASKS

### 11.1 — Melange Dashboard
- [ ] 🤖 Log in to the Melange dashboard and explore the model analytics features
- [ ] 🤖 Review the inference logs in the Melange dashboard to confirm on-device calls are recorded
- [ ] 🤖 Check if a newer version of `TextAnonymizer` is available — update if so
- [ ] 🤖 Explore the Melange model upload feature — document the ONNX export process for a potential custom model
- [ ] 🤖 Review the Melange Pro+ features activated by the hackathon coupon — note any limits on model calls

### 11.2 — Custom Model (Stretch Goal)
- [ ] 🤖 Research HuggingFace models suitable for PII detection: `dslim/bert-base-NER`, `Jean-Baptiste/roberta-large-ner-english`
- [ ] 🤖 Export the chosen model to ONNX format using `transformers.onnx` or `optimum`
- [ ] 🤖 Quantise the ONNX model to INT8 for NPU compatibility using ONNX Runtime quantisation tools
- [ ] 🤖 Upload the quantised ONNX model to the Melange dashboard under your account
- [ ] 🤖 Update `PrivacyModel.kt` to use your custom model key instead of `Team_ZETIC/TextAnonymizer`
- [ ] 🤖 Re-run the full model validation corpus against the custom model and compare accuracy
- [ ] 🤖 Compare inference latency of the custom model vs the Melange public model on your test device
- [ ] 🤖 Document the custom model's label set and update `OutputDecoder.kt` accordingly

### 11.3 — Melange Run Modes
- [ ] 🤖 Test inference with `ZETIC_MLANGE_RUN_MODE_QUANTIZED` and measure latency
- [ ] 🤖 Test inference with `ZETIC_MLANGE_RUN_MODE_STANDARD` and measure latency
- [ ] 🤖 Compare accuracy between quantised and standard mode — document any precision loss
- [ ] 🤖 Implement automatic mode selection: use quantised if NPU is available, standard as fallback
- [ ] 🤖 Display the active run mode on the dashboard performance section

---

*Grand Total: 520+ unique tasks across 11 phases, 55+ subsections*
*All tasks aligned with Melange Hackathon judging criteria: On-Device AI usage, Technical Implementation, Creativity, Demo Clarity*
