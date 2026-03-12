package com.privacyguard.ml

/**
 * Result of a PII analysis pass on a text input.
 *
 * @property entities List of detected PII entities
 * @property inferenceTimeMs Time taken for model inference in milliseconds
 * @property inputLength Length of the original input text
 */
data class PIIAnalysisResult(
    val entities: List<PIIEntity> = emptyList(),
    val inferenceTimeMs: Long = 0L,
    val inputLength: Int = 0
) {
    fun hasSensitiveData(): Boolean = entities.isNotEmpty()

    val highestSeverity: Severity?
        get() = entities.maxByOrNull { it.severity.ordinal }?.severity

    val entityCount: Int get() = entities.size

    val criticalEntities: List<PIIEntity>
        get() = entities.filter { it.severity == Severity.CRITICAL }

    val highEntities: List<PIIEntity>
        get() = entities.filter { it.severity == Severity.HIGH }

    val mediumEntities: List<PIIEntity>
        get() = entities.filter { it.severity == Severity.MEDIUM }

    fun entitiesBySeverity(): Map<Severity, List<PIIEntity>> =
        entities.groupBy { it.severity }

    companion object {
        val EMPTY = PIIAnalysisResult()
    }
}
