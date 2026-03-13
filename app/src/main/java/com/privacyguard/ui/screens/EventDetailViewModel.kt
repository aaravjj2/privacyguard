package com.privacyguard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.UserAction
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ---------------------------------------------------------------------------
// Supporting enums and data classes for EventDetailViewModel
// ---------------------------------------------------------------------------

/**
 * Represents the review status of a detection event from the user's
 * perspective. Events start as UNREVIEWED and transition to REVIEWED
 * once the user has acknowledged and assessed them.
 */
enum class ReviewStatus(val displayLabel: String, val description: String) {
    UNREVIEWED(
        displayLabel = "Unreviewed",
        description = "This event has not yet been reviewed by the user."
    ),
    REVIEWED(
        displayLabel = "Reviewed",
        description = "The user has reviewed and acknowledged this event."
    ),
    MARKED_FALSE_POSITIVE(
        displayLabel = "False Positive",
        description = "The user has flagged this detection as a false positive."
    ),
    RESOLVED(
        displayLabel = "Resolved",
        description = "The user has taken action and considers this event resolved."
    )
}

/**
 * Supported export formats for a single detection event.
 */
enum class EventExportFormat(val displayName: String, val mimeType: String, val fileExtension: String) {
    JSON(
        displayName = "JSON",
        mimeType = "application/json",
        fileExtension = "json"
    ),
    CSV(
        displayName = "CSV",
        mimeType = "text/csv",
        fileExtension = "csv"
    ),
    PLAIN_TEXT(
        displayName = "Plain Text",
        mimeType = "text/plain",
        fileExtension = "txt"
    )
}

/**
 * Sealed class representing the result of an asynchronous export operation.
 */
sealed class ExportOperationResult {
    /** Export completed successfully with the formatted content. */
    data class Success(
        val format: EventExportFormat,
        val content: String,
        val filename: String
    ) : ExportOperationResult()

    /** Export failed with a descriptive error message. */
    data class Failure(
        val format: EventExportFormat,
        val errorMessage: String,
        val cause: Throwable? = null
    ) : ExportOperationResult()

    /** Export is currently in progress. */
    data class InProgress(val format: EventExportFormat) : ExportOperationResult()
}

/**
 * Represents a single step in the detection event lifecycle timeline.
 * The timeline tracks everything from initial detection through user action.
 */
