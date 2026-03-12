package com.privacyguard.data

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import org.junit.Assert.*
import org.junit.Test

class DetectionEventTest {

    @Test
    fun `event serialization round-trip preserves all fields`() {
        val event = DetectionEvent(
            id = "test-uuid-123",
            timestamp = 1710000000000L,
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL,
            sourceApp = "com.example.app",
            sourceAppName = "Example App",
            actionTaken = UserAction.CLIPBOARD_CLEARED,
            confidence = 0.95f,
            inferenceTimeMs = 42L
        )

        val json = event.toJson()
        val restored = DetectionEvent.fromJson(json)

        assertEquals(event.id, restored.id)
        assertEquals(event.timestamp, restored.timestamp)
        assertEquals(event.entityType, restored.entityType)
        assertEquals(event.severity, restored.severity)
        assertEquals(event.sourceApp, restored.sourceApp)
        assertEquals(event.sourceAppName, restored.sourceAppName)
        assertEquals(event.actionTaken, restored.actionTaken)
        assertEquals(event.confidence, restored.confidence, 0.001f)
        assertEquals(event.inferenceTimeMs, restored.inferenceTimeMs)
    }

    @Test
    fun `event serialization with null source app`() {
        val event = DetectionEvent(
            entityType = EntityType.SSN,
            severity = Severity.CRITICAL,
            sourceApp = null,
            sourceAppName = null
        )

        val json = event.toJson()
        val restored = DetectionEvent.fromJson(json)

        assertNull(restored.sourceApp)
        assertNull(restored.sourceAppName)
        assertEquals(EntityType.SSN, restored.entityType)
    }

    @Test
    fun `event has auto-generated id`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertNotNull(event.id)
        assertTrue(event.id.isNotEmpty())
    }

    @Test
    fun `event has auto-generated timestamp`() {
        val before = System.currentTimeMillis()
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val after = System.currentTimeMillis()

        assertTrue(event.timestamp in before..after)
    }

    @Test
    fun `two events have different ids`() {
        val e1 = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH)
        val e2 = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH)
        assertNotEquals(e1.id, e2.id)
    }

    @Test
    fun `all user actions have display names`() {
        UserAction.entries.forEach { action ->
            assertTrue(action.displayName.isNotEmpty())
        }
    }

    @Test
    fun `event default action is NO_ACTION`() {
        val event = DetectionEvent(
            entityType = EntityType.PHONE,
            severity = Severity.HIGH
        )
        assertEquals(UserAction.NO_ACTION, event.actionTaken)
    }

    @Test
    fun `serialization preserves all user action types`() {
        UserAction.entries.forEach { action ->
            val event = DetectionEvent(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                actionTaken = action
            )
            val restored = DetectionEvent.fromJson(event.toJson())
            assertEquals(action, restored.actionTaken)
        }
    }

    @Test
    fun `serialization preserves all entity types`() {
        EntityType.entries.forEach { type ->
            val event = DetectionEvent(
                entityType = type,
                severity = type.severity
            )
            val restored = DetectionEvent.fromJson(event.toJson())
            assertEquals(type, restored.entityType)
        }
    }

    @Test
    fun `json output does not contain rawText field`() {
        val event = DetectionEvent(
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL
        )
        val json = event.toJson()
        assertFalse("JSON should not contain rawText", json.contains("rawText"))
    }
}
