package com.privacyguard.ml

import org.junit.Assert.*
import org.junit.Test

class EntityTypeTest {

    @Test
    fun `all entity types have correct severity`() {
        assertEquals(Severity.CRITICAL, EntityType.CREDIT_CARD.severity)
        assertEquals(Severity.CRITICAL, EntityType.SSN.severity)
        assertEquals(Severity.CRITICAL, EntityType.PASSWORD.severity)
        assertEquals(Severity.CRITICAL, EntityType.API_KEY.severity)
        assertEquals(Severity.HIGH, EntityType.EMAIL.severity)
        assertEquals(Severity.HIGH, EntityType.PHONE.severity)
        assertEquals(Severity.HIGH, EntityType.MEDICAL_ID.severity)
        assertEquals(Severity.MEDIUM, EntityType.PERSON_NAME.severity)
        assertEquals(Severity.MEDIUM, EntityType.ADDRESS.severity)
        assertEquals(Severity.MEDIUM, EntityType.DATE_OF_BIRTH.severity)
        assertEquals(Severity.MEDIUM, EntityType.UNKNOWN.severity)
    }

    @Test
    fun `fromLabelIndex returns correct type`() {
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
    fun `fromLabelIndex returns UNKNOWN for invalid index`() {
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(-1))
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(99))
        assertEquals(EntityType.UNKNOWN, EntityType.fromLabelIndex(100))
    }

    @Test
    fun `entity types have display names`() {
        EntityType.entries.forEach { type ->
            assertTrue("${type.name} should have a non-empty display name", type.displayName.isNotEmpty())
        }
    }

    @Test
    fun `all entity types have unique label indices except UNKNOWN`() {
        val nonUnknown = EntityType.entries.filter { it != EntityType.UNKNOWN }
        val indices = nonUnknown.map { it.labelIndex }
        assertEquals("Label indices should be unique", indices.size, indices.toSet().size)
    }

    @Test
    fun `critical types are financial and credential types`() {
        val criticalTypes = EntityType.entries.filter { it.severity == Severity.CRITICAL }
        assertTrue(criticalTypes.contains(EntityType.CREDIT_CARD))
        assertTrue(criticalTypes.contains(EntityType.SSN))
        assertTrue(criticalTypes.contains(EntityType.PASSWORD))
        assertTrue(criticalTypes.contains(EntityType.API_KEY))
    }
}
