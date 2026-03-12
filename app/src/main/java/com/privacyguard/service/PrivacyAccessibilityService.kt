package com.privacyguard.service

import android.accessibilityservice.AccessibilityService
import android.text.InputType
import android.view.accessibility.AccessibilityEvent
import com.privacyguard.data.WhitelistManager
import com.privacyguard.util.AnalysisPipeline

/**
 * Accessibility service that intercepts TYPE_VIEW_TEXT_CHANGED events
 * across all apps to detect PII typed into text fields.
 *
 * Privacy considerations:
 * - Only captures text change events (not screen layout)
 * - Skips password fields via InputType detection
 * - Checks whitelist before any analysis
 * - Never persists raw text
 */
class PrivacyAccessibilityService : AccessibilityService() {

    companion object {
        private const val MIN_TEXT_LENGTH = 5
    }

    private var pipeline: AnalysisPipeline? = null
    private var whitelistManager: WhitelistManager? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        // Skip self
        if (packageName == "com.privacyguard") return

        // Whitelist check
        if (whitelistManager?.isWhitelisted(packageName) == true) return

        // Skip password fields
        if (isPasswordField(event)) return

        // Extract text
        val text = event.text?.joinToString("") ?: return
        if (text.length < MIN_TEXT_LENGTH) return

        pipeline?.processText(text, packageName)
    }

    override fun onInterrupt() {
        // Required by abstract class, no action needed
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Service connected, ready to process events
    }

    fun setPipeline(analysisPipeline: AnalysisPipeline) {
        this.pipeline = analysisPipeline
    }

    fun setWhitelistManager(manager: WhitelistManager) {
        this.whitelistManager = manager
    }

    private fun isPasswordField(event: AccessibilityEvent): Boolean {
        val source = event.source ?: return false

        // Check text input type flags
        val inputType = source.inputType
        if (inputType != InputType.TYPE_NULL) {
            val variation = inputType and InputType.TYPE_MASK_VARIATION
            if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
                source.recycle()
                return true
            }
        }

        // Check hint text for password-related keywords
        val hint = source.hintText?.toString()?.lowercase() ?: ""
        if (hint.contains("password") || hint.contains("passcode") ||
            hint.contains("pin") || hint.contains("secret")) {
            source.recycle()
            return true
        }

        source.recycle()
        return false
    }
}
