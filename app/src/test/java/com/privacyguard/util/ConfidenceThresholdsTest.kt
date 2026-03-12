package com.privacyguard.util

import com.privacyguard.ml.EntityType
import org.junit.Assert.*
import org.junit.After
import org.junit.Test

class ConfidenceThresholdsTest {

    @After
    fun tearDown() {
        ConfidenceThresholds.resetToDefaults()
    }

    @Test
    fun `all entity types have a threshold`() {
        EntityType.entries.forEach { type ->
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("${type.name} threshold should be between 0 and 1",
                threshold in 0.0f..1.0f)
        }
    }

    @Test
    fun `credit card threshold is 0_90`() {
        assertEquals(0.90f, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    @Test
    fun `SSN threshold is 0_92`() {
        assertEquals(0.92f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.001f)
    }

    @Test
    fun `password threshold is 0_80`() {
        assertEquals(0.80f, ConfidenceThresholds.getThreshold(EntityType.PASSWORD), 0.001f)
    }

    @Test
    fun `API key threshold is 0_85`() {
        assertEquals(0.85f, ConfidenceThresholds.getThreshold(EntityType.API_KEY), 0.001f)
    }

    @Test
    fun `email threshold is 0_95`() {
        assertEquals(0.95f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `phone threshold is 0_88`() {
        assertEquals(0.88f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `person name threshold is 0_75`() {
        assertEquals(0.75f, ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME), 0.001f)
    }

    @Test
    fun `custom threshold overrides default`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.80f)
        assertEquals(0.80f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `reset restores defaults`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.95f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `get default threshold returns original value even with override`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        assertEquals(0.95f, ConfidenceThresholds.getDefaultThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setting threshold above 1 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 1.5f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setting threshold below 0 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, -0.1f)
    }

    @Test
    fun `getAllThresholds returns map for all types`() {
        val all = ConfidenceThresholds.getAllThresholds()
        assertEquals(EntityType.entries.size, all.size)
        EntityType.entries.forEach { type ->
            assertTrue(all.containsKey(type))
        }
    }
}
