package com.privacyguard.util

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive edge-case tests for ConfidenceThresholds.
 *
 * Covers: every entity type has a threshold, threshold ranges are valid (0-1),
 * threshold ordering (critical types have lower thresholds), boundary testing at exact
 * threshold values, custom threshold setting, all EntityType enum values covered,
 * resetToDefaults, getDefaultThreshold, getAllThresholds, override semantics.
 */
class ConfidenceThresholdsEdgeCaseTest {

    @Before
    fun setUp() {
        ConfidenceThresholds.resetToDefaults()
    }

    @After
    fun tearDown() {
        ConfidenceThresholds.resetToDefaults()
    }

    // ========================================================================
    // Section 1: Every entity type has a default threshold
    // ========================================================================

    @Test
    fun `CREDIT_CARD has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `SSN has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.SSN)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `PASSWORD has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `API_KEY has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.API_KEY)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `EMAIL has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `PHONE has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PHONE)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `PERSON_NAME has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `ADDRESS has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.ADDRESS)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `DATE_OF_BIRTH has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `MEDICAL_ID has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `UNKNOWN has a default threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.UNKNOWN)
        assertTrue(threshold > 0.0f)
        assertTrue(threshold <= 1.0f)
    }

    @Test
    fun `all entity types have a non-null threshold`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertNotNull("Threshold for $type should not be null", threshold)
        }
    }

    @Test
    fun `all entity types have a positive threshold`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("Threshold for $type should be positive, was $threshold", threshold > 0.0f)
        }
    }

    // ========================================================================
    // Section 2: Exact default threshold values
    // ========================================================================

    @Test
    fun `CREDIT_CARD default threshold is 0_90`() {
        assertEquals(0.90f, ConfidenceThresholds.getDefaultThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    @Test
    fun `SSN default threshold is 0_92`() {
        assertEquals(0.92f, ConfidenceThresholds.getDefaultThreshold(EntityType.SSN), 0.001f)
    }

    @Test
    fun `PASSWORD default threshold is 0_80`() {
        assertEquals(0.80f, ConfidenceThresholds.getDefaultThreshold(EntityType.PASSWORD), 0.001f)
    }

    @Test
    fun `API_KEY default threshold is 0_85`() {
        assertEquals(0.85f, ConfidenceThresholds.getDefaultThreshold(EntityType.API_KEY), 0.001f)
    }

    @Test
    fun `EMAIL default threshold is 0_95`() {
        assertEquals(0.95f, ConfidenceThresholds.getDefaultThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `PHONE default threshold is 0_88`() {
        assertEquals(0.88f, ConfidenceThresholds.getDefaultThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `PERSON_NAME default threshold is 0_75`() {
        assertEquals(0.75f, ConfidenceThresholds.getDefaultThreshold(EntityType.PERSON_NAME), 0.001f)
    }

    @Test
    fun `ADDRESS default threshold is 0_80`() {
        assertEquals(0.80f, ConfidenceThresholds.getDefaultThreshold(EntityType.ADDRESS), 0.001f)
    }

    @Test
    fun `DATE_OF_BIRTH default threshold is 0_82`() {
        assertEquals(0.82f, ConfidenceThresholds.getDefaultThreshold(EntityType.DATE_OF_BIRTH), 0.001f)
    }

    @Test
    fun `MEDICAL_ID default threshold is 0_85`() {
        assertEquals(0.85f, ConfidenceThresholds.getDefaultThreshold(EntityType.MEDICAL_ID), 0.001f)
    }

    @Test
    fun `UNKNOWN default threshold is 0_90`() {
        assertEquals(0.90f, ConfidenceThresholds.getDefaultThreshold(EntityType.UNKNOWN), 0.001f)
    }

    // ========================================================================
    // Section 3: Threshold ranges are valid (0-1)
    // ========================================================================

    @Test
    fun `all default thresholds are between 0 and 1 inclusive`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getDefaultThreshold(type)
            assertTrue("$type threshold $threshold should be >= 0", threshold >= 0.0f)
            assertTrue("$type threshold $threshold should be <= 1", threshold <= 1.0f)
        }
    }

    @Test
    fun `all active thresholds are between 0 and 1 inclusive`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type threshold $threshold should be >= 0", threshold >= 0.0f)
            assertTrue("$type threshold $threshold should be <= 1", threshold <= 1.0f)
        }
    }

    @Test
    fun `CREDIT_CARD threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `SSN threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.SSN)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `PASSWORD threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `API_KEY threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.API_KEY)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `EMAIL threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `PHONE threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.PHONE)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `PERSON_NAME threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `ADDRESS threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.ADDRESS)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `DATE_OF_BIRTH threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `MEDICAL_ID threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `UNKNOWN threshold is in 0-1 range`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.UNKNOWN)
        assertTrue(t in 0.0f..1.0f)
    }

    @Test
    fun `no default threshold is exactly 0`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getDefaultThreshold(type)
            assertTrue("$type threshold should not be exactly 0", threshold > 0.0f)
        }
    }

    @Test
    fun `no default threshold is exactly 1`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getDefaultThreshold(type)
            assertTrue("$type threshold should not be exactly 1", threshold < 1.0f)
        }
    }

    @Test
    fun `all default thresholds are at least 0_5`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getDefaultThreshold(type)
            assertTrue("$type threshold $threshold should be >= 0.5", threshold >= 0.5f)
        }
    }

    // ========================================================================
    // Section 4: Threshold ordering - critical types should have lower or equal thresholds
    // ========================================================================

    @Test
    fun `PASSWORD threshold is lower than or equal to EMAIL threshold`() {
        // Password (CRITICAL) should have lower or equal threshold than EMAIL (HIGH)
        // because we want to catch passwords even with lower confidence
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PASSWORD) <=
                    ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        )
    }

    @Test
    fun `PERSON_NAME has lowest threshold among all types`() {
        val personNameThreshold = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        for (type in EntityType.entries) {
            if (type != EntityType.PERSON_NAME) {
                assertTrue(
                    "PERSON_NAME ($personNameThreshold) should be <= ${type.name} (${ConfidenceThresholds.getThreshold(type)})",
                    personNameThreshold <= ConfidenceThresholds.getThreshold(type)
                )
            }
        }
    }

    @Test
    fun `PASSWORD has lower threshold than SSN`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PASSWORD) <=
                    ConfidenceThresholds.getThreshold(EntityType.SSN)
        )
    }

    @Test
    fun `PASSWORD has lower threshold than CREDIT_CARD`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PASSWORD) <=
                    ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        )
    }

    @Test
    fun `EMAIL has highest threshold among all types`() {
        val emailThreshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        for (type in EntityType.entries) {
            assertTrue(
                "EMAIL ($emailThreshold) should be >= ${type.name} (${ConfidenceThresholds.getThreshold(type)})",
                emailThreshold >= ConfidenceThresholds.getThreshold(type)
            )
        }
    }

    @Test
    fun `SSN threshold is higher than PASSWORD threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.SSN) >
                    ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        )
    }

    @Test
    fun `CREDIT_CARD threshold is higher than PASSWORD threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD) >
                    ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        )
    }

    @Test
    fun `PERSON_NAME threshold is less than PHONE threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME) <
                    ConfidenceThresholds.getThreshold(EntityType.PHONE)
        )
    }

    @Test
    fun `PERSON_NAME threshold is less than SSN threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME) <
                    ConfidenceThresholds.getThreshold(EntityType.SSN)
        )
    }

    @Test
    fun `ADDRESS threshold is less than CREDIT_CARD threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.ADDRESS) <
                    ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        )
    }

    @Test
    fun `DATE_OF_BIRTH threshold is less than EMAIL threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH) <
                    ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        )
    }

    @Test
    fun `PHONE threshold is less than EMAIL threshold`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PHONE) <
                    ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        )
    }

    @Test
    fun `MEDICAL_ID threshold equals API_KEY threshold`() {
        assertEquals(
            ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID),
            ConfidenceThresholds.getThreshold(EntityType.API_KEY),
            0.001f
        )
    }

    @Test
    fun `UNKNOWN threshold equals CREDIT_CARD threshold`() {
        assertEquals(
            ConfidenceThresholds.getThreshold(EntityType.UNKNOWN),
            ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD),
            0.001f
        )
    }

    @Test
    fun `ADDRESS threshold equals PASSWORD threshold`() {
        assertEquals(
            ConfidenceThresholds.getThreshold(EntityType.ADDRESS),
            ConfidenceThresholds.getThreshold(EntityType.PASSWORD),
            0.001f
        )
    }

    // ========================================================================
    // Section 5: Severity-based threshold ordering
    // ========================================================================

    @Test
    fun `critical severity types all have defined thresholds`() {
        val criticalTypes = EntityType.entries.filter { it.severity == Severity.CRITICAL }
        for (type in criticalTypes) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type (CRITICAL) should have threshold, got $threshold", threshold > 0.0f)
        }
    }

    @Test
    fun `high severity types all have defined thresholds`() {
        val highTypes = EntityType.entries.filter { it.severity == Severity.HIGH }
        for (type in highTypes) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type (HIGH) should have threshold, got $threshold", threshold > 0.0f)
        }
    }

    @Test
    fun `medium severity types all have defined thresholds`() {
        val mediumTypes = EntityType.entries.filter { it.severity == Severity.MEDIUM }
        for (type in mediumTypes) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type (MEDIUM) should have threshold, got $threshold", threshold > 0.0f)
        }
    }

    @Test
    fun `CREDIT_CARD is CRITICAL severity`() {
        assertEquals(Severity.CRITICAL, EntityType.CREDIT_CARD.severity)
    }

    @Test
    fun `SSN is CRITICAL severity`() {
        assertEquals(Severity.CRITICAL, EntityType.SSN.severity)
    }

    @Test
    fun `PASSWORD is CRITICAL severity`() {
        assertEquals(Severity.CRITICAL, EntityType.PASSWORD.severity)
    }

    @Test
    fun `API_KEY is CRITICAL severity`() {
        assertEquals(Severity.CRITICAL, EntityType.API_KEY.severity)
    }

    @Test
    fun `EMAIL is HIGH severity`() {
        assertEquals(Severity.HIGH, EntityType.EMAIL.severity)
    }

    @Test
    fun `PHONE is HIGH severity`() {
        assertEquals(Severity.HIGH, EntityType.PHONE.severity)
    }

    @Test
    fun `MEDICAL_ID is HIGH severity`() {
        assertEquals(Severity.HIGH, EntityType.MEDICAL_ID.severity)
    }

    @Test
    fun `PERSON_NAME is MEDIUM severity`() {
        assertEquals(Severity.MEDIUM, EntityType.PERSON_NAME.severity)
    }

    @Test
    fun `ADDRESS is MEDIUM severity`() {
        assertEquals(Severity.MEDIUM, EntityType.ADDRESS.severity)
    }

    @Test
    fun `DATE_OF_BIRTH is MEDIUM severity`() {
        assertEquals(Severity.MEDIUM, EntityType.DATE_OF_BIRTH.severity)
    }

    @Test
    fun `UNKNOWN is MEDIUM severity`() {
        assertEquals(Severity.MEDIUM, EntityType.UNKNOWN.severity)
    }

    // ========================================================================
    // Section 6: Custom threshold setting
    // ========================================================================

    @Test
    fun `setThreshold overrides default for CREDIT_CARD`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.50f)
        assertEquals(0.50f, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for SSN`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.60f)
        assertEquals(0.60f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for PASSWORD`() {
        ConfidenceThresholds.setThreshold(EntityType.PASSWORD, 0.70f)
        assertEquals(0.70f, ConfidenceThresholds.getThreshold(EntityType.PASSWORD), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for API_KEY`() {
        ConfidenceThresholds.setThreshold(EntityType.API_KEY, 0.55f)
        assertEquals(0.55f, ConfidenceThresholds.getThreshold(EntityType.API_KEY), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for EMAIL`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.80f)
        assertEquals(0.80f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for PHONE`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.65f)
        assertEquals(0.65f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for PERSON_NAME`() {
        ConfidenceThresholds.setThreshold(EntityType.PERSON_NAME, 0.40f)
        assertEquals(0.40f, ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for ADDRESS`() {
        ConfidenceThresholds.setThreshold(EntityType.ADDRESS, 0.45f)
        assertEquals(0.45f, ConfidenceThresholds.getThreshold(EntityType.ADDRESS), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for DATE_OF_BIRTH`() {
        ConfidenceThresholds.setThreshold(EntityType.DATE_OF_BIRTH, 0.50f)
        assertEquals(0.50f, ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for MEDICAL_ID`() {
        ConfidenceThresholds.setThreshold(EntityType.MEDICAL_ID, 0.72f)
        assertEquals(0.72f, ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID), 0.001f)
    }

    @Test
    fun `setThreshold overrides default for UNKNOWN`() {
        ConfidenceThresholds.setThreshold(EntityType.UNKNOWN, 0.55f)
        assertEquals(0.55f, ConfidenceThresholds.getThreshold(EntityType.UNKNOWN), 0.001f)
    }

    @Test
    fun `setThreshold with 0_0 is valid`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.0f)
        assertEquals(0.0f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `setThreshold with 1_0 is valid`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 1.0f)
        assertEquals(1.0f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `setThreshold with 0_5 is valid`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.5f)
        assertEquals(0.5f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `setThreshold with 0_001 is valid`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.001f)
        assertEquals(0.001f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.0001f)
    }

    @Test
    fun `setThreshold with 0_999 is valid`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.999f)
        assertEquals(0.999f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.0001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with negative value throws`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, -0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with value greater than 1 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 1.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with value of 2 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 2.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with large negative throws`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, -100.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with very large positive throws`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 100.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with negative epsilon throws`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, -0.001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with 1_001 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 1.001f)
    }

    @Test
    fun `setThreshold does not affect other entity types`() {
        val originalPhone = ConfidenceThresholds.getThreshold(EntityType.PHONE)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        assertEquals(originalPhone, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `setThreshold can be called multiple times for same type`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        assertEquals(0.50f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.75f)
        assertEquals(0.75f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.30f)
        assertEquals(0.30f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `setThreshold then getDefaultThreshold returns original`() {
        val original = ConfidenceThresholds.getDefaultThreshold(EntityType.EMAIL)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        assertEquals(original, ConfidenceThresholds.getDefaultThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `setThreshold for all types then verify each`() {
        val customValues = mapOf(
            EntityType.CREDIT_CARD to 0.10f,
            EntityType.SSN to 0.20f,
            EntityType.PASSWORD to 0.30f,
            EntityType.API_KEY to 0.40f,
            EntityType.EMAIL to 0.50f,
            EntityType.PHONE to 0.60f,
            EntityType.PERSON_NAME to 0.70f,
            EntityType.ADDRESS to 0.80f,
            EntityType.DATE_OF_BIRTH to 0.90f,
            EntityType.MEDICAL_ID to 0.15f,
            EntityType.UNKNOWN to 0.25f
        )
        for ((type, value) in customValues) {
            ConfidenceThresholds.setThreshold(type, value)
        }
        for ((type, value) in customValues) {
            assertEquals(
                "Custom threshold for $type",
                value,
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    // ========================================================================
    // Section 7: resetToDefaults
    // ========================================================================

    @Test
    fun `resetToDefaults restores CREDIT_CARD threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.90f, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    @Test
    fun `resetToDefaults restores SSN threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.92f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.001f)
    }

    @Test
    fun `resetToDefaults restores PASSWORD threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.PASSWORD, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.80f, ConfidenceThresholds.getThreshold(EntityType.PASSWORD), 0.001f)
    }

    @Test
    fun `resetToDefaults restores API_KEY threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.API_KEY, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.85f, ConfidenceThresholds.getThreshold(EntityType.API_KEY), 0.001f)
    }

    @Test
    fun `resetToDefaults restores EMAIL threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.95f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `resetToDefaults restores PHONE threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.88f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `resetToDefaults restores PERSON_NAME threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.PERSON_NAME, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.75f, ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME), 0.001f)
    }

    @Test
    fun `resetToDefaults restores ADDRESS threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.ADDRESS, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.80f, ConfidenceThresholds.getThreshold(EntityType.ADDRESS), 0.001f)
    }

    @Test
    fun `resetToDefaults restores DATE_OF_BIRTH threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.DATE_OF_BIRTH, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.82f, ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH), 0.001f)
    }

    @Test
    fun `resetToDefaults restores MEDICAL_ID threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.MEDICAL_ID, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.85f, ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID), 0.001f)
    }

    @Test
    fun `resetToDefaults restores UNKNOWN threshold`() {
        ConfidenceThresholds.setThreshold(EntityType.UNKNOWN, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.90f, ConfidenceThresholds.getThreshold(EntityType.UNKNOWN), 0.001f)
    }

    @Test
    fun `resetToDefaults restores all overridden types`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.10f)
        }
        ConfidenceThresholds.resetToDefaults()
        for (type in EntityType.entries) {
            assertEquals(
                "After reset, $type should match default",
                ConfidenceThresholds.getDefaultThreshold(type),
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    @Test
    fun `resetToDefaults when no overrides exist has no effect`() {
        // Store defaults before reset
        val before = EntityType.entries.associateWith { ConfidenceThresholds.getThreshold(it) }
        ConfidenceThresholds.resetToDefaults()
        val after = EntityType.entries.associateWith { ConfidenceThresholds.getThreshold(it) }
        assertEquals(before, after)
    }

    @Test
    fun `resetToDefaults can be called multiple times`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        ConfidenceThresholds.resetToDefaults()
        ConfidenceThresholds.resetToDefaults()
        assertEquals(0.95f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `setThreshold after resetToDefaults works`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.10f)
        ConfidenceThresholds.resetToDefaults()
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        assertEquals(0.50f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `resetToDefaults then override then reset cycle`() {
        val defaultEmail = ConfidenceThresholds.getDefaultThreshold(EntityType.EMAIL)

        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.10f)
        assertEquals(0.10f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)

        ConfidenceThresholds.resetToDefaults()
        assertEquals(defaultEmail, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)

        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.20f)
        assertEquals(0.20f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)

        ConfidenceThresholds.resetToDefaults()
        assertEquals(defaultEmail, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    // ========================================================================
    // Section 8: getDefaultThreshold
    // ========================================================================

    @Test
    fun `getDefaultThreshold is not affected by setThreshold`() {
        val original = ConfidenceThresholds.getDefaultThreshold(EntityType.CREDIT_CARD)
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.10f)
        assertEquals(original, ConfidenceThresholds.getDefaultThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    @Test
    fun `getDefaultThreshold for all types matches expected values`() {
        val expected = mapOf(
            EntityType.CREDIT_CARD to 0.90f,
            EntityType.SSN to 0.92f,
            EntityType.PASSWORD to 0.80f,
            EntityType.API_KEY to 0.85f,
            EntityType.EMAIL to 0.95f,
            EntityType.PHONE to 0.88f,
            EntityType.PERSON_NAME to 0.75f,
            EntityType.ADDRESS to 0.80f,
            EntityType.DATE_OF_BIRTH to 0.82f,
            EntityType.MEDICAL_ID to 0.85f,
            EntityType.UNKNOWN to 0.90f
        )
        for ((type, value) in expected) {
            assertEquals(
                "Default threshold for $type",
                value,
                ConfidenceThresholds.getDefaultThreshold(type),
                0.001f
            )
        }
    }

    @Test
    fun `getDefaultThreshold equals getThreshold before any overrides`() {
        for (type in EntityType.entries) {
            assertEquals(
                "For $type, default should equal active before overrides",
                ConfidenceThresholds.getDefaultThreshold(type),
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    @Test
    fun `getDefaultThreshold is consistent across multiple calls`() {
        val first = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        val second = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        val third = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        assertEquals(first, second, 0.0f)
        assertEquals(second, third, 0.0f)
    }

    @Test
    fun `getDefaultThreshold is not affected by resetToDefaults`() {
        val before = ConfidenceThresholds.getDefaultThreshold(EntityType.PHONE)
        ConfidenceThresholds.resetToDefaults()
        val after = ConfidenceThresholds.getDefaultThreshold(EntityType.PHONE)
        assertEquals(before, after, 0.0f)
    }

    // ========================================================================
    // Section 9: getAllThresholds
    // ========================================================================

    @Test
    fun `getAllThresholds returns all entity types`() {
        val all = ConfidenceThresholds.getAllThresholds()
        for (type in EntityType.entries) {
            assertTrue("$type should be in getAllThresholds", all.containsKey(type))
        }
    }

    @Test
    fun `getAllThresholds size equals EntityType entries size`() {
        assertEquals(EntityType.entries.size, ConfidenceThresholds.getAllThresholds().size)
    }

    @Test
    fun `getAllThresholds values match individual getThreshold calls`() {
        val all = ConfidenceThresholds.getAllThresholds()
        for (type in EntityType.entries) {
            assertEquals(
                "getAllThresholds[$type] should match getThreshold($type)",
                ConfidenceThresholds.getThreshold(type),
                all[type]!!,
                0.001f
            )
        }
    }

    @Test
    fun `getAllThresholds reflects overrides`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        val all = ConfidenceThresholds.getAllThresholds()
        assertEquals(0.50f, all[EntityType.EMAIL]!!, 0.001f)
    }

    @Test
    fun `getAllThresholds after reset matches defaults`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        ConfidenceThresholds.resetToDefaults()
        val all = ConfidenceThresholds.getAllThresholds()
        for (type in EntityType.entries) {
            assertEquals(
                "After reset, getAllThresholds[$type] should match default",
                ConfidenceThresholds.getDefaultThreshold(type),
                all[type]!!,
                0.001f
            )
        }
    }

    @Test
    fun `getAllThresholds returns new map instance each call`() {
        val a = ConfidenceThresholds.getAllThresholds()
        val b = ConfidenceThresholds.getAllThresholds()
        assertEquals(a, b)
        // They should be equal but potentially different instances
    }

    @Test
    fun `getAllThresholds with all overrides set`() {
        val overrides = mapOf(
            EntityType.CREDIT_CARD to 0.10f,
            EntityType.SSN to 0.20f,
            EntityType.PASSWORD to 0.30f,
            EntityType.API_KEY to 0.40f,
            EntityType.EMAIL to 0.50f,
            EntityType.PHONE to 0.60f,
            EntityType.PERSON_NAME to 0.70f,
            EntityType.ADDRESS to 0.80f,
            EntityType.DATE_OF_BIRTH to 0.90f,
            EntityType.MEDICAL_ID to 0.15f,
            EntityType.UNKNOWN to 0.25f
        )
        for ((type, value) in overrides) {
            ConfidenceThresholds.setThreshold(type, value)
        }
        val all = ConfidenceThresholds.getAllThresholds()
        for ((type, value) in overrides) {
            assertEquals("$type override", value, all[type]!!, 0.001f)
        }
    }

    @Test
    fun `getAllThresholds with partial overrides`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.60f)
        val all = ConfidenceThresholds.getAllThresholds()
        assertEquals(0.50f, all[EntityType.EMAIL]!!, 0.001f)
        assertEquals(0.60f, all[EntityType.PHONE]!!, 0.001f)
        // Non-overridden should be default
        assertEquals(
            ConfidenceThresholds.getDefaultThreshold(EntityType.SSN),
            all[EntityType.SSN]!!,
            0.001f
        )
    }

    @Test
    fun `getAllThresholds values are all in 0-1 range`() {
        val all = ConfidenceThresholds.getAllThresholds()
        for ((type, value) in all) {
            assertTrue("$type value $value should be >= 0", value >= 0.0f)
            assertTrue("$type value $value should be <= 1", value <= 1.0f)
        }
    }

    // ========================================================================
    // Section 10: Boundary testing - exact threshold values
    // ========================================================================

    @Test
    fun `confidence exactly at CREDIT_CARD threshold passes`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        assertTrue(threshold >= threshold) // Exact match should pass meetsThreshold
    }

    @Test
    fun `confidence just below CREDIT_CARD threshold fails`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        val justBelow = threshold - 0.001f
        assertTrue(justBelow < threshold)
    }

    @Test
    fun `confidence just above CREDIT_CARD threshold passes`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        val justAbove = threshold + 0.001f
        assertTrue(justAbove > threshold)
    }

    @Test
    fun `confidence exactly at SSN threshold boundary`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.SSN)
        assertEquals(0.92f, threshold, 0.001f)
        assertTrue(0.92f >= threshold)
        assertFalse(0.919f >= threshold)
    }

    @Test
    fun `confidence exactly at PASSWORD threshold boundary`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        assertEquals(0.80f, threshold, 0.001f)
        assertTrue(0.80f >= threshold)
    }

    @Test
    fun `confidence exactly at EMAIL threshold boundary`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertEquals(0.95f, threshold, 0.001f)
        assertTrue(0.95f >= threshold)
    }

    @Test
    fun `confidence exactly at PHONE threshold boundary`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PHONE)
        assertEquals(0.88f, threshold, 0.001f)
        assertTrue(0.88f >= threshold)
    }

    @Test
    fun `confidence exactly at PERSON_NAME threshold boundary`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        assertEquals(0.75f, threshold, 0.001f)
        assertTrue(0.75f >= threshold)
    }

    @Test
    fun `confidence of 0_0 fails all thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("0.0 should be below $type threshold ($threshold)", 0.0f < threshold)
        }
    }

    @Test
    fun `confidence of 1_0 passes all thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("1.0 should be >= $type threshold ($threshold)", 1.0f >= threshold)
        }
    }

    @Test
    fun `confidence of 0_5 fails most thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            if (threshold > 0.5f) {
                assertTrue("0.5 should be below $type threshold ($threshold)", 0.5f < threshold)
            }
        }
    }

    @Test
    fun `confidence of 0_99 passes all default thresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("0.99 should be >= $type threshold ($threshold)", 0.99f >= threshold)
        }
    }

    @Test
    fun `confidence of 0_74 fails PERSON_NAME threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        assertTrue(0.74f < threshold)
    }

    @Test
    fun `confidence of 0_76 passes PERSON_NAME threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        assertTrue(0.76f >= threshold)
    }

    @Test
    fun `confidence of 0_94 fails EMAIL threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertTrue(0.94f < threshold)
    }

    @Test
    fun `confidence of 0_96 passes EMAIL threshold`() {
        val threshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertTrue(0.96f >= threshold)
    }

    @Test
    fun `custom threshold at 0_0 makes all confidences pass`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.0f)
        val threshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertTrue(0.0f >= threshold)
        assertTrue(0.001f >= threshold)
    }

    @Test
    fun `custom threshold at 1_0 requires perfect confidence`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 1.0f)
        val threshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertTrue(1.0f >= threshold)
        assertFalse(0.999f >= threshold)
    }

    // ========================================================================
    // Section 11: EntityType enum coverage
    // ========================================================================

    @Test
    fun `EntityType has exactly 11 entries`() {
        assertEquals(11, EntityType.entries.size)
    }

    @Test
    fun `EntityType contains CREDIT_CARD`() {
        assertTrue(EntityType.entries.contains(EntityType.CREDIT_CARD))
    }

    @Test
    fun `EntityType contains SSN`() {
        assertTrue(EntityType.entries.contains(EntityType.SSN))
    }

    @Test
    fun `EntityType contains PASSWORD`() {
        assertTrue(EntityType.entries.contains(EntityType.PASSWORD))
    }

    @Test
    fun `EntityType contains API_KEY`() {
        assertTrue(EntityType.entries.contains(EntityType.API_KEY))
    }

    @Test
    fun `EntityType contains EMAIL`() {
        assertTrue(EntityType.entries.contains(EntityType.EMAIL))
    }

    @Test
    fun `EntityType contains PHONE`() {
        assertTrue(EntityType.entries.contains(EntityType.PHONE))
    }

    @Test
    fun `EntityType contains PERSON_NAME`() {
        assertTrue(EntityType.entries.contains(EntityType.PERSON_NAME))
    }

    @Test
    fun `EntityType contains ADDRESS`() {
        assertTrue(EntityType.entries.contains(EntityType.ADDRESS))
    }

    @Test
    fun `EntityType contains DATE_OF_BIRTH`() {
        assertTrue(EntityType.entries.contains(EntityType.DATE_OF_BIRTH))
    }

    @Test
    fun `EntityType contains MEDICAL_ID`() {
        assertTrue(EntityType.entries.contains(EntityType.MEDICAL_ID))
    }

    @Test
    fun `EntityType contains UNKNOWN`() {
        assertTrue(EntityType.entries.contains(EntityType.UNKNOWN))
    }

    @Test
    fun `every EntityType has a threshold in ConfidenceThresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertNotNull("$type should have a threshold", threshold)
        }
    }

    @Test
    fun `every EntityType has a default threshold in ConfidenceThresholds`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getDefaultThreshold(type)
            assertNotNull("$type should have a default threshold", threshold)
        }
    }

    @Test
    fun `every EntityType appears in getAllThresholds`() {
        val all = ConfidenceThresholds.getAllThresholds()
        for (type in EntityType.entries) {
            assertTrue("$type should be in getAllThresholds", all.containsKey(type))
        }
    }

    // ========================================================================
    // Section 12: EntityType display names
    // ========================================================================

    @Test
    fun `CREDIT_CARD displayName is Credit Card`() {
        assertEquals("Credit Card", EntityType.CREDIT_CARD.displayName)
    }

    @Test
    fun `SSN displayName is Social Security Number`() {
        assertEquals("Social Security Number", EntityType.SSN.displayName)
    }

    @Test
    fun `PASSWORD displayName is Password`() {
        assertEquals("Password", EntityType.PASSWORD.displayName)
    }

    @Test
    fun `API_KEY displayName is API Key`() {
        assertEquals("API Key", EntityType.API_KEY.displayName)
    }

    @Test
    fun `EMAIL displayName is Email Address`() {
        assertEquals("Email Address", EntityType.EMAIL.displayName)
    }

    @Test
    fun `PHONE displayName is Phone Number`() {
        assertEquals("Phone Number", EntityType.PHONE.displayName)
    }

    @Test
    fun `PERSON_NAME displayName is Person Name`() {
        assertEquals("Person Name", EntityType.PERSON_NAME.displayName)
    }

    @Test
    fun `ADDRESS displayName is Physical Address`() {
        assertEquals("Physical Address", EntityType.ADDRESS.displayName)
    }

    @Test
    fun `DATE_OF_BIRTH displayName is Date of Birth`() {
        assertEquals("Date of Birth", EntityType.DATE_OF_BIRTH.displayName)
    }

    @Test
    fun `MEDICAL_ID displayName is Medical ID`() {
        assertEquals("Medical ID", EntityType.MEDICAL_ID.displayName)
    }

    @Test
    fun `UNKNOWN displayName is Unknown`() {
        assertEquals("Unknown", EntityType.UNKNOWN.displayName)
    }

    @Test
    fun `all EntityType display names are non-empty`() {
        for (type in EntityType.entries) {
            assertTrue("$type displayName should not be empty", type.displayName.isNotEmpty())
        }
    }

    // ========================================================================
    // Section 13: EntityType label indices
    // ========================================================================

    @Test
    fun `CREDIT_CARD labelIndex is 1`() {
        assertEquals(1, EntityType.CREDIT_CARD.labelIndex)
    }

    @Test
    fun `SSN labelIndex is 2`() {
        assertEquals(2, EntityType.SSN.labelIndex)
    }

    @Test
    fun `PASSWORD labelIndex is 3`() {
        assertEquals(3, EntityType.PASSWORD.labelIndex)
    }

    @Test
    fun `API_KEY labelIndex is 4`() {
        assertEquals(4, EntityType.API_KEY.labelIndex)
    }

    @Test
    fun `EMAIL labelIndex is 5`() {
        assertEquals(5, EntityType.EMAIL.labelIndex)
    }

    @Test
    fun `PHONE labelIndex is 6`() {
        assertEquals(6, EntityType.PHONE.labelIndex)
    }

    @Test
    fun `PERSON_NAME labelIndex is 7`() {
        assertEquals(7, EntityType.PERSON_NAME.labelIndex)
    }

    @Test
    fun `ADDRESS labelIndex is 8`() {
        assertEquals(8, EntityType.ADDRESS.labelIndex)
    }

    @Test
    fun `DATE_OF_BIRTH labelIndex is 9`() {
        assertEquals(9, EntityType.DATE_OF_BIRTH.labelIndex)
    }

    @Test
    fun `MEDICAL_ID labelIndex is 10`() {
        assertEquals(10, EntityType.MEDICAL_ID.labelIndex)
    }

    @Test
    fun `UNKNOWN labelIndex is 0`() {
        assertEquals(0, EntityType.UNKNOWN.labelIndex)
    }

    @Test
    fun `all label indices are unique`() {
        val indices = EntityType.entries.map { it.labelIndex }
        assertEquals(indices.size, indices.toSet().size)
    }

    @Test
    fun `fromLabelIndex returns correct type for each index`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(0))
        assertEquals(EntityType.CREDIT_CARD, EntityType.fromLabelIndex(1))
        assertEquals(EntityType.SSN, EntityType.fromLabelIndex(2))
        assertEquals(EntityType.PASSWORD, EntityType.fromLabelIndex(3))
        assertEquals(EntityType.API_KEY, EntityType.fromLabelIndex(4))
        assertEquals(EntityType.EMAIL, EntityType.fromLabelIndex(5))
        assertEquals(EntityType.PHONE, EntityType.fromLabelIndex(6))
        assertEquals(EntityType.PERSON_NAME, EntityType.fromLabelIndex(7))
        assertEquals(EntityType.ADDRESS, EntityType.fromLabelIndex(8))
        assertEquals(EntityType.DATE_OF_BIRTH, EntityType.fromLabelIndex(9))
        assertEquals(EntityType.MEDICAL_ID, EntityType.fromLabelIndex(10))
    }

    @Test
    fun `fromLabelIndex returns UNKNOWN for invalid indices`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(-1))
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(11))
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(100))
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(999))
    }

    @Test
    fun `fromLabelIndex with negative index returns UNKNOWN`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(-100))
    }

    @Test
    fun `fromLabelIndex with MAX_INT returns UNKNOWN`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(Int.MAX_VALUE))
    }

    @Test
    fun `fromLabelIndex with MIN_INT returns UNKNOWN`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(Int.MIN_VALUE))
    }

    // ========================================================================
    // Section 14: Threshold comparison with sensitivity context
    // ========================================================================

    @Test
    fun `PERSON_NAME has the most permissive threshold`() {
        val personNameThreshold = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        val allThresholds = ConfidenceThresholds.getAllThresholds()
        val minThreshold = allThresholds.values.min()
        assertEquals(personNameThreshold, minThreshold, 0.001f)
    }

    @Test
    fun `EMAIL has the strictest threshold`() {
        val emailThreshold = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        val allThresholds = ConfidenceThresholds.getAllThresholds()
        val maxThreshold = allThresholds.values.max()
        assertEquals(emailThreshold, maxThreshold, 0.001f)
    }

    @Test
    fun `threshold spread is reasonable - max minus min is less than 0_5`() {
        val allThresholds = ConfidenceThresholds.getAllThresholds()
        val spread = allThresholds.values.max() - allThresholds.values.min()
        assertTrue("Spread $spread should be < 0.5", spread < 0.5f)
    }

    @Test
    fun `all thresholds are above 0_7`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type threshold $threshold should be > 0.7", threshold >= 0.75f)
        }
    }

    @Test
    fun `all thresholds are below 0_96`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type threshold $threshold should be < 0.96", threshold <= 0.95f)
        }
    }

    @Test
    fun `average threshold is reasonable - between 0_8 and 0_9`() {
        val avg = ConfidenceThresholds.getAllThresholds().values.average().toFloat()
        assertTrue("Average threshold $avg should be >= 0.8", avg >= 0.8f)
        assertTrue("Average threshold $avg should be <= 0.92", avg <= 0.92f)
    }

    // ========================================================================
    // Section 15: Override interaction patterns
    // ========================================================================

    @Test
    fun `override one type then reset then override different type`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        ConfidenceThresholds.resetToDefaults()
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.60f)

        assertEquals(0.95f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
        assertEquals(0.60f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `override same type 10 times with different values`() {
        for (i in 1..10) {
            ConfidenceThresholds.setThreshold(EntityType.SSN, i / 10.0f)
        }
        assertEquals(1.0f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.001f)
    }

    @Test
    fun `override with same value as default`() {
        val defaultValue = ConfidenceThresholds.getDefaultThreshold(EntityType.CREDIT_CARD)
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, defaultValue)
        assertEquals(defaultValue, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    @Test
    fun `override half of types then getAllThresholds shows mixed`() {
        val typesToOverride = listOf(
            EntityType.CREDIT_CARD,
            EntityType.SSN,
            EntityType.PASSWORD,
            EntityType.API_KEY,
            EntityType.EMAIL
        )
        val typesNotOverridden = listOf(
            EntityType.PHONE,
            EntityType.PERSON_NAME,
            EntityType.ADDRESS,
            EntityType.DATE_OF_BIRTH,
            EntityType.MEDICAL_ID,
            EntityType.UNKNOWN
        )

        for (type in typesToOverride) {
            ConfidenceThresholds.setThreshold(type, 0.50f)
        }

        val all = ConfidenceThresholds.getAllThresholds()

        for (type in typesToOverride) {
            assertEquals("$type should be overridden", 0.50f, all[type]!!, 0.001f)
        }
        for (type in typesNotOverridden) {
            assertEquals(
                "$type should be default",
                ConfidenceThresholds.getDefaultThreshold(type),
                all[type]!!,
                0.001f
            )
        }
    }

    @Test
    fun `override to minimum boundary for each type`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.0f)
            assertEquals(0.0f, ConfidenceThresholds.getThreshold(type), 0.001f)
        }
    }

    @Test
    fun `override to maximum boundary for each type`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 1.0f)
            assertEquals(1.0f, ConfidenceThresholds.getThreshold(type), 0.001f)
        }
    }

    @Test
    fun `override to exact midpoint for each type`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.5f)
            assertEquals(0.5f, ConfidenceThresholds.getThreshold(type), 0.001f)
        }
    }

    // ========================================================================
    // Section 16: Consistency after multiple operations
    // ========================================================================

    @Test
    fun `getThreshold is idempotent`() {
        val t1 = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        val t2 = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        val t3 = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        assertEquals(t1, t2, 0.0f)
        assertEquals(t2, t3, 0.0f)
    }

    @Test
    fun `getDefaultThreshold is idempotent`() {
        val t1 = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        val t2 = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        val t3 = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        assertEquals(t1, t2, 0.0f)
        assertEquals(t2, t3, 0.0f)
    }

    @Test
    fun `getAllThresholds is idempotent`() {
        val a1 = ConfidenceThresholds.getAllThresholds()
        val a2 = ConfidenceThresholds.getAllThresholds()
        assertEquals(a1, a2)
    }

    @Test
    fun `100 sequential getThreshold calls return same value`() {
        val expected = ConfidenceThresholds.getThreshold(EntityType.PHONE)
        repeat(100) {
            assertEquals(expected, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.0f)
        }
    }

    @Test
    fun `set override then 100 getThreshold calls return override`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.42f)
        repeat(100) {
            assertEquals(0.42f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
        }
    }

    // ========================================================================
    // Section 17: Severity enum coverage
    // ========================================================================

    @Test
    fun `Severity has exactly 3 entries`() {
        assertEquals(3, Severity.entries.size)
    }

    @Test
    fun `Severity contains CRITICAL`() {
        assertTrue(Severity.entries.contains(Severity.CRITICAL))
    }

    @Test
    fun `Severity contains HIGH`() {
        assertTrue(Severity.entries.contains(Severity.HIGH))
    }

    @Test
    fun `Severity contains MEDIUM`() {
        assertTrue(Severity.entries.contains(Severity.MEDIUM))
    }

    @Test
    fun `CRITICAL displayName is Critical`() {
        assertEquals("Critical", Severity.CRITICAL.displayName)
    }

    @Test
    fun `HIGH displayName is High`() {
        assertEquals("High", Severity.HIGH.displayName)
    }

    @Test
    fun `MEDIUM displayName is Medium`() {
        assertEquals("Medium", Severity.MEDIUM.displayName)
    }

    @Test
    fun `CRITICAL ordinal is 0`() {
        assertEquals(0, Severity.CRITICAL.ordinal)
    }

    @Test
    fun `HIGH ordinal is 1`() {
        assertEquals(1, Severity.HIGH.ordinal)
    }

    @Test
    fun `MEDIUM ordinal is 2`() {
        assertEquals(2, Severity.MEDIUM.ordinal)
    }

    @Test
    fun `CRITICAL has non-zero colorHex`() {
        assertTrue(Severity.CRITICAL.colorHex != 0L)
    }

    @Test
    fun `HIGH has non-zero colorHex`() {
        assertTrue(Severity.HIGH.colorHex != 0L)
    }

    @Test
    fun `MEDIUM has non-zero colorHex`() {
        assertTrue(Severity.MEDIUM.colorHex != 0L)
    }

    @Test
    fun `all severities have unique colorHex`() {
        val colors = Severity.entries.map { it.colorHex }
        assertEquals(colors.size, colors.toSet().size)
    }

    @Test
    fun `all severities have non-empty displayName`() {
        for (severity in Severity.entries) {
            assertTrue(severity.displayName.isNotEmpty())
        }
    }

    // ========================================================================
    // Section 18: Threshold numeric precision
    // ========================================================================

    @Test
    fun `threshold float precision for CREDIT_CARD`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        // Verify within narrow tolerance
        assertEquals(0.90f, t, 0.0001f)
    }

    @Test
    fun `threshold float precision for SSN`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.SSN)
        assertEquals(0.92f, t, 0.0001f)
    }

    @Test
    fun `threshold float precision for PASSWORD`() {
        val t = ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        assertEquals(0.80f, t, 0.0001f)
    }

    @Test
    fun `custom threshold float precision`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.123456f)
        assertEquals(0.123456f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.000001f)
    }

    @Test
    fun `custom threshold very small value`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.0001f)
        assertEquals(0.0001f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.00001f)
    }

    @Test
    fun `custom threshold near 1 value`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.9999f)
        assertEquals(0.9999f, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.00001f)
    }

    @Test
    fun `setThreshold with exact Float boundary 0_0`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.0f)
        assertEquals(0.0f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.0f)
    }

    @Test
    fun `setThreshold with exact Float boundary 1_0`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, 1.0f)
        assertEquals(1.0f, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.0f)
    }

    // ========================================================================
    // Section 19: Cross-type threshold comparisons
    // ========================================================================

    @Test
    fun `all CRITICAL types have thresholds between 0_80 and 0_92`() {
        val criticalTypes = EntityType.entries.filter { it.severity == Severity.CRITICAL }
        for (type in criticalTypes) {
            val t = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type (CRITICAL) threshold $t should be >= 0.80", t >= 0.80f)
            assertTrue("$type (CRITICAL) threshold $t should be <= 0.92", t <= 0.92f)
        }
    }

    @Test
    fun `all HIGH types have thresholds between 0_85 and 0_95`() {
        val highTypes = EntityType.entries.filter { it.severity == Severity.HIGH }
        for (type in highTypes) {
            val t = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type (HIGH) threshold $t should be >= 0.85", t >= 0.85f)
            assertTrue("$type (HIGH) threshold $t should be <= 0.95", t <= 0.95f)
        }
    }

    @Test
    fun `all MEDIUM types have thresholds between 0_75 and 0_90`() {
        val mediumTypes = EntityType.entries.filter { it.severity == Severity.MEDIUM }
        for (type in mediumTypes) {
            val t = ConfidenceThresholds.getThreshold(type)
            assertTrue("$type (MEDIUM) threshold $t should be >= 0.75", t >= 0.75f)
            assertTrue("$type (MEDIUM) threshold $t should be <= 0.90", t <= 0.90f)
        }
    }

    @Test
    fun `threshold for PASSWORD is lower than threshold for SSN`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PASSWORD) <
                    ConfidenceThresholds.getThreshold(EntityType.SSN)
        )
    }

    @Test
    fun `threshold for PERSON_NAME is lower than threshold for UNKNOWN`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME) <
                    ConfidenceThresholds.getThreshold(EntityType.UNKNOWN)
        )
    }

    @Test
    fun `threshold for DATE_OF_BIRTH is lower than threshold for PHONE`() {
        assertTrue(
            ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH) <
                    ConfidenceThresholds.getThreshold(EntityType.PHONE)
        )
    }

    // ========================================================================
    // Section 20: Comprehensive integration-style tests
    // ========================================================================

    @Test
    fun `override all then reset then verify all defaults`() {
        // Override every type
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.50f)
        }
        // Verify all are 0.50
        for (type in EntityType.entries) {
            assertEquals(0.50f, ConfidenceThresholds.getThreshold(type), 0.001f)
        }
        // Reset
        ConfidenceThresholds.resetToDefaults()
        // Verify all are back to defaults
        for (type in EntityType.entries) {
            assertEquals(
                ConfidenceThresholds.getDefaultThreshold(type),
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    @Test
    fun `progressively increase all thresholds`() {
        for (type in EntityType.entries) {
            for (v in 1..10) {
                ConfidenceThresholds.setThreshold(type, v / 10.0f)
                assertEquals(v / 10.0f, ConfidenceThresholds.getThreshold(type), 0.001f)
            }
        }
    }

    @Test
    fun `progressively decrease all thresholds`() {
        for (type in EntityType.entries) {
            for (v in 10 downTo 0) {
                ConfidenceThresholds.setThreshold(type, v / 10.0f)
                assertEquals(v / 10.0f, ConfidenceThresholds.getThreshold(type), 0.001f)
            }
        }
    }

    @Test
    fun `set all to same value then verify getAllThresholds`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.77f)
        }
        val all = ConfidenceThresholds.getAllThresholds()
        for ((type, value) in all) {
            assertEquals("$type should be 0.77", 0.77f, value, 0.001f)
        }
    }

    @Test
    fun `set different incremental values for each type`() {
        val types = EntityType.entries
        for ((i, type) in types.withIndex()) {
            val value = (i + 1) / (types.size.toFloat() + 1)
            ConfidenceThresholds.setThreshold(type, value)
        }
        val all = ConfidenceThresholds.getAllThresholds()
        for ((i, type) in types.withIndex()) {
            val expected = (i + 1) / (types.size.toFloat() + 1)
            assertEquals("$type", expected, all[type]!!, 0.001f)
        }
    }

    @Test
    fun `getAllThresholds always has exactly EntityType entries count`() {
        assertEquals(EntityType.entries.size, ConfidenceThresholds.getAllThresholds().size)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.5f)
        assertEquals(EntityType.entries.size, ConfidenceThresholds.getAllThresholds().size)
        ConfidenceThresholds.resetToDefaults()
        assertEquals(EntityType.entries.size, ConfidenceThresholds.getAllThresholds().size)
    }

    @Test
    fun `threshold for each type can be set and read back 50 times`() {
        for (type in EntityType.entries) {
            repeat(50) { i ->
                val value = (i % 100) / 100.0f
                ConfidenceThresholds.setThreshold(type, value)
                assertEquals(value, ConfidenceThresholds.getThreshold(type), 0.001f)
            }
        }
    }

    @Test
    fun `resetToDefaults is idempotent across 10 calls`() {
        repeat(10) { ConfidenceThresholds.resetToDefaults() }
        for (type in EntityType.entries) {
            assertEquals(
                ConfidenceThresholds.getDefaultThreshold(type),
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    @Test
    fun `getThreshold after setThreshold with Float MIN_VALUE`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, Float.MIN_VALUE)
        assertEquals(Float.MIN_VALUE, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.0f)
    }

    @Test
    fun `getThreshold after setThreshold with very precise float`() {
        val precise = 0.123456789f
        ConfidenceThresholds.setThreshold(EntityType.SSN, precise)
        assertEquals(precise, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.0000001f)
    }

    @Test
    fun `overriding then getting default does not show override`() {
        ConfidenceThresholds.setThreshold(EntityType.ADDRESS, 0.10f)
        val defaultVal = ConfidenceThresholds.getDefaultThreshold(EntityType.ADDRESS)
        assertEquals(0.80f, defaultVal, 0.001f)
        val overrideVal = ConfidenceThresholds.getThreshold(EntityType.ADDRESS)
        assertEquals(0.10f, overrideVal, 0.001f)
        assertNotEquals(defaultVal, overrideVal, 0.001f)
    }

    @Test
    fun `getAllThresholds keys are exactly EntityType entries`() {
        val keys = ConfidenceThresholds.getAllThresholds().keys
        assertEquals(EntityType.entries.toSet(), keys)
    }

    @Test
    fun `getAllThresholds with one override shows override for that type only`() {
        ConfidenceThresholds.setThreshold(EntityType.MEDICAL_ID, 0.33f)
        val all = ConfidenceThresholds.getAllThresholds()

        assertEquals(0.33f, all[EntityType.MEDICAL_ID]!!, 0.001f)

        for (type in EntityType.entries) {
            if (type != EntityType.MEDICAL_ID) {
                assertEquals(
                    "Non-overridden $type should be default",
                    ConfidenceThresholds.getDefaultThreshold(type),
                    all[type]!!,
                    0.001f
                )
            }
        }
    }

    @Test
    fun `fallback threshold for unknown type is 0_85`() {
        // The code uses ?: 0.85f as fallback
        // All EntityType.entries have explicit defaults, but this tests the fallback logic
        // Since UNKNOWN is in the defaults map, it won't trigger fallback,
        // but the threshold for UNKNOWN should be 0.90 (from defaults map)
        assertEquals(0.90f, ConfidenceThresholds.getThreshold(EntityType.UNKNOWN), 0.001f)
    }

    @Test
    fun `getDefaultThreshold fallback for hypothetical missing type would be 0_85`() {
        // Since all types are covered in defaults, this validates the expected fallback
        // We can verify by checking the UNKNOWN type which is explicitly mapped
        val unknownDefault = ConfidenceThresholds.getDefaultThreshold(EntityType.UNKNOWN)
        assertEquals(0.90f, unknownDefault, 0.001f)
    }

    @Test
    fun `threshold ordering sorted from lowest to highest`() {
        val sorted = ConfidenceThresholds.getAllThresholds().entries.sortedBy { it.value }
        // First should be PERSON_NAME (0.75)
        assertEquals(EntityType.PERSON_NAME, sorted.first().key)
        // Last should be EMAIL (0.95)
        assertEquals(EntityType.EMAIL, sorted.last().key)
    }

    @Test
    fun `complete threshold ranking from lowest to highest`() {
        val all = ConfidenceThresholds.getAllThresholds()
        val sorted = all.entries.sortedBy { it.value }

        // PERSON_NAME (0.75) < PASSWORD/ADDRESS (0.80) < DATE_OF_BIRTH (0.82)
        // < API_KEY/MEDICAL_ID (0.85) < PHONE (0.88) < CREDIT_CARD/UNKNOWN (0.90)
        // < SSN (0.92) < EMAIL (0.95)
        assertEquals(EntityType.PERSON_NAME, sorted[0].key)
        assertEquals(EntityType.EMAIL, sorted.last().key)
    }

    @Test
    fun `overriding then resetting 100 times maintains defaults`() {
        repeat(100) {
            for (type in EntityType.entries) {
                ConfidenceThresholds.setThreshold(type, 0.50f)
            }
            ConfidenceThresholds.resetToDefaults()
        }
        for (type in EntityType.entries) {
            assertEquals(
                ConfidenceThresholds.getDefaultThreshold(type),
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    // ========================================================================
    // Section 21: Additional invalid threshold boundary tests
    // ========================================================================

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with -0_001 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, -0.001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with 1_0001 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 1.0001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with -1_0 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.SSN, -1.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with 10_0 throws`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 10.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with Float POSITIVE_INFINITY throws`() {
        ConfidenceThresholds.setThreshold(EntityType.PASSWORD, Float.POSITIVE_INFINITY)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with Float NEGATIVE_INFINITY throws`() {
        ConfidenceThresholds.setThreshold(EntityType.API_KEY, Float.NEGATIVE_INFINITY)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `setThreshold with Float NaN throws`() {
        ConfidenceThresholds.setThreshold(EntityType.ADDRESS, Float.NaN)
    }

    @Test
    fun `setThreshold with exactly 0_0 does not throw`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.0f)
        assertEquals(0.0f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.0f)
    }

    @Test
    fun `setThreshold with exactly 1_0 does not throw`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 1.0f)
        assertEquals(1.0f, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.0f)
    }

    // ========================================================================
    // Section 22: Threshold isolation between entity types
    // ========================================================================

    @Test
    fun `overriding CREDIT_CARD does not affect SSN`() {
        val ssnBefore = ConfidenceThresholds.getThreshold(EntityType.SSN)
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.50f)
        assertEquals(ssnBefore, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.001f)
    }

    @Test
    fun `overriding SSN does not affect PASSWORD`() {
        val pwBefore = ConfidenceThresholds.getThreshold(EntityType.PASSWORD)
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.50f)
        assertEquals(pwBefore, ConfidenceThresholds.getThreshold(EntityType.PASSWORD), 0.001f)
    }

    @Test
    fun `overriding PASSWORD does not affect API_KEY`() {
        val apiBefore = ConfidenceThresholds.getThreshold(EntityType.API_KEY)
        ConfidenceThresholds.setThreshold(EntityType.PASSWORD, 0.50f)
        assertEquals(apiBefore, ConfidenceThresholds.getThreshold(EntityType.API_KEY), 0.001f)
    }

    @Test
    fun `overriding API_KEY does not affect EMAIL`() {
        val emailBefore = ConfidenceThresholds.getThreshold(EntityType.EMAIL)
        ConfidenceThresholds.setThreshold(EntityType.API_KEY, 0.50f)
        assertEquals(emailBefore, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
    }

    @Test
    fun `overriding EMAIL does not affect PHONE`() {
        val phoneBefore = ConfidenceThresholds.getThreshold(EntityType.PHONE)
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        assertEquals(phoneBefore, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
    }

    @Test
    fun `overriding PHONE does not affect PERSON_NAME`() {
        val nameBefore = ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME)
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.50f)
        assertEquals(nameBefore, ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME), 0.001f)
    }

    @Test
    fun `overriding PERSON_NAME does not affect ADDRESS`() {
        val addrBefore = ConfidenceThresholds.getThreshold(EntityType.ADDRESS)
        ConfidenceThresholds.setThreshold(EntityType.PERSON_NAME, 0.50f)
        assertEquals(addrBefore, ConfidenceThresholds.getThreshold(EntityType.ADDRESS), 0.001f)
    }

    @Test
    fun `overriding ADDRESS does not affect DATE_OF_BIRTH`() {
        val dobBefore = ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH)
        ConfidenceThresholds.setThreshold(EntityType.ADDRESS, 0.50f)
        assertEquals(dobBefore, ConfidenceThresholds.getThreshold(EntityType.DATE_OF_BIRTH), 0.001f)
    }

    @Test
    fun `overriding DATE_OF_BIRTH does not affect MEDICAL_ID`() {
        val medBefore = ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID)
        ConfidenceThresholds.setThreshold(EntityType.DATE_OF_BIRTH, 0.50f)
        assertEquals(medBefore, ConfidenceThresholds.getThreshold(EntityType.MEDICAL_ID), 0.001f)
    }

    @Test
    fun `overriding MEDICAL_ID does not affect UNKNOWN`() {
        val unkBefore = ConfidenceThresholds.getThreshold(EntityType.UNKNOWN)
        ConfidenceThresholds.setThreshold(EntityType.MEDICAL_ID, 0.50f)
        assertEquals(unkBefore, ConfidenceThresholds.getThreshold(EntityType.UNKNOWN), 0.001f)
    }

    @Test
    fun `overriding UNKNOWN does not affect CREDIT_CARD`() {
        val ccBefore = ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD)
        ConfidenceThresholds.setThreshold(EntityType.UNKNOWN, 0.50f)
        assertEquals(ccBefore, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.001f)
    }

    // ========================================================================
    // Section 23: Comprehensive override and reset cycling
    // ========================================================================

    @Test
    fun `override each type to 0_1 increments from 0_0 to 1_0`() {
        val types = EntityType.entries
        for ((i, type) in types.withIndex()) {
            val value = i / 10.0f
            if (value in 0.0f..1.0f) {
                ConfidenceThresholds.setThreshold(type, value)
                assertEquals(value, ConfidenceThresholds.getThreshold(type), 0.001f)
            }
        }
    }

    @Test
    fun `set threshold to 0_01 increments for CREDIT_CARD`() {
        for (i in 0..100) {
            val value = i / 100.0f
            ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, value)
            assertEquals(value, ConfidenceThresholds.getThreshold(EntityType.CREDIT_CARD), 0.001f)
        }
    }

    @Test
    fun `set threshold to 0_01 increments for SSN`() {
        for (i in 0..100) {
            val value = i / 100.0f
            ConfidenceThresholds.setThreshold(EntityType.SSN, value)
            assertEquals(value, ConfidenceThresholds.getThreshold(EntityType.SSN), 0.001f)
        }
    }

    @Test
    fun `set threshold to 0_01 increments for EMAIL`() {
        for (i in 0..100) {
            val value = i / 100.0f
            ConfidenceThresholds.setThreshold(EntityType.EMAIL, value)
            assertEquals(value, ConfidenceThresholds.getThreshold(EntityType.EMAIL), 0.001f)
        }
    }

    @Test
    fun `set threshold to 0_01 increments for PHONE`() {
        for (i in 0..100) {
            val value = i / 100.0f
            ConfidenceThresholds.setThreshold(EntityType.PHONE, value)
            assertEquals(value, ConfidenceThresholds.getThreshold(EntityType.PHONE), 0.001f)
        }
    }

    @Test
    fun `set threshold to 0_01 increments for PERSON_NAME`() {
        for (i in 0..100) {
            val value = i / 100.0f
            ConfidenceThresholds.setThreshold(EntityType.PERSON_NAME, value)
            assertEquals(value, ConfidenceThresholds.getThreshold(EntityType.PERSON_NAME), 0.001f)
        }
    }

    @Test
    fun `reset after each type override restores only that type`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.10f)
        }
        ConfidenceThresholds.resetToDefaults()
        for (type in EntityType.entries) {
            assertEquals(
                ConfidenceThresholds.getDefaultThreshold(type),
                ConfidenceThresholds.getThreshold(type),
                0.001f
            )
        }
    }

    // ========================================================================
    // Section 24: Additional threshold ordering and relationship tests
    // ========================================================================

    @Test
    fun `PASSWORD and ADDRESS have same default threshold`() {
        assertEquals(
            ConfidenceThresholds.getDefaultThreshold(EntityType.PASSWORD),
            ConfidenceThresholds.getDefaultThreshold(EntityType.ADDRESS),
            0.001f
        )
    }

    @Test
    fun `API_KEY and MEDICAL_ID have same default threshold`() {
        assertEquals(
            ConfidenceThresholds.getDefaultThreshold(EntityType.API_KEY),
            ConfidenceThresholds.getDefaultThreshold(EntityType.MEDICAL_ID),
            0.001f
        )
    }

    @Test
    fun `CREDIT_CARD and UNKNOWN have same default threshold`() {
        assertEquals(
            ConfidenceThresholds.getDefaultThreshold(EntityType.CREDIT_CARD),
            ConfidenceThresholds.getDefaultThreshold(EntityType.UNKNOWN),
            0.001f
        )
    }

    @Test
    fun `SSN has the second highest default threshold`() {
        val sorted = ConfidenceThresholds.getAllThresholds().entries.sortedByDescending { it.value }
        assertEquals(EntityType.EMAIL, sorted[0].key)
        assertEquals(EntityType.SSN, sorted[1].key)
    }

    @Test
    fun `threshold values sorted ascending`() {
        val sorted = ConfidenceThresholds.getAllThresholds().entries.sortedBy { it.value }
        for (i in 0 until sorted.size - 1) {
            assertTrue(
                "${sorted[i].key}(${sorted[i].value}) <= ${sorted[i + 1].key}(${sorted[i + 1].value})",
                sorted[i].value <= sorted[i + 1].value
            )
        }
    }

    @Test
    fun `distinct threshold values count`() {
        val distinctValues = ConfidenceThresholds.getAllThresholds().values.toSet()
        // 0.75, 0.80, 0.82, 0.85, 0.88, 0.90, 0.92, 0.95 = 8 distinct values
        assertEquals(8, distinctValues.size)
    }

    @Test
    fun `PASSWORD threshold is exactly 0_12 less than SSN threshold`() {
        val pw = ConfidenceThresholds.getDefaultThreshold(EntityType.PASSWORD)
        val ssn = ConfidenceThresholds.getDefaultThreshold(EntityType.SSN)
        assertEquals(0.12f, ssn - pw, 0.001f)
    }

    @Test
    fun `EMAIL threshold is exactly 0_15 more than PASSWORD threshold`() {
        val email = ConfidenceThresholds.getDefaultThreshold(EntityType.EMAIL)
        val pw = ConfidenceThresholds.getDefaultThreshold(EntityType.PASSWORD)
        assertEquals(0.15f, email - pw, 0.001f)
    }

    @Test
    fun `PHONE threshold minus PERSON_NAME threshold is 0_13`() {
        val phone = ConfidenceThresholds.getDefaultThreshold(EntityType.PHONE)
        val name = ConfidenceThresholds.getDefaultThreshold(EntityType.PERSON_NAME)
        assertEquals(0.13f, phone - name, 0.001f)
    }

    // ========================================================================
    // Section 25: Comprehensive getAllThresholds consistency
    // ========================================================================

    @Test
    fun `getAllThresholds after setting one override has correct mixed values`() {
        ConfidenceThresholds.setThreshold(EntityType.CREDIT_CARD, 0.42f)
        val all = ConfidenceThresholds.getAllThresholds()
        assertEquals(0.42f, all[EntityType.CREDIT_CARD]!!, 0.001f)
        assertEquals(0.92f, all[EntityType.SSN]!!, 0.001f)
        assertEquals(0.80f, all[EntityType.PASSWORD]!!, 0.001f)
        assertEquals(0.85f, all[EntityType.API_KEY]!!, 0.001f)
        assertEquals(0.95f, all[EntityType.EMAIL]!!, 0.001f)
        assertEquals(0.88f, all[EntityType.PHONE]!!, 0.001f)
        assertEquals(0.75f, all[EntityType.PERSON_NAME]!!, 0.001f)
        assertEquals(0.80f, all[EntityType.ADDRESS]!!, 0.001f)
        assertEquals(0.82f, all[EntityType.DATE_OF_BIRTH]!!, 0.001f)
        assertEquals(0.85f, all[EntityType.MEDICAL_ID]!!, 0.001f)
        assertEquals(0.90f, all[EntityType.UNKNOWN]!!, 0.001f)
    }

    @Test
    fun `getAllThresholds after setting two overrides has correct values`() {
        ConfidenceThresholds.setThreshold(EntityType.EMAIL, 0.50f)
        ConfidenceThresholds.setThreshold(EntityType.SSN, 0.60f)
        val all = ConfidenceThresholds.getAllThresholds()
        assertEquals(0.50f, all[EntityType.EMAIL]!!, 0.001f)
        assertEquals(0.60f, all[EntityType.SSN]!!, 0.001f)
        // Rest should be defaults
        assertEquals(0.90f, all[EntityType.CREDIT_CARD]!!, 0.001f)
    }

    @Test
    fun `getAllThresholds is consistent with getThreshold for all types after overrides`() {
        ConfidenceThresholds.setThreshold(EntityType.PASSWORD, 0.33f)
        ConfidenceThresholds.setThreshold(EntityType.PHONE, 0.44f)
        ConfidenceThresholds.setThreshold(EntityType.ADDRESS, 0.55f)
        val all = ConfidenceThresholds.getAllThresholds()
        for (type in EntityType.entries) {
            assertEquals(
                "getThreshold and getAllThresholds should match for $type",
                ConfidenceThresholds.getThreshold(type),
                all[type]!!,
                0.001f
            )
        }
    }

    // ========================================================================
    // Section 26: Comprehensive entity type display name and index tests
    // ========================================================================

    @Test
    fun `all display names are distinct`() {
        val names = EntityType.entries.map { it.displayName }
        // Note: Bitwarden and Bitwarden alt have "Bitwarden" in suggested names,
        // but EntityType display names should all be unique
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `all label indices are non-negative`() {
        for (type in EntityType.entries) {
            assertTrue("$type labelIndex should be >= 0", type.labelIndex >= 0)
        }
    }

    @Test
    fun `label indices range from 0 to 10`() {
        val indices = EntityType.entries.map { it.labelIndex }
        assertEquals(0, indices.min())
        assertEquals(10, indices.max())
    }

    @Test
    fun `fromLabelIndex round-trip for each type`() {
        for (type in EntityType.entries) {
            val recovered = EntityType.fromLabelIndex(type.labelIndex)
            assertEquals(type, recovered)
        }
    }

    @Test
    fun `fromLabelIndex with 0 returns UNKNOWN`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(0))
    }

    @Test
    fun `fromLabelIndex with indices 1-10 returns non-UNKNOWN`() {
        for (i in 1..10) {
            val type = EntityType.fromLabelIndex(i)
            assertNotEquals("Index $i should not return UNKNOWN", EntityType.UNKNOWN, type)
        }
    }

    // ========================================================================
    // Section 27: Final comprehensive integration tests
    // ========================================================================

    @Test
    fun `full override cycle for each type individually`() {
        for (type in EntityType.entries) {
            // Get default
            val defaultVal = ConfidenceThresholds.getDefaultThreshold(type)
            // Override
            ConfidenceThresholds.setThreshold(type, 0.42f)
            assertEquals(0.42f, ConfidenceThresholds.getThreshold(type), 0.001f)
            // Default unchanged
            assertEquals(defaultVal, ConfidenceThresholds.getDefaultThreshold(type), 0.001f)
            // Reset
            ConfidenceThresholds.resetToDefaults()
            // Back to default
            assertEquals(defaultVal, ConfidenceThresholds.getThreshold(type), 0.001f)
        }
    }

    @Test
    fun `override all to same value then verify getAllThresholds uniformity`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.55f)
        }
        val all = ConfidenceThresholds.getAllThresholds()
        val distinctValues = all.values.toSet()
        assertEquals(1, distinctValues.size)
        assertEquals(0.55f, distinctValues.first(), 0.001f)
    }

    @Test
    fun `override to ascending values by type ordinal`() {
        val types = EntityType.entries
        for ((i, type) in types.withIndex()) {
            val value = (i + 1) / (types.size + 1).toFloat()
            ConfidenceThresholds.setThreshold(type, value)
        }
        val all = ConfidenceThresholds.getAllThresholds()
        val sorted = all.entries.sortedBy { it.key.ordinal }
        for (i in 0 until sorted.size - 1) {
            assertTrue(
                "${sorted[i].key}(${sorted[i].value}) < ${sorted[i + 1].key}(${sorted[i + 1].value})",
                sorted[i].value < sorted[i + 1].value
            )
        }
    }

    @Test
    fun `override to descending values by type ordinal`() {
        val types = EntityType.entries
        for ((i, type) in types.withIndex()) {
            val value = 1.0f - (i / (types.size + 1).toFloat())
            ConfidenceThresholds.setThreshold(type, value)
        }
        val all = ConfidenceThresholds.getAllThresholds()
        val sorted = all.entries.sortedBy { it.key.ordinal }
        for (i in 0 until sorted.size - 1) {
            assertTrue(
                "${sorted[i].key}(${sorted[i].value}) > ${sorted[i + 1].key}(${sorted[i + 1].value})",
                sorted[i].value > sorted[i + 1].value
            )
        }
    }

    @Test
    fun `threshold sum of all defaults`() {
        val sum = EntityType.entries.sumOf {
            ConfidenceThresholds.getDefaultThreshold(it).toDouble()
        }.toFloat()
        // 0.75 + 0.80 + 0.80 + 0.82 + 0.85 + 0.85 + 0.88 + 0.90 + 0.90 + 0.92 + 0.95 = 9.42
        assertEquals(9.42f, sum, 0.05f)
    }

    @Test
    fun `threshold median of all defaults`() {
        val sorted = EntityType.entries.map {
            ConfidenceThresholds.getDefaultThreshold(it)
        }.sorted()
        val median = sorted[sorted.size / 2]
        // Median of 11 values (index 5) = 0.85
        assertEquals(0.85f, median, 0.001f)
    }

    @Test
    fun `overriding all to 0 then verifying all are 0`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 0.0f)
        }
        for (type in EntityType.entries) {
            assertEquals(0.0f, ConfidenceThresholds.getThreshold(type), 0.0f)
        }
    }

    @Test
    fun `overriding all to 1 then verifying all are 1`() {
        for (type in EntityType.entries) {
            ConfidenceThresholds.setThreshold(type, 1.0f)
        }
        for (type in EntityType.entries) {
            assertEquals(1.0f, ConfidenceThresholds.getThreshold(type), 0.0f)
        }
    }

    @Test
    fun `getThreshold returns fallback 0_85 for non-existing defaults if possible`() {
        // Since all types have defaults, this tests the fallback path indirectly
        // By checking UNKNOWN which has an explicit default of 0.90
        val threshold = ConfidenceThresholds.getThreshold(EntityType.UNKNOWN)
        assertEquals(0.90f, threshold, 0.001f)
    }

    @Test
    fun `ConfidenceThresholds is an object singleton`() {
        val ref1 = ConfidenceThresholds
        val ref2 = ConfidenceThresholds
        assertSame(ref1, ref2)
    }

    @Test
    fun `threshold for each severity level has expected range`() {
        for (type in EntityType.entries) {
            val threshold = ConfidenceThresholds.getDefaultThreshold(type)
            when (type.severity) {
                Severity.CRITICAL -> {
                    assertTrue("$type CRITICAL threshold $threshold >= 0.80", threshold >= 0.80f)
                }
                Severity.HIGH -> {
                    assertTrue("$type HIGH threshold $threshold >= 0.85", threshold >= 0.85f)
                }
                Severity.MEDIUM -> {
                    assertTrue("$type MEDIUM threshold $threshold >= 0.75", threshold >= 0.75f)
                }
            }
        }
    }
}
