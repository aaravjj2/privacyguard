package com.privacyguard.data

import android.content.SharedPreferences
import com.privacyguard.ml.EntityType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Encrypted local repository for PII detection event history.
 * Uses EncryptedSharedPreferences for AES-256-GCM encrypted storage.
 *
 * IMPORTANT: Never stores raw PII text. Only metadata about detections.
 */
class EncryptedLogRepository(private val prefs: SharedPreferences) {

    companion object {
        const val MAX_EVENTS = 1000
        private const val EVENT_KEY_PREFIX = "event_"
        private const val EVENT_COUNT_KEY = "event_count"
        private const val EVENT_IDS_KEY = "event_ids"
    }

    private val _events = MutableStateFlow<List<DetectionEvent>>(emptyList())

    init {
        refreshEvents()
    }

    /**
     * Reactive stream of all detection events.
     */
    fun getAllEvents(): Flow<List<DetectionEvent>> = _events.asStateFlow()

    /**
     * Record a new detection event.
     */
    fun record(event: DetectionEvent) {
        val json = event.toJson()
        val editor = prefs.edit()

        // Get current event IDs
        val ids = getEventIds().toMutableList()

        // LRU eviction: remove oldest if at capacity
        while (ids.size >= MAX_EVENTS) {
            val oldestId = ids.removeAt(0)
            editor.remove("$EVENT_KEY_PREFIX$oldestId")
        }

        // Add new event
        ids.add(event.id)
        editor.putString("$EVENT_KEY_PREFIX${event.id}", json)
        editor.putString(EVENT_IDS_KEY, ids.joinToString(","))
        editor.putInt(EVENT_COUNT_KEY, ids.size)
        editor.apply()

        refreshEvents()
    }

    /**
     * Delete a specific event by ID.
     */
    fun deleteEvent(id: String) {
        val ids = getEventIds().toMutableList()
        ids.remove(id)

        prefs.edit()
            .remove("$EVENT_KEY_PREFIX$id")
            .putString(EVENT_IDS_KEY, ids.joinToString(","))
            .putInt(EVENT_COUNT_KEY, ids.size)
            .apply()

        refreshEvents()
    }

    /**
     * Delete all detection events.
     */
    fun deleteAllEvents() {
        val ids = getEventIds()
        val editor = prefs.edit()
        ids.forEach { id ->
            editor.remove("$EVENT_KEY_PREFIX$id")
        }
        editor.remove(EVENT_IDS_KEY)
        editor.putInt(EVENT_COUNT_KEY, 0)
        editor.apply()

        refreshEvents()
    }

    /**
     * Get the N most recent detection events.
     */
    fun getRecentEvents(count: Int): List<DetectionEvent> {
        val ids = getEventIds()
        return ids.takeLast(count).reversed().mapNotNull { id ->
            val json = prefs.getString("$EVENT_KEY_PREFIX$id", null)
            json?.let { DetectionEvent.fromJson(it) }
        }
    }

    /**
     * Get count of events grouped by entity type.
     */
    fun getEventCountByType(): Map<EntityType, Int> {
        return getAllEventsSync().groupBy { it.entityType }.mapValues { it.value.size }
    }

    /**
     * Get total event count.
     */
    fun getEventCount(): Int = prefs.getInt(EVENT_COUNT_KEY, 0)

    /**
     * Get events for today.
     */
    fun getTodayEventCount(): Int {
        val startOfDay = getStartOfDay()
        return getAllEventsSync().count { it.timestamp >= startOfDay }
    }

    /**
     * Get events for this week.
     */
    fun getThisWeekEventCount(): Int {
        val startOfWeek = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        return getAllEventsSync().count { it.timestamp >= startOfWeek }
    }

    private fun getEventIds(): List<String> {
        val idsStr = prefs.getString(EVENT_IDS_KEY, "") ?: ""
        return if (idsStr.isBlank()) emptyList() else idsStr.split(",")
    }

    private fun getAllEventsSync(): List<DetectionEvent> {
        return getEventIds().mapNotNull { id ->
            val json = prefs.getString("$EVENT_KEY_PREFIX$id", null)
            json?.let {
                try { DetectionEvent.fromJson(it) } catch (_: Exception) { null }
            }
        }
    }

    private fun refreshEvents() {
        _events.value = getAllEventsSync().sortedByDescending { it.timestamp }
    }

    private fun getStartOfDay(): Long {
        val now = System.currentTimeMillis()
        return now - (now % (24 * 60 * 60 * 1000L))
    }
}
