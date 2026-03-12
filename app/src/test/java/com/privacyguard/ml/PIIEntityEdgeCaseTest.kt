package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive edge-case tests for PIIEntity.
 *
 * Covers: construction with all entity types, confidence boundary values (0.0, 0.5, 1.0, negative, >1),
 * index validation, empty/long rawText, meetsThreshold with edge values, severity computation,
 * equals/hashCode, toString, copy, all EntityType x Severity combinations, maskedText,
 * length computation, serialization.
 */
class PIIEntityEdgeCaseTest {

    // ========================================================================
    // Section 1: Construction with all entity types
    // ========================================================================

    @Test
    fun `construct PIIEntity with CREDIT_CARD type`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        assertEquals(EntityType.CREDIT_CARD, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with SSN type`() {
        val entity = PIIEntity(EntityType.SSN, 0.93f, 0, 11, "123-45-6789")
        assertEquals(EntityType.SSN, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with PASSWORD type`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.85f, 0, 8, "p@ssw0rd")
        assertEquals(EntityType.PASSWORD, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with API_KEY type`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.88f, 0, 32, "sk-1234567890abcdef1234567890ab")
        assertEquals(EntityType.API_KEY, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with EMAIL type`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 20, "user@example.com")
        assertEquals(EntityType.EMAIL, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with PHONE type`() {
        val entity = PIIEntity(EntityType.PHONE, 0.89f, 0, 14, "+1-555-123-4567")
        assertEquals(EntityType.PHONE, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with PERSON_NAME type`() {
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8, "John Doe")
        assertEquals(EntityType.PERSON_NAME, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with ADDRESS type`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.82f, 0, 30, "123 Main Street, Springfield")
        assertEquals(EntityType.ADDRESS, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with DATE_OF_BIRTH type`() {
        val entity = PIIEntity(EntityType.DATE_OF_BIRTH, 0.84f, 0, 10, "1990-01-15")
        assertEquals(EntityType.DATE_OF_BIRTH, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with MEDICAL_ID type`() {
        val entity = PIIEntity(EntityType.MEDICAL_ID, 0.87f, 0, 10, "MED-123456")
        assertEquals(EntityType.MEDICAL_ID, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with UNKNOWN type`() {
        val entity = PIIEntity(EntityType.UNKNOWN, 0.91f, 0, 5, "XXXXX")
        assertEquals(EntityType.UNKNOWN, entity.entityType)
    }

    @Test
    fun `construct PIIEntity with all entity types succeeds`() {
        for (type in EntityType.entries) {
            val entity = PIIEntity(type, 0.90f, 0, 10, "test-data")
            assertEquals(type, entity.entityType)
        }
    }

    @Test
    fun `construct PIIEntity with default rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 10)
        assertEquals("", entity.rawText)
    }

    @Test
    fun `construct PIIEntity with explicit empty rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 10, "")
        assertEquals("", entity.rawText)
    }

    // ========================================================================
    // Section 2: Confidence boundary values
    // ========================================================================

    @Test
    fun `confidence of 0_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.0f, 0, 10, "test")
        assertEquals(0.0f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 0_5`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.5f, 0, 10, "test")
        assertEquals(0.5f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 1_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 1.0f, 0, 10, "test")
        assertEquals(1.0f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 0_001`() {
        val entity = PIIEntity(EntityType.PHONE, 0.001f, 0, 5)
        assertEquals(0.001f, entity.confidence, 0.0001f)
    }

    @Test
    fun `confidence of 0_999`() {
        val entity = PIIEntity(EntityType.PHONE, 0.999f, 0, 5)
        assertEquals(0.999f, entity.confidence, 0.0001f)
    }

    @Test
    fun `confidence of 0_1`() {
        val entity = PIIEntity(EntityType.SSN, 0.1f, 0, 5)
        assertEquals(0.1f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 0_9`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, 5)
        assertEquals(0.9f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 0_25`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.25f, 0, 5)
        assertEquals(0.25f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 0_75`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.75f, 0, 5)
        assertEquals(0.75f, entity.confidence, 0.001f)
    }

    @Test
    fun `negative confidence is stored`() {
        val entity = PIIEntity(EntityType.EMAIL, -0.1f, 0, 10)
        assertEquals(-0.1f, entity.confidence, 0.001f)
    }

    @Test
    fun `large negative confidence is stored`() {
        val entity = PIIEntity(EntityType.EMAIL, -100.0f, 0, 10)
        assertEquals(-100.0f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence greater than 1 is stored`() {
        val entity = PIIEntity(EntityType.EMAIL, 1.5f, 0, 10)
        assertEquals(1.5f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of 2_0 is stored`() {
        val entity = PIIEntity(EntityType.PHONE, 2.0f, 0, 10)
        assertEquals(2.0f, entity.confidence, 0.001f)
    }

    @Test
    fun `confidence of Float MAX_VALUE is stored`() {
        val entity = PIIEntity(EntityType.SSN, Float.MAX_VALUE, 0, 10)
        assertEquals(Float.MAX_VALUE, entity.confidence, 0.0f)
    }

    @Test
    fun `confidence of Float MIN_VALUE is stored`() {
        val entity = PIIEntity(EntityType.SSN, Float.MIN_VALUE, 0, 10)
        assertEquals(Float.MIN_VALUE, entity.confidence, 0.0f)
    }

    @Test
    fun `confidence of negative Float MAX_VALUE is stored`() {
        val entity = PIIEntity(EntityType.SSN, -Float.MAX_VALUE, 0, 10)
        assertEquals(-Float.MAX_VALUE, entity.confidence, 0.0f)
    }

    @Test
    fun `confidence of Float POSITIVE_INFINITY is stored`() {
        val entity = PIIEntity(EntityType.SSN, Float.POSITIVE_INFINITY, 0, 10)
        assertEquals(Float.POSITIVE_INFINITY, entity.confidence, 0.0f)
    }

    @Test
    fun `confidence of Float NEGATIVE_INFINITY is stored`() {
        val entity = PIIEntity(EntityType.SSN, Float.NEGATIVE_INFINITY, 0, 10)
        assertEquals(Float.NEGATIVE_INFINITY, entity.confidence, 0.0f)
    }

    @Test
    fun `confidence of Float NaN is stored`() {
        val entity = PIIEntity(EntityType.SSN, Float.NaN, 0, 10)
        assertTrue(entity.confidence.isNaN())
    }

    // ========================================================================
    // Section 3: Index validation
    // ========================================================================

    @Test
    fun `startIndex of 0 and endIndex of 10`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 10)
        assertEquals(0, entity.startIndex)
        assertEquals(10, entity.endIndex)
    }

