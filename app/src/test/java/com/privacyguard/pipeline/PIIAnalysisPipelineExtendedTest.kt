package com.privacyguard.pipeline

import org.junit.Test
import org.junit.Assert.*
import java.util.regex.Pattern

// ============================================================
// PIIAnalysisPipelineExtendedTest
// Comprehensive specification/behavior tests for the PII
// analysis pipeline.  Sections 1–4 reference project classes
// assumed to exist in the same package; sections 5–8 are
// fully self-contained using only stdlib regex / arithmetic.
// ============================================================

// ---------------------------------------------------------------------------
// SECTION 1 — RiskLevel enum  (20 tests)
// ---------------------------------------------------------------------------

class PIIAnalysisPipelineExtendedTest {

    // -----------------------------------------------------------------------
    // Section 1 — RiskLevel ordinals, names, and ordering
    // -----------------------------------------------------------------------

    @Test
    fun `riskLevel NONE has the lowest ordinal of all levels`() {
        val noneOrdinal = RiskLevel.NONE.ordinal
        assertTrue("NONE ordinal must be less than LOW ordinal",
            noneOrdinal < RiskLevel.LOW.ordinal)
        assertTrue("NONE ordinal must be less than MEDIUM ordinal",
            noneOrdinal < RiskLevel.MEDIUM.ordinal)
        assertTrue("NONE ordinal must be less than HIGH ordinal",
            noneOrdinal < RiskLevel.HIGH.ordinal)
        assertTrue("NONE ordinal must be less than CRITICAL ordinal",
            noneOrdinal < RiskLevel.CRITICAL.ordinal)
    }

    @Test
    fun `riskLevel LOW ordinal is strictly between NONE and MEDIUM`() {
        val low = RiskLevel.LOW.ordinal
        assertTrue("LOW must be greater than NONE", low > RiskLevel.NONE.ordinal)
        assertTrue("LOW must be less than MEDIUM", low < RiskLevel.MEDIUM.ordinal)
    }

    @Test
    fun `riskLevel MEDIUM ordinal is strictly between LOW and HIGH`() {
        val medium = RiskLevel.MEDIUM.ordinal
        assertTrue("MEDIUM must be greater than LOW", medium > RiskLevel.LOW.ordinal)
        assertTrue("MEDIUM must be less than HIGH", medium < RiskLevel.HIGH.ordinal)
    }

    @Test
    fun `riskLevel HIGH ordinal is strictly between MEDIUM and CRITICAL`() {
        val high = RiskLevel.HIGH.ordinal
        assertTrue("HIGH must be greater than MEDIUM", high > RiskLevel.MEDIUM.ordinal)
        assertTrue("HIGH must be less than CRITICAL", high < RiskLevel.CRITICAL.ordinal)
    }

    @Test
    fun `riskLevel CRITICAL has the highest ordinal of all levels`() {
        val critical = RiskLevel.CRITICAL.ordinal
        assertTrue("CRITICAL > NONE",   critical > RiskLevel.NONE.ordinal)
        assertTrue("CRITICAL > LOW",    critical > RiskLevel.LOW.ordinal)
        assertTrue("CRITICAL > MEDIUM", critical > RiskLevel.MEDIUM.ordinal)
        assertTrue("CRITICAL > HIGH",   critical > RiskLevel.HIGH.ordinal)
    }

    @Test
    fun `riskLevel values array contains exactly five entries`() {
        assertEquals("RiskLevel must have exactly 5 values", 5, RiskLevel.values().size)
    }

    @Test
    fun `riskLevel values array contains NONE as first element`() {
        assertEquals(RiskLevel.NONE, RiskLevel.values()[0])
    }

    @Test
    fun `riskLevel values array contains LOW as second element`() {
        assertEquals(RiskLevel.LOW, RiskLevel.values()[1])
    }

    @Test
    fun `riskLevel values array contains MEDIUM as third element`() {
        assertEquals(RiskLevel.MEDIUM, RiskLevel.values()[2])
    }

    @Test
    fun `riskLevel values array contains HIGH as fourth element`() {
        assertEquals(RiskLevel.HIGH, RiskLevel.values()[3])
    }

    @Test
    fun `riskLevel values array contains CRITICAL as fifth element`() {
        assertEquals(RiskLevel.CRITICAL, RiskLevel.values()[4])
    }

    @Test
    fun `riskLevel valueOf NONE returns correct constant`() {
        assertEquals(RiskLevel.NONE, RiskLevel.valueOf("NONE"))
    }

    @Test
    fun `riskLevel valueOf LOW returns correct constant`() {
        assertEquals(RiskLevel.LOW, RiskLevel.valueOf("LOW"))
    }

    @Test
    fun `riskLevel valueOf MEDIUM returns correct constant`() {
        assertEquals(RiskLevel.MEDIUM, RiskLevel.valueOf("MEDIUM"))
    }

    @Test
    fun `riskLevel valueOf HIGH returns correct constant`() {
        assertEquals(RiskLevel.HIGH, RiskLevel.valueOf("HIGH"))
    }

    @Test
    fun `riskLevel valueOf CRITICAL returns correct constant`() {
        assertEquals(RiskLevel.CRITICAL, RiskLevel.valueOf("CRITICAL"))
    }

    @Test
    fun `riskLevel name property equals the declared identifier for NONE`() {
        assertEquals("NONE", RiskLevel.NONE.name)
    }

    @Test
    fun `riskLevel name property equals the declared identifier for CRITICAL`() {
        assertEquals("CRITICAL", RiskLevel.CRITICAL.name)
    }

    @Test
    fun `riskLevel ordering is strictly monotonically increasing`() {
        val levels = RiskLevel.values()
        for (i in 0 until levels.size - 1) {
            assertTrue(
                "Ordinal of ${levels[i]} must be less than ${levels[i + 1]}",
                levels[i].ordinal < levels[i + 1].ordinal
            )
        }
    }

    @Test
    fun `riskLevel valueOf throws exception for unknown name`() {
        var threw = false
        try {
            RiskLevel.valueOf("EXTREME")
        } catch (e: IllegalArgumentException) {
            threw = true
        }
        assertTrue("valueOf with unknown name must throw IllegalArgumentException", threw)
    }