data class EventTimelineStep(
    val stepType: EventTimelineStepType,
    val title: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean,
    val isActive: Boolean = false,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Classification of timeline step types used for icon resolution.
 */
enum class EventTimelineStepType {
    DETECTED,
    MODEL_ANALYZED,
    ALERT_RAISED,
    USER_NOTIFIED,
    USER_ACTION_TAKEN,
    RESOLVED
}

/**
 * Rich display metadata for the source application that triggered the event.
 * Includes app-level statistics computed from all events for that package.
 */
data class EventSourceAppInfo(
    val packageName: String,
    val displayName: String,
    val isCurrentlyWhitelisted: Boolean,
    val totalEventCount: Int,
    val recentEventCount: Int,
    val firstSeenTimestamp: Long,
    val lastSeenTimestamp: Long,
    val riskLevel: EventRiskLevel,
    val topEntityTypes: List<EntityType>
)

/**
 * Qualitative risk level for an event or app.
 */
enum class EventRiskLevel(val displayName: String, val numericScore: Int) {
    LOW("Low Risk", 25),
    MEDIUM("Medium Risk", 50),
    HIGH("High Risk", 75),
    CRITICAL("Critical Risk", 100)
}

/**
 * A textual recommendation card to guide the user on how to respond
 * to a specific detection event.
 */
data class ActionRecommendation(
    val title: String,
    val description: String,
    val priority: Int,
    val isUrgent: Boolean = false
)

/**
 * Complete UI state for the Event Detail screen.
 * All fields are immutable to support efficient Compose recomposition.
 */
data class EventDetailViewModelUiState(
    // Core event data
    val event: DetectionEvent? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Review and moderation state
    val reviewStatus: ReviewStatus = ReviewStatus.UNREVIEWED,
    val isWhitelisted: Boolean = false,
    val isDeleted: Boolean = false,

    // Export state
    val exportResult: ExportOperationResult? = null,
    val isExporting: Boolean = false,

    // Copy state
    val redactedSummary: String = "",
    val isCopied: Boolean = false,
    val copyMessage: String? = null,

    // Related events
    val relatedEvents: List<DetectionEvent> = emptyList(),
    val relatedEventsLoading: Boolean = false,

    // Source app info
    val sourceAppInfo: EventSourceAppInfo? = null,

    // Action recommendations
    val recommendations: List<ActionRecommendation> = emptyList(),

    // Dialog visibility
    val showDeleteConfirmation: Boolean = false,
    val showWhitelistConfirmation: Boolean = false,
    val showExportDialog: Boolean = false,
    val showFalsePositiveDialog: Boolean = false,
    val showWhitelistRemovalConfirmation: Boolean = false,

    // Timeline
    val timeline: List<EventTimelineStep> = emptyList(),

    // Risk metadata
    val riskScore: Int = 0,
    val riskDescription: String = "",
    val entityTypeDescription: String = "",

    // Action in progress
    val actionInProgress: Boolean = false,
    val actionMessage: String? = null,

    // Section expand/collapse state
    val isExpanded: Map<String, Boolean> = emptyMap()
)

// ---------------------------------------------------------------------------
// EventDetailViewModel
// ---------------------------------------------------------------------------

/**
 * ViewModel for the Event Detail screen, managing the full lifecycle
 * of a single PII detection event.
 *
 * Responsibilities:
 * - Loading the target event from the repository by ID
 * - Computing derived display data: risk score, timeline, recommendations
 * - Supporting CRUD-like operations: delete, whitelist, mark-reviewed
 * - Exporting the event in JSON, CSV, and plain-text formats
 * - Querying related events (same entity type or same source app)
 * - Generating a redacted summary safe for clipboard copying
 *
 * All repository interactions are dispatched on the viewModelScope's default
 * dispatcher. UI state is exposed as a single [StateFlow<EventDetailViewModelUiState>].
 */
class EventDetailViewModel : ViewModel() {

    companion object {
        /** Maximum number of related events to surface in the UI. */
        private const val MAX_RELATED_EVENTS = 10

        /** Milliseconds in a 24-hour window. */
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

        /** Milliseconds in a 7-day window. */
        private const val MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY

        /** Milliseconds in a 30-day window. */
        private const val MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY

        /** How long the "copied" indicator stays visible (ms). */
        private const val COPY_FEEDBACK_DURATION_MS = 2_000L

        /** How long an export-success indicator stays visible (ms). */
        private const val EXPORT_FEEDBACK_DURATION_MS = 3_000L

        /** Threshold for "high-frequency" source apps: detections per week. */
        private const val HIGH_FREQUENCY_APP_THRESHOLD = 10

        /** Date format for human-readable timestamps. */
        private const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy 'at' HH:mm:ss"

        /** Date format used in export filenames. */
        private const val FILENAME_DATE_FORMAT = "yyyyMMdd_HHmmss"

        /** ISO-8601 format for JSON exports. */
        private const val ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }

    // ---------------------------------------------------------------------------
    // State
    // ---------------------------------------------------------------------------

    private val _uiState = MutableStateFlow(EventDetailViewModelUiState())
    val uiState: StateFlow<EventDetailViewModelUiState> = _uiState.asStateFlow()

    private var logRepository: EncryptedLogRepository? = null
    private var currentEventId: String? = null

    /** In-memory whitelist of package names that the user has approved. */
    private val inMemoryWhitelist = mutableSetOf<String>()

    /** In-memory set of event IDs the user has marked as reviewed. */
    private val reviewedEventIds = mutableSetOf<String>()

    /** In-memory set of event IDs the user has deleted. */
    private val deletedEventIds = mutableSetOf<String>()

    /** Job used to auto-clear the copy feedback after a delay. */
    private var copyFeedbackJob: Job? = null

    /** Job used to auto-clear the export feedback after a delay. */
    private var exportFeedbackJob: Job? = null

    // ---------------------------------------------------------------------------
    // Repository Binding
    // ---------------------------------------------------------------------------

    /**
     * Binds an [EncryptedLogRepository] to this ViewModel.
     * Must be called before [loadEvent] to ensure data is available.
     *
     * @param repo The repository that provides detection events.
     */
    fun setRepository(repo: EncryptedLogRepository) {
        if (logRepository == repo) return
        logRepository = repo
    }

    // ---------------------------------------------------------------------------
    // Event Loading
    // ---------------------------------------------------------------------------

    /**
     * Loads the detection event with the given [eventId] from the repository.
     *
     * On success, the [EventDetailViewModelUiState.event] field is populated and
     * all derived fields (timeline, recommendations, risk score, source app info,
     * related events) are computed asynchronously.
     *
     * On failure, [EventDetailViewModelUiState.errorMessage] is populated.
     *
     * @param eventId The UUID string of the event to load.
     */
    fun loadEvent(eventId: String) {
        currentEventId = eventId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val repo = logRepository
                if (repo == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Repository not initialized. Please restart the app."
                        )
                    }
                    return@launch
                }

                // Retrieve the event by filtering all events
                val allEvents = repo.getAllEvents().first()
                val event = allEvents.find { it.id == eventId }

                if (event == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Detection event not found. It may have been deleted."
                        )
                    }
                    return@launch
                }

                // Compute all derived display data
                val riskScore = computeEventRiskScore(event)
                val riskDescription = getRiskDescription(event.severity)
                val entityDescription = getEntityTypeDescription(event.entityType)
                val timeline = buildEventTimeline(event)
                val recommendations = getRecommendedActions(event.entityType, event.severity)
                val redactedSummary = generateRedactedSummary(event)
                val isWhitelisted = event.sourceApp?.let { inMemoryWhitelist.contains(it) } ?: false
                val reviewStatus = when {
                    event.id in deletedEventIds -> ReviewStatus.RESOLVED
                    event.id in reviewedEventIds -> ReviewStatus.REVIEWED
                    event.actionTaken == UserAction.DISMISSED -> ReviewStatus.REVIEWED
                    event.actionTaken == UserAction.CLIPBOARD_CLEARED -> ReviewStatus.RESOLVED
                    event.actionTaken == UserAction.WHITELISTED_APP -> ReviewStatus.RESOLVED
                    else -> ReviewStatus.UNREVIEWED
                }

                _uiState.update { current ->
                    current.copy(
                        event = event,
                        isLoading = false,
                        errorMessage = null,
                        riskScore = riskScore,
                        riskDescription = riskDescription,
                        entityTypeDescription = entityDescription,
                        timeline = timeline,
                        recommendations = recommendations,
                        redactedSummary = redactedSummary,
                        isWhitelisted = isWhitelisted,
                        reviewStatus = reviewStatus
                    )
                }

                // Load supplementary data asynchronously
                loadSourceAppInfo(event, allEvents)
                loadRelatedEvents(event, allEvents)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load event: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Retries loading the event after an error.
     * Clears the current error state before attempting the reload.
     */
    fun retryLoad() {
        val eventId = currentEventId ?: return
        _uiState.update { it.copy(errorMessage = null) }
        loadEvent(eventId)
    }

    // ---------------------------------------------------------------------------
    // Delete Event
    // ---------------------------------------------------------------------------

    /**
     * Requests confirmation before deleting the current event.
     * Sets [EventDetailViewModelUiState.showDeleteConfirmation] to true.
     */
    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    /**
     * Dismisses the delete confirmation dialog without deleting.
     */
    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Permanently removes the current event from the log.
     *
     * The delete is performed immediately in-memory, and the state is updated
     * to reflect the deletion. In a full implementation the repository would
     * persist the change. The caller should navigate away after deletion.
     */
    fun deleteEvent() {
        val event = _uiState.value.event ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDeleteConfirmation = false,
                    actionInProgress = true,
                    actionMessage = "Deleting event..."
                )
            }

            try {
                // Record deletion in the in-memory set
                deletedEventIds.add(event.id)

                // Small delay to allow UI feedback
                delay(300)

                _uiState.update {
                    it.copy(
                        isDeleted = true,
                        actionInProgress = false,
                        actionMessage = null,
                        reviewStatus = ReviewStatus.RESOLVED
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        actionInProgress = false,
                        actionMessage = null,
                        errorMessage = "Failed to delete event: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Export Event
    // ---------------------------------------------------------------------------

    /**
     * Opens the export format selection dialog.
     */
    fun showExportDialog() {
        _uiState.update { it.copy(showExportDialog = true) }
    }

    /**
     * Dismisses the export dialog without exporting.
     */
    fun dismissExportDialog() {
        _uiState.update { it.copy(showExportDialog = false) }
    }

    /**
     * Exports the current event in the specified [format].
     *
     * The generated content is placed in [EventDetailViewModelUiState.exportResult]
     * as an [ExportOperationResult.Success]. If the export fails, the result is
     * [ExportOperationResult.Failure].
     *
     * @param format The output format to use for the export.
     */
    fun exportEvent(format: EventExportFormat) {
        val event = _uiState.value.event ?: run {
            _uiState.update {
                it.copy(
                    exportResult = ExportOperationResult.Failure(
                        format = format,
                        errorMessage = "No event loaded to export."
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isExporting = true,
                    showExportDialog = false,
                    exportResult = ExportOperationResult.InProgress(format)
                )
            }

            try {
                delay(200) // Simulate brief processing time for user feedback

                val content = when (format) {
                    EventExportFormat.JSON -> exportAsJson(event)
                    EventExportFormat.CSV -> exportAsCsv(event)
                    EventExportFormat.PLAIN_TEXT -> exportAsText(event)
                }

                val timestamp = SimpleDateFormat(FILENAME_DATE_FORMAT, Locale.US)
                    .format(Date(event.timestamp))
                val filename = "privacyguard_event_${event.entityType.name.lowercase()}_$timestamp.${format.fileExtension}"

                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportResult = ExportOperationResult.Success(
                            format = format,
                            content = content,
                            filename = filename
                        )
                    )
                }

                // Auto-clear export feedback after a delay
                exportFeedbackJob?.cancel()
                exportFeedbackJob = viewModelScope.launch {
                    delay(EXPORT_FEEDBACK_DURATION_MS)
                    clearExportStatus()
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportResult = ExportOperationResult.Failure(
                            format = format,
                            errorMessage = "Export failed: ${e.localizedMessage}",
                            cause = e
                        )
                    )
                }
            }
        }
    }

    /**
     * Serializes the detection event as a JSON string.
     *
     * The JSON output includes all event metadata: entity type, severity,
     * source app, confidence, inference time, timestamp (ISO-8601),
     * user action taken, and a generated risk score.
     *
     * @param event The event to serialize.
     * @return A pretty-printed JSON string.
     */
    private fun exportAsJson(event: DetectionEvent): String {
        val isoFormatter = SimpleDateFormat(ISO_DATE_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val displayFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())

        return buildString {
            appendLine("{")
            appendLine("  \"schemaVersion\": \"1.0\",")
            appendLine("  \"exportedAt\": \"${isoFormatter.format(Date())}\",")
            appendLine("  \"event\": {")
            appendLine("    \"id\": \"${event.id}\",")
            appendLine("    \"timestamp\": ${event.timestamp},")
            appendLine("    \"timestampHuman\": \"${displayFormatter.format(Date(event.timestamp))}\",")
            appendLine("    \"entityType\": {")
            appendLine("      \"name\": \"${event.entityType.name}\",")
            appendLine("      \"displayName\": \"${event.entityType.displayName}\",")
            appendLine("      \"labelIndex\": ${event.entityType.labelIndex}")
            appendLine("    },")
            appendLine("    \"severity\": {")
            appendLine("      \"level\": \"${event.severity.name}\",")
            appendLine("      \"numericScore\": ${severityToNumericScore(event.severity)}")
            appendLine("    },")
            appendLine("    \"confidence\": ${String.format("%.4f", event.confidence)},")
            appendLine("    \"confidencePercent\": ${String.format("%.1f", event.confidence * 100)},")
            appendLine("    \"inferenceTimeMs\": ${event.inferenceTimeMs},")
            appendLine("    \"actionTaken\": \"${event.actionTaken.name}\",")
            appendLine("    \"actionTakenDisplay\": \"${event.actionTaken.displayName}\",")
            if (event.sourceApp != null) {
                appendLine("    \"sourceApp\": \"${event.sourceApp}\",")
            }
            if (event.sourceAppName != null) {
                appendLine("    \"sourceAppName\": \"${event.sourceAppName}\",")
            }
            appendLine("    \"computedRiskScore\": ${computeEventRiskScore(event)},")
            appendLine("    \"isHighRisk\": ${isHighRiskEvent(event)}")
            appendLine("  }")
            append("}")
        }
    }

    /**
     * Serializes the detection event as a CSV string with a header row.
     *
     * The CSV includes all relevant fields and is formatted for import into
     * spreadsheet applications and data analysis tools.
     *
     * @param event The event to serialize.
     * @return A CSV-formatted string with header and one data row.
     */
    private fun exportAsCsv(event: DetectionEvent): String {
        val isoFormatter = SimpleDateFormat(ISO_DATE_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val displayFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())

        val header = listOf(
            "id", "timestamp_epoch", "timestamp_human", "timestamp_iso",
            "entity_type", "entity_type_display", "severity", "severity_score",
            "confidence", "confidence_percent", "inference_time_ms",
            "action_taken", "action_taken_display", "source_app_package",
            "source_app_name", "computed_risk_score", "is_high_risk"
        )

        val row = listOf(
            escapeCsvField(event.id),
            event.timestamp.toString(),
            escapeCsvField(displayFormatter.format(Date(event.timestamp))),
            escapeCsvField(isoFormatter.format(Date(event.timestamp))),
            escapeCsvField(event.entityType.name),
            escapeCsvField(event.entityType.displayName),
            escapeCsvField(event.severity.name),
            severityToNumericScore(event.severity).toString(),
            String.format("%.6f", event.confidence),
            String.format("%.2f", event.confidence * 100),
            event.inferenceTimeMs.toString(),
            escapeCsvField(event.actionTaken.name),
            escapeCsvField(event.actionTaken.displayName),
            escapeCsvField(event.sourceApp ?: ""),
            escapeCsvField(event.sourceAppName ?: ""),
            computeEventRiskScore(event).toString(),
            isHighRiskEvent(event).toString()
        )

        return buildString {
            appendLine(header.joinToString(","))
            append(row.joinToString(","))
        }
    }

    /**
     * Generates a human-readable plain-text representation of the event.
     *
     * This format is intended for sharing via email, messaging, or printing.
     * It omits raw PII and presents only metadata in an accessible layout.
     *
     * @param event The event to format.
     * @return A multi-line plain-text string.
     */
    private fun exportAsText(event: DetectionEvent): String {
        val displayFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
        val separator = "─".repeat(60)

        return buildString {
            appendLine("PrivacyGuard Detection Event Report")
            appendLine(separator)
            appendLine()
            appendLine("EVENT SUMMARY")
            appendLine("  Event ID:       ${event.id}")
            appendLine("  Detected At:    ${displayFormatter.format(Date(event.timestamp))}")
            appendLine()
            appendLine("DETECTION DETAILS")
            appendLine("  PII Type:       ${event.entityType.displayName}")
            appendLine("  Severity:       ${event.severity.name}")
            appendLine("  Confidence:     ${String.format("%.1f", event.confidence * 100)}%")
            appendLine("  Risk Score:     ${computeEventRiskScore(event)}/100")
            appendLine("  Inference Time: ${formatInferenceTime(event.inferenceTimeMs)}")
            appendLine()
            appendLine("SOURCE INFORMATION")
            if (event.sourceApp != null || event.sourceAppName != null) {
                appendLine("  App Name:       ${event.sourceAppName ?: "Unknown"}")
                appendLine("  Package:        ${event.sourceApp ?: "Unknown"}")
            } else {
                appendLine("  App Name:       Unknown")
                appendLine("  Package:        Unknown")
            }
            appendLine()
            appendLine("USER RESPONSE")
            appendLine("  Action Taken:   ${event.actionTaken.displayName}")
            appendLine()
            appendLine("RISK DESCRIPTION")
            val description = getRiskDescription(event.severity)
            description.chunked(60).forEach { line ->
                appendLine("  $line")
            }
            appendLine()
            appendLine("RECOMMENDATIONS")
            getRecommendedActions(event.entityType, event.severity).forEachIndexed { idx, rec ->
                appendLine("  ${idx + 1}. ${rec.title}")
                rec.description.chunked(55).forEach { line ->
                    appendLine("     $line")
                }
            }
            appendLine()
            appendLine(separator)
            appendLine("Generated by PrivacyGuard on ${displayFormatter.format(Date())}")
            appendLine("This report contains no raw PII data.")
        }
    }

    // ---------------------------------------------------------------------------
    // Whitelist Management
    // ---------------------------------------------------------------------------

    /**
     * Shows the confirmation dialog before whitelisting the source app.
     */
    fun showWhitelistConfirmation() {
        val event = _uiState.value.event ?: return
        if (event.sourceApp == null) return
        _uiState.update { it.copy(showWhitelistConfirmation = true) }
    }

    /**
     * Dismisses the whitelist confirmation dialog without making changes.
     */
    fun dismissWhitelistConfirmation() {
        _uiState.update { it.copy(showWhitelistConfirmation = false) }
    }

    /**
     * Shows the dialog confirming removal from the whitelist.
     */
    fun showWhitelistRemovalConfirmation() {
        _uiState.update { it.copy(showWhitelistRemovalConfirmation = true) }
    }

    /**
     * Dismisses the whitelist removal dialog without making changes.
     */
    fun dismissWhitelistRemovalConfirmation() {
        _uiState.update { it.copy(showWhitelistRemovalConfirmation = false) }
    }

    /**
     * Adds the source application of the current event to the whitelist.
     *
     * Once whitelisted, detections from this app will be treated with lower
     * priority. The whitelist is maintained in-memory for this session; a
     * production implementation would persist it via a preferences repository.
     */
    fun addToWhitelist() {
        val event = _uiState.value.event ?: return
        val packageName = event.sourceApp ?: run {
            _uiState.update {
                it.copy(
                    showWhitelistConfirmation = false,
                    actionMessage = "Cannot whitelist: source app unknown."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showWhitelistConfirmation = false,
                    actionInProgress = true,
                    actionMessage = "Adding ${event.sourceAppName ?: packageName} to whitelist..."
                )
            }

            try {
                delay(400) // Simulate persistence operation
                inMemoryWhitelist.add(packageName)

                // Update source app info to reflect whitelisted status
                val updatedSourceInfo = _uiState.value.sourceAppInfo?.copy(
                    isCurrentlyWhitelisted = true
                )

                _uiState.update {
                    it.copy(
                        isWhitelisted = true,
                        actionInProgress = false,
                        sourceAppInfo = updatedSourceInfo,
                        actionMessage = "${event.sourceAppName ?: packageName} added to whitelist."
                    )
                }

                // Auto-clear the success message
                delay(3_000)
                _uiState.update { it.copy(actionMessage = null) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        actionInProgress = false,
                        actionMessage = "Failed to add to whitelist: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Removes the source application of the current event from the whitelist.
     */
    fun removeFromWhitelist() {
        val event = _uiState.value.event ?: return
        val packageName = event.sourceApp ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showWhitelistRemovalConfirmation = false,
                    actionInProgress = true,
                    actionMessage = "Removing from whitelist..."
                )
            }

            try {
                delay(300)
                inMemoryWhitelist.remove(packageName)

                val updatedSourceInfo = _uiState.value.sourceAppInfo?.copy(
                    isCurrentlyWhitelisted = false
                )

                _uiState.update {
                    it.copy(
                        isWhitelisted = false,
                        actionInProgress = false,
                        actionMessage = "${event.sourceAppName ?: packageName} removed from whitelist.",
                        sourceAppInfo = updatedSourceInfo
                    )
                }

                delay(3_000)
                _uiState.update { it.copy(actionMessage = null) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        actionInProgress = false,
                        actionMessage = "Failed to remove from whitelist: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Compatibility alias - whitelists the source app. Used by EventDetailScreen.
     */
    fun whitelistSource() = addToWhitelist()

    // ---------------------------------------------------------------------------
    // Review Management
    // ---------------------------------------------------------------------------

    /**
     * Marks the current event as reviewed by the user.
     *
     * A reviewed event has been acknowledged but not necessarily acted upon.
     * This is distinct from [ReviewStatus.RESOLVED] which implies a corrective
     * action was taken.
     */
    fun markAsReviewed() {
        val event = _uiState.value.event ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    actionInProgress = true,
                    actionMessage = "Marking as reviewed..."
                )
            }

            try {
                delay(200)
                reviewedEventIds.add(event.id)

                _uiState.update {
                    it.copy(
                        reviewStatus = ReviewStatus.REVIEWED,
                        actionInProgress = false,
                        actionMessage = "Event marked as reviewed."
                    )
                }

                delay(2_000)
                _uiState.update { it.copy(actionMessage = null) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        actionInProgress = false,
                        actionMessage = "Failed to mark as reviewed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Reverts the current event's review status back to [ReviewStatus.UNREVIEWED].
     */
    fun markAsUnreviewed() {
        val event = _uiState.value.event ?: return

        viewModelScope.launch {
            reviewedEventIds.remove(event.id)
            _uiState.update {
                it.copy(
                    reviewStatus = ReviewStatus.UNREVIEWED,
                    actionMessage = "Review status cleared."
                )
            }
            delay(2_000)
            _uiState.update { it.copy(actionMessage = null) }
        }
    }

    /**
     * Shows the false positive reporting dialog.
     */
    fun showFalsePositiveDialog() {
        _uiState.update { it.copy(showFalsePositiveDialog = true) }
    }

    /**
     * Dismisses the false positive dialog without submitting.
     */
    fun dismissFalsePositiveDialog() {
        _uiState.update { it.copy(showFalsePositiveDialog = false) }
    }

    /**
     * Records the current event as a false positive with the given reason.
     *
     * @param reason The user-supplied reason for marking this as a false positive.
     */
    fun reportFalsePositive(reason: String) {
        val event = _uiState.value.event ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showFalsePositiveDialog = false,
                    actionInProgress = true,
                    actionMessage = "Recording false positive..."
                )
            }

            try {
                delay(300)

                _uiState.update {
                    it.copy(
                        reviewStatus = ReviewStatus.MARKED_FALSE_POSITIVE,
                        actionInProgress = false,
                        actionMessage = "Thank you. This detection has been flagged as a false positive."
                    )
                }

                delay(3_000)
                _uiState.update { it.copy(actionMessage = null) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        actionInProgress = false,
                        actionMessage = "Failed to submit false positive report: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Related Events Loading
    // ---------------------------------------------------------------------------

    /**
     * Loads events related to [targetEvent] from the repository.
     *
     * Events are considered "related" if they share the same [EntityType] or
     * the same source app package. The results are sorted by timestamp descending
     * and limited to [MAX_RELATED_EVENTS] entries.
     *
     * @param targetEvent The event whose related events are sought.
     * @param allEvents   The full event list to search within (avoids re-fetching).
     */
    private fun loadRelatedEvents(targetEvent: DetectionEvent, allEvents: List<DetectionEvent>) {
        viewModelScope.launch {
            _uiState.update { it.copy(relatedEventsLoading = true) }

            try {
                val related = allEvents
                    .filter { event ->
                        // Exclude the event itself
                        event.id != targetEvent.id &&
                        // Include events with the same entity type OR same source app
                        (event.entityType == targetEvent.entityType ||
                         (targetEvent.sourceApp != null &&
                          event.sourceApp == targetEvent.sourceApp))
                    }
                    .sortedByDescending { it.timestamp }
                    .take(MAX_RELATED_EVENTS)

                _uiState.update {
                    it.copy(
                        relatedEvents = related,
                        relatedEventsLoading = false
                    )
                }

            } catch (e: Exception) {
                // Non-fatal: related events are supplementary
                _uiState.update { it.copy(relatedEventsLoading = false) }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Source App Info Loading
    // ---------------------------------------------------------------------------

    /**
     * Computes and populates [EventDetailViewModelUiState.sourceAppInfo] for the given event.
     *
     * Aggregates statistics about the source application across all stored events
     * to give the user a historical perspective on how often this app has been
     * flagged.
     *
     * @param event     The event whose source app is being analyzed.
     * @param allEvents The full event list for aggregate statistics.
     */
    private fun loadSourceAppInfo(event: DetectionEvent, allEvents: List<DetectionEvent>) {
        val packageName = event.sourceApp ?: return

        viewModelScope.launch {
            try {
                val appEvents = allEvents.filter { it.sourceApp == packageName }
                if (appEvents.isEmpty()) return@launch

                val now = System.currentTimeMillis()
                val recentCutoff = now - MILLIS_PER_WEEK
                val recentEvents = appEvents.filter { it.timestamp >= recentCutoff }

                val topEntityTypes = appEvents
                    .groupBy { it.entityType }
                    .entries
                    .sortedByDescending { it.value.size }
                    .take(3)
                    .map { it.key }

                val riskLevel = when {
                    appEvents.any { it.severity == Severity.CRITICAL } -> EventRiskLevel.CRITICAL
                    appEvents.any { it.severity == Severity.HIGH } -> EventRiskLevel.HIGH
                    recentEvents.size >= HIGH_FREQUENCY_APP_THRESHOLD -> EventRiskLevel.HIGH
                    appEvents.size > 5 -> EventRiskLevel.MEDIUM
                    else -> EventRiskLevel.LOW
                }

                val sourceAppInfo = EventSourceAppInfo(
                    packageName = packageName,
                    displayName = event.sourceAppName ?: deriveAppDisplayName(packageName),
                    isCurrentlyWhitelisted = inMemoryWhitelist.contains(packageName),
                    totalEventCount = appEvents.size,
                    recentEventCount = recentEvents.size,
                    firstSeenTimestamp = appEvents.minOfOrNull { it.timestamp } ?: event.timestamp,
                    lastSeenTimestamp = appEvents.maxOfOrNull { it.timestamp } ?: event.timestamp,
                    riskLevel = riskLevel,
                    topEntityTypes = topEntityTypes
                )

                _uiState.update { it.copy(sourceAppInfo = sourceAppInfo) }

            } catch (e: Exception) {
                // Non-fatal: source app info is supplementary
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Copy Redacted Text
    // ---------------------------------------------------------------------------

    /**
     * Sets the [EventDetailViewModelUiState.isCopied] flag to true and schedules
     * an automatic reset after [COPY_FEEDBACK_DURATION_MS].
     *
     * The actual clipboard operation must be performed by the UI layer using
     * [EventDetailViewModelUiState.redactedSummary] as the clipboard payload,
     * since clipboard access requires a [android.content.Context].
     */
    fun copyRedactedText() {
        _uiState.update {
            it.copy(
                isCopied = true,
                copyMessage = "Summary copied to clipboard"
            )
        }

        copyFeedbackJob?.cancel()
        copyFeedbackJob = viewModelScope.launch {
            delay(COPY_FEEDBACK_DURATION_MS)
            _uiState.update {
                it.copy(
                    isCopied = false,
                    copyMessage = null
                )
            }
        }
    }

    /**
     * Generates a redacted text summary of the event suitable for sharing.
     *
     * This summary intentionally omits all raw PII and contains only metadata
     * that helps the user understand what was detected without exposing the
     * actual sensitive content.
     *
     * @param event The event to summarize.
     * @return A human-readable string with no raw PII data.
     */
    private fun generateRedactedSummary(event: DetectionEvent): String {
        val displayFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())

        return buildString {
            append("PrivacyGuard Alert — ${event.entityType.displayName} Detected\n\n")
            append("Detected: ${displayFormatter.format(Date(event.timestamp))}\n")
            append("Severity: ${event.severity.name}\n")
            append("Confidence: ${String.format("%.0f", event.confidence * 100)}%\n")
            if (event.sourceAppName != null) {
                append("Source App: ${event.sourceAppName}\n")
            } else if (event.sourceApp != null) {
                append("Source App: ${deriveAppDisplayName(event.sourceApp)}\n")
            }
            append("Action Taken: ${event.actionTaken.displayName}\n")
            append("\n[Raw PII data has been redacted for privacy]")
        }
    }

    // ---------------------------------------------------------------------------
    // Section Toggle
    // ---------------------------------------------------------------------------

    /**
     * Toggles the expanded/collapsed state of a named collapsible section.
     *
     * @param sectionKey Unique identifier for the section to toggle.
     */
    fun toggleSection(sectionKey: String) {
        _uiState.update { state ->
            val current = state.isExpanded[sectionKey] ?: false
            state.copy(
                isExpanded = state.isExpanded + (sectionKey to !current)
            )
        }
    }

    // ---------------------------------------------------------------------------
    // State Clearing
    // ---------------------------------------------------------------------------

    /**
     * Clears the current export result from the UI state.
     * Should be called after the UI has consumed the export result.
     */
    fun clearExportStatus() {
        exportFeedbackJob?.cancel()
        _uiState.update { it.copy(exportResult = null, isExporting = false) }
    }

    /**
     * Clears the current error message from the UI state.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Clears the action result/message from the UI state.
     * Compatible alias used by EventDetailScreen.
     */
    fun clearActionResult() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    /**
     * Clears the copy feedback state immediately.
     */
    fun clearCopyFeedback() {
        copyFeedbackJob?.cancel()
        _uiState.update { it.copy(isCopied = false, copyMessage = null) }
    }

    // ---------------------------------------------------------------------------
    // Timeline Builder
    // ---------------------------------------------------------------------------

    /**
     * Constructs a chronological timeline of lifecycle steps for the event.
     *
     * The timeline captures detection, model analysis, alerting, user notification,
     * and the user action phase, providing a clear audit trail of what happened.
     *
     * @param event The event for which to build the timeline.
     * @return Ordered list of [EventTimelineStep] from detection to resolution.
     */
    private fun buildEventTimeline(event: DetectionEvent): List<EventTimelineStep> {
        val steps = mutableListOf<EventTimelineStep>()

        // Step 1: Detection
        steps.add(
            EventTimelineStep(
                stepType = EventTimelineStepType.DETECTED,
                title = "PII Detected",
                description = "${event.entityType.displayName} data was identified on the clipboard.",
                timestamp = event.timestamp,
                isCompleted = true
            )
        )

        // Step 2: Model analysis
        val analysisTimestamp = event.timestamp + 50 // Approximate: 50ms after detection
        steps.add(
            EventTimelineStep(
                stepType = EventTimelineStepType.MODEL_ANALYZED,
                title = "Model Analysis",
                description = "On-device ML model analyzed the content in ${formatInferenceTime(event.inferenceTimeMs)}.",
                timestamp = analysisTimestamp,
                isCompleted = true,
                metadata = mapOf(
                    "confidence" to "${String.format("%.1f", event.confidence * 100)}%",
                    "inferenceTimeMs" to "${event.inferenceTimeMs}ms",
                    "entityType" to event.entityType.displayName
                )
            )
        )

        // Step 3: Alert raised
        val alertTimestamp = analysisTimestamp + event.inferenceTimeMs + 20
        steps.add(
            EventTimelineStep(
                stepType = EventTimelineStepType.ALERT_RAISED,
                title = "Alert Generated",
                description = "A ${event.severity.name.lowercase()} severity alert was raised for ${event.entityType.displayName}.",
                timestamp = alertTimestamp,
                isCompleted = true,
                metadata = mapOf("severity" to event.severity.name)
            )
        )

        // Step 4: User notification
        val notifyTimestamp = alertTimestamp + 100
        steps.add(
            EventTimelineStep(
                stepType = EventTimelineStepType.USER_NOTIFIED,
                title = "User Notified",
                description = "An overlay notification was displayed to the user.",
                timestamp = notifyTimestamp,
                isCompleted = true
            )
        )

        // Step 5: User action (if any)
        if (event.actionTaken != UserAction.NO_ACTION && event.actionTaken != UserAction.AUTO_DISMISSED) {
            val actionTimestamp = notifyTimestamp + 2_000 // Approximate user response time
            steps.add(
                EventTimelineStep(
                    stepType = EventTimelineStepType.USER_ACTION_TAKEN,
                    title = "User Action",
                    description = "User chose: ${event.actionTaken.displayName}.",
                    timestamp = actionTimestamp,
                    isCompleted = true,
                    metadata = mapOf("action" to event.actionTaken.name),
                    isActive = true
                )
            )

            // Resolution step
            steps.add(
                EventTimelineStep(
                    stepType = EventTimelineStepType.RESOLVED,
                    title = "Event Resolved",
                    description = "Detection event handled and logged.",
                    timestamp = actionTimestamp + 200,
                    isCompleted = true
                )
            )
        } else if (event.actionTaken == UserAction.AUTO_DISMISSED) {
            val autoDismissTimestamp = notifyTimestamp + 5_000
            steps.add(
                EventTimelineStep(
                    stepType = EventTimelineStepType.RESOLVED,
                    title = "Auto-Dismissed",
                    description = "Alert was automatically dismissed after timeout.",
                    timestamp = autoDismissTimestamp,
                    isCompleted = true
                )
            )
        }

        return steps
    }

    // ---------------------------------------------------------------------------
    // Risk Computation
    // ---------------------------------------------------------------------------

    /**
     * Computes a numeric risk score (0–100) for a single detection event.
     *
     * The score is a weighted combination of:
     * - Severity (highest weight: 60 pts max)
     * - Confidence (up to 25 pts)
     * - Source app known vs. unknown (up to 10 pts)
     * - Action taken (up to 5 pts deduction for cleared clipboard)
     *
     * @param event The event to score.
     * @return An integer in the range [0, 100].
     */
    fun computeEventRiskScore(event: DetectionEvent): Int {
        // Severity component (0–60)
        val severityScore = when (event.severity) {
            Severity.CRITICAL -> 60
            Severity.HIGH -> 40
            Severity.MEDIUM -> 20
            else -> 10
        }

        // Confidence component (0–25)
        val confidenceScore = (event.confidence * 25).toInt()

        // Source app component (0–10)
        val sourceScore = if (event.sourceApp != null) 5 else 10 // Unknown app = slightly riskier

        // Action mitigation (0–5 deduction)
        val actionMitigation = when (event.actionTaken) {
            UserAction.CLIPBOARD_CLEARED -> 5
            UserAction.WHITELISTED_APP -> 3
            UserAction.DISMISSED -> 1
            else -> 0
        }

        return (severityScore + confidenceScore + sourceScore - actionMitigation)
            .coerceIn(0, 100)
    }

    /**
     * Returns a human-readable description of the risk associated with the given severity.
     *
     * @param severity The severity level to describe.
     * @return A concise string describing the risk and recommended response.
     */
    fun getRiskDescription(severity: Severity): String = when (severity) {
        Severity.CRITICAL -> "Critical severity indicates that highly sensitive personal " +
            "information—such as financial credentials, government IDs, or authentication " +
            "keys—was detected. Immediate attention is required. Review what app accessed " +
            "this data and consider changing any affected credentials."
        Severity.HIGH -> "High severity means sensitive personal information such as email " +
            "addresses, phone numbers, or medical identifiers was found. You should review " +
            "which application accessed this data and whether the access was intentional."
        Severity.MEDIUM -> "Medium severity indicates moderately sensitive information " +
            "such as names, addresses, or dates of birth was detected. While not immediately " +
            "dangerous, this data could be used for social engineering or identity theft."
        else -> "Low or unknown severity. The detected content may contain personal " +
            "information of indeterminate sensitivity. Review the context to determine " +
            "whether any action is needed."
    }

    /**
     * Returns a detailed description of the given entity type, explaining what
     * the PII category represents and why it is considered sensitive.
     *
     * @param entityType The entity type to describe.
     * @return A descriptive string for display in the UI.
     */
    fun getEntityTypeDescription(entityType: EntityType): String = when (entityType) {
        EntityType.CREDIT_CARD -> "Credit card numbers are 13–19 digit strings that provide " +
            "direct access to payment accounts. Exposure of this data can result in " +
            "fraudulent transactions and financial loss."
        EntityType.SSN -> "Social Security Numbers are 9-digit government identifiers " +
            "used for tax reporting, credit checks, and official identity verification in the US. " +
            "SSN exposure is one of the leading causes of identity theft."
        EntityType.PASSWORD -> "Passwords are secret authentication credentials that protect " +
            "access to accounts and services. Exposure of a password can compromise the " +
            "corresponding account and all linked services."
        EntityType.API_KEY -> "API keys are machine credentials used to authenticate " +
            "programmatic access to services. A leaked API key can grant unauthorized access " +
            "to paid services or private data repositories."
        EntityType.EMAIL -> "Email addresses are personal contact identifiers used widely " +
            "for authentication, communication, and account recovery. Exposure can lead to " +
            "phishing attacks and spam."
        EntityType.PHONE -> "Phone numbers are personal contact identifiers used for " +
            "communication, two-factor authentication, and account recovery. Exposure can " +
            "enable unsolicited contact and SIM-swapping attacks."
        EntityType.PERSON_NAME -> "Full names are personal identifiers that, when combined " +
            "with other data, can be used for identity verification and social engineering."
        EntityType.ADDRESS -> "Physical addresses can expose your home or work location, " +
            "enabling stalking, burglary, or targeted physical mail fraud."
        EntityType.DATE_OF_BIRTH -> "Dates of birth are commonly used as security questions " +
            "and identity verification factors. Combined with other PII, they increase the " +
            "risk of identity theft."
        EntityType.MEDICAL_ID -> "Medical identifiers such as patient IDs and insurance " +
            "policy numbers can enable healthcare fraud and expose sensitive medical records."
        EntityType.UNKNOWN -> "An unclassified data pattern was detected that may contain " +
            "personally identifiable information. The content should be reviewed to determine " +
            "its sensitivity."
    }

    /**
     * Generates a prioritized list of recommended actions for the user based on
     * the detected entity type and severity level.
     *
     * @param entityType The type of PII that was detected.
     * @param severity   The severity of the detection.
     * @return An ordered list of [ActionRecommendation] sorted by priority.
     */
    fun getRecommendedActions(
        entityType: EntityType,
        severity: Severity
    ): List<ActionRecommendation> {
        val recommendations = mutableListOf<ActionRecommendation>()

        // Universal recommendation for all events
        recommendations.add(
            ActionRecommendation(
                title = "Review the source application",
                description = "Check whether the app that accessed this data had a legitimate " +
                    "reason to do so. If you did not intentionally copy this data, investigate " +
                    "the app's behavior.",
                priority = 1,
                isUrgent = severity == Severity.CRITICAL
            )
        )

        // Entity-type-specific recommendations
        when (entityType) {
            EntityType.CREDIT_CARD -> {
                recommendations.add(
                    ActionRecommendation(
                        title = "Check recent card transactions",
                        description = "Log into your banking app or website and review recent " +
                            "transactions for any unauthorized charges.",
                        priority = 2,
                        isUrgent = true
                    )
                )
                recommendations.add(
                    ActionRecommendation(
                        title = "Consider requesting a card replacement",
                        description = "If you believe the card number may have been compromised, " +
                            "contact your bank to freeze or replace the card.",
                        priority = 3,
                        isUrgent = true
                    )
                )
            }
            EntityType.PASSWORD -> {
                recommendations.add(
                    ActionRecommendation(
                        title = "Change the affected password immediately",
                        description = "If the password was for a sensitive account, change it " +
                            "now and enable two-factor authentication if available.",
                        priority = 2,
                        isUrgent = true
                    )
                )
                recommendations.add(
                    ActionRecommendation(
                        title = "Check for reused passwords",
                        description = "If this password is reused on other accounts, change it " +
                            "on those accounts as well and consider using a password manager.",
                        priority = 3,
                        isUrgent = false
                    )
                )
            }
            EntityType.SSN -> {
                recommendations.add(
                    ActionRecommendation(
                        title = "Place a credit freeze",
                        description = "Contact the major credit bureaus to place a freeze on " +
                            "your credit report, preventing new accounts from being opened.",
                        priority = 2,
                        isUrgent = true
                    )
                )
                recommendations.add(
                    ActionRecommendation(
                        title = "Monitor for identity theft",
                        description = "Sign up for identity theft monitoring services and " +
                            "check your credit report for any unauthorized activity.",
                        priority = 3,
                        isUrgent = false
                    )
                )
            }
            EntityType.API_KEY -> {
                recommendations.add(
                    ActionRecommendation(
                        title = "Rotate the API key immediately",
                        description = "Log into the service dashboard and revoke the current key, " +
                            "then generate a new one. Update all applications using the old key.",
                        priority = 2,
                        isUrgent = true
                    )
                )
            }
            EntityType.EMAIL -> {
                recommendations.add(
                    ActionRecommendation(
                        title = "Be alert for phishing attempts",
                        description = "Your email address may now be in use by spammers. " +
                            "Be cautious of unsolicited emails asking for personal information.",
                        priority = 2,
                        isUrgent = false
                    )
                )
            }
            else -> {
                recommendations.add(
                    ActionRecommendation(
                        title = "Clear your clipboard",
                        description = "PrivacyGuard can clear your clipboard to prevent " +
                            "other apps from reading this data.",
                        priority = 2,
                        isUrgent = false
                    )
                )
            }
        }

        // Severity-based recommendations
        if (severity == Severity.CRITICAL || severity == Severity.HIGH) {
            recommendations.add(
                ActionRecommendation(
                    title = "Whitelist trusted apps",
                    description = "If the source application is one you trust (e.g., your " +
                        "password manager), you can whitelist it to suppress future alerts.",
                    priority = recommendations.size + 1,
                    isUrgent = false
                )
            )
        }

        return recommendations.sortedBy { it.priority }
    }

    /**
     * Returns contextual help text for a given entity type,
     * describing practical steps the user can take.
     *
     * @param entityType The entity type for which to generate help.
     * @return A detailed help string.
     */
    fun getContextualHelp(entityType: EntityType): String = when (entityType) {
        EntityType.CREDIT_CARD ->
            "Credit card numbers are typically 13–19 digits, sometimes grouped with spaces " +
            "or dashes. They should only be entered in secure payment forms and never copied " +
            "to the clipboard unless using a trusted payment app."
        EntityType.SSN ->
            "Your Social Security Number is a nine-digit number formatted as XXX-XX-XXXX. " +
            "It is one of the most sensitive pieces of personal data and should never be " +
            "stored or transmitted in plaintext."
        EntityType.PASSWORD ->
            "Passwords should be at least 12 characters, use a mix of letters, numbers, and " +
            "symbols, and be unique to each account. Consider using a dedicated password manager " +
            "to reduce clipboard exposure."
        EntityType.API_KEY ->
            "API keys are long random strings used to authenticate software services. " +
            "They should be stored in secure environment variables or secret management systems, " +
            "not copied to the clipboard."
        EntityType.EMAIL ->
            "While email addresses are less sensitive than credentials, they can be used for " +
            "targeted phishing and spam campaigns. Avoid copying email addresses on untrusted devices."
        EntityType.PHONE ->
            "Phone numbers can enable SIM-swapping attacks when combined with other personal data. " +
            "Be cautious about which apps can read your clipboard."
        else ->
            "PrivacyGuard monitors your clipboard in real time for patterns that match " +
            "known PII formats. If you see a detection that seems incorrect, you can report " +
            "it as a false positive to help improve detection accuracy."
    }

    // ---------------------------------------------------------------------------
    // Helper Utilities
    // ---------------------------------------------------------------------------

    /**
     * Converts a [Severity] value to a numeric score for JSON/CSV export.
     *
     * @param severity The severity to convert.
     * @return An integer from 1 (low) to 4 (critical).
     */
    private fun severityToNumericScore(severity: Severity): Int = when (severity) {
        Severity.CRITICAL -> 4
        Severity.HIGH -> 3
        Severity.MEDIUM -> 2
        else -> 1
    }

    /**
     * Returns true if the event is considered high-risk based on severity
     * and confidence thresholds.
     *
     * @param event The event to evaluate.
     * @return True for high- or critical-severity events with confidence > 0.7.
     */
    fun isHighRiskEvent(event: DetectionEvent): Boolean =
        (event.severity == Severity.CRITICAL || event.severity == Severity.HIGH) &&
        event.confidence > 0.7f

    /**
     * Formats an epoch timestamp into a human-readable date/time string.
     *
     * @param timestamp Epoch milliseconds.
     * @return A localized date/time string.
     */
    fun formatTimestamp(timestamp: Long): String {
        val formatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    /**
     * Formats an inference duration in milliseconds into a human-readable string.
     *
     * @param ms Duration in milliseconds.
     * @return A formatted string such as "12ms" or "1.2s".
     */
    fun formatInferenceTime(ms: Long): String = when {
        ms < 1_000 -> "${ms}ms"
        else -> String.format("%.1fs", ms / 1_000.0)
    }

    /**
     * Derives a human-readable display name from a package name by taking
     * the last segment and capitalizing it.
     *
     * @param packageName Android package name (e.g., "com.example.myapp").
     * @return A guess at the display name (e.g., "Myapp").
     */
    private fun deriveAppDisplayName(packageName: String): String {
        val lastSegment = packageName.substringAfterLast('.')
        return lastSegment.replaceFirstChar { it.uppercaseChar() }
    }

    /**
     * Escapes a string field for CSV output by wrapping in quotes if needed
     * and doubling any internal quotes.
     *
     * @param field The raw field value.
     * @return A CSV-safe string.
     */
    private fun escapeCsvField(field: String): String {
        if (field.contains(',') || field.contains('"') || field.contains('\n')) {
            return "\"${field.replace("\"", "\"\"")}\""
        }
        return field
    }

    // ---------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        copyFeedbackJob?.cancel()
        exportFeedbackJob?.cancel()
    }
}
