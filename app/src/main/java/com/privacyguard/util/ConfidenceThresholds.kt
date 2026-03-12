package com.privacyguard.util

/**
 * Per-entity-type confidence thresholds for PII detection.
 * Values below these thresholds are filtered out to reduce false positives.
 */
object ConfidenceThresholds {

    private val defaults = mapOf(
        com.privacyguard.ml.EntityType.CREDIT_CARD to 0.90f,
        com.privacyguard.ml.EntityType.SSN to 0.92f,
        com.privacyguard.ml.EntityType.PASSWORD to 0.80f,
        com.privacyguard.ml.EntityType.API_KEY to 0.85f,
        com.privacyguard.ml.EntityType.EMAIL to 0.95f,
        com.privacyguard.ml.EntityType.PHONE to 0.88f,
        com.privacyguard.ml.EntityType.PERSON_NAME to 0.75f,
        com.privacyguard.ml.EntityType.ADDRESS to 0.80f,
        com.privacyguard.ml.EntityType.DATE_OF_BIRTH to 0.82f,
        com.privacyguard.ml.EntityType.MEDICAL_ID to 0.85f,
        com.privacyguard.ml.EntityType.UNKNOWN to 0.90f
    )

    private val overrides = mutableMapOf<com.privacyguard.ml.EntityType, Float>()

    fun getThreshold(entityType: com.privacyguard.ml.EntityType): Float {
        return overrides[entityType] ?: defaults[entityType] ?: 0.85f
    }

    fun setThreshold(entityType: com.privacyguard.ml.EntityType, threshold: Float) {
        require(threshold in 0.0f..1.0f) { "Threshold must be between 0.0 and 1.0" }
        overrides[entityType] = threshold
    }

    fun resetToDefaults() {
        overrides.clear()
    }

    fun getDefaultThreshold(entityType: com.privacyguard.ml.EntityType): Float {
        return defaults[entityType] ?: 0.85f
    }

    fun getAllThresholds(): Map<com.privacyguard.ml.EntityType, Float> {
        return com.privacyguard.ml.EntityType.entries.associateWith { getThreshold(it) }
    }
}