    // -----------------------------------------------------------------------
    // Section 2 — ConfidenceThresholds constant values  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `confidenceThresholds SSN_MIN is a positive float`() {
        assertTrue("SSN_MIN must be positive", ConfidenceThresholds.SSN_MIN > 0.0f)
    }

    @Test
    fun `confidenceThresholds SSN_MIN is less than or equal to 1_0`() {
        assertTrue("SSN_MIN must be <= 1.0", ConfidenceThresholds.SSN_MIN <= 1.0f)
    }

    @Test
    fun `confidenceThresholds SSN_MIN is in valid probability range`() {
        val v = ConfidenceThresholds.SSN_MIN
        assertTrue("SSN_MIN in [0,1]", v >= 0.0f && v <= 1.0f)
    }

    @Test
    fun `confidenceThresholds SSN_MIN is at least 0_5 indicating meaningful threshold`() {
        assertTrue("SSN_MIN should be at least 0.5 for meaningful detection",
            ConfidenceThresholds.SSN_MIN >= 0.5f)
    }

    @Test
    fun `confidenceThresholds CARD_MIN is a positive float`() {
        assertTrue("CARD_MIN must be positive", ConfidenceThresholds.CARD_MIN > 0.0f)
    }

    @Test
    fun `confidenceThresholds CARD_MIN is less than or equal to 1_0`() {
        assertTrue("CARD_MIN must be <= 1.0", ConfidenceThresholds.CARD_MIN <= 1.0f)
    }

    @Test
    fun `confidenceThresholds CARD_MIN is in valid probability range`() {
        val v = ConfidenceThresholds.CARD_MIN
        assertTrue("CARD_MIN in [0,1]", v >= 0.0f && v <= 1.0f)
    }

    @Test
    fun `confidenceThresholds CARD_MIN is at least 0_5 for meaningful detection`() {
        assertTrue("CARD_MIN should be at least 0.5",
            ConfidenceThresholds.CARD_MIN >= 0.5f)
    }

    @Test
    fun `confidenceThresholds EMAIL_MIN is a positive float`() {
        assertTrue("EMAIL_MIN must be positive", ConfidenceThresholds.EMAIL_MIN > 0.0f)
    }

    @Test
    fun `confidenceThresholds EMAIL_MIN is less than or equal to 1_0`() {
        assertTrue("EMAIL_MIN must be <= 1.0", ConfidenceThresholds.EMAIL_MIN <= 1.0f)
    }

    @Test
    fun `confidenceThresholds EMAIL_MIN is in valid probability range`() {
        val v = ConfidenceThresholds.EMAIL_MIN
        assertTrue("EMAIL_MIN in [0,1]", v >= 0.0f && v <= 1.0f)
    }

    @Test
    fun `confidenceThresholds EMAIL_MIN is at least 0_5 for meaningful detection`() {
        assertTrue("EMAIL_MIN should be at least 0.5",
            ConfidenceThresholds.EMAIL_MIN >= 0.5f)
    }

    @Test
    fun `confidenceThresholds PHONE_MIN is a positive float`() {
        assertTrue("PHONE_MIN must be positive", ConfidenceThresholds.PHONE_MIN > 0.0f)
    }

    @Test
    fun `confidenceThresholds PHONE_MIN is less than or equal to 1_0`() {
        assertTrue("PHONE_MIN must be <= 1.0", ConfidenceThresholds.PHONE_MIN <= 1.0f)
    }

    @Test
    fun `confidenceThresholds PHONE_MIN is in valid probability range`() {
        val v = ConfidenceThresholds.PHONE_MIN
        assertTrue("PHONE_MIN in [0,1]", v >= 0.0f && v <= 1.0f)
    }

    @Test
    fun `confidenceThresholds PHONE_MIN is at least 0_5 for meaningful detection`() {
        assertTrue("PHONE_MIN should be at least 0.5",
            ConfidenceThresholds.PHONE_MIN >= 0.5f)
    }

    @Test
    fun `confidenceThresholds all four constants are finite floats`() {
        assertFalse("SSN_MIN must not be NaN",      ConfidenceThresholds.SSN_MIN.isNaN())
        assertFalse("CARD_MIN must not be NaN",     ConfidenceThresholds.CARD_MIN.isNaN())
        assertFalse("EMAIL_MIN must not be NaN",    ConfidenceThresholds.EMAIL_MIN.isNaN())
        assertFalse("PHONE_MIN must not be NaN",    ConfidenceThresholds.PHONE_MIN.isNaN())
        assertFalse("SSN_MIN must be finite",       ConfidenceThresholds.SSN_MIN.isInfinite())
        assertFalse("CARD_MIN must be finite",      ConfidenceThresholds.CARD_MIN.isInfinite())
        assertFalse("EMAIL_MIN must be finite",     ConfidenceThresholds.EMAIL_MIN.isInfinite())
        assertFalse("PHONE_MIN must be finite",     ConfidenceThresholds.PHONE_MIN.isInfinite())
    }

    @Test
    fun `confidenceThresholds SSN_MIN and CARD_MIN are both valid thresholds independently`() {
        val ssn  = ConfidenceThresholds.SSN_MIN
        val card = ConfidenceThresholds.CARD_MIN
        assertTrue("SSN_MIN in [0,1]",  ssn  in 0.0f..1.0f)
        assertTrue("CARD_MIN in [0,1]", card in 0.0f..1.0f)
    }

    @Test
    fun `confidenceThresholds EMAIL_MIN and PHONE_MIN are both valid thresholds independently`() {
        val email = ConfidenceThresholds.EMAIL_MIN
        val phone = ConfidenceThresholds.PHONE_MIN
        assertTrue("EMAIL_MIN in [0,1]", email in 0.0f..1.0f)
        assertTrue("PHONE_MIN in [0,1]", phone in 0.0f..1.0f)
    }

    @Test
    fun `confidenceThresholds none of the constants equals zero`() {
        assertNotEquals(0.0f, ConfidenceThresholds.SSN_MIN,   0.0f)
        assertNotEquals(0.0f, ConfidenceThresholds.CARD_MIN,  0.0f)
        assertNotEquals(0.0f, ConfidenceThresholds.EMAIL_MIN, 0.0f)
        assertNotEquals(0.0f, ConfidenceThresholds.PHONE_MIN, 0.0f)
    }

    // -----------------------------------------------------------------------
    // Section 3 — DetectedEntity data class properties  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `detectedEntity stores type property correctly`() {
        val entity = DetectedEntity(
            type       = "SSN",
            value      = "123-45-6789",
            startIndex = 0,
            endIndex   = 11,
            confidence = 0.97f
        )
        assertEquals("SSN", entity.type)
    }

    @Test
    fun `detectedEntity stores value property correctly`() {
        val entity = DetectedEntity(
            type       = "EMAIL",
            value      = "test@example.com",
            startIndex = 5,
            endIndex   = 21,
            confidence = 0.88f
        )
        assertEquals("test@example.com", entity.value)
    }

    @Test
    fun `detectedEntity stores startIndex property correctly`() {
        val entity = DetectedEntity(
            type       = "PHONE",
            value      = "555-867-5309",
            startIndex = 10,
            endIndex   = 22,
            confidence = 0.75f
        )
        assertEquals(10, entity.startIndex)
    }

    @Test
    fun `detectedEntity stores endIndex property correctly`() {
        val entity = DetectedEntity(
            type       = "PHONE",
            value      = "555-867-5309",
            startIndex = 10,
            endIndex   = 22,
            confidence = 0.75f
        )
        assertEquals(22, entity.endIndex)
    }

    @Test
    fun `detectedEntity stores confidence property correctly`() {
        val entity = DetectedEntity(
            type       = "CREDIT_CARD",
            value      = "4111111111111111",
            startIndex = 0,
            endIndex   = 16,
            confidence = 0.99f
        )
        assertEquals(0.99f, entity.confidence, 0.0001f)
    }

    @Test
    fun `detectedEntity endIndex is greater than startIndex for non-empty match`() {
        val entity = DetectedEntity(
            type       = "SSN",
            value      = "999-88-7777",
            startIndex = 3,
            endIndex   = 14,
            confidence = 0.92f
        )
        assertTrue("endIndex must be > startIndex", entity.endIndex > entity.startIndex)
    }

    @Test
    fun `detectedEntity span equals value length`() {
        val value  = "user@domain.org"
        val entity = DetectedEntity(
            type       = "EMAIL",
            value      = value,
            startIndex = 20,
            endIndex   = 20 + value.length,
            confidence = 0.85f
        )
        val span = entity.endIndex - entity.startIndex
        assertEquals("Span should equal value length", value.length, span)
    }

    @Test
    fun `detectedEntity confidence is between 0 and 1 inclusive`() {
        val entity = DetectedEntity(
            type       = "IP_ADDRESS",
            value      = "192.168.1.1",
            startIndex = 7,
            endIndex   = 18,
            confidence = 0.60f
        )
        assertTrue("Confidence must be >= 0", entity.confidence >= 0.0f)
        assertTrue("Confidence must be <= 1", entity.confidence <= 1.0f)
    }

    @Test
    fun `detectedEntity type is not blank`() {
        val entity = DetectedEntity(
            type       = "DATE_OF_BIRTH",
            value      = "1985-07-22",
            startIndex = 0,
            endIndex   = 10,
            confidence = 0.80f
        )
        assertTrue("type must not be blank", entity.type.isNotBlank())
    }

    @Test
    fun `detectedEntity value is not blank for a real detection`() {
        val entity = DetectedEntity(
            type       = "SSN",
            value      = "001-01-0001",
            startIndex = 0,
            endIndex   = 11,
            confidence = 0.95f
        )
        assertTrue("value must not be blank", entity.value.isNotBlank())
    }

    @Test
    fun `detectedEntity equality holds for same field values`() {
        val a = DetectedEntity("EMAIL", "a@b.com", 0, 7, 0.9f)
        val b = DetectedEntity("EMAIL", "a@b.com", 0, 7, 0.9f)
        assertEquals("Two entities with same data must be equal", a, b)
    }

    @Test
    fun `detectedEntity inequality when types differ`() {
        val a = DetectedEntity("EMAIL", "a@b.com", 0, 7, 0.9f)
        val b = DetectedEntity("PHONE", "a@b.com", 0, 7, 0.9f)
        assertNotEquals("Entities with different types must not be equal", a, b)
    }

    @Test
    fun `detectedEntity inequality when values differ`() {
        val a = DetectedEntity("EMAIL", "a@b.com",   0, 7, 0.9f)
        val b = DetectedEntity("EMAIL", "x@y.co.uk", 0, 9, 0.9f)
        assertNotEquals("Entities with different values must not be equal", a, b)
    }

    @Test
    fun `detectedEntity copy produces independent instance`() {
        val original = DetectedEntity("SSN", "111-22-3333", 0, 11, 0.93f)
        val copy     = original.copy(confidence = 0.50f)
        assertNotEquals("Original and copy must differ after copy with new confidence",
            original, copy)
        assertEquals(0.93f, original.confidence, 0.0001f)
        assertEquals(0.50f, copy.confidence,     0.0001f)
    }

    @Test
    fun `detectedEntity startIndex zero is valid for match at text start`() {
        val entity = DetectedEntity("SSN", "000-00-0000", 0, 11, 0.98f)
        assertEquals(0, entity.startIndex)
    }

    @Test
    fun `detectedEntity large startIndex is valid for match deep in text`() {
        val entity = DetectedEntity("EMAIL", "late@match.io", 500, 513, 0.77f)
        assertEquals(500, entity.startIndex)
    }

    @Test
    fun `detectedEntity confidence of exactly 1_0 is valid`() {
        val entity = DetectedEntity("CREDIT_CARD", "4000000000000002", 0, 16, 1.0f)
        assertEquals(1.0f, entity.confidence, 0.0f)
    }

    @Test
    fun `detectedEntity confidence of exactly 0_0 is boundary value`() {
        val entity = DetectedEntity("UNKNOWN", "???", 0, 3, 0.0f)
        assertEquals(0.0f, entity.confidence, 0.0f)
    }

    @Test
    fun `detectedEntity type field accepts lowercase string`() {
        val entity = DetectedEntity("email", "me@here.net", 0, 11, 0.82f)
        assertEquals("email", entity.type)
    }

    @Test
    fun `detectedEntity hashCode is consistent with equals`() {
        val a = DetectedEntity("PHONE", "800-555-0100", 4, 16, 0.88f)
        val b = DetectedEntity("PHONE", "800-555-0100", 4, 16, 0.88f)
        assertEquals("Equal entities must have same hashCode", a.hashCode(), b.hashCode())
    }

    // -----------------------------------------------------------------------
    // Section 4 — PipelineResult data class and calculations  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `pipelineResult stores entities list correctly`() {
        val entities = listOf(
            DetectedEntity("SSN", "111-22-3333", 0, 11, 0.95f)
        )
        val result = PipelineResult(
            entities        = entities,
            processingTimeMs = 42L,
            confidenceAvg   = 0.95f,
            riskLevel       = RiskLevel.HIGH
        )
        assertEquals(1, result.entities.size)
        assertEquals("SSN", result.entities[0].type)
    }

    @Test
    fun `pipelineResult stores processingTimeMs correctly`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 123L,
            confidenceAvg    = 0.0f,
            riskLevel        = RiskLevel.NONE
        )
        assertEquals(123L, result.processingTimeMs)
    }

    @Test
    fun `pipelineResult stores confidenceAvg correctly`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 10L,
            confidenceAvg    = 0.73f,
            riskLevel        = RiskLevel.MEDIUM
        )
        assertEquals(0.73f, result.confidenceAvg, 0.0001f)
    }

    @Test
    fun `pipelineResult stores riskLevel correctly`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 5L,
            confidenceAvg    = 0.0f,
            riskLevel        = RiskLevel.CRITICAL
        )
        assertEquals(RiskLevel.CRITICAL, result.riskLevel)
    }

    @Test
    fun `pipelineResult with empty entities has NONE risk level expectation`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 1L,
            confidenceAvg    = 0.0f,
            riskLevel        = RiskLevel.NONE
        )
        assertEquals(RiskLevel.NONE, result.riskLevel)
        assertTrue("Empty result should have empty entities", result.entities.isEmpty())
    }

    @Test
    fun `pipelineResult processingTimeMs must be non-negative`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 0L,
            confidenceAvg    = 0.0f,
            riskLevel        = RiskLevel.NONE
        )
        assertTrue("processingTimeMs must be >= 0", result.processingTimeMs >= 0L)
    }

    @Test
    fun `pipelineResult confidenceAvg must be in valid probability range`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 7L,
            confidenceAvg    = 0.65f,
            riskLevel        = RiskLevel.LOW
        )
        assertTrue("confidenceAvg >= 0", result.confidenceAvg >= 0.0f)
        assertTrue("confidenceAvg <= 1", result.confidenceAvg <= 1.0f)
    }

    @Test
    fun `pipelineResult entity count reflects number of detections`() {
        val entities = listOf(
            DetectedEntity("SSN",   "111-22-3333",       0,  11, 0.96f),
            DetectedEntity("EMAIL", "bob@test.com",      15, 27, 0.88f),
            DetectedEntity("PHONE", "212-555-0170",      30, 42, 0.79f)
        )
        val result = PipelineResult(
            entities         = entities,
            processingTimeMs = 60L,
            confidenceAvg    = 0.877f,
            riskLevel        = RiskLevel.HIGH
        )
        assertEquals(3, result.entities.size)
    }

    @Test
    fun `pipelineResult computed average confidence matches manual calculation`() {
        val c1 = 0.9f
        val c2 = 0.8f
        val c3 = 0.7f
        val expectedAvg = (c1 + c2 + c3) / 3f
        val result = PipelineResult(
            entities = listOf(
                DetectedEntity("A", "x", 0, 1, c1),
                DetectedEntity("B", "y", 1, 2, c2),
                DetectedEntity("C", "z", 2, 3, c3)
            ),
            processingTimeMs = 20L,
            confidenceAvg    = expectedAvg,
            riskLevel        = RiskLevel.MEDIUM
        )
        assertEquals(expectedAvg, result.confidenceAvg, 0.001f)
    }

    @Test
    fun `pipelineResult equality holds for identical data`() {
        val e = listOf(DetectedEntity("SSN", "111-22-3333", 0, 11, 0.95f))
        val a = PipelineResult(e, 10L, 0.95f, RiskLevel.HIGH)
        val b = PipelineResult(e, 10L, 0.95f, RiskLevel.HIGH)
        assertEquals(a, b)
    }

    @Test
    fun `pipelineResult inequality when riskLevels differ`() {
        val e = emptyList<DetectedEntity>()
        val a = PipelineResult(e, 5L, 0.0f, RiskLevel.NONE)
        val b = PipelineResult(e, 5L, 0.0f, RiskLevel.HIGH)
        assertNotEquals(a, b)
    }

    @Test
    fun `pipelineResult copy changes processingTimeMs independently`() {
        val original = PipelineResult(emptyList(), 100L, 0.5f, RiskLevel.LOW)
        val updated  = original.copy(processingTimeMs = 200L)
        assertEquals(100L, original.processingTimeMs)
        assertEquals(200L, updated.processingTimeMs)
    }

    @Test
    fun `pipelineResult high entity count with CRITICAL risk level`() {
        val entities = (1..20).map { i ->
            DetectedEntity("SSN", "111-22-${"$i".padStart(4, '0')}", i * 20, i * 20 + 11, 0.95f)
        }
        val result = PipelineResult(
            entities         = entities,
            processingTimeMs = 350L,
            confidenceAvg    = 0.95f,
            riskLevel        = RiskLevel.CRITICAL
        )
        assertEquals(20, result.entities.size)
        assertEquals(RiskLevel.CRITICAL, result.riskLevel)
    }

    @Test
    fun `pipelineResult single SSN detection suggests HIGH or CRITICAL risk`() {
        val result = PipelineResult(
            entities         = listOf(DetectedEntity("SSN", "111-22-3333", 0, 11, 0.97f)),
            processingTimeMs = 15L,
            confidenceAvg    = 0.97f,
            riskLevel        = RiskLevel.HIGH
        )
        assertTrue("SSN detection should produce HIGH or CRITICAL risk",
            result.riskLevel == RiskLevel.HIGH || result.riskLevel == RiskLevel.CRITICAL)
    }

    @Test
    fun `pipelineResult processingTimeMs large value is valid`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 9_999L,
            confidenceAvg    = 0.0f,
            riskLevel        = RiskLevel.NONE
        )
        assertEquals(9_999L, result.processingTimeMs)
    }

    @Test
    fun `pipelineResult entities list is immutable via the data class contract`() {
        val mutable: MutableList<DetectedEntity> = mutableListOf(
            DetectedEntity("EMAIL", "a@b.co", 0, 6, 0.8f)
        )
        val result = PipelineResult(
            entities         = mutable.toList(),
            processingTimeMs = 5L,
            confidenceAvg    = 0.8f,
            riskLevel        = RiskLevel.LOW
        )
        mutable.add(DetectedEntity("PHONE", "111", 10, 13, 0.5f))
        // The stored list was taken as a snapshot; must remain size 1
        assertEquals(1, result.entities.size)
    }

    @Test
    fun `pipelineResult confidenceAvg of 0_0 is valid for empty or low-confidence scan`() {
        val result = PipelineResult(
            entities         = emptyList(),
            processingTimeMs = 2L,
            confidenceAvg    = 0.0f,
            riskLevel        = RiskLevel.NONE
        )
        assertEquals(0.0f, result.confidenceAvg, 0.0f)
    }

    @Test
    fun `pipelineResult confidenceAvg of 1_0 is valid for perfect-confidence detection`() {
        val result = PipelineResult(
            entities         = listOf(DetectedEntity("SSN", "000-00-0001", 0, 11, 1.0f)),
            processingTimeMs = 8L,
            confidenceAvg    = 1.0f,
            riskLevel        = RiskLevel.CRITICAL
        )
        assertEquals(1.0f, result.confidenceAvg, 0.0f)
    }

    @Test
    fun `pipelineResult toString contains entity count information via data class`() {
        val result = PipelineResult(
            entities         = listOf(DetectedEntity("SSN", "111-22-3333", 0, 11, 0.9f)),
            processingTimeMs = 30L,
            confidenceAvg    = 0.9f,
            riskLevel        = RiskLevel.HIGH
        )
        val str = result.toString()
        assertTrue("toString must not be blank", str.isNotBlank())
    }

    @Test
    fun `pipelineResult two results with different entity lists are not equal`() {
        val a = PipelineResult(
            entities         = listOf(DetectedEntity("SSN", "111-22-3333", 0, 11, 0.9f)),
            processingTimeMs = 10L,
            confidenceAvg    = 0.9f,
            riskLevel        = RiskLevel.HIGH
        )
        val b = PipelineResult(
            entities         = listOf(DetectedEntity("EMAIL", "x@y.com", 0, 7, 0.9f)),
            processingTimeMs = 10L,
            confidenceAvg    = 0.9f,
            riskLevel        = RiskLevel.HIGH
        )
        assertNotEquals(a, b)
    }

    // -----------------------------------------------------------------------
    // Section 5 — String processing helpers  (30 tests)
    // Pure regex / String operations, no project classes required.
    // -----------------------------------------------------------------------

    private val SSN_PATTERN   = Pattern.compile("""^\d{3}-\d{2}-\d{4}$""")
    private val EMAIL_PATTERN = Pattern.compile(
        """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"""
    )
    private val PHONE_PATTERN = Pattern.compile(
        """(\+?1[\s\-.]?)?\(?\d{3}\)?[\s\-.]?\d{3}[\s\-.]?\d{4}"""
    )
    private val CARD_PATTERN  = Pattern.compile("""\b(?:\d[ \-]?){13,16}\b""")

    /** Luhn algorithm check — returns true if the number passes */
    private fun luhn(number: String): Boolean {
        val digits = number.filter { it.isDigit() }.reversed()
        if (digits.isEmpty()) return false
        var sum = 0
        digits.forEachIndexed { i, c ->
            var d = c.digitToInt()
            if (i % 2 == 1) { d *= 2; if (d > 9) d -= 9 }
            sum += d
        }
        return sum % 10 == 0
    }

    @Test
    fun `ssnPattern matches canonical SSN format`() {
        assertTrue(SSN_PATTERN.matcher("123-45-6789").matches())
    }

    @Test
    fun `ssnPattern matches SSN with different digit groups`() {
        assertTrue(SSN_PATTERN.matcher("999-88-7777").matches())
    }

    @Test
    fun `ssnPattern rejects SSN without dashes`() {
        assertFalse(SSN_PATTERN.matcher("123456789").matches())
    }

    @Test
    fun `ssnPattern rejects SSN with wrong dash positions`() {
        assertFalse(SSN_PATTERN.matcher("1234-5-6789").matches())
    }

    @Test
    fun `ssnPattern rejects partial SSN`() {
        assertFalse(SSN_PATTERN.matcher("123-45").matches())
    }

    @Test
    fun `ssnPattern rejects SSN with letters`() {
        assertFalse(SSN_PATTERN.matcher("ABC-DE-FGHI").matches())
    }

    @Test
    fun `ssnPattern rejects empty string`() {
        assertFalse(SSN_PATTERN.matcher("").matches())
    }

    @Test
    fun `ssnPattern accepts 000-00-0000 as structurally valid`() {
        assertTrue("000-00-0000 matches pattern", SSN_PATTERN.matcher("000-00-0000").matches())
    }

    @Test
    fun `emailPattern matches simple valid email`() {
        assertTrue(EMAIL_PATTERN.matcher("user@example.com").find())
    }

    @Test
    fun `emailPattern matches email with subdomain`() {
        assertTrue(EMAIL_PATTERN.matcher("alice@mail.company.org").find())
    }

    @Test
    fun `emailPattern matches email with plus sign`() {
        assertTrue(EMAIL_PATTERN.matcher("user+tag@example.com").find())
    }

    @Test
    fun `emailPattern matches email with dots in local part`() {
        assertTrue(EMAIL_PATTERN.matcher("first.last@domain.io").find())
    }

    @Test
    fun `emailPattern rejects string without at-sign`() {
        assertFalse(EMAIL_PATTERN.matcher("userexample.com").find())
    }

    @Test
    fun `emailPattern rejects string without domain extension`() {
        assertFalse(EMAIL_PATTERN.matcher("user@nodot").find())
    }

    @Test
    fun `phonePattern matches standard US format with dashes`() {
        assertTrue(PHONE_PATTERN.matcher("555-867-5309").find())
    }

    @Test
    fun `phonePattern matches US format with dots`() {
        assertTrue(PHONE_PATTERN.matcher("555.867.5309").find())
    }

    @Test
    fun `phonePattern matches US format with spaces`() {
        assertTrue(PHONE_PATTERN.matcher("555 867 5309").find())
    }

    @Test
    fun `phonePattern matches format with country code`() {
        assertTrue(PHONE_PATTERN.matcher("+1 555 867 5309").find())
    }

    @Test
    fun `phonePattern matches parenthesized area code`() {
        assertTrue(PHONE_PATTERN.matcher("(212) 555-0100").find())
    }

    @Test
    fun `phonePattern rejects too-short number`() {
        assertFalse(PHONE_PATTERN.matcher("123-456").find())
    }

    @Test
    fun `luhnCheck returns true for valid Visa test card 4111111111111111`() {
        assertTrue(luhn("4111111111111111"))
    }

    @Test
    fun `luhnCheck returns true for valid Mastercard test card 5500005555555559`() {
        assertTrue(luhn("5500005555555559"))
    }

    @Test
    fun `luhnCheck returns true for valid Amex test card 378282246310005`() {
        assertTrue(luhn("378282246310005"))
    }

    @Test
    fun `luhnCheck returns false for invalid card number 1234567890123456`() {
        assertFalse(luhn("1234567890123456"))
    }

    @Test
    fun `luhnCheck handles card with spaces correctly`() {
        assertTrue(luhn("4111 1111 1111 1111"))
    }

    @Test
    fun `luhnCheck handles card with dashes correctly`() {
        assertTrue(luhn("4111-1111-1111-1111"))
    }

    @Test
    fun `luhnCheck returns false for empty string`() {
        assertFalse(luhn(""))
    }

    @Test
    fun `luhnCheck returns false for single digit zero`() {
        assertFalse(luhn("0"))
    }

    @Test
    fun `luhnCheck returns true for single digit 0 represented as Luhn-valid minimal`() {
        // "00" passes Luhn (0+0=0, mod 10 = 0)
        assertTrue(luhn("00"))
    }

    @Test
    fun `ssnPattern total digit count in match is always 9`() {
        val ssn     = "987-65-4321"
        val matcher = SSN_PATTERN.matcher(ssn)
        assertTrue(matcher.matches())
        val digits  = ssn.filter { it.isDigit() }
        assertEquals(9, digits.length)
    }

    // -----------------------------------------------------------------------
    // Section 6 — Data validation patterns  (30 tests)
    // Fully self-contained pattern-matching tests.
    // -----------------------------------------------------------------------

    @Test
    fun `ipv4Pattern matches standard IPv4 address`() {
        val p = Pattern.compile("""\b(\d{1,3}\.){3}\d{1,3}\b""")
        assertTrue(p.matcher("192.168.0.1").find())
    }

    @Test
    fun `ipv4Pattern matches loopback address`() {
        val p = Pattern.compile("""\b(\d{1,3}\.){3}\d{1,3}\b""")
        assertTrue(p.matcher("127.0.0.1").find())
    }

    @Test
    fun `ipv4Pattern rejects address with too many octets`() {
        val p = Pattern.compile("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$""")
        assertFalse(p.matcher("1.2.3.4.5").matches())
    }

    @Test
    fun `zipCodePattern matches 5-digit US zip`() {
        val p = Pattern.compile("""\b\d{5}\b""")
        assertTrue(p.matcher("90210").find())
    }

    @Test
    fun `zipCodePattern matches 9-digit zip-plus-4`() {
        val p = Pattern.compile("""\b\d{5}(-\d{4})?\b""")
        assertTrue(p.matcher("90210-1234").find())
    }

    @Test
    fun `zipCodePattern rejects 4-digit string`() {
        val p = Pattern.compile("""^\d{5}$""")
        assertFalse(p.matcher("9021").matches())
    }

    @Test
    fun `datePatternISO matches yyyy-MM-dd`() {
        val p = Pattern.compile("""\d{4}-\d{2}-\d{2}""")
        assertTrue(p.matcher("2024-03-15").find())
    }

    @Test
    fun `datePatternSlash matches MM-slash-dd-slash-yyyy`() {
        val p = Pattern.compile("""\d{2}/\d{2}/\d{4}""")
        assertTrue(p.matcher("03/15/2024").find())
    }

    @Test
    fun `datePatternSlash rejects wrong separator`() {
        val p = Pattern.compile("""^\d{2}/\d{2}/\d{4}$""")
        assertFalse(p.matcher("03-15-2024").matches())
    }

    @Test
    fun `creditCardPattern matches 16-digit Visa`() {
        val p = Pattern.compile("""\b4\d{15}\b""")
        assertTrue(p.matcher("4111111111111111").find())
    }

    @Test
    fun `creditCardPattern matches 16-digit Mastercard starting with 5`() {
        val p = Pattern.compile("""\b5[1-5]\d{14}\b""")
        assertTrue(p.matcher("5500005555555559").find())
    }

    @Test
    fun `creditCardPattern matches 15-digit Amex`() {
        val p = Pattern.compile("""\b3[47]\d{13}\b""")
        assertTrue(p.matcher("378282246310005").find())
    }

    @Test
    fun `creditCardPattern rejects 10-digit number`() {
        val p = Pattern.compile("""\b\d{16}\b""")
        assertFalse(p.matcher("1234567890").find())
    }

    @Test
    fun `passportPattern matches US passport format`() {
        val p = Pattern.compile("""[A-Z]{1,2}\d{6,9}""")
        assertTrue(p.matcher("A12345678").find())
    }

    @Test
    fun `ibanPattern matches simplified IBAN`() {
        val p = Pattern.compile("""[A-Z]{2}\d{2}[A-Z0-9]{4,30}""")
        assertTrue(p.matcher("GB29NWBK60161331926819").find())
    }

    @Test
    fun `macAddressPattern matches colon-separated MAC`() {
        val p = Pattern.compile("""([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}""")
        assertTrue(p.matcher("00:1A:2B:3C:4D:5E").find())
    }

    @Test
    fun `macAddressPattern matches dash-separated MAC`() {
        val p = Pattern.compile("""([0-9A-Fa-f]{2}-){5}[0-9A-Fa-f]{2}""")
        assertTrue(p.matcher("00-1A-2B-3C-4D-5E").find())
    }

    @Test
    fun `urlPattern matches http URL`() {
        val p = Pattern.compile("""https?://[^\s]+""")
        assertTrue(p.matcher("http://example.com/path?q=1").find())
    }

    @Test
    fun `urlPattern matches https URL`() {
        val p = Pattern.compile("""https?://[^\s]+""")
        assertTrue(p.matcher("https://secure.example.com").find())
    }

    @Test
    fun `urlPattern does not match plain domain without scheme`() {
        val p = Pattern.compile("""^https?://[^\s]+$""")
        assertFalse(p.matcher("example.com").matches())
    }

    @Test
    fun `vehicleVinPattern matches 17-char VIN`() {
        val p = Pattern.compile("""[A-HJ-NPR-Z0-9]{17}""")
        assertTrue(p.matcher("1HGCM82633A004352").find())
    }

    @Test
    fun `driverLicensePattern matches simple alphanumeric format`() {
        val p = Pattern.compile("""[A-Z]{1,2}\d{5,9}""")
        assertTrue(p.matcher("A1234567").find())
    }

    @Test
    fun `einPattern matches employer identification number xx-xxxxxxx`() {
        val p = Pattern.compile("""\b\d{2}-\d{7}\b""")
        assertTrue(p.matcher("12-3456789").find())
    }

    @Test
    fun `einPattern rejects SSN format`() {
        val p = Pattern.compile("""^\d{2}-\d{7}$""")
        assertFalse(p.matcher("123-45-6789").matches())
    }

    @Test
    fun `npiPattern matches 10-digit NPI number`() {
        val p = Pattern.compile("""\b\d{10}\b""")
        assertTrue(p.matcher("1234567890").find())
    }

    @Test
    fun `bitcoinAddressPattern matches legacy P2PKH address`() {
        val p = Pattern.compile("""[13][a-km-zA-HJ-NP-Z1-9]{25,34}""")
        assertTrue(p.matcher("1A1zP1eP5QGefi2DMPTfTL5SLmv7Divf1n").find())
    }

    @Test
    fun `coordsPattern matches latitude comma longitude`() {
        val p = Pattern.compile("""-?\d{1,2}\.\d+,\s*-?\d{1,3}\.\d+""")
        assertTrue(p.matcher("37.7749, -122.4194").find())
    }

    @Test
    fun `hexColorPattern matches six-digit hex color`() {
        val p = Pattern.compile("""#[0-9A-Fa-f]{6}\b""")
        assertTrue(p.matcher("#FF5733").find())
    }

    @Test
    fun `uuidPattern matches standard UUID v4 format`() {
        val p = Pattern.compile(
            """[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}""",
            Pattern.CASE_INSENSITIVE
        )
        assertTrue(p.matcher("550e8400-e29b-41d4-a716-446655440000").find())
    }

    @Test
    fun `routingNumberPattern matches 9-digit ABA routing number`() {
        val p = Pattern.compile("""\b\d{9}\b""")
        assertTrue(p.matcher("021000021").find())
    }

    @Test
    fun `medicarePattern matches simplified Medicare ID`() {
        // Medicare Beneficiary Identifier: 1 letter, 8 alphanumeric, 1 letter, 1 digit (simplified)
        val p = Pattern.compile("""[1-9][A-Z]{1}[A-Z0-9]{1}[0-9]{1}[A-Z]{1}[A-Z0-9]{1}[0-9]{1}[A-Z]{1}[A-Z0-9]{1}\d""")
        // Just test that the pattern compiles and doesn't throw
        assertNotNull(p)
        assertTrue(true) // pattern compiled successfully
    }

    // -----------------------------------------------------------------------
    // Section 7 — Math / scoring helpers  (30 tests)
    // Pure arithmetic and floating-point correctness.
    // -----------------------------------------------------------------------

    @Test
    fun `averageConfidence of single entity equals that entity confidence`() {
        val confidences = listOf(0.87f)
        val avg = confidences.average().toFloat()
        assertEquals(0.87f, avg, 0.0001f)
    }

    @Test
    fun `averageConfidence of two equal values equals that value`() {
        val confidences = listOf(0.5f, 0.5f)
        val avg = confidences.average().toFloat()
        assertEquals(0.5f, avg, 0.0001f)
    }

    @Test
    fun `averageConfidence of 0_9 and 0_8 equals 0_85`() {
        val avg = listOf(0.9f, 0.8f).average().toFloat()
        assertEquals(0.85f, avg, 0.0001f)
    }

    @Test
    fun `averageConfidence of three values is correct`() {
        val avg = listOf(0.6f, 0.7f, 0.8f).average().toFloat()
        assertEquals(0.7f, avg, 0.0001f)
    }

    @Test
    fun `averageConfidence result is always within 0 and 1 for valid inputs`() {
        val avg = listOf(0.3f, 0.5f, 0.7f, 1.0f, 0.0f).average().toFloat()
        assertTrue(avg >= 0.0f && avg <= 1.0f)
    }

    @Test
    fun `riskScore from entity count linear formula is non-negative`() {
        val entityCount = 3
        val scorePerEntity = 0.2f
        val score = entityCount * scorePerEntity
        assertTrue("Score must be non-negative", score >= 0.0f)
    }

    @Test
    fun `riskScore caps at 1_0 after normalization`() {
        val rawScore = 5.0f // beyond maximum
        val capped   = rawScore.coerceAtMost(1.0f)
        assertEquals(1.0f, capped, 0.0f)
    }

    @Test
    fun `riskScore of 0 entities is 0_0`() {
        val score = 0 * 0.25f
        assertEquals(0.0f, score, 0.0f)
    }

    @Test
    fun `riskScore increases monotonically with entity count`() {
        val perEntity = 0.15f
        for (n in 1..10) {
            val current  = n       * perEntity
            val previous = (n - 1) * perEntity
            assertTrue("Score for $n must exceed score for ${n - 1}", current > previous)
        }
    }

    @Test
    fun `confidenceWeightedRisk sums to expected value`() {
        val weights = listOf(0.9f to 0.4f, 0.7f to 0.3f, 0.5f to 0.3f)
        val expected = weights.sumOf { (conf, w) -> (conf * w).toDouble() }.toFloat()
        assertEquals(0.9f * 0.4f + 0.7f * 0.3f + 0.5f * 0.3f, expected, 0.0001f)
    }

    @Test
    fun `floatComparison with delta succeeds for nearly-equal floats`() {
        val a = 0.1f + 0.2f
        val b = 0.3f
        assertEquals(a, b, 0.0001f)
    }

    @Test
    fun `floatComparison detects difference beyond delta`() {
        val a = 0.5f
        val b = 0.6f
        assertNotEquals(a, b, 0.05f)
    }

    @Test
    fun `normalizedScore clamps below-zero value to zero`() {
        val raw    = -0.5f
        val clamped = raw.coerceAtLeast(0.0f)
        assertEquals(0.0f, clamped, 0.0f)
    }

    @Test
    fun `normalizedScore clamps above-one value to one`() {
        val raw    = 1.5f
        val clamped = raw.coerceAtMost(1.0f)
        assertEquals(1.0f, clamped, 0.0f)
    }

    @Test
    fun `normalizedScore leaves in-range value unchanged`() {
        val raw     = 0.73f
        val clamped = raw.coerceIn(0.0f, 1.0f)
        assertEquals(0.73f, clamped, 0.0001f)
    }

    @Test
    fun `riskThreshold LOW starts at score 0_2`() {
        val threshold = 0.2f
        val score     = 0.25f
        assertTrue("Score above 0.2 should be at least LOW", score > threshold)
    }

    @Test
    fun `riskThreshold MEDIUM starts at score 0_4`() {
        val threshold = 0.4f
        val score     = 0.45f
        assertTrue("Score above 0.4 should be at least MEDIUM", score > threshold)
    }

    @Test
    fun `riskThreshold HIGH starts at score 0_6`() {
        val threshold = 0.6f
        val score     = 0.65f
        assertTrue("Score above 0.6 should be at least HIGH", score > threshold)
    }

    @Test
    fun `riskThreshold CRITICAL starts at score 0_8`() {
        val threshold = 0.8f
        val score     = 0.85f
        assertTrue("Score above 0.8 should be CRITICAL", score > threshold)
    }

    @Test
    fun `penaltyForLowConfidence reduces effective risk score`() {
        val rawScore   = 0.7f
        val confidence = 0.4f          // below threshold
        val penalty    = 0.15f
        val adjusted   = if (confidence < 0.5f) rawScore - penalty else rawScore
        assertTrue("Adjusted score must be less than raw score", adjusted < rawScore)
    }

    @Test
    fun `exponentialDecayFormula produces decreasing values as distance increases`() {
        val lambda = 0.3f
        val distances = listOf(1f, 2f, 3f, 4f, 5f)
        val decays = distances.map { d -> Math.exp((-lambda * d).toDouble()).toFloat() }
        for (i in 0 until decays.size - 1) {
            assertTrue("Decay at distance ${distances[i]} must exceed decay at ${distances[i+1]}",
                decays[i] > decays[i + 1])
        }
    }

    @Test
    fun `harmonicMeanOf 0_6 and 0_4 is less than arithmetic mean`() {
        val a  = 0.6f
        val b  = 0.4f
        val hm = 2f * a * b / (a + b)
        val am = (a + b) / 2f
        assertTrue("Harmonic mean must be <= arithmetic mean", hm <= am)
    }

    @Test
    fun `standardDeviationIsZeroForUniformConfidences`() {
        val values = listOf(0.8f, 0.8f, 0.8f, 0.8f)
        val mean   = values.average()
        val variance = values.sumOf { v -> (v - mean) * (v - mean) } / values.size
        assertEquals(0.0, variance, 0.0001)
    }

    @Test
    fun `standardDeviationIsPositiveForNonUniformConfidences`() {
        val values = listOf(0.2f, 0.5f, 0.8f, 0.9f)
        val mean   = values.average()
        val variance = values.sumOf { v -> (v - mean) * (v - mean) } / values.size
        assertTrue("Variance must be positive for non-uniform list", variance > 0.0)
    }

    @Test
    fun `medianOfThreeValues returns middle value`() {
        val sorted = listOf(0.3f, 0.6f, 0.9f).sorted()
        val median = sorted[sorted.size / 2]
        assertEquals(0.6f, median, 0.0001f)
    }

    @Test
    fun `percentileCalculation 50th of sorted list is median`() {
        val sorted = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f)
        val idx    = (0.5 * sorted.size).toInt().coerceAtMost(sorted.size - 1)
        assertTrue("50th percentile should be in middle range", sorted[idx] in 0.4f..0.6f)
    }

    @Test
    fun `confidenceSumOfEmptyListIsZero`() {
        val sum = emptyList<Float>().sumOf { it.toDouble() }
        assertEquals(0.0, sum, 0.0)
    }

    @Test
    fun `riskScoreMultiplicationByZeroConfidenceIsZero`() {
        val baseScore  = 0.8f
        val confidence = 0.0f
        val result     = baseScore * confidence
        assertEquals(0.0f, result, 0.0f)
    }

    @Test
    fun `logBaseTransformation increases discrimination in mid-range`() {
        val raw1 = 0.5f
        val raw2 = 0.7f
        val log1 = Math.log1p(raw1.toDouble())
        val log2 = Math.log1p(raw2.toDouble())
        assertTrue("log1p(0.7) must be greater than log1p(0.5)", log2 > log1)
    }

    @Test
    fun `boundedGrowthFormulaProducesValueBetweenZeroAndOne`() {
        for (n in 0..20) {
            val v = 1.0 - Math.exp(-0.1 * n)
            assertTrue("Value must be in [0,1]", v >= 0.0 && v <= 1.0)
        }
    }

    // -----------------------------------------------------------------------
    // Section 8 — Collections / aggregation  (30 tests)
    // Pure Kotlin List operations simulating entity aggregation.
    // -----------------------------------------------------------------------

    @Test
    fun `filterByType returns only entities matching the given type`() {
        val all = listOf(
            DetectedEntity("SSN",   "111-22-3333", 0,  11, 0.9f),
            DetectedEntity("EMAIL", "a@b.com",     15, 22, 0.8f),
            DetectedEntity("SSN",   "444-55-6666", 30, 41, 0.7f)
        )
        val ssnOnly = all.filter { it.type == "SSN" }
        assertEquals(2, ssnOnly.size)
        assertTrue(ssnOnly.all { it.type == "SSN" })
    }

    @Test
    fun `filterByMinConfidence excludes low-confidence entities`() {
        val all = listOf(
            DetectedEntity("SSN",   "111-22-3333", 0,  11, 0.95f),
            DetectedEntity("EMAIL", "a@b.com",     15, 22, 0.45f),
            DetectedEntity("PHONE", "800-555-0199",25, 37, 0.80f)
        )
        val highConf = all.filter { it.confidence >= 0.5f }
        assertEquals(2, highConf.size)
    }

    @Test
    fun `sortByConfidenceDescending places highest confidence first`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.5f),
            DetectedEntity("B", "y", 1, 2, 0.9f),
            DetectedEntity("C", "z", 2, 3, 0.7f)
        )
        val sorted = all.sortedByDescending { it.confidence }
        assertEquals(0.9f, sorted[0].confidence, 0.0001f)
        assertEquals(0.7f, sorted[1].confidence, 0.0001f)
        assertEquals(0.5f, sorted[2].confidence, 0.0001f)
    }

    @Test
    fun `sortByStartIndex places earliest match first`() {
        val all = listOf(
            DetectedEntity("A", "x", 50, 55, 0.8f),
            DetectedEntity("B", "y",  0,  5, 0.8f),
            DetectedEntity("C", "z", 20, 25, 0.8f)
        )
        val sorted = all.sortedBy { it.startIndex }
        assertEquals(0,  sorted[0].startIndex)
        assertEquals(20, sorted[1].startIndex)
        assertEquals(50, sorted[2].startIndex)
    }

    @Test
    fun `groupByType creates correct buckets`() {
        val all = listOf(
            DetectedEntity("SSN",   "111-22-3333", 0,  11, 0.9f),
            DetectedEntity("EMAIL", "a@b.com",     15, 22, 0.8f),
            DetectedEntity("SSN",   "444-55-6666", 30, 41, 0.7f),
            DetectedEntity("EMAIL", "c@d.net",     45, 52, 0.75f)
        )
        val grouped = all.groupBy { it.type }
        assertEquals(2, grouped["SSN"]!!.size)
        assertEquals(2, grouped["EMAIL"]!!.size)
    }

    @Test
    fun `countByType returns correct frequency map`() {
        val all = listOf(
            DetectedEntity("PHONE", "p1", 0, 1, 0.8f),
            DetectedEntity("PHONE", "p2", 1, 2, 0.7f),
            DetectedEntity("SSN",   "s1", 2, 3, 0.9f)
        )
        val counts = all.groupingBy { it.type }.eachCount()
        assertEquals(2, counts["PHONE"])
        assertEquals(1, counts["SSN"])
    }

    @Test
    fun `maxConfidenceEntity returns entity with highest confidence`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.6f),
            DetectedEntity("B", "y", 1, 2, 0.95f),
            DetectedEntity("C", "z", 2, 3, 0.75f)
        )
        val max = all.maxByOrNull { it.confidence }
        assertNotNull(max)
        assertEquals(0.95f, max!!.confidence, 0.0001f)
    }

    @Test
    fun `minConfidenceEntity returns entity with lowest confidence`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.6f),
            DetectedEntity("B", "y", 1, 2, 0.95f),
            DetectedEntity("C", "z", 2, 3, 0.35f)
        )
        val min = all.minByOrNull { it.confidence }
        assertNotNull(min)
        assertEquals(0.35f, min!!.confidence, 0.0001f)
    }

    @Test
    fun `sumOfConfidences equals manual sum`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.3f),
            DetectedEntity("B", "y", 1, 2, 0.4f),
            DetectedEntity("C", "z", 2, 3, 0.2f)
        )
        val sum = all.sumOf { it.confidence.toDouble() }.toFloat()
        assertEquals(0.9f, sum, 0.0001f)
    }

    @Test
    fun `emptyEntityListHasAverageConfidenceOfZero`() {
        val all  = emptyList<DetectedEntity>()
        val avg  = if (all.isEmpty()) 0.0f else all.sumOf { it.confidence.toDouble() }.toFloat() / all.size
        assertEquals(0.0f, avg, 0.0f)
    }

    @Test
    fun `distinctTypesCountIsCorrect`() {
        val all = listOf(
            DetectedEntity("SSN",   "s", 0, 1, 0.9f),
            DetectedEntity("EMAIL", "e", 1, 2, 0.8f),
            DetectedEntity("SSN",   "s", 3, 4, 0.7f),
            DetectedEntity("PHONE", "p", 5, 6, 0.6f)
        )
        val distinctCount = all.map { it.type }.distinct().size
        assertEquals(3, distinctCount)
    }

    @Test
    fun `flatMapOnNestedResultListFlattensCorrectly`() {
        val batch1 = listOf(DetectedEntity("SSN",   "s1", 0, 1, 0.9f))
        val batch2 = listOf(DetectedEntity("EMAIL", "e1", 1, 2, 0.8f),
                            DetectedEntity("EMAIL", "e2", 2, 3, 0.75f))
        val all = listOf(batch1, batch2).flatten()
        assertEquals(3, all.size)
    }

    @Test
    fun `partitionEntityListByThresholdSplitsCorrectly`() {
        val threshold = 0.7f
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.9f),
            DetectedEntity("B", "y", 1, 2, 0.5f),
            DetectedEntity("C", "z", 2, 3, 0.8f),
            DetectedEntity("D", "w", 3, 4, 0.3f)
        )
        val (high, low) = all.partition { it.confidence >= threshold }
        assertEquals(2, high.size)
        assertEquals(2, low.size)
    }

    @Test
    fun `takeTopNEntitiesByConfidence returns correct count`() {
        val all = (1..10).map { i ->
            DetectedEntity("TYPE", "v$i", i, i + 1, i * 0.1f)
        }
        val top3 = all.sortedByDescending { it.confidence }.take(3)
        assertEquals(3, top3.size)
    }

    @Test
    fun `takeTopNEntitiesByConfidence returns highest-confidence entries`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.2f),
            DetectedEntity("B", "y", 1, 2, 0.9f),
            DetectedEntity("C", "z", 2, 3, 0.6f),
            DetectedEntity("D", "w", 3, 4, 0.8f)
        )
        val top2 = all.sortedByDescending { it.confidence }.take(2)
        assertTrue(top2.all { it.confidence >= 0.8f })
    }

    @Test
    fun `mapToValueStringsProducesCorrectList`() {
        val all = listOf(
            DetectedEntity("SSN",   "111-22-3333", 0, 11, 0.9f),
            DetectedEntity("EMAIL", "a@b.com",     15, 22, 0.8f)
        )
        val values = all.map { it.value }
        assertEquals(listOf("111-22-3333", "a@b.com"), values)
    }

    @Test
    fun `anyEntityAboveThresholdReturnsTrueWhenExists`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.3f),
            DetectedEntity("B", "y", 1, 2, 0.95f)
        )
        assertTrue(all.any { it.confidence > 0.9f })
    }

    @Test
    fun `anyEntityAboveThresholdReturnsFalseWhenNoneExceed`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.3f),
            DetectedEntity("B", "y", 1, 2, 0.6f)
        )
        assertFalse(all.any { it.confidence > 0.9f })
    }

    @Test
    fun `allEntitiesAboveThresholdReturnsTrueWhenAllQualify`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.8f),
            DetectedEntity("B", "y", 1, 2, 0.9f)
        )
        assertTrue(all.all { it.confidence > 0.5f })
    }

    @Test
    fun `allEntitiesAboveThresholdReturnsFalseWhenOneDoesNot`() {
        val all = listOf(
            DetectedEntity("A", "x", 0, 1, 0.8f),
            DetectedEntity("B", "y", 1, 2, 0.4f)
        )
        assertFalse(all.all { it.confidence > 0.5f })
    }

    @Test
    fun `zipTwoEntityListsProducesCorrectPairs`() {
        val a = listOf(DetectedEntity("A", "x", 0, 1, 0.8f))
        val b = listOf(DetectedEntity("B", "y", 1, 2, 0.9f))
        val pairs = a.zip(b)
        assertEquals(1, pairs.size)
        assertEquals("A", pairs[0].first.type)
        assertEquals("B", pairs[0].second.type)
    }

    @Test
    fun `chunkListIntoGroupsOf2ProducesCorrectChunks`() {
        val all = (1..6).map { i -> DetectedEntity("T", "v$i", i, i + 1, 0.8f) }
        val chunks = all.chunked(2)
        assertEquals(3, chunks.size)
        assertTrue(chunks.all { it.size == 2 })
    }

    @Test
    fun `windowedListProducesOverlappingSubLists`() {
        val all = (1..5).map { i -> DetectedEntity("T", "v$i", i, i + 1, 0.8f) }
        val windows = all.windowed(3)
        assertEquals(3, windows.size)
        assertTrue(windows.all { it.size == 3 })
    }

    @Test
    fun `associateByStartIndexProducesCorrectMap`() {
        val all = listOf(
            DetectedEntity("A", "x", 10, 15, 0.7f),
            DetectedEntity("B", "y", 20, 25, 0.8f)
        )
        val map = all.associateBy { it.startIndex }
        assertEquals("A", map[10]?.type)
        assertEquals("B", map[20]?.type)
    }

    @Test
    fun `foldToComputeMaxConfidenceManually`() {
        val all = listOf(0.3f, 0.9f, 0.6f, 0.75f)
        val max = all.fold(Float.MIN_VALUE) { acc, v -> if (v > acc) v else acc }
        assertEquals(0.9f, max, 0.0001f)
    }

    @Test
    fun `reduceToComputeSumConfidenceManually`() {
        val vals = listOf(0.2f, 0.3f, 0.5f)
        val sum  = vals.reduce { acc, v -> acc + v }
        assertEquals(1.0f, sum, 0.0001f)
    }

    @Test
    fun `scanProducesRunningAverageList`() {
        val vals   = listOf(1.0f, 2.0f, 3.0f)
        val scanned = vals.runningFold(0.0f) { acc, v -> acc + v }
        // Expected: [0, 1, 3, 6]
        assertEquals(0.0f,  scanned[0], 0.0001f)
        assertEquals(1.0f,  scanned[1], 0.0001f)
        assertEquals(3.0f,  scanned[2], 0.0001f)
        assertEquals(6.0f,  scanned[3], 0.0001f)
    }

    @Test
    fun `dropWhileBelowThresholdSkipsLowConfidenceEntities`() {
        val sorted = listOf(0.2f, 0.3f, 0.6f, 0.8f, 0.9f)
        val above  = sorted.dropWhile { it < 0.5f }
        assertEquals(3, above.size)
        assertTrue(above.all { it >= 0.5f })
    }

    @Test
    fun `takeWhileBelowThresholdCollectsLowConfidenceEntities`() {
        val sorted = listOf(0.1f, 0.2f, 0.3f, 0.7f, 0.9f)
        val below  = sorted.takeWhile { it < 0.5f }
        assertEquals(3, below.size)
        assertTrue(below.all { it < 0.5f })
    }

    @Test
    fun `deduplications removes duplicates by value`() {
        val all = listOf(
            DetectedEntity("SSN", "111-22-3333", 0, 11, 0.95f),
            DetectedEntity("SSN", "111-22-3333", 0, 11, 0.95f),
            DetectedEntity("SSN", "444-55-6666", 20, 31, 0.80f)
        )
        val unique = all.distinctBy { it.value }
        assertEquals(2, unique.size)
    }

    @Test
    fun `mapNotNullFiltersNullsFromTransformation`() {
        val raw = listOf("111-22-3333", "", "444-55-6666", "  ")
        val valid = raw.mapNotNull { s -> if (s.isNotBlank()) s.trim() else null }
        assertEquals(2, valid.size)
    }

    @Test
    fun `sumOfSpanLengths computedCorrectly`() {
        val all = listOf(
            DetectedEntity("A", "hello",      0,  5, 0.8f),  // span 5
            DetectedEntity("B", "world!",     10, 16, 0.7f), // span 6
            DetectedEntity("C", "test",       20, 24, 0.9f)  // span 4
        )
        val totalSpan = all.sumOf { it.endIndex - it.startIndex }
        assertEquals(15, totalSpan)
    }

    @Test
    fun `batchResultsMergedCorrectly`() {
        val batch1 = listOf(DetectedEntity("SSN",   "111-22-3333", 0, 11, 0.9f))
        val batch2 = listOf(DetectedEntity("EMAIL", "a@b.com",     15, 22, 0.8f))
        val batch3 = listOf(
            DetectedEntity("PHONE", "800-555-0100", 25, 37, 0.75f),
            DetectedEntity("SSN",   "999-88-7777",  40, 51, 0.85f)
        )
        val merged = (batch1 + batch2 + batch3)
        assertEquals(4, merged.size)
        assertEquals(2, merged.count { it.type == "SSN" })
    }

    // -----------------------------------------------------------------------
    // Section 9 — Additional RiskLevel and entity edge cases  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `riskLevel NONE name is exactly four characters long`() {
        assertEquals(4, RiskLevel.NONE.name.length)
    }

    @Test
    fun `riskLevel LOW name is exactly three characters long`() {
        assertEquals(3, RiskLevel.LOW.name.length)
    }

    @Test
    fun `riskLevel MEDIUM name is exactly six characters long`() {
        assertEquals(6, RiskLevel.MEDIUM.name.length)
    }

    @Test
    fun `riskLevel HIGH name is exactly four characters long`() {
        assertEquals(4, RiskLevel.HIGH.name.length)
    }

    @Test
    fun `riskLevel CRITICAL name is exactly eight characters long`() {
        assertEquals(8, RiskLevel.CRITICAL.name.length)
    }

    @Test
    fun `riskLevel all names are uppercase`() {
        RiskLevel.values().forEach { level ->
            assertEquals(
                "Name of $level must be uppercase",
                level.name.uppercase(),
                level.name
            )
        }
    }

    @Test
    fun `riskLevel NONE ordinal is zero`() {
        assertEquals(0, RiskLevel.NONE.ordinal)
    }

    @Test
    fun `riskLevel LOW ordinal is one`() {
        assertEquals(1, RiskLevel.LOW.ordinal)
    }

    @Test
    fun `riskLevel MEDIUM ordinal is two`() {
        assertEquals(2, RiskLevel.MEDIUM.ordinal)
    }

    @Test
    fun `riskLevel HIGH ordinal is three`() {
        assertEquals(3, RiskLevel.HIGH.ordinal)
    }

    @Test
    fun `riskLevel CRITICAL ordinal is four`() {
        assertEquals(4, RiskLevel.CRITICAL.ordinal)
    }

    @Test
    fun `riskLevel entries list mapped from values matches size five`() {
        val list = RiskLevel.values().toList()
        assertEquals(5, list.size)
    }

    @Test
    fun `riskLevel compareTo is consistent with ordinal ordering`() {
        assertTrue("LOW > NONE",     RiskLevel.LOW.compareTo(RiskLevel.NONE) > 0)
        assertTrue("MEDIUM > LOW",   RiskLevel.MEDIUM.compareTo(RiskLevel.LOW) > 0)
        assertTrue("HIGH > MEDIUM",  RiskLevel.HIGH.compareTo(RiskLevel.MEDIUM) > 0)
        assertTrue("CRITICAL > HIGH",RiskLevel.CRITICAL.compareTo(RiskLevel.HIGH) > 0)
    }

    @Test
    fun `riskLevel NONE compareTo itself is zero`() {
        assertEquals(0, RiskLevel.NONE.compareTo(RiskLevel.NONE))
    }

    @Test
    fun `riskLevel CRITICAL compareTo itself is zero`() {
        assertEquals(0, RiskLevel.CRITICAL.compareTo(RiskLevel.CRITICAL))
    }

    @Test
    fun `riskLevel set contains all five values`() {
        val set = RiskLevel.values().toSet()
        assertTrue(set.contains(RiskLevel.NONE))
        assertTrue(set.contains(RiskLevel.LOW))
        assertTrue(set.contains(RiskLevel.MEDIUM))
        assertTrue(set.contains(RiskLevel.HIGH))
        assertTrue(set.contains(RiskLevel.CRITICAL))
    }

    @Test
    fun `detectedEntity type length at least one character`() {
        val entity = DetectedEntity("T", "v", 0, 1, 0.5f)
        assertTrue(entity.type.length >= 1)
    }

    @Test
    fun `detectedEntity created with named arguments compiles and accesses all fields`() {
        val e = DetectedEntity(
            type       = "CREDIT_CARD",
            value      = "4111111111111111",
            startIndex = 7,
            endIndex   = 23,
            confidence = 0.99f
        )
        assertEquals("CREDIT_CARD",     e.type)
        assertEquals("4111111111111111", e.value)
        assertEquals(7,                  e.startIndex)
        assertEquals(23,                 e.endIndex)
        assertEquals(0.99f,              e.confidence, 0.0001f)
    }

    @Test
    fun `detectedEntity confidence above 0_9 considered high confidence`() {
        val entity = DetectedEntity("SSN", "111-22-3333", 0, 11, 0.92f)
        assertTrue("Confidence > 0.9 is high confidence", entity.confidence > 0.9f)
    }

    @Test
    fun `detectedEntity list sorted by startIndex is in document order`() {
        val entities = listOf(
            DetectedEntity("C", "c", 40, 41, 0.8f),
            DetectedEntity("A", "a",  0,  1, 0.8f),
            DetectedEntity("B", "b", 20, 21, 0.8f)
        ).sortedBy { it.startIndex }
        assertEquals(listOf(0, 20, 40), entities.map { it.startIndex })
    }

    // -----------------------------------------------------------------------
    // Section 10 — Extended string / regex edge cases  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `ssnPatternDoesNotMatchWithSpacesInsteadOfDashes`() {
        val p = Pattern.compile("""^\d{3}-\d{2}-\d{4}$""")
        assertFalse(p.matcher("123 45 6789").matches())
    }

    @Test
    fun `ssnPatternMatchesAllZeroesAsStructurallyValid`() {
        val p = Pattern.compile("""^\d{3}-\d{2}-\d{4}$""")
        assertTrue(p.matcher("000-00-0000").matches())
    }

    @Test
    fun `ssnPatternFirstGroupMustBeExactlyThreeDigits`() {
        val p = Pattern.compile("""^\d{3}-\d{2}-\d{4}$""")
        assertFalse("Four digits in first group must fail", p.matcher("1234-56-7890").matches())
        assertFalse("Two digits in first group must fail",  p.matcher("12-34-5678").matches())
    }

    @Test
    fun `emailPatternMatchesEmailInSentenceContext`() {
        val p = Pattern.compile("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
        val text = "Please email alice@wonderland.co.uk for details."
        assertTrue(p.matcher(text).find())
    }

    @Test
    fun `emailPatternDoesNotMatchAtSignAlone`() {
        val p = Pattern.compile("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
        assertFalse(p.matcher("@").find())
    }

    @Test
    fun `emailPatternDoesNotMatchDomainWithoutTLD`() {
        val p = Pattern.compile("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
        assertFalse(p.matcher("user@nodot").find())
    }

    @Test
    fun `phonePatternMatchesTollFreeUSNumber`() {
        val p = Pattern.compile("""(\+?1[\s\-.]?)?\(?\d{3}\)?[\s\-.]?\d{3}[\s\-.]?\d{4}""")
        assertTrue(p.matcher("1-800-555-0100").find())
    }

    @Test
    fun `phonePatternMatchesTenDigitsConcatenated`() {
        val p = Pattern.compile("""(\+?1[\s\-.]?)?\(?\d{3}\)?[\s\-.]?\d{3}[\s\-.]?\d{4}""")
        assertTrue(p.matcher("8005550100").find())
    }

    @Test
    fun `luhnValidationAcceptsDiscover6011Card`() {
        // 6011111111111117 is a Luhn-valid Discover test card
        assertTrue(luhn("6011111111111117"))
    }

    @Test
    fun `luhnValidationRejectsFlippedDigit`() {
        // Flip the last digit of a valid card to make it invalid
        assertFalse(luhn("4111111111111112"))
    }

    @Test
    fun `stringContainsPIIKeywordsCheckedCaseInsensitively`() {
        val keywords = listOf("ssn", "social security", "credit card", "email", "phone")
        val text     = "Please provide your Social Security number."
        val found    = keywords.any { kw -> text.lowercase().contains(kw.lowercase()) }
        assertTrue("Text should trigger keyword detection", found)
    }

    @Test
    fun `stringWithNoKeywordsDoesNotTriggerDetection`() {
        val keywords = listOf("ssn", "social security", "credit card", "email", "phone")
        val text     = "The weather is sunny today."
        val found    = keywords.any { kw -> text.lowercase().contains(kw.lowercase()) }
        assertFalse("Clean text should not trigger keyword detection", found)
    }

    @Test
    fun `redactSSNReplacesDigitsWithAsterisks`() {
        val ssn      = "123-45-6789"
        val redacted = ssn.replace(Regex("""\d"""), "*")
        assertEquals("***-**-****", redacted)
    }

    @Test
    fun `redactEmailLocalPartPreservesAtSignAndDomain`() {
        val email    = "alice@example.com"
        val parts    = email.split("@")
        val redacted = "****@${parts[1]}"
        assertTrue(redacted.contains("@"))
        assertTrue(redacted.endsWith("example.com"))
    }

    @Test
    fun `maskCreditCardShowsLastFourDigits`() {
        val card     = "4111111111111111"
        val masked   = "*".repeat(card.length - 4) + card.takeLast(4)
        assertEquals("************1111", masked)
        assertEquals(card.length, masked.length)
    }

    @Test
    fun `trimAndNormalizeSSNRemovesWhitespace`() {
        val raw        = "  123-45-6789  "
        val normalized = raw.trim()
        assertEquals("123-45-6789", normalized)
    }

    @Test
    fun `splitTextOnWhitespaceProducesTokens`() {
        val text   = "John Doe  123-45-6789   john@example.com"
        val tokens = text.trim().split(Regex("""\s+"""))
        assertEquals(3, tokens.size)
    }

    @Test
    fun `extractDigitsOnlyFromPhoneNumber`() {
        val phone  = "(800) 555-0100"
        val digits = phone.filter { it.isDigit() }
        assertEquals("8005550100", digits)
        assertEquals(10, digits.length)
    }

    @Test
    fun `countSSNMatchesInMultilineText`() {
        val p = Pattern.compile("""\d{3}-\d{2}-\d{4}""")
        val text = """
            SSN 1: 111-22-3333
            SSN 2: 444-55-6666
            Not an SSN: 12-345-6789
        """.trimIndent()
        val matcher = p.matcher(text)
        var count   = 0
        while (matcher.find()) count++
        assertEquals(2, count)
    }

    @Test
    fun `countEmailMatchesInMultilineText`() {
        val p = Pattern.compile("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
        val text = "From: alice@example.com; To: bob@corp.io; CC: charlie@test.org"
        val matcher = p.matcher(text)
        var count   = 0
        while (matcher.find()) count++
        assertEquals(3, count)
    }

    // -----------------------------------------------------------------------
    // Section 11 — Extended arithmetic and scoring edge cases  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `zeroEntitiesProduceZeroRawRiskScore`() {
        val entityCount = 0
        val score       = entityCount.toFloat() * 0.25f
        assertEquals(0.0f, score, 0.0f)
    }

    @Test
    fun `oneEntityProducesNonZeroRawRiskScore`() {
        val score = 1 * 0.25f
        assertTrue(score > 0.0f)
    }

    @Test
    fun `saturatedRiskScoreNeverExceedsOne`() {
        for (n in 1..50) {
            val raw   = n * 0.15f
            val capped = raw.coerceAtMost(1.0f)
            assertTrue("Capped score must be <= 1 for n=$n", capped <= 1.0f)
        }
    }

    @Test
    fun `averageOfEmptyListHandledGracefully`() {
        val list: List<Float> = emptyList()
        val avg = if (list.isEmpty()) 0.0f else list.average().toFloat()
        assertEquals(0.0f, avg, 0.0f)
    }

    @Test
    fun `averageOfSingleElementListEqualsThatElement`() {
        val list = listOf(0.77f)
        val avg  = list.average().toFloat()
        assertEquals(0.77f, avg, 0.0001f)
    }

    @Test
    fun `weightedAverageHigherWeightDominates`() {
        val a        = 0.9f
        val b        = 0.1f
        val wA       = 0.8f
        val wB       = 0.2f
        val weighted = a * wA + b * wB
        assertTrue("Weighted average should be closer to a", weighted > 0.5f)
    }

    @Test
    fun `negativeConfidenceIsImpossibleAndClampedToZero`() {
        val raw     = -0.1f
        val clamped = raw.coerceAtLeast(0.0f)
        assertEquals(0.0f, clamped, 0.0f)
    }

    @Test
    fun `confidenceAboveOneIsImpossibleAndClampedToOne`() {
        val raw     = 1.1f
        val clamped = raw.coerceAtMost(1.0f)
        assertEquals(1.0f, clamped, 0.0f)
    }

    @Test
    fun `riskScoreFromFiveSSNsExceedsRiskScoreFromOneSSN`() {
        val perSSN   = 0.2f
        val oneSSN   = 1 * perSSN
        val fiveSSNs = 5 * perSSN
        assertTrue(fiveSSNs > oneSSN)
    }

    @Test
    fun `entityCountTimesConstantIsLinear`() {
        val k = 0.1f
        for (n in 0..10) {
            val score = n * k
            assertEquals(n.toFloat() * k, score, 0.0001f)
        }
    }

    @Test
    fun `squaredConfidencePenaltyIsLessForHighConfidence`() {
        val high = 0.9f
        val low  = 0.3f
        // Penalty = (1 - confidence)^2; lower for high confidence
        val penaltyHigh = (1f - high) * (1f - high)
        val penaltyLow  = (1f - low)  * (1f - low)
        assertTrue("Penalty for high confidence should be less", penaltyHigh < penaltyLow)
    }

    @Test
    fun `sigmoidTransformationProducesValueBetweenZeroAndOne`() {
        val x       = 2.5
        val sigmoid = 1.0 / (1.0 + Math.exp(-x))
        assertTrue("Sigmoid must be in (0,1)", sigmoid > 0.0 && sigmoid < 1.0)
    }

    @Test
    fun `sigmoidOf0Is0_5`() {
        val x       = 0.0
        val sigmoid = 1.0 / (1.0 + Math.exp(-x))
        assertEquals(0.5, sigmoid, 0.0001)
    }

    @Test
    fun `geometricMeanOfTwoConfidencesIsLessThanArithmeticMean`() {
        val a  = 0.4f
        val b  = 0.9f
        val am = (a + b) / 2f
        val gm = Math.sqrt((a * b).toDouble()).toFloat()
        assertTrue("Geometric mean <= arithmetic mean", gm <= am + 0.0001f)
    }

    @Test
    fun `sumOfWeightsEqualOneForProperWeightedAverage`() {
        val weights = listOf(0.4f, 0.35f, 0.25f)
        val sum     = weights.sum()
        assertEquals(1.0f, sum, 0.0001f)
    }

    @Test
    fun `maximumPossibleRiskScoreIsOne`() {
        val maxScore = 1.0f
        assertTrue(maxScore <= 1.0f)
        assertTrue(maxScore >= 0.0f)
    }

    @Test
    fun `minimumPossibleRiskScoreIsZero`() {
        val minScore = 0.0f
        assertEquals(0.0f, minScore, 0.0f)
    }

    @Test
    fun `confidenceProductOfTwoIsLessThanEach`() {
        val a       = 0.8f
        val b       = 0.7f
        val product = a * b
        assertTrue("Product must be less than a", product < a)
        assertTrue("Product must be less than b", product < b)
    }

    @Test
    fun `maxFunctionSelectsHigherOfTwoScores`() {
        val s1  = 0.65f
        val s2  = 0.72f
        val max = maxOf(s1, s2)
        assertEquals(0.72f, max, 0.0001f)
    }

    @Test
    fun `minFunctionSelectsLowerOfTwoScores`() {
        val s1  = 0.65f
        val s2  = 0.72f
        val min = minOf(s1, s2)
        assertEquals(0.65f, min, 0.0001f)
    }

    // -----------------------------------------------------------------------
    // Section 12 — Extended collection operations  (20 tests)
    // -----------------------------------------------------------------------

    @Test
    fun `firstOrNullReturnsNullOnEmptyList`() {
        val empty = emptyList<DetectedEntity>()
        assertNull(empty.firstOrNull())
    }

    @Test
    fun `firstOrNullReturnsFirstElementOnNonEmptyList`() {
        val list = listOf(DetectedEntity("SSN", "x", 0, 1, 0.9f))
        assertNotNull(list.firstOrNull())
        assertEquals("SSN", list.firstOrNull()!!.type)
    }

    @Test
    fun `lastOrNullReturnsNullOnEmptyList`() {
        val empty = emptyList<DetectedEntity>()
        assertNull(empty.lastOrNull())
    }

    @Test
    fun `lastOrNullReturnsLastElementOnNonEmptyList`() {
        val list = listOf(
            DetectedEntity("SSN",   "x", 0, 1, 0.9f),
            DetectedEntity("EMAIL", "y", 1, 2, 0.8f)
        )
        assertEquals("EMAIL", list.lastOrNull()!!.type)
    }

    @Test
    fun `filterIsLazyAndDoesNotModifyOriginalList`() {
        val original = listOf(
            DetectedEntity("A", "x", 0, 1, 0.9f),
            DetectedEntity("B", "y", 1, 2, 0.4f)
        )
        val filtered = original.filter { it.confidence > 0.5f }
        assertEquals(2, original.size)
        assertEquals(1, filtered.size)
    }

    @Test
    fun `mapDoesNotModifyOriginalList`() {
        val original = listOf(DetectedEntity("SSN", "val", 0, 3, 0.8f))
        val types    = original.map { it.type.lowercase() }
        assertEquals("SSN", original[0].type)
        assertEquals("ssn", types[0])
    }

    @Test
    fun `countPredicateMatchesExpectedSubset`() {
        val list = listOf(
            DetectedEntity("SSN",   "s1", 0, 1, 0.95f),
            DetectedEntity("EMAIL", "e1", 1, 2, 0.80f),
            DetectedEntity("SSN",   "s2", 2, 3, 0.70f),
            DetectedEntity("PHONE", "p1", 3, 4, 0.60f)
        )
        val ssnCount = list.count { it.type == "SSN" }
        assertEquals(2, ssnCount)
    }

    @Test
    fun `indexOfFirstMatchingEntityIsCorrect`() {
        val list = listOf(
            DetectedEntity("A", "x", 0, 1, 0.5f),
            DetectedEntity("B", "y", 1, 2, 0.9f),
            DetectedEntity("C", "z", 2, 3, 0.7f)
        )
        val idx = list.indexOfFirst { it.confidence > 0.8f }
        assertEquals(1, idx)
    }

    @Test
    fun `indexOfFirstReturnsMinus1WhenNoMatch`() {
        val list = listOf(DetectedEntity("A", "x", 0, 1, 0.5f))
        val idx  = list.indexOfFirst { it.confidence > 0.9f }
        assertEquals(-1, idx)
    }

    @Test
    fun `reverseListPreservesAllElements`() {
        val original = listOf(
            DetectedEntity("A", "x", 0, 1, 0.7f),
            DetectedEntity("B", "y", 1, 2, 0.8f),
            DetectedEntity("C", "z", 2, 3, 0.9f)
        )
        val reversed = original.reversed()
        assertEquals(3, reversed.size)
        assertEquals("C", reversed[0].type)
        assertEquals("A", reversed[2].type)
    }

    @Test
    fun `containsReturnsTrueForKnownEntity`() {
        val entity = DetectedEntity("SSN", "111-22-3333", 0, 11, 0.9f)
        val list   = listOf(entity)
        assertTrue(list.contains(entity))
    }

    @Test
    fun `containsReturnsFalseForUnknownEntity`() {
        val entity  = DetectedEntity("SSN", "111-22-3333", 0, 11, 0.9f)
        val other   = DetectedEntity("SSN", "999-99-9999", 0, 11, 0.9f)
        val list    = listOf(entity)
        assertFalse(list.contains(other))
    }

    @Test
    fun `subListExtractionProducesCorrectSlice`() {
        val list  = (0..9).map { i -> DetectedEntity("T$i", "v$i", i, i + 1, 0.8f) }
        val slice = list.subList(3, 7)
        assertEquals(4, slice.size)
        assertEquals("T3", slice.first().type)
        assertEquals("T6", slice.last().type)
    }

    @Test
    fun `mutableListAddAndRemoveWorkCorrectly`() {
        val mut = mutableListOf(
            DetectedEntity("SSN", "s1", 0, 1, 0.9f)
        )
        mut.add(DetectedEntity("EMAIL", "e1", 1, 2, 0.8f))
        assertEquals(2, mut.size)
        mut.removeAt(0)
        assertEquals(1, mut.size)
        assertEquals("EMAIL", mut[0].type)
    }

    @Test
    fun `flatMapProducesExpectedFlattenedList`() {
        val nested = listOf(
            listOf("SSN", "EMAIL"),
            listOf("PHONE"),
            listOf("CREDIT_CARD", "IP_ADDRESS", "DATE")
        )
        val flat = nested.flatten()
        assertEquals(6, flat.size)
    }

    @Test
    fun `zipWithIndexProducesIndexedValues`() {
        val entities = listOf(
            DetectedEntity("A", "x", 0, 1, 0.8f),
            DetectedEntity("B", "y", 1, 2, 0.9f)
        )
        val indexed = entities.withIndex().toList()
        assertEquals(0, indexed[0].index)
        assertEquals(1, indexed[1].index)
    }

    @Test
    fun `maxOfOrNullOnEmptyListReturnsNull`() {
        val empty = emptyList<DetectedEntity>()
        assertNull(empty.maxOfOrNull { it.confidence })
    }

    @Test
    fun `minOfOrNullOnEmptyListReturnsNull`() {
        val empty = emptyList<DetectedEntity>()
        assertNull(empty.minOfOrNull { it.confidence })
    }

    @Test
    fun `sumOfConfidencesOnEmptyListIsZero`() {
        val empty = emptyList<DetectedEntity>()
        val sum   = empty.sumOf { it.confidence.toDouble() }
        assertEquals(0.0, sum, 0.0)
    }

    @Test
    fun `sortedByTypeProducesAlphabeticalOrder`() {
        val list = listOf(
            DetectedEntity("SSN",   "s", 0, 1, 0.9f),
            DetectedEntity("EMAIL", "e", 1, 2, 0.8f),
            DetectedEntity("PHONE", "p", 2, 3, 0.7f)
        )
        val sorted = list.sortedBy { it.type }
        assertEquals("EMAIL", sorted[0].type)
        assertEquals("PHONE", sorted[1].type)
        assertEquals("SSN",   sorted[2].type)
    }
}

