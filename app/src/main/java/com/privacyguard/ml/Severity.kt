package com.privacyguard.ml

import androidx.compose.ui.graphics.Color

/**
 * Severity levels for PII detections.
 * Maps to alert types: CRITICAL -> full overlay, HIGH -> banner, MEDIUM -> toast.
 */
enum class Severity(val displayName: String, val colorHex: Long) {
    CRITICAL("Critical", 0xFFE11D48),
    HIGH("High", 0xFFF97316),
    MEDIUM("Medium", 0xFFEAB308);

    val color: Color get() = Color(colorHex)
}