    @Test
    fun `startIndex equals endIndex`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 5, 5)
        assertEquals(5, entity.startIndex)
        assertEquals(5, entity.endIndex)
    }

    @Test
    fun `large indices`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 10000, 20000)
        assertEquals(10000, entity.startIndex)
        assertEquals(20000, entity.endIndex)
    }

    @Test
    fun `startIndex greater than endIndex`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 20, 10)
        assertEquals(20, entity.startIndex)
        assertEquals(10, entity.endIndex)
    }

    @Test
    fun `negative startIndex`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, -1, 10)
        assertEquals(-1, entity.startIndex)
    }

    @Test
    fun `negative endIndex`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, -1)
        assertEquals(-1, entity.endIndex)
    }

    @Test
    fun `both indices negative`() {
        val entity = PIIEntity(EntityType.PHONE, 0.9f, -10, -5)
        assertEquals(-10, entity.startIndex)
        assertEquals(-5, entity.endIndex)
    }

    @Test
    fun `startIndex of Int MAX_VALUE`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, Int.MAX_VALUE, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, entity.startIndex)
    }

    @Test
    fun `endIndex of Int MAX_VALUE`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, entity.endIndex)
    }

    @Test
    fun `startIndex of Int MIN_VALUE`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, Int.MIN_VALUE, 0)
        assertEquals(Int.MIN_VALUE, entity.startIndex)
    }

    @Test
    fun `consecutive index range 0 to 1`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 1)
        assertEquals(0, entity.startIndex)
        assertEquals(1, entity.endIndex)
    }

    @Test
    fun `index range for single character detection`() {
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.9f, 42, 43, "X")
        assertEquals(42, entity.startIndex)
        assertEquals(43, entity.endIndex)
    }

    // ========================================================================
    // Section 4: Length computation
    // ========================================================================

    @Test
    fun `length for 0 to 10 is 10`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 10)
        assertEquals(10, entity.length)
    }

    @Test
    fun `length for 5 to 5 is 0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 5, 5)
        assertEquals(0, entity.length)
    }

    @Test
    fun `length for 0 to 1 is 1`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 1)
        assertEquals(1, entity.length)
    }

    @Test
    fun `length for 100 to 200 is 100`() {
        val entity = PIIEntity(EntityType.PHONE, 0.9f, 100, 200)
        assertEquals(100, entity.length)
    }

    @Test
    fun `length for 0 to 1000 is 1000`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.9f, 0, 1000)
        assertEquals(1000, entity.length)
    }

    @Test
    fun `length when startIndex greater than endIndex is negative`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 20, 10)
        assertEquals(-10, entity.length)
    }

    @Test
    fun `length for very large range`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 100000)
        assertEquals(100000, entity.length)
    }

    @Test
    fun `length for adjacent indices is 1`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.9f, 99, 100)
        assertEquals(1, entity.length)
    }

    // ========================================================================
    // Section 5: Empty and long rawText
    // ========================================================================

    @Test
    fun `empty rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 0, "")
        assertEquals("", entity.rawText)
    }

    @Test
    fun `single character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 1, "a")
        assertEquals("a", entity.rawText)
    }

    @Test
    fun `two character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 2, "ab")
        assertEquals("ab", entity.rawText)
    }

    @Test
    fun `three character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 3, "abc")
        assertEquals("abc", entity.rawText)
    }

    @Test
    fun `four character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 4, "abcd")
        assertEquals("abcd", entity.rawText)
    }

    @Test
    fun `five character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 5, "abcde")
        assertEquals("abcde", entity.rawText)
    }

    @Test
    fun `long rawText of 100 characters`() {
        val text = "a".repeat(100)
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, 100, text)
        assertEquals(text, entity.rawText)
    }

    @Test
    fun `long rawText of 1000 characters`() {
        val text = "b".repeat(1000)
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, 1000, text)
        assertEquals(text, entity.rawText)
    }

    @Test
    fun `long rawText of 10000 characters`() {
        val text = "c".repeat(10000)
        val entity = PIIEntity(EntityType.API_KEY, 0.9f, 0, 10000, text)
        assertEquals(text, entity.rawText)
    }

    @Test
    fun `rawText with Unicode characters`() {
        val text = "\u00e9\u00e8\u00ea\u00eb"
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.9f, 0, 4, text)
        assertEquals(text, entity.rawText)
    }

    @Test
    fun `rawText with Chinese characters`() {
        val text = "\u4e2d\u6587\u6d4b\u8bd5"
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.9f, 0, 4, text)
        assertEquals(text, entity.rawText)
    }

    @Test
    fun `rawText with emoji`() {
        val text = "\ud83d\ude00\ud83d\ude01\ud83d\ude02"
        val entity = PIIEntity(EntityType.UNKNOWN, 0.9f, 0, text.length, text)
        assertEquals(text, entity.rawText)
    }

    @Test
    fun `rawText with whitespace only`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.9f, 0, 5, "     ")
        assertEquals("     ", entity.rawText)
    }

    @Test
    fun `rawText with newlines`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.9f, 0, 10, "line1\nline2")
        assertEquals("line1\nline2", entity.rawText)
    }

    @Test
    fun `rawText with tabs`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.9f, 0, 10, "col1\tcol2")
        assertEquals("col1\tcol2", entity.rawText)
    }

    @Test
    fun `rawText with special characters`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.9f, 0, 20, "sk-!@#$%^&*()_+=[]")
        assertEquals("sk-!@#\$%^&*()_+=[]", entity.rawText)
    }

    @Test
    fun `rawText with null characters`() {
        val text = "test\u0000null"
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, text.length, text)
        assertEquals(text, entity.rawText)
    }

    // ========================================================================
    // Section 6: meetsThreshold
    // ========================================================================

    @Test
    fun `meetsThreshold returns true when confidence equals threshold`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 10)
        assertTrue(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `meetsThreshold returns true when confidence exceeds threshold`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.99f, 0, 10)
        assertTrue(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `meetsThreshold returns false when confidence below threshold`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.90f, 0, 10)
        assertFalse(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `meetsThreshold with threshold 0_0 always returns true for non-negative confidence`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.001f, 0, 10)
        assertTrue(entity.meetsThreshold(0.0f))
    }

    @Test
    fun `meetsThreshold with threshold 0_0 and confidence 0_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.0f, 0, 10)
        assertTrue(entity.meetsThreshold(0.0f))
    }

    @Test
    fun `meetsThreshold with threshold 1_0 and confidence 1_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 1.0f, 0, 10)
        assertTrue(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold with threshold 1_0 and confidence 0_999`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.999f, 0, 10)
        assertFalse(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold with threshold 0_5 and confidence 0_5`() {
        val entity = PIIEntity(EntityType.PHONE, 0.5f, 0, 10)
        assertTrue(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold with threshold 0_5 and confidence 0_49`() {
        val entity = PIIEntity(EntityType.PHONE, 0.49f, 0, 10)
        assertFalse(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold with threshold 0_5 and confidence 0_51`() {
        val entity = PIIEntity(EntityType.PHONE, 0.51f, 0, 10)
        assertTrue(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold with negative confidence and threshold 0_0`() {
        val entity = PIIEntity(EntityType.SSN, -0.1f, 0, 10)
        assertFalse(entity.meetsThreshold(0.0f))
    }

    @Test
    fun `meetsThreshold with negative confidence and negative threshold`() {
        val entity = PIIEntity(EntityType.SSN, -0.1f, 0, 10)
        assertTrue(entity.meetsThreshold(-0.5f))
    }

    @Test
    fun `meetsThreshold with confidence greater than 1 and threshold 1_0`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 1.5f, 0, 16)
        assertTrue(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold with very high threshold`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.99f, 0, 16)
        assertFalse(entity.meetsThreshold(0.999f))
    }

    @Test
    fun `meetsThreshold with very low threshold`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.01f, 0, 16)
        assertTrue(entity.meetsThreshold(0.001f))
    }

    @Test
    fun `meetsThreshold for all entity types at their default thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            val entityAtThreshold = PIIEntity(type, threshold, 0, 10)
            assertTrue(
                "$type at threshold $threshold should meet threshold",
                entityAtThreshold.meetsThreshold(threshold)
            )
        }
    }

    @Test
    fun `meetsThreshold for all entity types just below their default thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            val entityBelowThreshold = PIIEntity(type, threshold - 0.001f, 0, 10)
            assertFalse(
                "$type just below threshold should not meet threshold",
                entityBelowThreshold.meetsThreshold(threshold)
            )
        }
    }

    @Test
    fun `meetsThreshold for all entity types just above their default thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            val entityAboveThreshold = PIIEntity(type, threshold + 0.001f, 0, 10)
            assertTrue(
                "$type just above threshold should meet threshold",
                entityAboveThreshold.meetsThreshold(threshold)
            )
        }
    }

    @Test
    fun `meetsThreshold is consistent across multiple calls`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 10)
        repeat(100) {
            assertTrue(entity.meetsThreshold(0.95f))
        }
    }

    @Test
    fun `meetsThreshold with Float NaN confidence`() {
        val entity = PIIEntity(EntityType.EMAIL, Float.NaN, 0, 10)
        // NaN >= anything is false
        assertFalse(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold with Float NaN threshold`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.5f, 0, 10)
        // anything >= NaN is false
        assertFalse(entity.meetsThreshold(Float.NaN))
    }

    @Test
    fun `meetsThreshold with POSITIVE_INFINITY confidence`() {
        val entity = PIIEntity(EntityType.EMAIL, Float.POSITIVE_INFINITY, 0, 10)
        assertTrue(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold with NEGATIVE_INFINITY confidence`() {
        val entity = PIIEntity(EntityType.EMAIL, Float.NEGATIVE_INFINITY, 0, 10)
        assertFalse(entity.meetsThreshold(0.0f))
    }

    // ========================================================================
    // Section 7: Severity computation
    // ========================================================================

    @Test
    fun `CREDIT_CARD entity has CRITICAL severity`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `SSN entity has CRITICAL severity`() {
        val entity = PIIEntity(EntityType.SSN, 0.95f, 0, 11)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `PASSWORD entity has CRITICAL severity`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.85f, 0, 8)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `API_KEY entity has CRITICAL severity`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.90f, 0, 32)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `EMAIL entity has HIGH severity`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 20)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `PHONE entity has HIGH severity`() {
        val entity = PIIEntity(EntityType.PHONE, 0.90f, 0, 14)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `MEDICAL_ID entity has HIGH severity`() {
        val entity = PIIEntity(EntityType.MEDICAL_ID, 0.88f, 0, 10)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `PERSON_NAME entity has MEDIUM severity`() {
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `ADDRESS entity has MEDIUM severity`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.82f, 0, 30)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `DATE_OF_BIRTH entity has MEDIUM severity`() {
        val entity = PIIEntity(EntityType.DATE_OF_BIRTH, 0.84f, 0, 10)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `UNKNOWN entity has MEDIUM severity`() {
        val entity = PIIEntity(EntityType.UNKNOWN, 0.91f, 0, 5)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `severity is derived from entityType not confidence`() {
        // Even with low confidence, severity comes from type
        val lowConfidence = PIIEntity(EntityType.CREDIT_CARD, 0.01f, 0, 16)
        assertEquals(Severity.CRITICAL, lowConfidence.severity)

        val highConfidence = PIIEntity(EntityType.PERSON_NAME, 0.99f, 0, 8)
        assertEquals(Severity.MEDIUM, highConfidence.severity)
    }

    @Test
    fun `severity does not change when confidence changes via copy`() {
        val original = PIIEntity(EntityType.SSN, 0.95f, 0, 11)
        assertEquals(Severity.CRITICAL, original.severity)
        val lowConf = original.copy(confidence = 0.01f)
        assertEquals(Severity.CRITICAL, lowConf.severity)
    }

    @Test
    fun `all entity types map to a valid severity`() {
        for (type in EntityType.entries) {
            val entity = PIIEntity(type, 0.9f, 0, 10)
            assertNotNull(entity.severity)
            assertTrue(
                "Severity should be one of CRITICAL, HIGH, MEDIUM",
                entity.severity in listOf(Severity.CRITICAL, Severity.HIGH, Severity.MEDIUM)
            )
        }
    }

    @Test
    fun `severity is consistent across multiple accesses`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 20)
        repeat(100) {
            assertEquals(Severity.HIGH, entity.severity)
        }
    }

    // ========================================================================
    // Section 8: maskedText
    // ========================================================================

    @Test
    fun `maskedText for empty rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 0, "")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `maskedText for 1 character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 1, "a")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `maskedText for 2 character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 2, "ab")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `maskedText for 3 character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 3, "abc")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `maskedText for 4 character rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 4, "abcd")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `maskedText for 5 character rawText shows last 4`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 5, "abcde")
        assertEquals("*bcde", entity.maskedText)
    }

    @Test
    fun `maskedText for 6 character rawText shows last 4`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 6, "abcdef")
        assertEquals("**cdef", entity.maskedText)
    }

    @Test
    fun `maskedText for 8 character rawText shows last 4`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, 8, "p@ssw0rd")
        assertEquals("****w0rd", entity.maskedText)
    }

    @Test
    fun `maskedText for 10 character rawText shows last 4`() {
        val entity = PIIEntity(EntityType.PHONE, 0.9f, 0, 10, "1234567890")
        assertEquals("******7890", entity.maskedText)
    }

    @Test
    fun `maskedText for 16 character credit card shows last 4`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        assertEquals("************1111", entity.maskedText)
    }

    @Test
    fun `maskedText for SSN shows last 4`() {
        val entity = PIIEntity(EntityType.SSN, 0.93f, 0, 9, "123456789")
        assertEquals("*****6789", entity.maskedText)
    }

    @Test
    fun `maskedText for email shows last 4`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 16, "user@example.com")
        assertEquals("************e.com", entity.maskedText)
    }

    @Test
    fun `maskedText for 20 character rawText`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.9f, 0, 20, "12345678901234567890")
        assertEquals("****************7890", entity.maskedText)
    }

    @Test
    fun `maskedText for 100 character rawText`() {
        val text = "a".repeat(96) + "bcde"
        val entity = PIIEntity(EntityType.API_KEY, 0.9f, 0, 100, text)
        assertEquals("*".repeat(96) + "bcde", entity.maskedText)
    }

    @Test
    fun `maskedText preserves last 4 Unicode characters`() {
        val text = "test\u00e9\u00e8\u00ea\u00eb"
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.9f, 0, text.length, text)
        val masked = entity.maskedText
        assertTrue(masked.endsWith("\u00e9\u00e8\u00ea\u00eb"))
    }

    @Test
    fun `maskedText is consistent across multiple accesses`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 16, "user@example.com")
        val first = entity.maskedText
        val second = entity.maskedText
        val third = entity.maskedText
        assertEquals(first, second)
        assertEquals(second, third)
    }

    @Test
    fun `maskedText for rawText of all spaces`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.9f, 0, 8, "        ")
        val masked = entity.maskedText
        assertEquals("****    ", masked)
    }

    @Test
    fun `maskedText for rawText of all asterisks`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, 8, "********")
        assertEquals("********", entity.maskedText)
    }

    // ========================================================================
    // Section 9: equals and hashCode (data class)
    // ========================================================================

    @Test
    fun `equal entities have same hashCode`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `equal entities are equal`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        assertEquals(a, b)
    }

    @Test
    fun `different entityType makes entities unequal`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val b = PIIEntity(EntityType.PHONE, 0.95f, 0, 16, "test")
        assertNotEquals(a, b)
    }

    @Test
    fun `different confidence makes entities unequal`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val b = PIIEntity(EntityType.EMAIL, 0.90f, 0, 16, "test")
        assertNotEquals(a, b)
    }

    @Test
    fun `different startIndex makes entities unequal`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 1, 16, "test")
        assertNotEquals(a, b)
    }

    @Test
    fun `different endIndex makes entities unequal`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 17, "test")
        assertNotEquals(a, b)
    }

    @Test
    fun `different rawText makes entities unequal`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "text1")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "text2")
        assertNotEquals(a, b)
    }

    @Test
    fun `entity with default rawText equals entity with explicit empty rawText`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "")
        assertEquals(a, b)
    }

    @Test
    fun `entity is not equal to null`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        assertNotEquals(entity, null)
    }

    @Test
    fun `entity is equal to itself`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        assertEquals(entity, entity)
    }

    @Test
    fun `hashCode is consistent across multiple calls`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val hash1 = entity.hashCode()
        val hash2 = entity.hashCode()
        val hash3 = entity.hashCode()
        assertEquals(hash1, hash2)
        assertEquals(hash2, hash3)
    }

    @Test
    fun `different entities typically have different hashCodes`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "email@test.com")
        val b = PIIEntity(EntityType.SSN, 0.93f, 10, 21, "123-45-6789")
        // Not guaranteed but very likely
        assertNotEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `equality is symmetric`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        assertEquals(a, b)
        assertEquals(b, a)
    }

    @Test
    fun `equality is transitive`() {
        val a = PIIEntity(EntityType.PHONE, 0.90f, 5, 15, "5551234567")
        val b = PIIEntity(EntityType.PHONE, 0.90f, 5, 15, "5551234567")
        val c = PIIEntity(EntityType.PHONE, 0.90f, 5, 15, "5551234567")
        assertEquals(a, b)
        assertEquals(b, c)
        assertEquals(a, c)
    }

    @Test
    fun `entities in a set deduplicate correctly`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val set = setOf(a, b)
        assertEquals(1, set.size)
    }

    @Test
    fun `entities in a set keep distinct entries`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test1")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test2")
        val set = setOf(a, b)
        assertEquals(2, set.size)
    }

    @Test
    fun `entity can be used as map key`() {
        val entity = PIIEntity(EntityType.SSN, 0.93f, 0, 11, "123-45-6789")
        val map = mapOf(entity to "found")
        assertEquals("found", map[entity])
    }

    @Test
    fun `equal entity retrieves value from map`() {
        val key = PIIEntity(EntityType.SSN, 0.93f, 0, 11, "123-45-6789")
        val lookup = PIIEntity(EntityType.SSN, 0.93f, 0, 11, "123-45-6789")
        val map = mapOf(key to "value")
        assertEquals("value", map[lookup])
    }

    // ========================================================================
    // Section 10: toString
    // ========================================================================

    @Test
    fun `toString contains entity type`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        assertTrue(entity.toString().contains("EMAIL"))
    }

    @Test
    fun `toString contains confidence`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        assertTrue(entity.toString().contains("0.95"))
    }

    @Test
    fun `toString contains startIndex`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 42, 58, "test")
        assertTrue(entity.toString().contains("42"))
    }

    @Test
    fun `toString contains endIndex`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 58, "test")
        assertTrue(entity.toString().contains("58"))
    }

    @Test
    fun `toString contains rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        assertTrue(entity.toString().contains("user@example.com"))
    }

    @Test
    fun `toString contains PIIEntity class name`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        assertTrue(entity.toString().contains("PIIEntity"))
    }

    @Test
    fun `toString for each entity type`() {
        for (type in EntityType.entries) {
            val entity = PIIEntity(type, 0.9f, 0, 10, "test")
            val str = entity.toString()
            assertTrue("toString should contain $type", str.contains(type.name))
        }
    }

    @Test
    fun `toString is non-empty`() {
        val entity = PIIEntity(EntityType.UNKNOWN, 0.5f, 0, 5)
        assertTrue(entity.toString().isNotEmpty())
    }

    @Test
    fun `toString is consistent across multiple calls`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val s1 = entity.toString()
        val s2 = entity.toString()
        assertEquals(s1, s2)
    }

    // ========================================================================
    // Section 11: copy (data class)
    // ========================================================================

    @Test
    fun `copy with no changes creates equal entity`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val copied = original.copy()
        assertEquals(original, copied)
    }

    @Test
    fun `copy with different entityType`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val copied = original.copy(entityType = EntityType.PHONE)
        assertEquals(EntityType.PHONE, copied.entityType)
        assertEquals(original.confidence, copied.confidence, 0.001f)
    }

    @Test
    fun `copy with different confidence`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val copied = original.copy(confidence = 0.50f)
        assertEquals(0.50f, copied.confidence, 0.001f)
        assertEquals(original.entityType, copied.entityType)
    }

    @Test
    fun `copy with different startIndex`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val copied = original.copy(startIndex = 100)
        assertEquals(100, copied.startIndex)
        assertEquals(original.endIndex, copied.endIndex)
    }

    @Test
    fun `copy with different endIndex`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val copied = original.copy(endIndex = 200)
        assertEquals(200, copied.endIndex)
        assertEquals(original.startIndex, copied.startIndex)
    }

    @Test
    fun `copy with different rawText`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "original")
        val copied = original.copy(rawText = "modified")
        assertEquals("modified", copied.rawText)
        assertEquals("original", original.rawText)
    }

    @Test
    fun `copy with all different values`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        val copied = original.copy(
            entityType = EntityType.SSN,
            confidence = 0.50f,
            startIndex = 10,
            endIndex = 21,
            rawText = "123-45-6789"
        )
        assertEquals(EntityType.SSN, copied.entityType)
        assertEquals(0.50f, copied.confidence, 0.001f)
        assertEquals(10, copied.startIndex)
        assertEquals(21, copied.endIndex)
        assertEquals("123-45-6789", copied.rawText)
    }

    @Test
    fun `copy does not modify original`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        original.copy(entityType = EntityType.SSN, confidence = 0.10f)
        assertEquals(EntityType.EMAIL, original.entityType)
        assertEquals(0.95f, original.confidence, 0.001f)
    }

    @Test
    fun `copy changes severity when entityType changes`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16) // HIGH
        val copied = original.copy(entityType = EntityType.CREDIT_CARD) // CRITICAL
        assertEquals(Severity.HIGH, original.severity)
        assertEquals(Severity.CRITICAL, copied.severity)
    }

    @Test
    fun `copy changes severity from CRITICAL to MEDIUM`() {
        val original = PIIEntity(EntityType.SSN, 0.95f, 0, 11)
        assertEquals(Severity.CRITICAL, original.severity)
        val copied = original.copy(entityType = EntityType.PERSON_NAME)
        assertEquals(Severity.MEDIUM, copied.severity)
    }

    @Test
    fun `copy changes length when indices change`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        assertEquals(16, original.length)
        val copied = original.copy(startIndex = 0, endIndex = 32)
        assertEquals(32, copied.length)
    }

    @Test
    fun `copy preserves maskedText logic`() {
        val original = PIIEntity(EntityType.EMAIL, 0.9f, 0, 16, "user@example.com")
        val copied = original.copy(rawText = "newtext123")
        assertEquals("******3123", copied.maskedText)
    }

    @Test
    fun `chained copy operations`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
            .copy(confidence = 0.80f)
            .copy(entityType = EntityType.PHONE)
            .copy(rawText = "5551234567")
        assertEquals(EntityType.PHONE, entity.entityType)
        assertEquals(0.80f, entity.confidence, 0.001f)
        assertEquals("5551234567", entity.rawText)
    }

    // ========================================================================
    // Section 12: All EntityType x Severity combinations
    // ========================================================================

    @Test
    fun `CREDIT_CARD x CRITICAL combination`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16)
        assertEquals(EntityType.CREDIT_CARD, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `SSN x CRITICAL combination`() {
        val entity = PIIEntity(EntityType.SSN, 0.95f, 0, 11)
        assertEquals(EntityType.SSN, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `PASSWORD x CRITICAL combination`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.85f, 0, 8)
        assertEquals(EntityType.PASSWORD, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `API_KEY x CRITICAL combination`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.88f, 0, 32)
        assertEquals(EntityType.API_KEY, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
    }

    @Test
    fun `EMAIL x HIGH combination`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 20)
        assertEquals(EntityType.EMAIL, entity.entityType)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `PHONE x HIGH combination`() {
        val entity = PIIEntity(EntityType.PHONE, 0.90f, 0, 14)
        assertEquals(EntityType.PHONE, entity.entityType)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `MEDICAL_ID x HIGH combination`() {
        val entity = PIIEntity(EntityType.MEDICAL_ID, 0.87f, 0, 10)
        assertEquals(EntityType.MEDICAL_ID, entity.entityType)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `PERSON_NAME x MEDIUM combination`() {
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8)
        assertEquals(EntityType.PERSON_NAME, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `ADDRESS x MEDIUM combination`() {
        val entity = PIIEntity(EntityType.ADDRESS, 0.82f, 0, 30)
        assertEquals(EntityType.ADDRESS, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `DATE_OF_BIRTH x MEDIUM combination`() {
        val entity = PIIEntity(EntityType.DATE_OF_BIRTH, 0.84f, 0, 10)
        assertEquals(EntityType.DATE_OF_BIRTH, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `UNKNOWN x MEDIUM combination`() {
        val entity = PIIEntity(EntityType.UNKNOWN, 0.91f, 0, 5)
        assertEquals(EntityType.UNKNOWN, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `count of CRITICAL entity types is 4`() {
        val criticalTypes = EntityType.entries.filter { it.severity == Severity.CRITICAL }
        assertEquals(4, criticalTypes.size)
    }

    @Test
    fun `count of HIGH entity types is 3`() {
        val highTypes = EntityType.entries.filter { it.severity == Severity.HIGH }
        assertEquals(3, highTypes.size)
    }

    @Test
    fun `count of MEDIUM entity types is 4`() {
        val mediumTypes = EntityType.entries.filter { it.severity == Severity.MEDIUM }
        assertEquals(4, mediumTypes.size)
    }

    @Test
    fun `total entity type to severity mappings equal total entity types`() {
        val critical = EntityType.entries.count { it.severity == Severity.CRITICAL }
        val high = EntityType.entries.count { it.severity == Severity.HIGH }
        val medium = EntityType.entries.count { it.severity == Severity.MEDIUM }
        assertEquals(EntityType.entries.size, critical + high + medium)
    }

    // ========================================================================
    // Section 13: PIIAnalysisResult integration
    // ========================================================================

    @Test
    fun `PIIAnalysisResult EMPTY has no entities`() {
        assertFalse(PIIAnalysisResult.EMPTY.hasSensitiveData())
    }

    @Test
    fun `PIIAnalysisResult EMPTY has zero entities`() {
        assertEquals(0, PIIAnalysisResult.EMPTY.entityCount)
    }

    @Test
    fun `PIIAnalysisResult EMPTY has null highestSeverity`() {
        assertNull(PIIAnalysisResult.EMPTY.highestSeverity)
    }

    @Test
    fun `PIIAnalysisResult EMPTY has empty criticalEntities`() {
        assertTrue(PIIAnalysisResult.EMPTY.criticalEntities.isEmpty())
    }

    @Test
    fun `PIIAnalysisResult EMPTY has empty highEntities`() {
        assertTrue(PIIAnalysisResult.EMPTY.highEntities.isEmpty())
    }

    @Test
    fun `PIIAnalysisResult EMPTY has empty mediumEntities`() {
        assertTrue(PIIAnalysisResult.EMPTY.mediumEntities.isEmpty())
    }

    @Test
    fun `PIIAnalysisResult with single CRITICAL entity`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        val result = PIIAnalysisResult(listOf(entity), 50L, 100)
        assertTrue(result.hasSensitiveData())
        assertEquals(1, result.entityCount)
        assertEquals(Severity.CRITICAL, result.highestSeverity)
        assertEquals(1, result.criticalEntities.size)
        assertTrue(result.highEntities.isEmpty())
        assertTrue(result.mediumEntities.isEmpty())
    }

    @Test
    fun `PIIAnalysisResult with single HIGH entity`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.96f, 0, 20, "user@example.com")
        val result = PIIAnalysisResult(listOf(entity), 30L, 50)
        assertTrue(result.hasSensitiveData())
        assertEquals(Severity.HIGH, result.highestSeverity)
        assertTrue(result.criticalEntities.isEmpty())
        assertEquals(1, result.highEntities.size)
    }

    @Test
    fun `PIIAnalysisResult with single MEDIUM entity`() {
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8, "John Doe")
        val result = PIIAnalysisResult(listOf(entity), 20L, 30)
        assertTrue(result.hasSensitiveData())
        assertEquals(Severity.MEDIUM, result.highestSeverity)
        assertEquals(1, result.mediumEntities.size)
    }

    @Test
    fun `PIIAnalysisResult with mixed severity entities`() {
        val entities = listOf(
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111"),
            PIIEntity(EntityType.EMAIL, 0.96f, 20, 40, "user@example.com"),
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 45, 53, "John Doe")
        )
        val result = PIIAnalysisResult(entities, 100L, 200)
        assertEquals(3, result.entityCount)
        assertEquals(Severity.CRITICAL, result.highestSeverity)
        assertEquals(1, result.criticalEntities.size)
        assertEquals(1, result.highEntities.size)
        assertEquals(1, result.mediumEntities.size)
    }

    @Test
    fun `PIIAnalysisResult with multiple CRITICAL entities`() {
        val entities = listOf(
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16),
            PIIEntity(EntityType.SSN, 0.93f, 20, 31),
            PIIEntity(EntityType.PASSWORD, 0.85f, 35, 43)
        )
        val result = PIIAnalysisResult(entities, 80L, 100)
        assertEquals(3, result.criticalEntities.size)
        assertEquals(Severity.CRITICAL, result.highestSeverity)
    }

    @Test
    fun `PIIAnalysisResult entitiesBySeverity groups correctly`() {
        val entities = listOf(
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16),
            PIIEntity(EntityType.EMAIL, 0.96f, 20, 40),
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 45, 53),
            PIIEntity(EntityType.SSN, 0.93f, 60, 71),
            PIIEntity(EntityType.PHONE, 0.90f, 75, 85)
        )
        val result = PIIAnalysisResult(entities, 120L, 300)
        val bySeverity = result.entitiesBySeverity()
        assertEquals(2, bySeverity[Severity.CRITICAL]?.size)
        assertEquals(2, bySeverity[Severity.HIGH]?.size)
        assertEquals(1, bySeverity[Severity.MEDIUM]?.size)
    }

    @Test
    fun `PIIAnalysisResult inferenceTimeMs is stored`() {
        val result = PIIAnalysisResult(emptyList(), 42L, 100)
        assertEquals(42L, result.inferenceTimeMs)
    }

    @Test
    fun `PIIAnalysisResult inputLength is stored`() {
        val result = PIIAnalysisResult(emptyList(), 0L, 256)
        assertEquals(256, result.inputLength)
    }

    @Test
    fun `PIIAnalysisResult with zero inference time`() {
        val result = PIIAnalysisResult(emptyList(), 0L, 0)
        assertEquals(0L, result.inferenceTimeMs)
    }

    @Test
    fun `PIIAnalysisResult with large inference time`() {
        val result = PIIAnalysisResult(emptyList(), 999999L, 0)
        assertEquals(999999L, result.inferenceTimeMs)
    }

    @Test
    fun `PIIAnalysisResult default values`() {
        val result = PIIAnalysisResult()
        assertTrue(result.entities.isEmpty())
        assertEquals(0L, result.inferenceTimeMs)
        assertEquals(0, result.inputLength)
    }

    // ========================================================================
    // Section 14: Comprehensive maskedText edge cases
    // ========================================================================

    @Test
    fun `maskedText for rawText exactly 4 chars returns all stars`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, 4, "1234")
        assertEquals("****", entity.maskedText)
    }

    @Test
    fun `maskedText for rawText exactly 5 chars shows 1 star plus last 4`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, 5, "12345")
        assertEquals("*2345", entity.maskedText)
    }

    @Test
    fun `maskedText for rawText of 7 chars`() {
        val entity = PIIEntity(EntityType.PHONE, 0.9f, 0, 7, "1234567")
        assertEquals("***4567", entity.maskedText)
    }

    @Test
    fun `maskedText with rawText that has repeating characters`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, 8, "aaaaaaaa")
        assertEquals("****aaaa", entity.maskedText)
    }

    @Test
    fun `maskedText with rawText that has mixed case`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.9f, 0, 8, "AbCdEfGh")
        assertEquals("****EfGh", entity.maskedText)
    }

    @Test
    fun `maskedText with rawText containing numbers only`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.9f, 0, 12, "123456789012")
        assertEquals("********9012", entity.maskedText)
    }

    @Test
    fun `maskedText with default empty rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, 10)
        assertEquals("****", entity.maskedText) // empty string has length <= 4
    }

    @Test
    fun `maskedText star count equals rawText length minus 4 for long strings`() {
        for (len in 5..20) {
            val text = "x".repeat(len)
            val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, len, text)
            val starCount = entity.maskedText.count { it == '*' }
            assertEquals("For length $len", len - 4, starCount)
        }
    }

    @Test
    fun `maskedText last 4 chars match rawText last 4 chars for long strings`() {
        for (len in 5..20) {
            val text = (1..len).map { ('a' + (it % 26)).toChar() }.joinToString("")
            val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, len, text)
            val last4 = entity.maskedText.takeLast(4)
            assertEquals(text.takeLast(4), last4)
        }
    }

    // ========================================================================
    // Section 15: Entity with various realistic data
    // ========================================================================

    @Test
    fun `realistic credit card entity`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.97f, 15, 31, "4532015112830366")
        assertEquals(EntityType.CREDIT_CARD, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
        assertEquals(16, entity.length)
        assertTrue(entity.meetsThreshold(0.90f))
        assertEquals("************0366", entity.maskedText)
    }

    @Test
    fun `realistic SSN entity`() {
        val entity = PIIEntity(EntityType.SSN, 0.94f, 5, 16, "078-05-1120")
        assertEquals(EntityType.SSN, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
        assertEquals(11, entity.length)
        assertTrue(entity.meetsThreshold(0.92f))
        assertEquals("*******1120", entity.maskedText)
    }

    @Test
    fun `realistic email entity`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.98f, 0, 23, "john.doe@company.co.uk")
        assertEquals(EntityType.EMAIL, entity.entityType)
        assertEquals(Severity.HIGH, entity.severity)
        assertTrue(entity.meetsThreshold(0.95f))
    }

    @Test
    fun `realistic phone entity with country code`() {
        val entity = PIIEntity(EntityType.PHONE, 0.91f, 10, 25, "+1 (555) 123-4567")
        assertEquals(EntityType.PHONE, entity.entityType)
        assertEquals(Severity.HIGH, entity.severity)
        assertTrue(entity.meetsThreshold(0.88f))
    }

    @Test
    fun `realistic person name entity`() {
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.82f, 0, 15, "Dr. John Smith")
        assertEquals(EntityType.PERSON_NAME, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
        assertTrue(entity.meetsThreshold(0.75f))
    }

    @Test
    fun `realistic address entity`() {
        val entity = PIIEntity(
            EntityType.ADDRESS, 0.83f, 0, 40,
            "1234 Elm Street, Suite 567, New York, NY"
        )
        assertEquals(EntityType.ADDRESS, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
        assertTrue(entity.meetsThreshold(0.80f))
    }

    @Test
    fun `realistic date of birth entity`() {
        val entity = PIIEntity(EntityType.DATE_OF_BIRTH, 0.86f, 5, 15, "01/15/1990")
        assertEquals(EntityType.DATE_OF_BIRTH, entity.entityType)
        assertEquals(Severity.MEDIUM, entity.severity)
        assertTrue(entity.meetsThreshold(0.82f))
    }

    @Test
    fun `realistic API key entity`() {
        val entity = PIIEntity(
            EntityType.API_KEY, 0.92f, 0, 51,
            "sk-proj-1234567890abcdef1234567890abcdef12345678"
        )
        assertEquals(EntityType.API_KEY, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
        assertTrue(entity.meetsThreshold(0.85f))
    }

    @Test
    fun `realistic password entity`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.88f, 10, 30, "P@ssw0rd!2024#Secure")
        assertEquals(EntityType.PASSWORD, entity.entityType)
        assertEquals(Severity.CRITICAL, entity.severity)
        assertTrue(entity.meetsThreshold(0.80f))
    }

    @Test
    fun `realistic medical ID entity`() {
        val entity = PIIEntity(EntityType.MEDICAL_ID, 0.89f, 0, 12, "MRN-12345678")
        assertEquals(EntityType.MEDICAL_ID, entity.entityType)
        assertEquals(Severity.HIGH, entity.severity)
        assertTrue(entity.meetsThreshold(0.85f))
    }

    // ========================================================================
    // Section 16: Collections of PIIEntity
    // ========================================================================

    @Test
    fun `list of entities maintains order`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.95f, 0, 10),
            PIIEntity(EntityType.PHONE, 0.90f, 10, 20),
            PIIEntity(EntityType.SSN, 0.93f, 20, 30)
        )
        assertEquals(EntityType.EMAIL, entities[0].entityType)
        assertEquals(EntityType.PHONE, entities[1].entityType)
        assertEquals(EntityType.SSN, entities[2].entityType)
    }

    @Test
    fun `filtering entities by severity`() {
        val entities = listOf(
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16),
            PIIEntity(EntityType.EMAIL, 0.96f, 20, 40),
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 45, 53),
            PIIEntity(EntityType.SSN, 0.93f, 60, 71)
        )
        val critical = entities.filter { it.severity == Severity.CRITICAL }
        assertEquals(2, critical.size)
        assertTrue(critical.all { it.severity == Severity.CRITICAL })
    }

    @Test
    fun `sorting entities by confidence descending`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.80f, 0, 10),
            PIIEntity(EntityType.PHONE, 0.95f, 10, 20),
            PIIEntity(EntityType.SSN, 0.90f, 20, 30)
        )
        val sorted = entities.sortedByDescending { it.confidence }
        assertEquals(0.95f, sorted[0].confidence, 0.001f)
        assertEquals(0.90f, sorted[1].confidence, 0.001f)
        assertEquals(0.80f, sorted[2].confidence, 0.001f)
    }

    @Test
    fun `sorting entities by severity ordinal`() {
        val entities = listOf(
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8),
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 10, 26),
            PIIEntity(EntityType.EMAIL, 0.96f, 30, 50)
        )
        val sorted = entities.sortedBy { it.severity.ordinal }
        assertEquals(Severity.CRITICAL, sorted[0].severity)
        assertEquals(Severity.HIGH, sorted[1].severity)
        assertEquals(Severity.MEDIUM, sorted[2].severity)
    }

    @Test
    fun `grouping entities by entity type`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.95f, 0, 10),
            PIIEntity(EntityType.EMAIL, 0.90f, 20, 30),
            PIIEntity(EntityType.PHONE, 0.88f, 35, 45),
            PIIEntity(EntityType.SSN, 0.93f, 50, 61)
        )
        val grouped = entities.groupBy { it.entityType }
        assertEquals(2, grouped[EntityType.EMAIL]?.size)
        assertEquals(1, grouped[EntityType.PHONE]?.size)
        assertEquals(1, grouped[EntityType.SSN]?.size)
    }

    @Test
    fun `filtering entities that meet their default thresholds`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.96f, 0, 10), // meets 0.95
            PIIEntity(EntityType.EMAIL, 0.90f, 20, 30), // does not meet 0.95
            PIIEntity(EntityType.PHONE, 0.89f, 35, 45), // meets 0.88
            PIIEntity(EntityType.SSN, 0.91f, 50, 61) // does not meet 0.92
        )
        ConfidenceThresholds.resetToDefaults()
        val passing = entities.filter {
            it.meetsThreshold(ConfidenceThresholds.getThreshold(it.entityType))
        }
        assertEquals(2, passing.size)
        assertEquals(EntityType.EMAIL, passing[0].entityType)
        assertEquals(EntityType.PHONE, passing[1].entityType)
    }

    @Test
    fun `maxByOrNull severity on empty list returns null`() {
        val entities = emptyList<PIIEntity>()
        val highest = entities.maxByOrNull { it.severity.ordinal }
        assertNull(highest)
    }

    @Test
    fun `maxByOrNull severity on list with one element`() {
        val entities = listOf(PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8))
        val highest = entities.maxByOrNull { it.severity.ordinal }
        assertEquals(EntityType.PERSON_NAME, highest?.entityType)
    }

    @Test
    fun `count entities by severity`() {
        val entities = EntityType.entries.map { PIIEntity(it, 0.90f, 0, 10) }
        val criticalCount = entities.count { it.severity == Severity.CRITICAL }
        val highCount = entities.count { it.severity == Severity.HIGH }
        val mediumCount = entities.count { it.severity == Severity.MEDIUM }
        assertEquals(4, criticalCount)
        assertEquals(3, highCount)
        assertEquals(4, mediumCount)
    }

    // ========================================================================
    // Section 17: Destructuring
    // ========================================================================

    @Test
    fun `destructuring declaration works`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 10, 30, "user@example.com")
        val (type, confidence, start, end, text) = entity
        assertEquals(EntityType.EMAIL, type)
        assertEquals(0.95f, confidence, 0.001f)
        assertEquals(10, start)
        assertEquals(30, end)
        assertEquals("user@example.com", text)
    }

    @Test
    fun `destructuring with default rawText`() {
        val entity = PIIEntity(EntityType.SSN, 0.93f, 0, 11)
        val (type, confidence, start, end, text) = entity
        assertEquals(EntityType.SSN, type)
        assertEquals("", text)
    }

    @Test
    fun `destructuring preserves all values`() {
        for (type in EntityType.entries) {
            val entity = PIIEntity(type, 0.88f, 42, 99, "data-$type")
            val (t, c, s, e, r) = entity
            assertEquals(type, t)
            assertEquals(0.88f, c, 0.001f)
            assertEquals(42, s)
            assertEquals(99, e)
            assertEquals("data-$type", r)
        }
    }

    // ========================================================================
    // Section 18: Edge cases with data class identity
    // ========================================================================

    @Test
    fun `two entities with same data are structurally equal`() {
        val a = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        val b = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        assertTrue(a == b)
    }

    @Test
    fun `two entities with same data are not referentially identical unless same object`() {
        val a = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        val b = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        assertTrue(a == b) // structural equality
        assertFalse(a === b) // referential identity (different objects)
    }

    @Test
    fun `entity in a list is found by structural equality`() {
        val list = listOf(
            PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test@test.com"),
            PIIEntity(EntityType.PHONE, 0.90f, 20, 30, "5551234567")
        )
        val search = PIIEntity(EntityType.PHONE, 0.90f, 20, 30, "5551234567")
        assertTrue(list.contains(search))
    }

    @Test
    fun `entity can be used in when expression`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        val result = when (entity.severity) {
            Severity.CRITICAL -> "critical"
            Severity.HIGH -> "high"
            Severity.MEDIUM -> "medium"
        }
        assertEquals("high", result)
    }

    @Test
    fun `entity properties are val and immutable`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test")
        // Properties are val, so they don't change
        assertEquals(EntityType.EMAIL, entity.entityType)
        assertEquals(0.95f, entity.confidence, 0.001f)
        assertEquals(0, entity.startIndex)
        assertEquals(16, entity.endIndex)
        assertEquals("test", entity.rawText)
        // Access again to verify immutability
        assertEquals(EntityType.EMAIL, entity.entityType)
        assertEquals(0.95f, entity.confidence, 0.001f)
    }

    // ========================================================================
    // Section 19: Confidence comparisons across entities
    // ========================================================================

    @Test
    fun `comparing confidence of two entities`() {
        val high = PIIEntity(EntityType.EMAIL, 0.99f, 0, 10)
        val low = PIIEntity(EntityType.EMAIL, 0.50f, 0, 10)
        assertTrue(high.confidence > low.confidence)
    }

    @Test
    fun `sorting list of entities by confidence`() {
        val entities = (1..10).map {
            PIIEntity(EntityType.UNKNOWN, it / 10.0f, 0, 10)
        }
        val sorted = entities.sortedBy { it.confidence }
        for (i in 0 until sorted.size - 1) {
            assertTrue(sorted[i].confidence <= sorted[i + 1].confidence)
        }
    }

    @Test
    fun `entity with highest confidence in list`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.80f, 0, 10),
            PIIEntity(EntityType.EMAIL, 0.99f, 0, 10),
            PIIEntity(EntityType.EMAIL, 0.85f, 0, 10)
        )
        val highest = entities.maxByOrNull { it.confidence }
        assertEquals(0.99f, highest?.confidence ?: 0f, 0.001f)
    }

    @Test
    fun `entity with lowest confidence in list`() {
        val entities = listOf(
            PIIEntity(EntityType.PHONE, 0.80f, 0, 10),
            PIIEntity(EntityType.PHONE, 0.50f, 0, 10),
            PIIEntity(EntityType.PHONE, 0.75f, 0, 10)
        )
        val lowest = entities.minByOrNull { it.confidence }
        assertEquals(0.50f, lowest?.confidence ?: 0f, 0.001f)
    }

    @Test
    fun `average confidence of entity list`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.80f, 0, 10),
            PIIEntity(EntityType.PHONE, 0.90f, 0, 10),
            PIIEntity(EntityType.SSN, 1.00f, 0, 10)
        )
        val avg = entities.map { it.confidence }.average().toFloat()
        assertEquals(0.90f, avg, 0.001f)
    }

    // ========================================================================
    // Section 20: Additional comprehensive edge cases
    // ========================================================================

    @Test
    fun `entity with all zeros`() {
        val entity = PIIEntity(EntityType.UNKNOWN, 0.0f, 0, 0, "")
        assertEquals(EntityType.UNKNOWN, entity.entityType)
        assertEquals(0.0f, entity.confidence, 0.0f)
        assertEquals(0, entity.startIndex)
        assertEquals(0, entity.endIndex)
        assertEquals("", entity.rawText)
        assertEquals(0, entity.length)
        assertEquals(Severity.MEDIUM, entity.severity)
    }

    @Test
    fun `entity with maximum realistic values`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 1.0f, 0, 10000, "x".repeat(10000))
        assertEquals(10000, entity.length)
        assertEquals(1.0f, entity.confidence, 0.0f)
        assertTrue(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `entity rawText does not affect severity`() {
        val a = PIIEntity(EntityType.EMAIL, 0.95f, 0, 10, "")
        val b = PIIEntity(EntityType.EMAIL, 0.95f, 0, 10, "very long raw text that is different")
        assertEquals(a.severity, b.severity)
    }

    @Test
    fun `entity indices do not affect severity`() {
        val a = PIIEntity(EntityType.SSN, 0.93f, 0, 11)
        val b = PIIEntity(EntityType.SSN, 0.93f, 1000, 1011)
        assertEquals(a.severity, b.severity)
    }

    @Test
    fun `entity confidence does not affect severity`() {
        val a = PIIEntity(EntityType.PASSWORD, 0.01f, 0, 8)
        val b = PIIEntity(EntityType.PASSWORD, 0.99f, 0, 8)
        assertEquals(a.severity, b.severity)
        assertEquals(Severity.CRITICAL, a.severity)
    }

    @Test
    fun `entity severity is a computed property with no backing field`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        // Access severity multiple times to ensure it's computed correctly each time
        assertEquals(Severity.HIGH, entity.severity)
        assertEquals(Severity.HIGH, entity.severity)
        assertEquals(Severity.HIGH, entity.severity)
    }

    @Test
    fun `entity length is a computed property`() {
        val entity = PIIEntity(EntityType.PHONE, 0.90f, 5, 15)
        assertEquals(10, entity.length)
        assertEquals(10, entity.length)
        assertEquals(10, entity.length)
    }

    @Test
    fun `entity maskedText is a computed property`() {
        val entity = PIIEntity(EntityType.SSN, 0.93f, 0, 11, "123-45-6789")
        val m1 = entity.maskedText
        val m2 = entity.maskedText
        assertEquals(m1, m2)
    }

    @Test
    fun `every entity type can be constructed and has valid severity`() {
        for (type in EntityType.entries) {
            val entity = PIIEntity(type, 0.90f, 0, 10, "test-data")
            assertNotNull(entity)
            assertNotNull(entity.severity)
            assertTrue(entity.length >= 0)
            assertTrue(entity.rawText.isNotEmpty())
        }
    }

    @Test
    fun `PIIAnalysisResult can hold 1000 entities`() {
        val entities = (0 until 1000).map {
            PIIEntity(EntityType.entries[it % EntityType.entries.size], 0.90f, it * 10, it * 10 + 10)
        }
        val result = PIIAnalysisResult(entities, 500L, 10000)
        assertEquals(1000, result.entityCount)
        assertTrue(result.hasSensitiveData())
    }

    @Test
    fun `PIIAnalysisResult entitiesBySeverity returns empty map for no entities`() {
        val result = PIIAnalysisResult()
        val grouped = result.entitiesBySeverity()
        assertTrue(grouped.isEmpty())
    }

    @Test
    fun `PIIAnalysisResult copy with different entities`() {
        val original = PIIAnalysisResult(
            listOf(PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)),
            50L,
            100
        )
        val modified = original.copy(entities = emptyList())
        assertTrue(original.hasSensitiveData())
        assertFalse(modified.hasSensitiveData())
    }

    @Test
    fun `PIIAnalysisResult equals and hashCode`() {
        val a = PIIAnalysisResult(
            listOf(PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)),
            50L,
            100
        )
        val b = PIIAnalysisResult(
            listOf(PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)),
            50L,
            100
        )
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `PIIAnalysisResult toString contains entities info`() {
        val result = PIIAnalysisResult(
            listOf(PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)),
            50L,
            100
        )
        val str = result.toString()
        assertTrue(str.contains("PIIAnalysisResult"))
    }

    @Test
    fun `PIIEntity with rawText matching index range`() {
        val text = "Hello World, this is a test of PII detection."
        val entity = PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 11, text.substring(0, 11))
        assertEquals("Hello World", entity.rawText)
        assertEquals(11, entity.length)
    }

    @Test
    fun `meetsThreshold is a pure function`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 10)
        // Calling meetsThreshold does not modify the entity
        entity.meetsThreshold(0.90f)
        entity.meetsThreshold(0.99f)
        entity.meetsThreshold(0.50f)
        assertEquals(0.95f, entity.confidence, 0.001f)
        assertEquals(EntityType.EMAIL, entity.entityType)
    }

    @Test
    fun `PIIEntity is a data class`() {
        // Verify data class features work
        val entity = PIIEntity(EntityType.SSN, 0.93f, 5, 16, "078-05-1120")

        // copy
        val copied = entity.copy()
        assertEquals(entity, copied)

        // toString
        assertTrue(entity.toString().isNotEmpty())

        // hashCode
        assertEquals(entity.hashCode(), copied.hashCode())

        // equals
        assertTrue(entity == copied)

        // destructuring
        val (type, conf, start, end, raw) = entity
        assertEquals(EntityType.SSN, type)
    }

    // ========================================================================
    // Section 21: Additional maskedText tests for completeness
    // ========================================================================

    @Test
    fun `maskedText for rawText of 9 characters`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, 9, "123456789")
        assertEquals("*****6789", entity.maskedText)
    }

    @Test
    fun `maskedText for rawText of 11 characters`() {
        val entity = PIIEntity(EntityType.SSN, 0.9f, 0, 11, "12345678901")
        assertEquals("*******8901", entity.maskedText)
    }

    @Test
    fun `maskedText for rawText of 15 characters`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.9f, 0, 15, "123456789012345")
        assertEquals("***********2345", entity.maskedText)
    }

    @Test
    fun `maskedText for rawText of 50 characters`() {
        val text = "a".repeat(46) + "bcde"
        val entity = PIIEntity(EntityType.API_KEY, 0.9f, 0, 50, text)
        assertEquals("*".repeat(46) + "bcde", entity.maskedText)
    }

    @Test
    fun `maskedText with numeric ending`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "5425233430109903")
        assertEquals("************9903", entity.maskedText)
    }

    @Test
    fun `maskedText with alphabetic ending`() {
        val entity = PIIEntity(EntityType.API_KEY, 0.88f, 0, 12, "sk-12345abcd")
        assertEquals("********abcd", entity.maskedText)
    }

    @Test
    fun `maskedText with mixed ending`() {
        val entity = PIIEntity(EntityType.PASSWORD, 0.85f, 0, 10, "P@ss1234ab")
        assertEquals("******34ab", entity.maskedText)
    }

    @Test
    fun `maskedText length matches rawText length for strings longer than 4`() {
        for (len in 5..30) {
            val text = "x".repeat(len)
            val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, len, text)
            assertEquals(len, entity.maskedText.length)
        }
    }

    @Test
    fun `maskedText length is 4 for strings of length 0 to 4`() {
        for (len in 0..4) {
            val text = "x".repeat(len)
            val entity = PIIEntity(EntityType.EMAIL, 0.9f, 0, len, text)
            assertEquals(4, entity.maskedText.length)
        }
    }

    // ========================================================================
    // Section 22: Additional confidence and meetsThreshold combinations
    // ========================================================================

    @Test
    fun `meetsThreshold truth table - confidence 0_0 threshold 0_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.0f, 0, 10)
        assertTrue(entity.meetsThreshold(0.0f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 0_0 threshold 0_5`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.0f, 0, 10)
        assertFalse(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 0_0 threshold 1_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.0f, 0, 10)
        assertFalse(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 0_5 threshold 0_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.5f, 0, 10)
        assertTrue(entity.meetsThreshold(0.0f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 0_5 threshold 0_5`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.5f, 0, 10)
        assertTrue(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 0_5 threshold 1_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.5f, 0, 10)
        assertFalse(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 1_0 threshold 0_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 1.0f, 0, 10)
        assertTrue(entity.meetsThreshold(0.0f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 1_0 threshold 0_5`() {
        val entity = PIIEntity(EntityType.EMAIL, 1.0f, 0, 10)
        assertTrue(entity.meetsThreshold(0.5f))
    }

    @Test
    fun `meetsThreshold truth table - confidence 1_0 threshold 1_0`() {
        val entity = PIIEntity(EntityType.EMAIL, 1.0f, 0, 10)
        assertTrue(entity.meetsThreshold(1.0f))
    }

    @Test
    fun `meetsThreshold with incremental confidence values at fixed threshold`() {
        val threshold = 0.50f
        for (i in 0..100) {
            val confidence = i / 100.0f
            val entity = PIIEntity(EntityType.SSN, confidence, 0, 10)
            if (confidence >= threshold) {
                assertTrue("Confidence $confidence should meet threshold $threshold",
                    entity.meetsThreshold(threshold))
            } else {
                assertFalse("Confidence $confidence should not meet threshold $threshold",
                    entity.meetsThreshold(threshold))
            }
        }
    }

    @Test
    fun `meetsThreshold with incremental threshold values at fixed confidence`() {
        val confidence = 0.75f
        val entity = PIIEntity(EntityType.PHONE, confidence, 0, 10)
        for (i in 0..100) {
            val threshold = i / 100.0f
            if (confidence >= threshold) {
                assertTrue("Threshold $threshold should be met by confidence $confidence",
                    entity.meetsThreshold(threshold))
            } else {
                assertFalse("Threshold $threshold should not be met by confidence $confidence",
                    entity.meetsThreshold(threshold))
            }
        }
    }

    // ========================================================================
    // Section 23: PIIAnalysisResult additional edge cases
    // ========================================================================

    @Test
    fun `PIIAnalysisResult with only CRITICAL entities`() {
        val entities = listOf(
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16),
            PIIEntity(EntityType.SSN, 0.93f, 20, 31),
            PIIEntity(EntityType.PASSWORD, 0.85f, 35, 43),
            PIIEntity(EntityType.API_KEY, 0.90f, 50, 82)
        )
        val result = PIIAnalysisResult(entities, 100L, 200)
        assertEquals(4, result.criticalEntities.size)
        assertTrue(result.highEntities.isEmpty())
        assertTrue(result.mediumEntities.isEmpty())
        assertEquals(Severity.CRITICAL, result.highestSeverity)
    }

    @Test
    fun `PIIAnalysisResult with only HIGH entities`() {
        val entities = listOf(
            PIIEntity(EntityType.EMAIL, 0.96f, 0, 20),
            PIIEntity(EntityType.PHONE, 0.90f, 25, 35),
            PIIEntity(EntityType.MEDICAL_ID, 0.88f, 40, 50)
        )
        val result = PIIAnalysisResult(entities, 80L, 150)
        assertTrue(result.criticalEntities.isEmpty())
        assertEquals(3, result.highEntities.size)
        assertTrue(result.mediumEntities.isEmpty())
        assertEquals(Severity.HIGH, result.highestSeverity)
    }

    @Test
    fun `PIIAnalysisResult with only MEDIUM entities`() {
        val entities = listOf(
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8),
            PIIEntity(EntityType.ADDRESS, 0.82f, 10, 40),
            PIIEntity(EntityType.DATE_OF_BIRTH, 0.84f, 45, 55),
            PIIEntity(EntityType.UNKNOWN, 0.91f, 60, 70)
        )
        val result = PIIAnalysisResult(entities, 60L, 120)
        assertTrue(result.criticalEntities.isEmpty())
        assertTrue(result.highEntities.isEmpty())
        assertEquals(4, result.mediumEntities.size)
        assertEquals(Severity.MEDIUM, result.highestSeverity)
    }

    @Test
    fun `PIIAnalysisResult entitiesBySeverity with all types`() {
        val entities = EntityType.entries.map {
            PIIEntity(it, 0.90f, 0, 10, "test")
        }
        val result = PIIAnalysisResult(entities, 200L, 500)
        val grouped = result.entitiesBySeverity()
        assertEquals(4, grouped[Severity.CRITICAL]?.size)
        assertEquals(3, grouped[Severity.HIGH]?.size)
        assertEquals(4, grouped[Severity.MEDIUM]?.size)
    }

    @Test
    fun `PIIAnalysisResult with duplicate entities`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@test.com")
        val result = PIIAnalysisResult(listOf(entity, entity, entity), 30L, 50)
        assertEquals(3, result.entityCount)
        // entitiesBySeverity groups all three
        assertEquals(3, result.highEntities.size)
    }

    @Test
    fun `PIIAnalysisResult copy preserves all properties`() {
        val original = PIIAnalysisResult(
            listOf(PIIEntity(EntityType.SSN, 0.93f, 0, 11)),
            42L,
            100
        )
        val copy = original.copy()
        assertEquals(original.entities, copy.entities)
        assertEquals(original.inferenceTimeMs, copy.inferenceTimeMs)
        assertEquals(original.inputLength, copy.inputLength)
    }

    @Test
    fun `PIIAnalysisResult with negative inferenceTimeMs`() {
        val result = PIIAnalysisResult(emptyList(), -1L, 0)
        assertEquals(-1L, result.inferenceTimeMs)
    }

    @Test
    fun `PIIAnalysisResult with negative inputLength`() {
        val result = PIIAnalysisResult(emptyList(), 0L, -1)
        assertEquals(-1, result.inputLength)
    }

    @Test
    fun `PIIAnalysisResult with Long MAX_VALUE inferenceTimeMs`() {
        val result = PIIAnalysisResult(emptyList(), Long.MAX_VALUE, 0)
        assertEquals(Long.MAX_VALUE, result.inferenceTimeMs)
    }

    @Test
    fun `PIIAnalysisResult with Int MAX_VALUE inputLength`() {
        val result = PIIAnalysisResult(emptyList(), 0L, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, result.inputLength)
    }

    // ========================================================================
    // Section 24: Entity severity ordering verification
    // ========================================================================

    @Test
    fun `CRITICAL ordinal is less than HIGH ordinal`() {
        assertTrue(Severity.CRITICAL.ordinal < Severity.HIGH.ordinal)
    }

    @Test
    fun `HIGH ordinal is less than MEDIUM ordinal`() {
        assertTrue(Severity.HIGH.ordinal < Severity.MEDIUM.ordinal)
    }

    @Test
    fun `CRITICAL is most severe based on ordinal`() {
        assertEquals(0, Severity.CRITICAL.ordinal)
    }

    @Test
    fun `MEDIUM is least severe based on ordinal`() {
        assertEquals(Severity.entries.size - 1, Severity.MEDIUM.ordinal)
    }

    @Test
    fun `entities can be sorted by severity ordinal ascending`() {
        val entities = listOf(
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8),
            PIIEntity(EntityType.CREDIT_CARD, 0.95f, 10, 26),
            PIIEntity(EntityType.EMAIL, 0.96f, 30, 50),
            PIIEntity(EntityType.ADDRESS, 0.82f, 55, 85),
            PIIEntity(EntityType.SSN, 0.93f, 90, 101)
        )
        val sorted = entities.sortedBy { it.severity.ordinal }
        assertEquals(Severity.CRITICAL, sorted[0].severity)
        assertEquals(Severity.CRITICAL, sorted[1].severity)
        assertEquals(Severity.HIGH, sorted[2].severity)
        assertEquals(Severity.MEDIUM, sorted[3].severity)
        assertEquals(Severity.MEDIUM, sorted[4].severity)
    }

    @Test
    fun `highest severity in result with all types is CRITICAL`() {
        val entities = EntityType.entries.map { PIIEntity(it, 0.90f, 0, 10) }
        val result = PIIAnalysisResult(entities, 100L, 200)
        assertEquals(Severity.CRITICAL, result.highestSeverity)
    }

    @Test
    fun `highest severity in result with only MEDIUM entities is MEDIUM`() {
        val entities = listOf(
            PIIEntity(EntityType.PERSON_NAME, 0.80f, 0, 8),
            PIIEntity(EntityType.DATE_OF_BIRTH, 0.84f, 10, 20)
        )
        val result = PIIAnalysisResult(entities, 50L, 100)
        assertEquals(Severity.MEDIUM, result.highestSeverity)
    }

    // ========================================================================
    // Section 25: Additional copy and immutability tests
    // ========================================================================

    @Test
    fun `copy with entityType change updates severity`() {
        val criticalEntity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16)
        assertEquals(Severity.CRITICAL, criticalEntity.severity)

        val highEntity = criticalEntity.copy(entityType = EntityType.EMAIL)
        assertEquals(Severity.HIGH, highEntity.severity)

        val mediumEntity = criticalEntity.copy(entityType = EntityType.PERSON_NAME)
        assertEquals(Severity.MEDIUM, mediumEntity.severity)
    }

    @Test
    fun `copy with entityType change updates maskedText only via rawText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        val copied = entity.copy(entityType = EntityType.SSN)
        // maskedText depends on rawText, not entityType
        assertEquals(entity.maskedText, copied.maskedText)
    }

    @Test
    fun `copy with rawText change updates maskedText`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "user@example.com")
        val copied = entity.copy(rawText = "1234567890")
        assertNotEquals(entity.maskedText, copied.maskedText)
        assertEquals("******7890", copied.maskedText)
    }

    @Test
    fun `copy with index change updates length`() {
        val entity = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16)
        assertEquals(16, entity.length)
        val copied = entity.copy(startIndex = 0, endIndex = 32)
        assertEquals(32, copied.length)
    }

    @Test
    fun `entity created via copy is independent`() {
        val original = PIIEntity(EntityType.EMAIL, 0.95f, 0, 16, "test@test.com")
        val copy = original.copy()
        // Modifying one doesn't affect the other (both are immutable data classes anyway)
        assertNotSame(original, copy)
        assertEquals(original, copy)
    }

    // ========================================================================
    // Section 26: Comprehensive entityType property tests
    // ========================================================================

    @Test
    fun `every entityType has a non-null severity`() {
        for (type in EntityType.entries) {
            assertNotNull("$type should have non-null severity", type.severity)
        }
    }

    @Test
    fun `every entityType has a non-empty displayName`() {
        for (type in EntityType.entries) {
            assertTrue("$type should have non-empty displayName", type.displayName.isNotEmpty())
        }
    }

    @Test
    fun `every entityType has a non-negative labelIndex`() {
        for (type in EntityType.entries) {
            assertTrue("$type should have non-negative labelIndex", type.labelIndex >= 0)
        }
    }

    @Test
    fun `entityType name matches expected string`() {
        assertEquals("CREDIT_CARD", EntityType.CREDIT_CARD.name)
        assertEquals("SSN", EntityType.SSN.name)
        assertEquals("PASSWORD", EntityType.PASSWORD.name)
        assertEquals("API_KEY", EntityType.API_KEY.name)
        assertEquals("EMAIL", EntityType.EMAIL.name)
        assertEquals("PHONE", EntityType.PHONE.name)
        assertEquals("PERSON_NAME", EntityType.PERSON_NAME.name)
        assertEquals("ADDRESS", EntityType.ADDRESS.name)
        assertEquals("DATE_OF_BIRTH", EntityType.DATE_OF_BIRTH.name)
        assertEquals("MEDICAL_ID", EntityType.MEDICAL_ID.name)
        assertEquals("UNKNOWN", EntityType.UNKNOWN.name)
    }

    @Test
    fun `entityType valueOf works for all types`() {
        for (type in EntityType.entries) {
            assertEquals(type, EntityType.valueOf(type.name))
        }
    }

    @Test
    fun `entityType ordinal is sequential from 0`() {
        for ((i, type) in EntityType.entries.withIndex()) {
            assertEquals(i, type.ordinal)
        }
    }

    // ========================================================================
    // Section 27: Final stress and boundary tests
    // ========================================================================

    @Test
    fun `create 1000 entities of different types`() {
        val entities = (0 until 1000).map { i ->
            PIIEntity(
                EntityType.entries[i % EntityType.entries.size],
                (i % 100) / 100.0f,
                i * 10,
                i * 10 + 10,
                "entity-$i"
            )
        }
        assertEquals(1000, entities.size)
        // Verify each has correct properties
        for ((i, entity) in entities.withIndex()) {
            assertEquals(EntityType.entries[i % EntityType.entries.size], entity.entityType)
            assertEquals(10, entity.length)
            assertEquals("entity-$i", entity.rawText)
        }
    }

    @Test
    fun `entities with same indices but different types are not equal`() {
        val types = EntityType.entries
        for (i in types.indices) {
            for (j in i + 1 until types.size) {
                val a = PIIEntity(types[i], 0.90f, 0, 10, "test")
                val b = PIIEntity(types[j], 0.90f, 0, 10, "test")
                assertNotEquals("$types[i] should not equal ${types[j]}", a, b)
            }
        }
    }

    @Test
    fun `entity hashCode distribution across all types`() {
        val hashCodes = EntityType.entries.map {
            PIIEntity(it, 0.90f, 0, 10, "test").hashCode()
        }
        // All hashCodes should be different (very likely for different entity types)
        assertEquals(hashCodes.size, hashCodes.toSet().size)
    }

    @Test
    fun `entity can be stored in and retrieved from a list`() {
        val entity = PIIEntity(EntityType.CREDIT_CARD, 0.95f, 0, 16, "4111111111111111")
        val list = mutableListOf<PIIEntity>()
        list.add(entity)
        assertEquals(entity, list[0])
        assertEquals(1, list.size)
    }

    @Test
    fun `100 entities in a set with unique properties`() {
        val entities = (0 until 100).map {
            PIIEntity(EntityType.EMAIL, 0.95f, it, it + 10, "email$it@test.com")
        }.toSet()
        assertEquals(100, entities.size)
    }

    @Test
    fun `entity maskedText does not reveal first characters for short strings`() {
        val shortTexts = listOf("a", "ab", "abc", "abcd")
        for (text in shortTexts) {
            val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, text.length, text)
            assertEquals("****", entity.maskedText)
            assertFalse(entity.maskedText.contains(text.first().toString()))
        }
    }

    @Test
    fun `entity maskedText reveals only last 4 chars for long strings`() {
        val text = "supersecretpassword123"
        val entity = PIIEntity(EntityType.PASSWORD, 0.9f, 0, text.length, text)
        val masked = entity.maskedText
        // First chars should all be asterisks
        val stars = masked.dropLast(4)
        assertTrue(stars.all { it == '*' })
        // Last 4 should match
        assertEquals("d123", masked.takeLast(4))
    }

    @Test
    fun `PIIAnalysisResult EMPTY is reusable singleton`() {
        val a = PIIAnalysisResult.EMPTY
        val b = PIIAnalysisResult.EMPTY
        assertSame(a, b)
    }
}
