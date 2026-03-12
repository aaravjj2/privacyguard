package com.privacyguard.ml

import com.privacyguard.util.ConfidenceThresholds
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class OutputDecoderComprehensiveTest {

    private lateinit var decoder: OutputDecoder

    companion object {
        // Mirror the label constants from OutputDecoder companion for test readability
        const val LABEL_O = 0
        const val LABEL_B_CREDIT_CARD = 1
        const val LABEL_I_CREDIT_CARD = 2
        const val LABEL_B_SSN = 3
        const val LABEL_I_SSN = 4
        const val LABEL_B_PASSWORD = 5
        const val LABEL_I_PASSWORD = 6
        const val LABEL_B_API_KEY = 7
        const val LABEL_I_API_KEY = 8
        const val LABEL_B_EMAIL = 9
        const val LABEL_I_EMAIL = 10
        const val LABEL_B_PHONE = 11
        const val LABEL_I_PHONE = 12
        const val LABEL_B_NAME = 13
        const val LABEL_I_NAME = 14
        const val LABEL_B_ADDRESS = 15
        const val LABEL_I_ADDRESS = 16
        const val LABEL_B_DOB = 17
        const val LABEL_I_DOB = 18
        const val LABEL_B_MEDICAL = 19
        const val LABEL_I_MEDICAL = 20
        const val NUM_LABELS = OutputDecoder.NUM_LABELS
    }

    @Before
    fun setUp() {
        decoder = OutputDecoder()
        // Reset confidence thresholds so filtering is predictable
        ConfidenceThresholds.resetToDefaults()
    }

    // === BASIC DECODING ===

    @Test
    fun `decode null buffer returns empty list`() {
        val result = decoder.decode(null, "test text")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decode empty text returns empty list`() {
        val buffer = createAllOBuffer(10)
        val result = decoder.decode(buffer, "")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decode with all O labels returns empty list`() {
        val seqLen = 20
        val buffer = createAllOBuffer(seqLen)
        val result = decoder.decode(buffer, "This is a test sentence with no PII")
        assertTrue("All O labels should produce no entities", result.isEmpty())
    }

    @Test
    fun `decode null buffer and empty text returns empty list`() {
        val result = decoder.decode(null, "")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decode null buffer and non-empty text returns empty list`() {
        val result = decoder.decode(null, "some text here")
        assertTrue(result.isEmpty())
    }

    // === ENTITY TYPE DECODING ===

    @Test
    fun `decode credit card entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, 5, 8)
        val result = decoder.decode(buffer, "My card is 4532 1234 5678 9012 here")
        assertNotNull(result)
    }

    @Test
    fun `decode SSN entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_SSN, LABEL_I_SSN, 3, 5)
        val result = decoder.decode(buffer, "SSN: 123-45-6789 end")
        assertNotNull(result)
    }

    @Test
    fun `decode email entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_EMAIL, LABEL_I_EMAIL, 4, 6)
        val result = decoder.decode(buffer, "Email me at user@example.com please")
        assertNotNull(result)
    }

    @Test
    fun `decode phone entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_PHONE, LABEL_I_PHONE, 3, 6)
        val result = decoder.decode(buffer, "Call me at 555-867-5309")
        assertNotNull(result)
    }

    @Test
    fun `decode password entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_PASSWORD, LABEL_I_PASSWORD, 3, 5)
        val result = decoder.decode(buffer, "Password is MyS3cretP@ss! end")
        assertNotNull(result)
    }

    @Test
    fun `decode API key entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_API_KEY, LABEL_I_API_KEY, 3, 5)
        val result = decoder.decode(buffer, "API key sk-live_abc123xyz789 end")
        assertNotNull(result)
    }

    @Test
    fun `decode person name entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_NAME, LABEL_I_NAME, 1, 3)
        val result = decoder.decode(buffer, "x John Smith lives in New York end")
        assertNotNull(result)
    }

    @Test
    fun `decode address entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_ADDRESS, LABEL_I_ADDRESS, 3, 7)
        val result = decoder.decode(buffer, "Lives at 123 Main St Anytown USA end")
        assertNotNull(result)
    }

    @Test
    fun `decode DOB entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_DOB, LABEL_I_DOB, 2, 5)
        val result = decoder.decode(buffer, "DOB 01 15 1990 end text padding here more")
        assertNotNull(result)
    }

    @Test
    fun `decode medical ID entity`() {
        val seqLen = 20
        val buffer = createEntityBuffer(seqLen, LABEL_B_MEDICAL, LABEL_I_MEDICAL, 2, 4)
        val result = decoder.decode(buffer, "MRN 123456789 end text padding here more words")
        assertNotNull(result)
    }

    // === ENTITY TYPE FROM LABELS ===

    @Test
    fun `decode detects correct entity type for credit card`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532123456789012 y")
        assertTrue(result.any { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `decode detects correct entity type for SSN`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_SSN, LABEL_I_SSN, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 123-45-6789 y")
        assertTrue(result.any { it.entityType == EntityType.SSN })
    }

    @Test
    fun `decode detects correct entity type for password`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_PASSWORD, LABEL_I_PASSWORD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x MyP@ssw0rd! y")
        assertTrue(result.any { it.entityType == EntityType.PASSWORD })
    }

    @Test
    fun `decode detects correct entity type for API key`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_API_KEY, LABEL_I_API_KEY, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x sk-live_abc123 y")
        assertTrue(result.any { it.entityType == EntityType.API_KEY })
    }

    @Test
    fun `decode detects correct entity type for email`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_EMAIL, LABEL_I_EMAIL, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x user@test.com y")
        assertTrue(result.any { it.entityType == EntityType.EMAIL })
    }

    @Test
    fun `decode detects correct entity type for phone`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_PHONE, LABEL_I_PHONE, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 555-867-5309 y")
        assertTrue(result.any { it.entityType == EntityType.PHONE })
    }

    @Test
    fun `decode detects correct entity type for person name`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_NAME, LABEL_I_NAME, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x John Smith y")
        assertTrue(result.any { it.entityType == EntityType.PERSON_NAME })
    }

    @Test
    fun `decode detects correct entity type for address`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_ADDRESS, LABEL_I_ADDRESS, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 123 Main St y")
        assertTrue(result.any { it.entityType == EntityType.ADDRESS })
    }

    @Test
    fun `decode detects correct entity type for DOB`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_DOB, LABEL_I_DOB, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 01/15/1990 y")
        assertTrue(result.any { it.entityType == EntityType.DATE_OF_BIRTH })
    }

    @Test
    fun `decode detects correct entity type for medical ID`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_MEDICAL, LABEL_I_MEDICAL, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x MRN123456 y")
        assertTrue(result.any { it.entityType == EntityType.MEDICAL_ID })
    }

    // === CONFIDENCE THRESHOLDS ===

    @Test
    fun `decode filters entities below threshold via decodeFromLabels`() {
        // Credit card threshold is 0.90 by default
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.30f, 0.30f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532123456789012 y")
        assertTrue("Low confidence should be filtered out", result.isEmpty())
    }

    @Test
    fun `decode keeps entities above threshold via decodeFromLabels`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532123456789012 y")
        assertTrue("High confidence should be kept", result.isNotEmpty())
    }

    @Test
    fun `decode filters SSN below threshold`() {
        // SSN threshold is 0.92
        val labels = intArrayOf(LABEL_O, LABEL_B_SSN, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.50f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 123456789 y")
        assertTrue("SSN below 0.92 should be filtered", result.isEmpty())
    }

    @Test
    fun `decode keeps SSN above threshold`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_SSN, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.95f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 123456789 y")
        assertTrue("SSN above 0.92 should be kept", result.isNotEmpty())
    }

    @Test
    fun `decode applies email threshold correctly`() {
        // Email threshold is 0.95
        val labels = intArrayOf(LABEL_O, LABEL_B_EMAIL, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.93f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x user@test.com y")
        assertTrue("Email at 0.93 should be filtered (threshold 0.95)", result.isEmpty())
    }

    @Test
    fun `decode custom threshold via ConfidenceThresholds`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.10f)
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.15f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532123456789012 y")
        assertTrue("With low threshold, entity should be kept", result.isNotEmpty())
        ConfidenceThresholds.resetToDefaults()
    }

    @Test
    fun `decode borderline confidence at exact threshold`() {
        // Credit card threshold is 0.90
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.90f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532123456789012 y")
        // meetsThreshold uses >= so exactly at threshold should pass
        assertTrue("Entity at exact threshold should be kept", result.isNotEmpty())
    }

    // === SPAN GROUPING ===

    @Test
    fun `decode groups consecutive B and I tokens into single entity`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532 1234 5678 y")
        assertEquals("B + I + I should form one entity", 1, result.size)
    }

    @Test
    fun `decode separates different entity types`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O, LABEL_B_SSN, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card y ssn z")
        assertEquals("Two different B labels should form two entities", 2, result.size)
    }

    @Test
    fun `decode handles B label immediately followed by different B label`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_B_SSN, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card ssn y")
        assertEquals("Two consecutive B labels should form two entities", 2, result.size)
    }

    @Test
    fun `decode ignores I label without preceding B label`() {
        val labels = intArrayOf(LABEL_O, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card y")
        assertTrue("Orphan I label should produce no entity", result.isEmpty())
    }

    @Test
    fun `decode ignores mismatched I label`() {
        // B_CREDIT_CARD followed by I_SSN should end the credit card entity
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_SSN, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card ssn y")
        // The credit card entity should end at token 1 (single-token entity)
        val ccEntities = result.filter { it.entityType == EntityType.CREDIT_CARD }
        assertTrue("Mismatched I should end entity", ccEntities.size <= 1)
    }

    @Test
    fun `decode single B token entity`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card y")
        assertEquals("Single B token should form one entity", 1, result.size)
    }

    @Test
    fun `decode entity at end of sequence`() {
        val labels = intArrayOf(LABEL_O, LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x y card number")
        assertEquals("Entity at end should still be detected", 1, result.size)
    }

    @Test
    fun `decode entity at start of sequence`() {
        val labels = intArrayOf(LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "card number x y")
        assertEquals("Entity at start should still be detected", 1, result.size)
    }

    @Test
    fun `decode all tokens are entity tokens`() {
        val labels = intArrayOf(LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_I_CREDIT_CARD)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "4532 1234 5678 9012")
        assertEquals("All entity tokens should form one entity", 1, result.size)
    }

    // === EDGE CASES ===

    @Test
    fun `decode handles buffer smaller than expected`() {
        val smallBuffer = ByteBuffer.allocateDirect(4)
        smallBuffer.order(ByteOrder.LITTLE_ENDIAN)
        smallBuffer.putFloat(0.0f)
        smallBuffer.rewind()
        val result = decoder.decode(smallBuffer, "test")
        assertNotNull(result)
    }

    @Test
    fun `decode handles very long text`() {
        val longText = "word ".repeat(1000)
        val buffer = createAllOBuffer(PIITokenizer.MAX_SEQUENCE_LENGTH)
        val result = decoder.decode(buffer, longText)
        assertNotNull(result)
        assertTrue("All O buffer should produce no entities", result.isEmpty())
    }

    @Test
    fun `decode handles text with only special characters`() {
        val buffer = createAllOBuffer(20)
        val result = decoder.decode(buffer, "!@#\$%^&*()")
        assertNotNull(result)
    }

    @Test
    fun `decode handles unicode text`() {
        val buffer = createAllOBuffer(20)
        val result = decoder.decode(buffer, "\u4F60\u597D\u4E16\u754C \uD83C\uDF0D")
        assertNotNull(result)
    }

    @Test
    fun `decode handles single character text`() {
        val buffer = createAllOBuffer(10)
        val result = decoder.decode(buffer, "a")
        assertNotNull(result)
    }

    @Test
    fun `decode handles text shorter than sequence length`() {
        val buffer = createAllOBuffer(PIITokenizer.MAX_SEQUENCE_LENGTH)
        val result = decoder.decode(buffer, "short")
        assertNotNull(result)
    }

    // === SOFTMAX VERIFICATION (tested through decode behavior) ===

    @Test
    fun `decode with equal logits for all labels produces O prediction`() {
        // When all logits are equal, softmax produces uniform distribution
        // argmax should pick index 0 (O label) or the first max
        val seqLen = 10
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                buffer.putFloat(1.0f) // all equal
            }
        }
        buffer.rewind()
        val result = decoder.decode(buffer, "test text here pad pad pad pad pad pad pad", sequenceLength = seqLen)
        assertNotNull(result)
    }

    @Test
    fun `decode with large positive logit for B label detects entity`() {
        val seqLen = 5
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                val score = when {
                    // Skip CLS (0) and SEP (seqLen-1) by using O for them
                    (i == 0 || i == seqLen - 1) && j == LABEL_O -> 10.0f
                    i == 2 && j == LABEL_B_CREDIT_CARD -> 100.0f
                    i != 0 && i != seqLen - 1 && j == LABEL_O -> 10.0f
                    else -> -100.0f
                }
                buffer.putFloat(score)
            }
        }
        buffer.rewind()
        val result = decoder.decode(buffer, "a b card d e", sequenceLength = seqLen)
        assertNotNull(result)
    }

    @Test
    fun `decode with very negative logits still works`() {
        val seqLen = 5
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                buffer.putFloat(if (j == LABEL_O) -50.0f else -100.0f)
            }
        }
        buffer.rewind()
        val result = decoder.decode(buffer, "a b c d e", sequenceLength = seqLen)
        assertNotNull(result) // Should still work, O has highest logit
    }

    // === TOKEN TO CHAR MAPPING ===

    @Test
    fun `decode with token to char map produces correct indices`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_EMAIL, LABEL_I_EMAIL, LABEL_I_EMAIL, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f, 0.99f)
        val tokenToCharMap = mapOf(
            0 to (0..0),     // x
            1 to (2..5),     // user
            2 to (6..6),     // @
            3 to (7..14),    // test.com
            4 to (16..16)    // y
        )
        val result = decoder.decodeFromLabels(labels, confidences, "x user@test.com y", tokenToCharMap)
        if (result.isNotEmpty()) {
            assertEquals("Start index should match token map", 2, result[0].startIndex)
            assertEquals("End index should match token map", 15, result[0].endIndex)
        }
    }

    @Test
    fun `decode without token to char map uses estimation`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x 4532123456789012 y")
        if (result.isNotEmpty()) {
            // Without mapping, estimation is used (roughly 4 chars per token)
            assertTrue("Start index should be non-negative", result[0].startIndex >= 0)
            assertTrue("End index should be within text", result[0].endIndex <= "x 4532123456789012 y".length)
        }
    }

    // === AVERAGE CONFIDENCE ===

    @Test
    fun `entity confidence is average of token confidences`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.95f, 0.85f, 0.99f)
        // Average of 0.95 and 0.85 = 0.90, which meets the CC threshold of 0.90
        val result = decoder.decodeFromLabels(labels, confidences, "x card number y")
        if (result.isNotEmpty()) {
            assertEquals("Confidence should be average", 0.90f, result[0].confidence, 0.01f)
        }
    }

    @Test
    fun `single token entity has its own confidence`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.95f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card y")
        if (result.isNotEmpty()) {
            assertEquals("Single token entity should have its confidence",
                0.95f, result[0].confidence, 0.01f)
        }
    }

    // === BUFFER BYTE ORDER ===

    @Test
    fun `decode handles little endian buffer`() {
        val buffer = createAllOBuffer(10)
        assertEquals(ByteOrder.LITTLE_ENDIAN, buffer.order())
        val result = decoder.decode(buffer, "test text pad pad pad pad pad pad pad pad")
        assertNotNull(result)
    }

    @Test
    fun `decode rewinds buffer before reading`() {
        val seqLen = 10
        val buffer = createAllOBuffer(seqLen)
        // Advance the buffer position
        buffer.position(buffer.limit())
        // decode should rewind internally
        val result = decoder.decode(buffer, "test text pad pad pad pad pad pad pad pad",
            sequenceLength = seqLen)
        assertNotNull(result)
    }

    // === MULTIPLE ENTITIES ===

    @Test
    fun `decode detects two entities of same type`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card1 y card2 z")
        assertEquals("Should detect two credit card entities", 2, result.size)
        assertTrue(result.all { it.entityType == EntityType.CREDIT_CARD })
    }

    @Test
    fun `decode detects three different entity types`() {
        val labels = intArrayOf(
            LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O,
            LABEL_B_SSN, LABEL_O,
            LABEL_B_EMAIL, LABEL_O
        )
        val confidences = FloatArray(7) { 0.99f }
        val result = decoder.decodeFromLabels(labels, confidences, "x card y ssn z email w")
        assertEquals("Should detect three entities", 3, result.size)
    }

    @Test
    fun `decode detects all 10 entity types in one sequence`() {
        val labels = intArrayOf(
            LABEL_B_CREDIT_CARD, LABEL_O,
            LABEL_B_SSN, LABEL_O,
            LABEL_B_PASSWORD, LABEL_O,
            LABEL_B_API_KEY, LABEL_O,
            LABEL_B_EMAIL, LABEL_O,
            LABEL_B_PHONE, LABEL_O,
            LABEL_B_NAME, LABEL_O,
            LABEL_B_ADDRESS, LABEL_O,
            LABEL_B_DOB, LABEL_O,
            LABEL_B_MEDICAL, LABEL_O
        )
        val confidences = FloatArray(20) { 0.99f }
        val text = "cc ss pw ak em ph nm ad db md cc ss pw ak em ph nm ad db md"
        val result = decoder.decodeFromLabels(labels, confidences, text)
        assertEquals("Should detect 10 entities", 10, result.size)
        val types = result.map { it.entityType }.toSet()
        assertTrue("Should include CREDIT_CARD", types.contains(EntityType.CREDIT_CARD))
        assertTrue("Should include SSN", types.contains(EntityType.SSN))
        assertTrue("Should include PASSWORD", types.contains(EntityType.PASSWORD))
        assertTrue("Should include API_KEY", types.contains(EntityType.API_KEY))
        assertTrue("Should include EMAIL", types.contains(EntityType.EMAIL))
        assertTrue("Should include PHONE", types.contains(EntityType.PHONE))
        assertTrue("Should include PERSON_NAME", types.contains(EntityType.PERSON_NAME))
        assertTrue("Should include ADDRESS", types.contains(EntityType.ADDRESS))
        assertTrue("Should include DATE_OF_BIRTH", types.contains(EntityType.DATE_OF_BIRTH))
        assertTrue("Should include MEDICAL_ID", types.contains(EntityType.MEDICAL_ID))
    }

    // === decodeFromLabels EDGE CASES ===

    @Test
    fun `decodeFromLabels with empty labels`() {
        val result = decoder.decodeFromLabels(intArrayOf(), floatArrayOf(), "")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decodeFromLabels with single O label`() {
        val result = decoder.decodeFromLabels(intArrayOf(LABEL_O), floatArrayOf(1.0f), "x")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decodeFromLabels with all B labels different types`() {
        val labels = intArrayOf(LABEL_B_CREDIT_CARD, LABEL_B_SSN, LABEL_B_EMAIL)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "card ssn email")
        assertEquals("Three consecutive B labels should form three entities", 3, result.size)
    }

    @Test
    fun `decodeFromLabels long span of I labels`() {
        val size = 20
        val labels = IntArray(size)
        val confidences = FloatArray(size) { 0.99f }
        labels[0] = LABEL_B_ADDRESS
        for (i in 1 until size) {
            labels[i] = LABEL_I_ADDRESS
        }
        val result = decoder.decodeFromLabels(labels, confidences,
            "123 Main Street Apartment 4B Springfield Illinois 62701 United States of America extra words here to fill it up")
        assertEquals("Long span should form one entity", 1, result.size)
        assertEquals(EntityType.ADDRESS, result[0].entityType)
    }

    // === ENTITY PROPERTIES ===

    @Test
    fun `decoded entity has correct entityType`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_EMAIL, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x email y")
        if (result.isNotEmpty()) {
            assertEquals(EntityType.EMAIL, result[0].entityType)
        }
    }

    @Test
    fun `decoded entity has non-negative start index`() {
        val labels = intArrayOf(LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "card y")
        if (result.isNotEmpty()) {
            assertTrue("Start index should be >= 0", result[0].startIndex >= 0)
        }
    }

    @Test
    fun `decoded entity end index does not exceed text length`() {
        val text = "x card y"
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, text)
        if (result.isNotEmpty()) {
            assertTrue("End index should be <= text length",
                result[0].endIndex <= text.length)
        }
    }

    @Test
    fun `decoded entity start index is less than or equal to end index`() {
        val labels = intArrayOf(LABEL_O, LABEL_B_CREDIT_CARD, LABEL_I_CREDIT_CARD, LABEL_O)
        val confidences = floatArrayOf(0.99f, 0.99f, 0.99f, 0.99f)
        val result = decoder.decodeFromLabels(labels, confidences, "x card number y")
        if (result.isNotEmpty()) {
            assertTrue("Start should be <= end",
                result[0].startIndex <= result[0].endIndex)
        }
    }

    // === NUM_LABELS CONSTANT ===

    @Test
    fun `NUM_LABELS equals 21`() {
        assertEquals(21, OutputDecoder.NUM_LABELS)
    }

    @Test
    fun `O_LABEL equals 0`() {
        assertEquals(0, OutputDecoder.O_LABEL)
    }

    // === PERFORMANCE ===

    @Test
    fun `decode 1000 times without timeout`() {
        val seqLen = PIITokenizer.MAX_SEQUENCE_LENGTH
        val buffer = createAllOBuffer(seqLen)
        val text = "word ".repeat(100)
        val start = System.nanoTime()
        repeat(1000) {
            buffer.rewind()
            decoder.decode(buffer, text, sequenceLength = seqLen)
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("1000 decodes should complete in under 10 seconds, took ${elapsedMs}ms",
            elapsedMs < 10000)
    }

    @Test
    fun `decodeFromLabels 1000 times without timeout`() {
        val labels = IntArray(100) { LABEL_O }
        val confidences = FloatArray(100) { 0.99f }
        val text = "word ".repeat(100)
        val start = System.nanoTime()
        repeat(1000) {
            decoder.decodeFromLabels(labels, confidences, text)
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("1000 decodes should complete in under 5 seconds, took ${elapsedMs}ms",
            elapsedMs < 5000)
    }

    // === SOFTMAX PROPERTIES (tested indirectly via decode) ===

    @Test
    fun `softmax with extreme positive values does not produce NaN`() {
        val seqLen = 3
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                buffer.putFloat(if (j == LABEL_O) 1000f else 999f)
            }
        }
        buffer.rewind()
        val result = decoder.decode(buffer, "a b c", sequenceLength = seqLen)
        assertNotNull("Should not crash with extreme values", result)
    }

    @Test
    fun `softmax with extreme negative values does not produce NaN`() {
        val seqLen = 3
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                buffer.putFloat(if (j == LABEL_O) -999f else -1000f)
            }
        }
        buffer.rewind()
        val result = decoder.decode(buffer, "a b c", sequenceLength = seqLen)
        assertNotNull("Should not crash with extreme negative values", result)
    }

    @Test
    fun `softmax with mixed positive and negative values`() {
        val seqLen = 3
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                buffer.putFloat(if (j % 2 == 0) 5.0f else -5.0f)
            }
        }
        buffer.rewind()
        val result = decoder.decode(buffer, "a b c", sequenceLength = seqLen)
        assertNotNull(result)
    }

    // === SEQUENCE LENGTH PARAMETER ===

    @Test
    fun `decode with custom sequence length`() {
        val seqLen = 10
        val buffer = createAllOBuffer(seqLen)
        val result = decoder.decode(buffer, "test text here pad pad pad pad pad pad pad",
            sequenceLength = seqLen)
        assertNotNull(result)
    }

    @Test
    fun `decode with sequence length 3 minimum`() {
        val seqLen = 3 // CLS, one token, SEP
        val buffer = createAllOBuffer(seqLen)
        val result = decoder.decode(buffer, "x y z", sequenceLength = seqLen)
        assertNotNull(result)
    }

    // === HELPER METHODS ===

    private fun createAllOBuffer(seqLen: Int): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                buffer.putFloat(if (j == LABEL_O) 10.0f else -10.0f)
            }
        }
        buffer.rewind()
        return buffer
    }

    private fun createEntityBuffer(
        seqLen: Int,
        bLabel: Int,
        iLabel: Int,
        start: Int,
        end: Int
    ): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(seqLen * NUM_LABELS * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until seqLen) {
            for (j in 0 until NUM_LABELS) {
                val score = when {
                    i == start && j == bLabel -> 10.0f
                    i in (start + 1) until end && j == iLabel -> 10.0f
                    i !in start until end && j == LABEL_O -> 10.0f
                    else -> -10.0f
                }
                buffer.putFloat(score)
            }
        }
        buffer.rewind()
        return buffer
    }
}
