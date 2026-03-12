package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Test

class PIIAnalysisResultTest {

    @Test
    fun `empty result has no sensitive data`() {
        val result = PIIAnalysisResult()
        assertFalse(result.hasSensitiveData())
    }

    @Test
    fun `EMPTY constant has no sensitive data`() {
        assertFalse(PIIAnalysisResult.EMPTY.hasSensitiveData())
        assertEquals(0, PIIAnalysisResult.EMPTY.entityCount)
    }

    @Test
    fun `result with entities has sensitive data`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 19)
            )
        )
        assertTrue(result.hasSensitiveData())
    }

    @Test
    fun `highest severity returns CRITICAL when present`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 10),
                PIIEntity(EntityType.CREDIT_CARD, 0.95f, 15, 34),
                PIIEntity(EntityType.EMAIL, 0.90f, 40, 60)
            )
        )
        assertEquals(Severity.CRITICAL, result.highestSeverity)
    }

    @Test
    fun `highest severity returns null for empty list`() {
        val result = PIIAnalysisResult()
        assertNull(result.highestSeverity)
    }

    @Test
    fun `highest severity returns HIGH when no CRITICAL present`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.EMAIL, 0.95f, 0, 20),
                PIIEntity(EntityType.PERSON_NAME, 0.80f, 25, 35)
            )
        )
        assertEquals(Severity.HIGH, result.highestSeverity)
    }

    @Test
    fun `highest severity returns MEDIUM when only MEDIUM present`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 10)
            )
        )
        assertEquals(Severity.MEDIUM, result.highestSeverity)
    }

    @Test
    fun `entity count is correct`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 19),
                PIIEntity(EntityType.EMAIL, 0.90f, 20, 40),
                PIIEntity(EntityType.PHONE, 0.88f, 45, 55)
            )
        )
        assertEquals(3, result.entityCount)
    }

    @Test
    fun `critical entities filtered correctly`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 19),
                PIIEntity(EntityType.EMAIL, 0.90f, 20, 40),
                PIIEntity(EntityType.SSN, 0.92f, 45, 56)
            )
        )
        assertEquals(2, result.criticalEntities.size)
        assertTrue(result.criticalEntities.all { it.severity == Severity.CRITICAL })
    }

    @Test
    fun `high entities filtered correctly`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 19),
                PIIEntity(EntityType.EMAIL, 0.90f, 20, 40),
                PIIEntity(EntityType.PHONE, 0.88f, 45, 55)
            )
        )
        assertEquals(2, result.highEntities.size)
    }

    @Test
    fun `medium entities filtered correctly`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 10),
                PIIEntity(EntityType.ADDRESS, 0.85f, 15, 30)
            )
        )
        assertEquals(2, result.mediumEntities.size)
    }

    @Test
    fun `entities by severity groups correctly`() {
        val result = PIIAnalysisResult(
            entities = listOf(
                PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 19),
                PIIEntity(EntityType.EMAIL, 0.90f, 20, 40),
                PIIEntity(EntityType.PERSON_NAME, 0.80f, 45, 55)
            )
        )
        val grouped = result.entitiesBySeverity()
        assertEquals(3, grouped.size)
        assertEquals(1, grouped[Severity.CRITICAL]?.size)
        assertEquals(1, grouped[Severity.HIGH]?.size)
        assertEquals(1, grouped[Severity.MEDIUM]?.size)
    }

    @Test
    fun `inference time is stored`() {
        val result = PIIAnalysisResult(inferenceTimeMs = 42L)
        assertEquals(42L, result.inferenceTimeMs)
    }

    @Test
    fun `input length is stored`() {
        val result = PIIAnalysisResult(inputLength = 256)
        assertEquals(256, result.inputLength)
    }
}
