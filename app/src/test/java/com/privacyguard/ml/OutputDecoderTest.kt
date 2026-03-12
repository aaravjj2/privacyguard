package com.privacyguard.ml

import com.privacyguard.util.ConfidenceThresholds
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class OutputDecoderTest {

    private lateinit var decoder: OutputDecoder

    @Before
    fun setUp() {
        decoder = OutputDecoder()
        ConfidenceThresholds.resetToDefaults()
    }

    @After
    fun tearDown() {
        ConfidenceThresholds.resetToDefaults()
    }

    @Test
    fun `decode null buffer returns empty list`() {
        val result = decoder.decode(null, "test text")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decode empty text returns empty list`() {
        val buffer = createMockOutputBuffer(10, OutputDecoder.NUM_LABELS)
        val result = decoder.decode(buffer, "")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `decodeFromLabels extracts credit card entity`() {
        // Set lower thresholds for testing
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.5f)

        val labels = intArrayOf(0, 0, 1, 2, 2, 2, 0, 0) // O O B-CC I-CC I-CC I-CC O O
        val confidences = floatArrayOf(0.9f, 0.9f, 0.95f, 0.93f, 0.92f, 0.94f, 0.9f, 0.9f)

        val result = decoder.decodeFromLabels(labels, confidences, "My card 4532 1234 5678 9012 here")

        assertTrue("Should detect at least one entity", result.isNotEmpty())
        assertEquals(EntityType.CREDIT_CARD, result[0].entityType)
    }

    @Test
    fun `decodeFromLabels extracts SSN entity`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.5f)

        val labels = intArrayOf(0, 3, 4, 4, 0) // O B-SSN I-SSN I-SSN O
        val confidences = floatArrayOf(0.9f, 0.95f, 0.93f, 0.92f, 0.9f)

        val result = decoder.decodeFromLabels(labels, confidences, "SSN: 123-45-6789")

        assertTrue("Should detect SSN", result.isNotEmpty())
        assertEquals(EntityType.SSN, result[0].entityType)
    }

    @Test
    fun `decodeFromLabels extracts email entity`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.5f)

        val labels = intArrayOf(0, 0, 9, 10, 10, 0) // O O B-EMAIL I-EMAIL I-EMAIL O
        val confidences = floatArrayOf(0.9f, 0.9f, 0.97f, 0.96f, 0.95f, 0.9f)

        val result = decoder.decodeFromLabels(labels, confidences, "Email me at user@example.com please")

        assertTrue("Should detect email", result.isNotEmpty())
        assertEquals(EntityType.EMAIL, result[0].entityType)
    }

    @Test
    fun `decodeFromLabels filters by confidence threshold`() {
        // Keep default high threshold for EMAIL (0.95)
        val labels = intArrayOf(0, 9, 10, 0) // O B-EMAIL I-EMAIL O
        val confidences = floatArrayOf(0.9f, 0.50f, 0.50f, 0.9f) // Low confidence

        val result = decoder.decodeFromLabels(labels, confidences, "user@test.com")

        assertTrue("Low confidence should be filtered", result.isEmpty())
    }

    @Test
    fun `decodeFromLabels extracts multiple entities`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.5f)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.5f)

        val labels = intArrayOf(0, 1, 2, 0, 9, 10, 0) // B-CC I-CC O B-EMAIL I-EMAIL O
        val confidences = floatArrayOf(0.9f, 0.95f, 0.93f, 0.9f, 0.97f, 0.96f, 0.9f)

        val result = decoder.decodeFromLabels(labels, confidences, "Card 4532 email user@test.com")

        assertEquals("Should detect both entities", 2, result.size)
    }

    @Test
    fun `decodeFromLabels handles all O labels`() {
        val labels = intArrayOf(0, 0, 0, 0, 0)
        val confidences = floatArrayOf(0.9f, 0.9f, 0.9f, 0.9f, 0.9f)

        val result = decoder.decodeFromLabels(labels, confidences, "no entities here")

        assertTrue("All O labels should produce no entities", result.isEmpty())
    }

    @Test
    fun `decodeFromLabels entity at end of sequence`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.5f)

        val labels = intArrayOf(0, 0, 11, 12, 12) // Ends with entity
        val confidences = floatArrayOf(0.9f, 0.9f, 0.95f, 0.93f, 0.92f)

        val result = decoder.decodeFromLabels(labels, confidences, "call 555-867-5309")

        assertTrue("Should detect entity at end", result.isNotEmpty())
        assertEquals(EntityType.PHONE, result[0].entityType)
    }

    @Test
    fun `decodeFromLabels B label without matching I label`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.5f)

        val labels = intArrayOf(0, 1, 0) // Single B label, no I continuation
        val confidences = floatArrayOf(0.9f, 0.95f, 0.9f)

        val result = decoder.decodeFromLabels(labels, confidences, "test 4532 text")

        // Single token entity is valid
        assertTrue(result.isNotEmpty())
    }

    private fun createMockOutputBuffer(seqLen: Int, numLabels: Int): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(seqLen * numLabels * 4)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        // Fill with zeros (all O predictions)
        for (i in 0 until seqLen * numLabels) {
            buffer.putFloat(0.0f)
        }
        buffer.rewind()
        return buffer
    }
}
