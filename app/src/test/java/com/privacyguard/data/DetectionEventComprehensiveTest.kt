package com.privacyguard.data

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import org.junit.Assert.*
import org.junit.Test

class DetectionEventComprehensiveTest {

    // === CONSTRUCTION ===

    @Test
    fun `create event with all fields`() {
        val event = DetectionEvent(
            id = "test-id",
            timestamp = 1000L,
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL,
            sourceApp = "com.test",
            sourceAppName = "Test App",
            actionTaken = UserAction.CLIPBOARD_CLEARED,
            confidence = 0.99f,
            inferenceTimeMs = 25L
        )
        assertEquals("test-id", event.id)
        assertEquals(1000L, event.timestamp)
        assertEquals(EntityType.CREDIT_CARD, event.entityType)
        assertEquals(Severity.CRITICAL, event.severity)
        assertEquals("com.test", event.sourceApp)
        assertEquals("Test App", event.sourceAppName)
        assertEquals(UserAction.CLIPBOARD_CLEARED, event.actionTaken)
        assertEquals(0.99f, event.confidence, 0.001f)
        assertEquals(25L, event.inferenceTimeMs)
    }

    @Test
    fun `create event with defaults`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertNotNull(event.id)
        assertTrue(event.id.isNotEmpty())
        assertTrue(event.timestamp > 0)
        assertNull(event.sourceApp)
        assertNull(event.sourceAppName)
        assertEquals(UserAction.NO_ACTION, event.actionTaken)
        assertEquals(0f, event.confidence, 0.001f)
        assertEquals(0L, event.inferenceTimeMs)
    }

    @Test
    fun `each event gets unique id`() {
        val ids = (1..100).map {
            DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH).id
        }.toSet()
        assertEquals("All 100 events should have unique IDs", 100, ids.size)
    }

    @Test
    fun `timestamp represents current time`() {
        val before = System.currentTimeMillis()
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH)
        val after = System.currentTimeMillis()
        assertTrue(event.timestamp >= before)
        assertTrue(event.timestamp <= after)
    }

    @Test
    fun `custom id is preserved`() {
        val customId = "my-custom-id-12345"
        val event = DetectionEvent(id = customId, entityType = EntityType.SSN, severity = Severity.CRITICAL)
        assertEquals(customId, event.id)
    }

    @Test
    fun `custom timestamp is preserved`() {
        val customTimestamp = 1609459200000L // Jan 1, 2021
        val event = DetectionEvent(
            timestamp = customTimestamp,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertEquals(customTimestamp, event.timestamp)
    }

    @Test
    fun `create event for every entity type`() {
        EntityType.entries.forEach { type ->
            val event = DetectionEvent(entityType = type, severity = type.severity)
            assertEquals(type, event.entityType)
            assertEquals(type.severity, event.severity)
        }
    }

    @Test
    fun `create event for every severity`() {
        Severity.entries.forEach { sev ->
            val event = DetectionEvent(entityType = EntityType.EMAIL, severity = sev)
            assertEquals(sev, event.severity)
        }
    }

    @Test
    fun `create event for every user action`() {
        UserAction.entries.forEach { action ->
            val event = DetectionEvent(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                actionTaken = action
            )
            assertEquals(action, event.actionTaken)
        }
    }

    // === SERIALIZATION / DESERIALIZATION ===

    @Test
    fun `round trip for every entity type`() {
        EntityType.entries.forEach { type ->
            val original = DetectionEvent(
                entityType = type,
                severity = type.severity,
                sourceApp = "com.test.${type.name.lowercase()}",
                confidence = 0.85f + (type.ordinal * 0.01f),
                inferenceTimeMs = type.ordinal.toLong() * 10
            )
            val json = original.toJson()
            val restored = DetectionEvent.fromJson(json)
            assertEquals("EntityType mismatch for $type", type, restored.entityType)
            assertEquals("Severity mismatch for $type", type.severity, restored.severity)
            assertEquals("SourceApp mismatch for $type", original.sourceApp, restored.sourceApp)
            assertEquals("Confidence mismatch for $type", original.confidence, restored.confidence, 0.001f)
            assertEquals("InferenceTime mismatch for $type", original.inferenceTimeMs, restored.inferenceTimeMs)
        }
    }

    @Test
    fun `round trip for every user action`() {
        UserAction.entries.forEach { action ->
            val original = DetectionEvent(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                actionTaken = action
            )
            val restored = DetectionEvent.fromJson(original.toJson())
            assertEquals("Action mismatch for $action", action, restored.actionTaken)
        }
    }

    @Test
    fun `round trip for every severity`() {
        Severity.entries.forEach { sev ->
            val original = DetectionEvent(
                entityType = EntityType.EMAIL,
                severity = sev
            )
            val restored = DetectionEvent.fromJson(original.toJson())
            assertEquals("Severity mismatch for $sev", sev, restored.severity)
        }
    }

    @Test
    fun `serialization preserves null source app`() {
        val event = DetectionEvent(
            entityType = EntityType.SSN,
            severity = Severity.CRITICAL,
            sourceApp = null
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertNull(restored.sourceApp)
    }

    @Test
    fun `serialization preserves null source app name`() {
        val event = DetectionEvent(
            entityType = EntityType.SSN,
            severity = Severity.CRITICAL,
            sourceAppName = null
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertNull(restored.sourceAppName)
    }

    @Test
    fun `serialization handles special characters in source app name`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            sourceAppName = "App with \"quotes\" and <brackets> & ampersands"
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(event.sourceAppName, restored.sourceAppName)
    }

    @Test
    fun `serialization handles unicode in source app name`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            sourceAppName = "\u5E94\u7528\u540D\u79F0 \uD83C\uDF89"
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(event.sourceAppName, restored.sourceAppName)
    }

    @Test
    fun `json does not contain raw text field`() {
        val event = DetectionEvent(
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL,
            confidence = 0.99f
        )
        val json = event.toJson()
        assertFalse("JSON must not contain rawText", json.contains("rawText"))
        assertFalse("JSON must not contain sensitiveText", json.contains("sensitiveText"))
        assertFalse("JSON must not contain detectedText", json.contains("detectedText"))
    }

    @Test
    fun `json output is valid json`() {
        val event = DetectionEvent(
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL
        )
        val json = event.toJson()
        assertTrue("Should start with {", json.startsWith("{"))
        assertTrue("Should end with }", json.endsWith("}"))
    }

    @Test
    fun `serialize 100 events without error`() {
        val events = (1..100).map {
            DetectionEvent(
                entityType = EntityType.entries[it % EntityType.entries.size],
                severity = Severity.entries[it % Severity.entries.size],
                confidence = it / 100f
            )
        }
        events.forEach { event ->
            val json = event.toJson()
            val restored = DetectionEvent.fromJson(json)
            assertEquals(event.entityType, restored.entityType)
        }
    }

    @Test
    fun `serialization preserves id`() {
        val event = DetectionEvent(
            id = "preserve-this-id",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals("preserve-this-id", restored.id)
    }

    @Test
    fun `serialization preserves timestamp`() {
        val event = DetectionEvent(
            timestamp = 1609459200000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(1609459200000L, restored.timestamp)
    }

    @Test
    fun `serialization preserves all fields together`() {
        val event = DetectionEvent(
            id = "full-test",
            timestamp = 12345678L,
            entityType = EntityType.API_KEY,
            severity = Severity.CRITICAL,
            sourceApp = "com.example.app",
            sourceAppName = "Example App",
            actionTaken = UserAction.CLIPBOARD_CLEARED,
            confidence = 0.97f,
            inferenceTimeMs = 42L
        )
        val restored = DetectionEvent.fromJson(event.toJson())
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
    fun `json contains expected field names`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            sourceApp = "com.test",
            confidence = 0.88f
        )
        val json = event.toJson()
        assertTrue("JSON should contain id field", json.contains("\"id\""))
        assertTrue("JSON should contain timestamp field", json.contains("\"timestamp\""))
        assertTrue("JSON should contain entityType field", json.contains("\"entityType\""))
        assertTrue("JSON should contain severity field", json.contains("\"severity\""))
        assertTrue("JSON should contain sourceApp field", json.contains("\"sourceApp\""))
        assertTrue("JSON should contain actionTaken field", json.contains("\"actionTaken\""))
        assertTrue("JSON should contain confidence field", json.contains("\"confidence\""))
        assertTrue("JSON should contain inferenceTimeMs field", json.contains("\"inferenceTimeMs\""))
    }

    @Test
    fun `serialization handles backslash in source app name`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            sourceAppName = "C:\\Users\\test"
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals("C:\\Users\\test", restored.sourceAppName)
    }

    @Test
    fun `serialization handles newlines in source app name`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            sourceAppName = "Line1\nLine2\tTabbed"
        )
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals("Line1\nLine2\tTabbed", restored.sourceAppName)
    }

    // === DATA CLASS FEATURES ===

    @Test
    fun `copy preserves all fields`() {
        val original = DetectionEvent(
            id = "copy-test",
            entityType = EntityType.PHONE,
            severity = Severity.HIGH,
            sourceApp = "com.test",
            confidence = 0.88f
        )
        val copy = original.copy(actionTaken = UserAction.DISMISSED)
        assertEquals(original.id, copy.id)
        assertEquals(original.entityType, copy.entityType)
        assertEquals(original.severity, copy.severity)
        assertEquals(original.sourceApp, copy.sourceApp)
        assertEquals(original.confidence, copy.confidence, 0.001f)
        assertEquals(UserAction.DISMISSED, copy.actionTaken)
    }

    @Test
    fun `copy with changed entity type`() {
        val original = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val copy = original.copy(entityType = EntityType.CREDIT_CARD)
        assertEquals(EntityType.CREDIT_CARD, copy.entityType)
        assertEquals(original.id, copy.id)
        assertEquals(original.timestamp, copy.timestamp)
    }

    @Test
    fun `copy with changed severity`() {
        val original = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val copy = original.copy(severity = Severity.CRITICAL)
        assertEquals(Severity.CRITICAL, copy.severity)
    }

    @Test
    fun `copy with changed confidence`() {
        val original = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            confidence = 0.5f
        )
        val copy = original.copy(confidence = 0.99f)
        assertEquals(0.99f, copy.confidence, 0.001f)
        assertEquals(0.5f, original.confidence, 0.001f) // original unchanged
    }

    @Test
    fun `equals works for identical events`() {
        val event1 = DetectionEvent(
            id = "same-id",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val event2 = DetectionEvent(
            id = "same-id",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertEquals(event1, event2)
    }

    @Test
    fun `not equals for different id`() {
        val event1 = DetectionEvent(
            id = "id-1",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val event2 = DetectionEvent(
            id = "id-2",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertNotEquals(event1, event2)
    }

    @Test
    fun `not equals for different entity type`() {
        val event1 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val event2 = DetectionEvent(
            id = "same",
            entityType = EntityType.PHONE,
            severity = Severity.HIGH
        )
        assertNotEquals(event1, event2)
    }

    @Test
    fun `not equals for different severity`() {
        val event1 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val event2 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.CRITICAL
        )
        assertNotEquals(event1, event2)
    }

    @Test
    fun `not equals for different action`() {
        val event1 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            actionTaken = UserAction.NO_ACTION
        )
        val event2 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            actionTaken = UserAction.DISMISSED
        )
        assertNotEquals(event1, event2)
    }

    @Test
    fun `not equals for different confidence`() {
        val event1 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            confidence = 0.5f
        )
        val event2 = DetectionEvent(
            id = "same",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            confidence = 0.9f
        )
        assertNotEquals(event1, event2)
    }

    @Test
    fun `hashCode is consistent`() {
        val event = DetectionEvent(
            id = "hash-test",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val hash1 = event.hashCode()
        val hash2 = event.hashCode()
        assertEquals(hash1, hash2)
    }

    @Test
    fun `hashCode is same for equal events`() {
        val event1 = DetectionEvent(
            id = "hash-same",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            confidence = 0.5f
        )
        val event2 = DetectionEvent(
            id = "hash-same",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            confidence = 0.5f
        )
        assertEquals(event1.hashCode(), event2.hashCode())
    }

    @Test
    fun `toString contains entity type`() {
        val event = DetectionEvent(
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL
        )
        assertTrue(event.toString().contains("CREDIT_CARD"))
    }

    @Test
    fun `toString contains severity`() {
        val event = DetectionEvent(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertTrue(event.toString().contains("HIGH"))
    }

    @Test
    fun `toString contains id`() {
        val event = DetectionEvent(
            id = "my-id-xyz",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertTrue(event.toString().contains("my-id-xyz"))
    }

    @Test
    fun `equals itself`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH)
        assertEquals(event, event)
    }

    @Test
    fun `not equals null`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH)
        assertNotEquals(event, null)
    }

    @Test
    fun `not equals different type`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH)
        assertNotEquals(event, "not an event")
    }

    // === DESTRUCTURING ===

    @Test
    fun `destructuring works`() {
        val event = DetectionEvent(
            id = "destr-id",
            timestamp = 5000L,
            entityType = EntityType.SSN,
            severity = Severity.CRITICAL,
            sourceApp = "com.app",
            sourceAppName = "App",
            actionTaken = UserAction.DISMISSED,
            confidence = 0.75f,
            inferenceTimeMs = 10L
        )
        val (id, timestamp, entityType, severity, sourceApp, sourceAppName, actionTaken, confidence, inferenceTimeMs) = event
        assertEquals("destr-id", id)
        assertEquals(5000L, timestamp)
        assertEquals(EntityType.SSN, entityType)
        assertEquals(Severity.CRITICAL, severity)
        assertEquals("com.app", sourceApp)
        assertEquals("App", sourceAppName)
        assertEquals(UserAction.DISMISSED, actionTaken)
        assertEquals(0.75f, confidence, 0.001f)
        assertEquals(10L, inferenceTimeMs)
    }

    // === BOUNDARY VALUES ===

    @Test
    fun `zero confidence`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, confidence = 0f)
        assertEquals(0f, event.confidence, 0.001f)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(0f, restored.confidence, 0.001f)
    }

    @Test
    fun `max confidence 1f`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, confidence = 1.0f)
        assertEquals(1.0f, event.confidence, 0.001f)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(1.0f, restored.confidence, 0.001f)
    }

    @Test
    fun `half confidence 0_5f`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, confidence = 0.5f)
        assertEquals(0.5f, event.confidence, 0.001f)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(0.5f, restored.confidence, 0.001f)
    }

    @Test
    fun `very small confidence`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, confidence = 0.001f)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(0.001f, restored.confidence, 0.0001f)
    }

    @Test
    fun `confidence near 1`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, confidence = 0.9999f)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(0.9999f, restored.confidence, 0.0001f)
    }

    @Test
    fun `zero inference time`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, inferenceTimeMs = 0L)
        assertEquals(0L, event.inferenceTimeMs)
    }

    @Test
    fun `large inference time`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, inferenceTimeMs = Long.MAX_VALUE)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(Long.MAX_VALUE, restored.inferenceTimeMs)
    }

    @Test
    fun `inference time 1ms`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, inferenceTimeMs = 1L)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(1L, restored.inferenceTimeMs)
    }

    @Test
    fun `inference time 1000ms`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, inferenceTimeMs = 1000L)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(1000L, restored.inferenceTimeMs)
    }

    @Test
    fun `max timestamp`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, timestamp = Long.MAX_VALUE)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(Long.MAX_VALUE, restored.timestamp)
    }

    @Test
    fun `zero timestamp`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, timestamp = 0L)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(0L, restored.timestamp)
    }

    @Test
    fun `empty string source app`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, sourceApp = "")
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals("", restored.sourceApp)
    }

    @Test
    fun `very long source app name`() {
        val longName = "a".repeat(10000)
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, sourceApp = longName)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(longName, restored.sourceApp)
    }

    @Test
    fun `empty string source app name`() {
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, sourceAppName = "")
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals("", restored.sourceAppName)
    }

    @Test
    fun `very long source app name display name`() {
        val longName = "b".repeat(5000)
        val event = DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH, sourceAppName = longName)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(longName, restored.sourceAppName)
    }

    @Test
    fun `empty string id`() {
        val event = DetectionEvent(id = "", entityType = EntityType.EMAIL, severity = Severity.HIGH)
        assertEquals("", event.id)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals("", restored.id)
    }

    @Test
    fun `very long id`() {
        val longId = "x".repeat(1000)
        val event = DetectionEvent(id = longId, entityType = EntityType.EMAIL, severity = Severity.HIGH)
        val restored = DetectionEvent.fromJson(event.toJson())
        assertEquals(longId, restored.id)
    }

    // === USER ACTION ===

    @Test
    fun `every user action has a display name`() {
        UserAction.entries.forEach { action ->
            assertTrue("${action.name} should have display name", action.displayName.isNotEmpty())
        }
    }

    @Test
    fun `user action display names are unique`() {
        val names = UserAction.entries.map { it.displayName }.toSet()
        assertEquals(UserAction.entries.size, names.size)
    }

    @Test
    fun `user action CLIPBOARD_CLEARED display name`() {
        assertEquals("Clipboard Cleared", UserAction.CLIPBOARD_CLEARED.displayName)
    }

    @Test
    fun `user action DISMISSED display name`() {
        assertEquals("Dismissed", UserAction.DISMISSED.displayName)
    }

    @Test
    fun `user action WHITELISTED_APP display name`() {
        assertEquals("App Whitelisted", UserAction.WHITELISTED_APP.displayName)
    }

    @Test
    fun `user action AUTO_DISMISSED display name`() {
        assertEquals("Auto-dismissed", UserAction.AUTO_DISMISSED.displayName)
    }

    @Test
    fun `user action NO_ACTION display name`() {
        assertEquals("No Action", UserAction.NO_ACTION.displayName)
    }

    @Test
    fun `user action count is 5`() {
        assertEquals(5, UserAction.entries.size)
    }

    @Test
    fun `user action valueOf works for all actions`() {
        UserAction.entries.forEach { action ->
            assertEquals(action, UserAction.valueOf(action.name))
        }
    }

    @Test
    fun `user action ordinal values are sequential`() {
        UserAction.entries.forEachIndexed { index, action ->
            assertEquals(index, action.ordinal)
        }
    }

    // === ENTITY TYPE SEVERITY MAPPING ===

    @Test
    fun `credit card has critical severity`() {
        assertEquals(Severity.CRITICAL, EntityType.CREDIT_CARD.severity)
    }

    @Test
    fun `SSN has critical severity`() {
        assertEquals(Severity.CRITICAL, EntityType.SSN.severity)
    }

    @Test
    fun `password has critical severity`() {
        assertEquals(Severity.CRITICAL, EntityType.PASSWORD.severity)
    }

    @Test
    fun `API key has critical severity`() {
        assertEquals(Severity.CRITICAL, EntityType.API_KEY.severity)
    }

    @Test
    fun `email has high severity`() {
        assertEquals(Severity.HIGH, EntityType.EMAIL.severity)
    }

    @Test
    fun `phone has high severity`() {
        assertEquals(Severity.HIGH, EntityType.PHONE.severity)
    }

    @Test
    fun `person name has medium severity`() {
        assertEquals(Severity.MEDIUM, EntityType.PERSON_NAME.severity)
    }

    @Test
    fun `address has medium severity`() {
        assertEquals(Severity.MEDIUM, EntityType.ADDRESS.severity)
    }

    @Test
    fun `date of birth has medium severity`() {
        assertEquals(Severity.MEDIUM, EntityType.DATE_OF_BIRTH.severity)
    }

    @Test
    fun `medical ID has high severity`() {
        assertEquals(Severity.HIGH, EntityType.MEDICAL_ID.severity)
    }

    // === EVENTS IN COLLECTIONS ===

    @Test
    fun `events in set use equals correctly`() {
        val event1 = DetectionEvent(
            id = "set-test",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val event2 = DetectionEvent(
            id = "set-test",
            timestamp = 1000L,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val set = setOf(event1, event2)
        assertEquals("Equal events should deduplicate in set", 1, set.size)
    }

    @Test
    fun `events in map use hashCode correctly`() {
        val event = DetectionEvent(
            id = "map-test",
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        val map = mapOf(event to "value")
        val sameEvent = DetectionEvent(
            id = "map-test",
            timestamp = event.timestamp,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH
        )
        assertEquals("value", map[sameEvent])
    }

    @Test
    fun `events sortable by timestamp`() {
        val events = listOf(
            DetectionEvent(timestamp = 3000L, entityType = EntityType.EMAIL, severity = Severity.HIGH),
            DetectionEvent(timestamp = 1000L, entityType = EntityType.SSN, severity = Severity.CRITICAL),
            DetectionEvent(timestamp = 2000L, entityType = EntityType.PHONE, severity = Severity.HIGH)
        )
        val sorted = events.sortedBy { it.timestamp }
        assertEquals(1000L, sorted[0].timestamp)
        assertEquals(2000L, sorted[1].timestamp)
        assertEquals(3000L, sorted[2].timestamp)
    }

    @Test
    fun `events filterable by entity type`() {
        val events = listOf(
            DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH),
            DetectionEvent(entityType = EntityType.SSN, severity = Severity.CRITICAL),
            DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH),
            DetectionEvent(entityType = EntityType.PHONE, severity = Severity.HIGH)
        )
        val emails = events.filter { it.entityType == EntityType.EMAIL }
        assertEquals(2, emails.size)
    }

    @Test
    fun `events filterable by severity`() {
        val events = listOf(
            DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH),
            DetectionEvent(entityType = EntityType.SSN, severity = Severity.CRITICAL),
            DetectionEvent(entityType = EntityType.PASSWORD, severity = Severity.CRITICAL),
            DetectionEvent(entityType = EntityType.PERSON_NAME, severity = Severity.MEDIUM)
        )
        val critical = events.filter { it.severity == Severity.CRITICAL }
        assertEquals(2, critical.size)
    }

    @Test
    fun `events groupable by entity type`() {
        val events = listOf(
            DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH),
            DetectionEvent(entityType = EntityType.EMAIL, severity = Severity.HIGH),
            DetectionEvent(entityType = EntityType.SSN, severity = Severity.CRITICAL),
            DetectionEvent(entityType = EntityType.PHONE, severity = Severity.HIGH)
        )
        val grouped = events.groupBy { it.entityType }
        assertEquals(3, grouped.size)
        assertEquals(2, grouped[EntityType.EMAIL]?.size)
        assertEquals(1, grouped[EntityType.SSN]?.size)
        assertEquals(1, grouped[EntityType.PHONE]?.size)
    }

    // === PERFORMANCE ===

    @Test
    fun `create 10000 events without timeout`() {
        val start = System.nanoTime()
        val events = (1..10000).map {
            DetectionEvent(
                entityType = EntityType.entries[it % EntityType.entries.size],
                severity = Severity.entries[it % Severity.entries.size],
                confidence = (it % 100) / 100f
            )
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertEquals(10000, events.size)
        assertTrue("10000 event creations should be fast, took ${elapsedMs}ms", elapsedMs < 5000)
    }

    @Test
    fun `serialize and deserialize 1000 events without timeout`() {
        val events = (1..1000).map {
            DetectionEvent(
                entityType = EntityType.entries[it % EntityType.entries.size],
                severity = Severity.entries[it % Severity.entries.size],
                sourceApp = "com.test.app$it",
                confidence = (it % 100) / 100f,
                inferenceTimeMs = it.toLong()
            )
        }
        val start = System.nanoTime()
        events.forEach { event ->
            val json = event.toJson()
            val restored = DetectionEvent.fromJson(json)
            assertEquals(event.entityType, restored.entityType)
        }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        assertTrue("1000 round trips should be fast, took ${elapsedMs}ms", elapsedMs < 5000)
    }
}
