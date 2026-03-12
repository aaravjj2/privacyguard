package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Test

class PIIEntityTest {

    @Test
    fun `entity reports correct severity from entity type`() {
        val entity = PIIEntity(
            entityType = EntityType.CREDIT_CARD,
            confidence = 0.95f,
            startIndex = 0,
            endIndex = 19,
            rawText = "4532 1234 5678 9012"
        )
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `entity meets threshold when confidence is above`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 20)
        assertTrue(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `entity does not meet threshold when confidence is below`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.90f, 0, 20)
        assertFalse(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `entity meets threshold when confidence equals threshold`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 20)
        assertTrue(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `entity length is calculated correctly`() {
        val entity = PIIEntity(EntityType.SSN, 0.92f, 5, 16)
        assertEquals(11, entity.length)
    }

    @Test
    fun `masked text hides most characters`() {
        val entity = PIIEntity(
            EntityType.CREDIT_CARD, 0.95f, 0, 19,
            rawText = "4532123456789012"
        )
        val masked = entity.maskedText
        assertTrue(masked.endsWith("9012"))
        assertTrue(masked.startsWith("*"))
        assertEquals(16, masked.length)
    }

    @Test
    fun `masked text handles short strings`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.90f, 0, 3, rawText = "abc")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `masked text handles empty string`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.90f, 0, 0, rawText = "")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `data class equality works`() {
        val e1 = PIIEntity(EntityType.EMAIL, 0.95f, 0, 20, "test@example.com")
        val e2 = PIIEntity(EntityType.EMAIL, 0.95f, 0, 20, "test@example.com")
        assertEquals(e1, e2)
    }

    @Test
    fun `data class copy works`() {
        val e1 = PIIEntity(EntityType.EMAIL, 0.95f, 0, 20, "test@example.com")
        val e2 = e1.copy(confidence = 0.99f)
        assertEquals(0.99f, e2.confidence, 0.001f)
        assertEquals(EntityType.EMAIL, e2.entityType)
    }
}
