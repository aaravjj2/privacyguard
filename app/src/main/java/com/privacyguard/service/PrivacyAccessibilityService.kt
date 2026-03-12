package com.privacyguard.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.privacyguard.data.WhitelistManager
import com.privacyguard.util.AnalysisPipeline
import com.privacyguard.util.RegexScreener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Accessibility service that intercepts TYPE_VIEW_TEXT_CHANGED events across all apps
 * to detect PII typed into text fields in real-time.
 *
 * Privacy and security design considerations:
 * 1. Only captures TYPE_VIEW_TEXT_CHANGED events — cannot read screen layout or other content
 * 2. Skips password fields by detecting InputType.TYPE_TEXT_VARIATION_PASSWORD and variants
 * 3. Skips web password fields by checking hint text for password-related keywords
 * 4. Checks whitelist FIRST before any text processing — whitelisted apps are never analyzed
 * 5. Skips PrivacyGuard's own package to prevent recursive event loops
 * 6. Never persists or logs raw text — only passes to in-memory inference
 * 7. Pre-screens with regex before calling ML model to minimize unnecessary inference
 * 8. Debounces rapid keystrokes via the analysis pipeline
 * 9. Properly recycles AccessibilityNodeInfo to prevent memory leaks
 * 10. Reports statistics without exposing sensitive content
 */
class PrivacyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "PrivacyA11yService"
        private const val MIN_TEXT_LENGTH = 5
        private const val MAX_TEXT_LENGTH = 5000

        // Password-related hint keywords
        private val PASSWORD_HINTS = setOf(
            "password", "passcode", "pin", "secret", "passphrase",
            "contraseña", "mot de passe", "kennwort", "пароль",
            "密码", "パスワード", "비밀번호"
        )

        // Self-exclusion package
        private const val SELF_PACKAGE = "com.privacyguard"

        @Volatile
        private var isConnected = false

        fun isServiceConnected(): Boolean = isConnected
    }

    // Coroutine scope for background work
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Dependencies
    private var pipeline: AnalysisPipeline? = null
    private var whitelistManager: WhitelistManager? = null

    // Statistics
    private val _eventsReceived = MutableStateFlow(0L)
    val eventsReceived: StateFlow<Long> = _eventsReceived.asStateFlow()

    private val _eventsProcessed = MutableStateFlow(0L)
    val eventsProcessed: StateFlow<Long> = _eventsProcessed.asStateFlow()

    private val _eventsSkippedWhitelist = MutableStateFlow(0L)
    val eventsSkippedWhitelist: StateFlow<Long> = _eventsSkippedWhitelist.asStateFlow()

    private val _eventsSkippedPassword = MutableStateFlow(0L)
    val eventsSkippedPassword: StateFlow<Long> = _eventsSkippedPassword.asStateFlow()

    private val _eventsSkippedShort = MutableStateFlow(0L)
    val eventsSkippedShort: StateFlow<Long> = _eventsSkippedShort.asStateFlow()

    private val _eventsSkippedPrescreen = MutableStateFlow(0L)
    val eventsSkippedPrescreen: StateFlow<Long> = _eventsSkippedPrescreen.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    // Deduplication: avoid processing identical text from rapid events
    private var lastTextHash: Int = 0
    private var lastTextTime: Long = 0
    private val DEDUP_WINDOW_MS = 500L

    // ==================
    // LIFECYCLE
    // ==================

    override fun onServiceConnected() {
        super.onServiceConnected()
        isConnected = true
        _isActive.value = true

        // Configure service info programmatically as a fallback
        try {
            val info = AccessibilityServiceInfo().apply {
                eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
                feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                notificationTimeout = 100L
                flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            }
            serviceInfo = info
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure service info programmatically", e)
        }

        Log.d(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return

        _eventsReceived.value += 1

        val packageName = event.packageName?.toString() ?: return

        // 1. Skip self to prevent recursive loops
        if (packageName == SELF_PACKAGE) return

        // 2. Whitelist check — FIRST operation, before any text processing
        if (whitelistManager?.isWhitelisted(packageName) == true) {
            _eventsSkippedWhitelist.value += 1
            return
        }

        // 3. Skip password fields
        if (isPasswordField(event)) {
            _eventsSkippedPassword.value += 1
            return
        }

        // 4. Extract and validate text
        val text = extractText(event) ?: return
        if (text.length < MIN_TEXT_LENGTH) {
            _eventsSkippedShort.value += 1
            return
        }

        // 5. Deduplication check
        val hash = text.hashCode()
        val now = System.currentTimeMillis()
        if (hash == lastTextHash && (now - lastTextTime) < DEDUP_WINDOW_MS) return
        lastTextHash = hash
        lastTextTime = now

        // 6. Truncate if needed
        val processedText = if (text.length > MAX_TEXT_LENGTH) {
            text.substring(0, MAX_TEXT_LENGTH)
        } else text

        // 7. Pre-screen with regex — skip inference if no PII patterns
        if (!RegexScreener.containsPotentialPII(processedText)) {
            _eventsSkippedPrescreen.value += 1
            return
        }

        // 8. Send to analysis pipeline (which debounces internally)
        _eventsProcessed.value += 1
        pipeline?.processText(processedText, packageName)
    }

    override fun onInterrupt() {
        // Required by abstract class — no action needed
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        isConnected = false
        _isActive.value = false
        serviceScope.cancel()
        Log.d(TAG, "Accessibility service destroyed")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isConnected = false
        _isActive.value = false
        return super.onUnbind(intent)
    }

    // ==================
    // TEXT EXTRACTION
    // ==================

    /**
     * Extract text from an accessibility event.
     * Joins all text elements from the event.
     */
    private fun extractText(event: AccessibilityEvent): String? {
        val textParts = event.text
        if (textParts.isNullOrEmpty()) return null

        return try {
            textParts.joinToString("")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract text from event", e)
            null
        }
    }

    // ==================
    // PASSWORD FIELD DETECTION
    // ==================

    /**
     * Detect whether an accessibility event originates from a password field.
     * Uses multiple heuristics to catch various password input types.
     */
    private fun isPasswordField(event: AccessibilityEvent): Boolean {
        val source = event.source ?: return false

        try {
            // Check Android input type flags
            if (checkInputTypeFlags(source)) return true

            // Check hint text for password keywords
            if (checkHintText(source)) return true

            // Check view ID for password-related names
            if (checkViewId(source)) return true

            // Check class name for password-specific views
            if (checkClassName(source)) return true

            return false
        } finally {
            // Always recycle to prevent memory leaks
            try {
                source.recycle()
            } catch (e: Exception) {
                // Some Android versions may throw if already recycled
            }
        }
    }

    private fun checkInputTypeFlags(source: AccessibilityNodeInfo): Boolean {
        val inputType = source.inputType
        if (inputType == InputType.TYPE_NULL) return false

        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    private fun checkHintText(source: AccessibilityNodeInfo): Boolean {
        val hint = source.hintText?.toString()?.lowercase() ?: return false
        return PASSWORD_HINTS.any { hint.contains(it) }
    }

    private fun checkViewId(source: AccessibilityNodeInfo): Boolean {
        val viewId = source.viewIdResourceName?.lowercase() ?: return false
        return viewId.contains("password") || viewId.contains("passcode") ||
                viewId.contains("pin_input") || viewId.contains("secret")
    }

    private fun checkClassName(source: AccessibilityNodeInfo): Boolean {
        val className = source.className?.toString() ?: return false
        return className.contains("PasswordEditText", ignoreCase = true) ||
                className.contains("PinEntryEditText", ignoreCase = true)
    }

    // ==================
    // DEPENDENCY INJECTION
    // ==================

    fun setPipeline(analysisPipeline: AnalysisPipeline) {
        this.pipeline = analysisPipeline
    }

    fun setWhitelistManager(manager: WhitelistManager) {
        this.whitelistManager = manager
    }

    // ==================
    // STATISTICS
    // ==================

    fun getStatsSummary(): Map<String, Long> = mapOf(
        "received" to _eventsReceived.value,
        "processed" to _eventsProcessed.value,
        "skipped_whitelist" to _eventsSkippedWhitelist.value,
        "skipped_password" to _eventsSkippedPassword.value,
        "skipped_short" to _eventsSkippedShort.value,
        "skipped_prescreen" to _eventsSkippedPrescreen.value
    )

    fun resetStats() {
        _eventsReceived.value = 0
        _eventsProcessed.value = 0
        _eventsSkippedWhitelist.value = 0
        _eventsSkippedPassword.value = 0
        _eventsSkippedShort.value = 0
        _eventsSkippedPrescreen.value = 0
    }
}
