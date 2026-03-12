package com.privacyguard.data

import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.UUID

/**
 * Actions a user can take when alerted about PII detection.
 */
enum class UserAction(val displayName: String) {
    CLIPBOARD_CLEARED("Clipboard Cleared"),
    DISMISSED("Dismissed"),
    WHITELISTED_APP("App Whitelisted"),
    AUTO_DISMISSED("Auto-dismissed"),
    NO_ACTION("No Action")
}

/**
 * Represents a single PII detection event for persistence.
 *
 * IMPORTANT: This class deliberately does NOT contain the raw detected text.
 * Only metadata about the detection is stored.
 */
data class DetectionEvent(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val entityType: EntityType,
    val severity: Severity,
    val sourceApp: String? = null,
    val sourceAppName: String? = null,
    val actionTaken: UserAction = UserAction.NO_ACTION,
    val confidence: Float = 0f,
    val inferenceTimeMs: Long = 0L
) {
    fun toJson(): String = gson.toJson(this)

    companion object {
        private val gson: Gson = GsonBuilder().create()

        fun fromJson(json: String): DetectionEvent = gson.fromJson(json, DetectionEvent::class.java)
    }
}
