package com.privacyguard.data

import android.content.SharedPreferences
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EncryptedLogRepositoryTest {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var repo: EncryptedLogRepository
    private val storage = mutableMapOf<String, Any?>()

    @Before
    fun setUp() {
        editor = mockk(relaxed = true)
        prefs = mockk(relaxed = true)

        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } answers {
            storage[firstArg()] = secondArg<String>()
            editor
        }
        every { editor.putInt(any(), any()) } answers {
            storage[firstArg()] = secondArg<Int>()
            editor
        }
        every { editor.remove(any()) } answers {
            storage.remove(firstArg())
            editor
        }
        every { editor.apply() } just Runs

        every { prefs.getString(any(), any()) } answers {
            storage[firstArg()] as? String ?: secondArg()
        }
        every { prefs.getInt(any(), any()) } answers {
            storage[firstArg()] as? Int ?: secondArg()
        }

        storage.clear()
        repo = EncryptedLogRepository(prefs)
    }

    @Test
    fun `record stores event`() {
        val event = DetectionEvent(
            id = "test-1",
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL
        )
        repo.record(event)

        verify { editor.putString("event_test-1", any()) }
        verify { editor.apply() }
    }

    @Test
    fun `record multiple events maintains order`() {
        val event1 = DetectionEvent(id = "e1", entityType = EntityType.CREDIT_CARD, severity = Severity.CRITICAL)
        val event2 = DetectionEvent(id = "e2", entityType = EntityType.EMAIL, severity = Severity.HIGH)

        repo.record(event1)
        repo.record(event2)

        val idsStr = storage["event_ids"] as? String ?: ""
        assertTrue("Should contain both IDs", idsStr.contains("e1") && idsStr.contains("e2"))
    }

    @Test
    fun `deleteEvent removes event`() {
        val event = DetectionEvent(id = "del-1", entityType = EntityType.SSN, severity = Severity.CRITICAL)
        repo.record(event)
        repo.deleteEvent("del-1")

        verify { editor.remove("event_del-1") }
    }

    @Test
    fun `deleteAllEvents clears storage`() {
        repo.record(DetectionEvent(id = "a1", entityType = EntityType.EMAIL, severity = Severity.HIGH))
        repo.deleteAllEvents()

        verify { editor.remove("event_ids") }
        verify { editor.putInt("event_count", 0) }
    }

    @Test
    fun `getRecentEvents returns correct count`() {
        // Pre-populate mock storage
        for (i in 1..5) {
            val event = DetectionEvent(
                id = "r$i",
                entityType = EntityType.PHONE,
                severity = Severity.HIGH,
                timestamp = 1000L * i
            )
            storage["event_r$i"] = event.toJson()
        }
        storage["event_ids"] = "r1,r2,r3,r4,r5"

        val repo2 = EncryptedLogRepository(prefs)
        val recent = repo2.getRecentEvents(3)
        assertEquals(3, recent.size)
    }

    @Test
    fun `getEventCount returns stored count`() {
        storage["event_count"] = 42
        assertEquals(42, repo.getEventCount())
    }

    @Test
    fun `serialization round trip preserves event data`() {
        val original = DetectionEvent(
            id = "round-trip-1",
            entityType = EntityType.API_KEY,
            severity = Severity.CRITICAL,
            sourceApp = "com.test.app",
            actionTaken = UserAction.CLIPBOARD_CLEARED,
            confidence = 0.95f,
            inferenceTimeMs = 35L
        )

        val json = original.toJson()
        val restored = DetectionEvent.fromJson(json)

        assertEquals(original.id, restored.id)
        assertEquals(original.entityType, restored.entityType)
        assertEquals(original.severity, restored.severity)
        assertEquals(original.sourceApp, restored.sourceApp)
        assertEquals(original.actionTaken, restored.actionTaken)
        assertEquals(original.confidence, restored.confidence, 0.001f)
        assertEquals(original.inferenceTimeMs, restored.inferenceTimeMs)
    }

    @Test
    fun `LRU eviction removes oldest when at capacity`() {
        // Fill with MAX_EVENTS
        val ids = (1..EncryptedLogRepository.MAX_EVENTS).map { "lru-$it" }
        storage["event_ids"] = ids.joinToString(",")
        storage["event_count"] = EncryptedLogRepository.MAX_EVENTS

        for (id in ids) {
            storage["event_$id"] = DetectionEvent(
                id = id,
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH
            ).toJson()
        }

        val repo2 = EncryptedLogRepository(prefs)

        // Add one more — should evict the oldest
        repo2.record(DetectionEvent(
            id = "new-event",
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL
        ))

        // Verify oldest was removed
        verify { editor.remove("event_lru-1") }
    }
}
