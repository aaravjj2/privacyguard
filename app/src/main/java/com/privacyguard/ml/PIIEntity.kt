package com.privacyguard.ml

/**
 * Represents a single detected PII entity within analyzed text.
 *
 * @property entityType The classification of the PII entity
 * @property confidence Model confidence score (0.0 to 1.0)
 * @property startIndex Start character index in the source text
 * @property endIndex End character index in the source text (exclusive)
 * @property rawText The actual detected text (for in-memory use only, NEVER persisted)
 */
data class PIIEntity(
    val entityType: EntityType,
    val confidence: Float,
    val startIndex: Int,
    val endIndex: Int,
    val rawText: String = ""
) {
    val severity: Severity get() = entityType.severity

    val length: Int get() = endIndex - startIndex

    fun meetsThreshold(threshold: Float): Boolean = confidence >= threshold

    val maskedText: String
        get() = if (rawText.length <= 4) "****"
                else "${"*".repeat(rawText.length - 4)}${rawText.takeLast(4)}"
}
