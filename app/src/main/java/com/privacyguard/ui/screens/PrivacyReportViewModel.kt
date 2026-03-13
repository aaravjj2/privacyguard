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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Enums
// ---------------------------------------------------------------------------

/**
 * Defines the time window over which the privacy report is computed.
 *
 * Each period maps to a specific [durationMs] that is subtracted from the
 * current epoch time to derive the report start boundary. The [CUSTOM] period
 * uses explicitly supplied start and end timestamps instead.
 */
enum class ReportPeriod(
    val displayName: String,
    val shortLabel: String,
    val durationMs: Long
) {
    TODAY(
        displayName = "Today",
        shortLabel = "Today",
        durationMs = 24 * 60 * 60 * 1000L
    ),
    WEEK(
        displayName = "Last 7 Days",
        shortLabel = "7 Days",
        durationMs = 7 * 24 * 60 * 60 * 1000L
    ),
    MONTH(
        displayName = "Last 30 Days",
        shortLabel = "30 Days",
        durationMs = 30 * 24 * 60 * 60 * 1000L
    ),
    CUSTOM(
        displayName = "Custom Range",
        shortLabel = "Custom",
        durationMs = 0L  // Not used for CUSTOM; see customStartMs / customEndMs
    )
}

/**
 * Supported output formats for report export operations.
 *
 * Each format has an associated MIME type and file extension used when
 * creating the export filename and sharing the file externally.
 */
enum class ReportExportFormat(
    val displayName: String,
    val description: String,
    val mimeType: String,
    val fileExtension: String
) {
    PDF(
        displayName = "PDF Report",
        description = "Formatted report document suitable for printing or archiving.",
        mimeType = "application/pdf",
        fileExtension = "pdf"
    ),
    CSV(
        displayName = "CSV Spreadsheet",
        description = "Raw event data in comma-separated format for analysis in spreadsheets.",
        mimeType = "text/csv",
        fileExtension = "csv"
    ),
    JSON(
        displayName = "JSON Data",
        description = "Structured event data in JSON format for programmatic processing.",
        mimeType = "application/json",
        fileExtension = "json"
    )
}

/**
 * Represents the current status of an in-progress or completed export operation.
 */
sealed class ReportExportStatus {
    /** No export is in progress. */
    object Idle : ReportExportStatus()

    /** Export is actively being generated. */
    data class Generating(val format: ReportExportFormat, val progressPercent: Int = 0) : ReportExportStatus()

    /** Export completed. The payload is the generated content. */
    data class Ready(
        val format: ReportExportFormat,
        val content: String,
        val filename: String,
        val eventCount: Int
    ) : ReportExportStatus()

    /** Export failed due to an error. */
    data class Failed(val format: ReportExportFormat, val reason: String) : ReportExportStatus()
}

// ---------------------------------------------------------------------------
// Data model classes
// ---------------------------------------------------------------------------

/**
 * Aggregated statistics for a single [EntityType] within the selected report period.
 *
 * Used to populate the PII-type breakdown bar chart and the "Top Detected Types"
 * ranked list.
 *
 * @param entityType       The PII category being reported on.
 * @param count            Total number of detections in the period.
 * @param percentOfTotal   Share of this type in the total detection count (0.0–1.0).
 * @param averageConfidence Mean model confidence across all detections of this type.
 * @param criticalCount    Number of critical-severity detections of this type.
 * @param highCount        Number of high-severity detections of this type.
 * @param mediumCount      Number of medium-severity detections of this type.
 * @param trend            Percentage change in count vs. the previous equivalent period.
 */
data class EntityTypeStats(
    val entityType: EntityType,
    val count: Int,
    val percentOfTotal: Float,
    val averageConfidence: Float,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val trend: Float  // positive = increased, negative = decreased
) {
    /** The dominant severity level for this entity type. */
    val dominantSeverity: Severity
        get() = when {
            criticalCount > 0 -> Severity.CRITICAL
            highCount > 0 -> Severity.HIGH
            mediumCount > 0 -> Severity.MEDIUM
            else -> Severity.MEDIUM
        }

    /** Whether detections of this type have increased vs. the prior period. */
    val isTrendingUp: Boolean get() = trend > 0f

    /** Whether detections of this type have decreased vs. the prior period. */
    val isTrendingDown: Boolean get() = trend < 0f
}

/**
 * Aggregated statistics for a single source application across all detections
 * within the report period.
 *
 * Used to populate the app-by-app breakdown section of the report.
 *
 * @param packageName         Android package name (e.g., "com.example.browser").
 * @param displayName         Human-readable app name.
 * @param detectionCount      Total detections attributed to this app.
 * @param percentOfTotal      Share of total detections attributed to this app (0.0–1.0).
 * @param criticalCount       Critical-severity detections for this app.
 * @param highCount           High-severity detections.
 * @param mediumCount         Medium-severity detections.
 * @param topEntityTypes      The top 3 entity types detected from this app.
 * @param firstDetectionMs    Epoch ms of the earliest detection from this app in the period.
 * @param lastDetectionMs     Epoch ms of the most recent detection from this app in the period.
 * @param averageConfidence   Mean detection confidence for this app.
 * @param isWhitelisted       Whether the app is currently in the user's whitelist.
 */
data class AppStats(
    val packageName: String,
    val displayName: String,
    val detectionCount: Int,
    val percentOfTotal: Float,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val topEntityTypes: List<EntityType>,
    val firstDetectionMs: Long,
    val lastDetectionMs: Long,
    val averageConfidence: Float,
    val isWhitelisted: Boolean = false
) {
    /** The highest severity observed for this app. */
    val maxSeverity: Severity
        get() = when {
            criticalCount > 0 -> Severity.CRITICAL
            highCount > 0 -> Severity.HIGH
            mediumCount > 0 -> Severity.MEDIUM
            else -> Severity.MEDIUM
        }
}

/**
 * A single time-series data point for the report charts.
 *
 * Each point represents one calendar day, recording the count of detections
 * that occurred on that day as well as the computed risk score for that day.
 *
 * @param epochDayMs    Epoch ms for midnight at the start of the represented day.
 * @param label         Short display label for the day (e.g., "Mon", "Mar 5").
 * @param count         Total number of detections on this day.
 * @param criticalCount Number of critical-severity detections on this day.
 * @param highCount     Number of high-severity detections on this day.
 * @param mediumCount   Number of medium-severity detections on this day.
 * @param riskScore     Computed risk score for this day (0–100).
 */
data class DailyStatPoint(
    val epochDayMs: Long,
    val label: String,
    val count: Int,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val riskScore: Int
) {
    /** Total weighted severity score for visual height normalization. */
    val weightedScore: Int
        get() = criticalCount * 4 + highCount * 3 + mediumCount * 2
}

/**
 * Represents a single point on the risk trend line chart.
 *
 * @param epochMs   Epoch ms for the data point.
 * @param label     Human-readable label for the x-axis.
 * @param riskScore The risk score at this point in time (0–100).
 */
data class RiskTrendPoint(
    val epochMs: Long,
    val label: String,
    val riskScore: Int
)

/**
 * Breakdown of the report period's detections by severity level.
 *
 * @param criticalCount     Detections classified as CRITICAL.
 * @param highCount         Detections classified as HIGH.
 * @param mediumCount       Detections classified as MEDIUM.
 * @param totalCount        Total detections in the period.
 * @param criticalPercent   Fraction of total that is CRITICAL (0.0–1.0).
 * @param highPercent       Fraction of total that is HIGH (0.0–1.0).
 * @param mediumPercent     Fraction of total that is MEDIUM (0.0–1.0).
 */
data class SeverityBreakdown(
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val totalCount: Int,
    val criticalPercent: Float,
    val highPercent: Float,
    val mediumPercent: Float
)

/**
 * Comparison of detection counts and risk scores between the current period
 * and the previous equivalent period.
 *
 * @param currentPeriodCount        Total detections in the current period.
 * @param previousPeriodCount       Total detections in the previous equivalent period.
 * @param countChangePercent        Percentage change in count (positive = worse).
 * @param currentPeriodRiskScore    Average risk score in current period (0–100).
 * @param previousPeriodRiskScore   Average risk score in previous period (0–100).
 * @param riskScoreChange           Absolute change in risk score (positive = worse).
 * @param isImproving               Whether the user's privacy posture has improved.
 */
data class WeeklyComparison(
    val currentPeriodCount: Int,
    val previousPeriodCount: Int,
    val countChangePercent: Float,
    val currentPeriodRiskScore: Int,
    val previousPeriodRiskScore: Int,
    val riskScoreChange: Int,
    val isImproving: Boolean
)

/**
 * Distribution of user actions taken across all events in the report period.
 *
 * @param clipboardClearedCount  Events where user cleared the clipboard.
 * @param dismissedCount         Events where user dismissed the alert.
 * @param whitelistedCount       Events where user whitelisted the source app.
 * @param autoDismissedCount     Events that were auto-dismissed.
 * @param noActionCount          Events where no action was taken.
 * @param totalCount             Total events in the distribution.
 */
data class ActionDistribution(
    val clipboardClearedCount: Int,
    val dismissedCount: Int,
    val whitelistedCount: Int,
    val autoDismissedCount: Int,
    val noActionCount: Int,
    val totalCount: Int
) {
    val clipboardClearedPercent: Float
        get() = if (totalCount > 0) clipboardClearedCount.toFloat() / totalCount else 0f
    val dismissedPercent: Float
        get() = if (totalCount > 0) dismissedCount.toFloat() / totalCount else 0f
    val proactiveResponsePercent: Float
        get() = if (totalCount > 0) (clipboardClearedCount + whitelistedCount).toFloat() / totalCount else 0f
}

// ---------------------------------------------------------------------------
// UI State
// ---------------------------------------------------------------------------

/**
 * Immutable snapshot of the entire Privacy Report screen's UI state.
 *
 * Designed to be consumed by Compose's [collectAsState] for efficient
 * recomposition. All derived / computed fields are computed in the ViewModel
 * and stored here to keep the UI purely reactive.
 */
data class PrivacyReportUiState(
    // Loading and error state
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,

    // Period selection
    val selectedPeriod: ReportPeriod = ReportPeriod.WEEK,
    val customStartMs: Long = 0L,
    val customEndMs: Long = 0L,

    // Summary statistics
    val totalDetections: Int = 0,
    val uniqueAppsCount: Int = 0,
    val uniqueEntityTypesCount: Int = 0,
    val averageRiskScore: Int = 0,
    val peakRiskScore: Int = 0,
    val averageConfidence: Float = 0f,

    // Breakdown data
    val entityTypeStats: List<EntityTypeStats> = emptyList(),
    val appStats: List<AppStats> = emptyList(),
    val severityBreakdown: SeverityBreakdown? = null,
    val actionDistribution: ActionDistribution? = null,

    // Chart data
    val dailyStatPoints: List<DailyStatPoint> = emptyList(),
    val riskTrendPoints: List<RiskTrendPoint> = emptyList(),

    // Comparison
    val weeklyComparison: WeeklyComparison? = null,

    // Top / notable items
    val topEntityType: EntityType? = null,
    val topRiskApp: AppStats? = null,
    val mostActiveDay: DailyStatPoint? = null,

    // Export state
    val exportStatus: ReportExportStatus = ReportExportStatus.Idle,
    val showExportDialog: Boolean = false,

    // Share state
    val shareContent: String? = null,
    val isSharing: Boolean = false,

    // Date range picker dialog
    val showDateRangePicker: Boolean = false,

    // Display metadata
    val reportTitle: String = "",
    val reportDateRangeLabel: String = "",
    val lastRefreshedMs: Long = 0L
) {
    /** Whether the report has data to display. */
    val hasData: Boolean get() = totalDetections > 0

    /** Whether the custom period fields are validly set. */
    val isCustomRangeValid: Boolean
        get() = selectedPeriod == ReportPeriod.CUSTOM &&
            customStartMs > 0L &&
            customEndMs > customStartMs

    /** The displayed date range label based on the active period. */
    val effectiveDateRangeLabel: String
        get() = reportDateRangeLabel.ifEmpty { selectedPeriod.displayName }
}

// ---------------------------------------------------------------------------
// PrivacyReportViewModel
// ---------------------------------------------------------------------------

/**
 * ViewModel for the [PrivacyReportScreen].
 *
 * Owns all logic for:
 * - Filtering the event log by the selected [ReportPeriod]
 * - Computing entity-type statistics, app-level statistics, and daily chart data
 * - Managing report export in PDF, CSV, and JSON formats
 * - Building a shareable text summary of the report
 * - Comparing the current period against the previous equivalent window
 * - Periodic background refresh
 *
 * All state is exposed as a single [StateFlow<PrivacyReportUiState>].
 * The UI should never compute derived data itself; all display values
 * are pre-computed here.
 */
class PrivacyReportViewModel : ViewModel() {

    // ---------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------

    companion object {
        /** Millis in one hour. */
        private const val MILLIS_PER_HOUR = 60 * 60 * 1000L

        /** Millis in 24 hours. */
        private const val MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR

        /** Millis in 7 days. */
        private const val MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY

        /** Millis in 30 days. */
        private const val MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY

        /** How often to automatically refresh the report in the background. */
        private const val AUTO_REFRESH_INTERVAL_MS = 60_000L

        /** Maximum number of apps to include in the app breakdown list. */
        private const val MAX_APP_STATS = 20

        /** Maximum number of entity type stats to include in the breakdown. */
        private val MAX_ENTITY_TYPE_STATS = EntityType.entries.size

        /** Penalty per critical detection when computing a day's risk score. */
        private const val CRITICAL_DAILY_PENALTY = 20

        /** Penalty per high detection when computing a day's risk score. */
        private const val HIGH_DAILY_PENALTY = 12

        /** Penalty per medium detection when computing a day's risk score. */
        private const val MEDIUM_DAILY_PENALTY = 5

        /** Base risk score for a day with no detections. */
        private const val BASE_DAILY_RISK = 0

        /** Date format for report titles and headers. */
        private const val REPORT_HEADER_DATE_FORMAT = "MMMM dd, yyyy"

        /** Date format used in the date range label. */
        private const val DATE_RANGE_FORMAT = "MMM dd"

        /** ISO format for JSON export timestamps. */
        private const val ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        /** Short day-of-week label format for chart x-axis. */
        private const val DAY_OF_WEEK_FORMAT = "EEE"

        /** Short month-day format for chart x-axis on longer periods. */
        private const val MONTH_DAY_FORMAT = "MMM d"

        /** Filename date stamp format. */
        private const val FILENAME_DATE_FORMAT = "yyyyMMdd_HHmmss"
    }

    // ---------------------------------------------------------------------------
    // State
    // ---------------------------------------------------------------------------

    private val _uiState = MutableStateFlow(PrivacyReportUiState())
    val uiState: StateFlow<PrivacyReportUiState> = _uiState.asStateFlow()

    private var logRepository: EncryptedLogRepository? = null
    private var autoRefreshJob: Job? = null

    /** Cached snapshot of all events to avoid redundant repository calls. */
    private var cachedAllEvents: List<DetectionEvent> = emptyList()

    // ---------------------------------------------------------------------------
    // Repository Binding
    // ---------------------------------------------------------------------------

    /**
     * Binds an [EncryptedLogRepository] to this ViewModel.
     *
     * Calling this triggers an immediate report load. The repository is observed
     * for changes so that the report refreshes automatically when new events arrive.
     *
     * @param repo The repository providing access to the event log.
     */
    fun setRepository(repo: EncryptedLogRepository) {
        if (logRepository == repo) return
        logRepository = repo

        // Observe the event flow for real-time updates
        viewModelScope.launch {
            repo.getAllEvents().collect { events ->
                cachedAllEvents = events
                computeAndPublishReport(events)
            }
        }

        startAutoRefresh()
    }

    // ---------------------------------------------------------------------------
    // Report Loading
    // ---------------------------------------------------------------------------

    /**
     * Triggers a full report load for the currently selected period.
     *
     * If a repository is not yet bound, the loading state will timeout
     * gracefully. This method is safe to call multiple times.
     */
    fun loadReport() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val repo = logRepository ?: run {
                    // No repository yet — show empty loading state but don't error
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }

                val allEvents = repo.getAllEvents().first()
                cachedAllEvents = allEvents
                computeAndPublishReport(allEvents)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load report: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Refreshes the report statistics without showing the full loading skeleton.
     *
     * Suitable for pull-to-refresh interactions where partial UI should remain
     * visible while new data is fetched.
     */
    fun refreshStats() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val repo = logRepository ?: run {
                    _uiState.update { it.copy(isRefreshing = false) }
                    return@launch
                }

                val allEvents = repo.getAllEvents().first()
                cachedAllEvents = allEvents
                computeAndPublishReport(allEvents)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        errorMessage = "Refresh failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Core computation pipeline: filters events by the active period,
     * computes all derived statistics, and atomically publishes the result.
     *
     * @param allEvents The full unfiltered list of all detection events.
     */
    private fun computeAndPublishReport(allEvents: List<DetectionEvent>) {
        val state = _uiState.value
        val now = System.currentTimeMillis()

        // Determine the active time window
        val (startMs, endMs) = getActivePeriodBounds(state, now)

        // Filter to the active window
        val periodEvents = allEvents.filter { it.timestamp in startMs..endMs }

        // Compute the previous equivalent window for comparison
        val windowDuration = endMs - startMs
        val prevStartMs = startMs - windowDuration
        val prevEndMs = startMs
        val prevPeriodEvents = allEvents.filter { it.timestamp in prevStartMs..prevEndMs }

        // Compute all statistics
        val entityTypeStats = computeEntityTypeStats(periodEvents, prevPeriodEvents)
        val appStats = computeAppStats(periodEvents)
        val dailyPoints = computeDailyStatPoints(periodEvents, startMs, endMs)
        val riskTrendPoints = computeRiskTrend(dailyPoints)
        val severityBreakdown = computeSeverityBreakdown(periodEvents)
        val actionDistribution = computeActionDistribution(periodEvents)
        val comparison = computeWeeklyComparison(periodEvents, prevPeriodEvents)
        val averageRisk = computeAverageRiskScore(periodEvents)
        val peakRisk = dailyPoints.maxOfOrNull { it.riskScore } ?: 0
        val avgConfidence = computeAverageConfidence(periodEvents)
        val topEntityType = entityTypeStats.maxByOrNull { it.count }?.entityType
        val topRiskApp = appStats.maxByOrNull { it.criticalCount * 4 + it.highCount * 3 + it.mediumCount }
        val mostActiveDay = dailyPoints.maxByOrNull { it.count }

        val reportTitle = generateReportTitle(state.selectedPeriod, startMs, endMs)
        val dateRangeLabel = formatDateRange(startMs, endMs)

        _uiState.update { current ->
            current.copy(
                isLoading = false,
                isRefreshing = false,
                totalDetections = periodEvents.size,
                uniqueAppsCount = periodEvents.mapNotNull { it.sourceApp }.distinct().size,
                uniqueEntityTypesCount = periodEvents.map { it.entityType }.distinct().size,
                averageRiskScore = averageRisk,
                peakRiskScore = peakRisk,
                averageConfidence = avgConfidence,
                entityTypeStats = entityTypeStats,
                appStats = appStats,
                dailyStatPoints = dailyPoints,
                riskTrendPoints = riskTrendPoints,
                severityBreakdown = severityBreakdown,
                actionDistribution = actionDistribution,
                weeklyComparison = comparison,
                topEntityType = topEntityType,
                topRiskApp = topRiskApp,
                mostActiveDay = mostActiveDay,
                reportTitle = reportTitle,
                reportDateRangeLabel = dateRangeLabel,
                lastRefreshedMs = System.currentTimeMillis()
            )
        }
    }

    // ---------------------------------------------------------------------------
    // Period Selection
    // ---------------------------------------------------------------------------

    /**
     * Changes the active report period and triggers an immediate reload.
     *
     * @param period The [ReportPeriod] to activate.
     */
    fun setReportPeriod(period: ReportPeriod) {
        if (_uiState.value.selectedPeriod == period && period != ReportPeriod.CUSTOM) return

        _uiState.update {
            it.copy(
                selectedPeriod = period,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            computeAndPublishReport(cachedAllEvents)
        }
    }

    /**
     * Opens the custom date range picker dialog.
     */
    fun showDateRangePicker() {
        _uiState.update { it.copy(showDateRangePicker = true) }
    }

    /**
     * Dismisses the custom date range picker dialog without applying changes.
     */
    fun dismissDateRangePicker() {
        _uiState.update { it.copy(showDateRangePicker = false) }
    }

    /**
     * Applies a custom date range and reloads the report.
     *
     * The [startMs] and [endMs] parameters must satisfy startMs < endMs.
     * If the range is invalid, the call is silently ignored.
     *
     * @param startMs Epoch ms for the start of the custom range (inclusive).
     * @param endMs   Epoch ms for the end of the custom range (inclusive).
     */
    fun setCustomDateRange(startMs: Long, endMs: Long) {
        if (startMs >= endMs) return

        _uiState.update {
            it.copy(
                selectedPeriod = ReportPeriod.CUSTOM,
                customStartMs = startMs,
                customEndMs = endMs,
                showDateRangePicker = false,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            computeAndPublishReport(cachedAllEvents)
        }
    }

    // ---------------------------------------------------------------------------
    // Export
    // ---------------------------------------------------------------------------

    /**
     * Opens the export format selection dialog.
     */
    fun showExportDialog() {
        _uiState.update { it.copy(showExportDialog = true) }
    }

    /**
     * Dismisses the export dialog without starting an export.
     */
    fun dismissExportDialog() {
        _uiState.update { it.copy(showExportDialog = false) }
    }

    /**
     * Generates a report export in the specified [format].
     *
     * Export progresses through [ReportExportStatus.Generating] and resolves to
     * either [ReportExportStatus.Ready] or [ReportExportStatus.Failed].
     *
     * The generated content is accessible from [PrivacyReportUiState.exportStatus]
     * as [ReportExportStatus.Ready.content]. The UI is responsible for actually
     * writing the file or invoking a share intent.
     *
     * @param format The desired export format.
     */
    fun exportReport(format: ReportExportFormat) {
        val currentState = _uiState.value
        val eventCount = currentState.totalDetections

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showExportDialog = false,
                    exportStatus = ReportExportStatus.Generating(format, 0)
                )
            }

            try {
                // Simulate staged progress feedback
                delay(100)
                _uiState.update {
                    it.copy(exportStatus = ReportExportStatus.Generating(format, 25))
                }

                val (startMs, endMs) = getActivePeriodBounds(currentState, System.currentTimeMillis())
                val periodEvents = cachedAllEvents.filter { it.timestamp in startMs..endMs }

                delay(150)
                _uiState.update {
                    it.copy(exportStatus = ReportExportStatus.Generating(format, 60))
                }

                val content = when (format) {
                    ReportExportFormat.CSV -> exportAsCsv(periodEvents, currentState)
                    ReportExportFormat.JSON -> exportAsJson(periodEvents, currentState)
                    ReportExportFormat.PDF -> exportAsPdfMarkup(periodEvents, currentState)
                }

                delay(100)
                _uiState.update {
                    it.copy(exportStatus = ReportExportStatus.Generating(format, 90))
                }

                val timestamp = SimpleDateFormat(FILENAME_DATE_FORMAT, Locale.US)
                    .format(Date())
                val filename = "privacyguard_report_${format.name.lowercase()}_$timestamp.${format.fileExtension}"

                delay(50)
                _uiState.update {
                    it.copy(
                        exportStatus = ReportExportStatus.Ready(
                            format = format,
                            content = content,
                            filename = filename,
                            eventCount = periodEvents.size
                        )
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        exportStatus = ReportExportStatus.Failed(
                            format = format,
                            reason = "Export failed: ${e.localizedMessage}"
                        )
                    )
                }
            }
        }
    }

    /**
     * Clears the current export status, returning it to [ReportExportStatus.Idle].
     * Should be called after the UI has consumed the export result.
     */
    fun clearExportStatus() {
        _uiState.update { it.copy(exportStatus = ReportExportStatus.Idle) }
    }

    /**
     * Generates a CSV export of the given events.
     *
     * Produces a header row followed by one row per event. All fields are
     * properly escaped for RFC 4180 CSV compatibility.
     *
     * @param events The filtered list of events to include.
     * @param state  The current UI state for metadata headers.
     * @return A CSV-formatted string with header and data rows.
     */
    private fun exportAsCsv(
        events: List<DetectionEvent>,
        state: PrivacyReportUiState
    ): String {
        val isoFormatter = SimpleDateFormat(ISO_DATE_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val displayFormatter = SimpleDateFormat(REPORT_HEADER_DATE_FORMAT, Locale.getDefault())

        return buildString {
            // Metadata header block (commented lines)
            appendLine("# PrivacyGuard Privacy Report")
            appendLine("# Period: ${state.reportTitle}")
            appendLine("# Generated: ${displayFormatter.format(Date())}")
            appendLine("# Total Events: ${events.size}")
            appendLine("#")

            // Column header row
            val headers = listOf(
                "id", "timestamp_epoch", "timestamp_iso",
                "entity_type", "entity_type_display",
                "severity", "confidence", "confidence_percent",
                "inference_time_ms", "action_taken", "action_taken_display",
                "source_app_package", "source_app_name"
            )
            appendLine(headers.joinToString(","))

            // Data rows
            events.sortedByDescending { it.timestamp }.forEach { event ->
                val row = listOf(
                    csvEscape(event.id),
                    event.timestamp.toString(),
                    csvEscape(isoFormatter.format(Date(event.timestamp))),
                    csvEscape(event.entityType.name),
                    csvEscape(event.entityType.displayName),
                    csvEscape(event.severity.name),
                    String.format("%.6f", event.confidence),
                    String.format("%.2f", event.confidence * 100),
                    event.inferenceTimeMs.toString(),
                    csvEscape(event.actionTaken.name),
                    csvEscape(event.actionTaken.displayName),
                    csvEscape(event.sourceApp ?: ""),
                    csvEscape(event.sourceAppName ?: "")
                )
                appendLine(row.joinToString(","))
            }

            // Summary footer
            appendLine()
            appendLine("# SUMMARY STATISTICS")
            appendLine("# Total Detections,${events.size}")
            appendLine("# Unique Apps,${events.mapNotNull { it.sourceApp }.distinct().size}")
            appendLine("# Critical Count,${events.count { it.severity == Severity.CRITICAL }}")
            appendLine("# High Count,${events.count { it.severity == Severity.HIGH }}")
            appendLine("# Medium Count,${events.count { it.severity == Severity.MEDIUM }}")
            appendLine("# Average Confidence,${String.format("%.2f", events.map { it.confidence }.average())}")
        }
    }

    /**
     * Generates a JSON export of the given events including a summary section.
     *
     * The output conforms to a custom PrivacyGuard JSON schema with a top-level
     * metadata object, a summary statistics object, and an array of event objects.
     *
     * @param events The filtered list of events to include.
     * @param state  The current UI state for metadata.
     * @return A pretty-printed JSON string.
     */
    private fun exportAsJson(
        events: List<DetectionEvent>,
        state: PrivacyReportUiState
    ): String {
        val isoFormatter = SimpleDateFormat(ISO_DATE_FORMAT, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        return buildString {
            appendLine("{")
            appendLine("  \"schema\": \"privacyguard-report-v1\",")
            appendLine("  \"exportedAt\": \"${isoFormatter.format(Date())}\",")
            appendLine("  \"metadata\": {")
            appendLine("    \"reportTitle\": \"${jsonEscape(state.reportTitle)}\",")
            appendLine("    \"period\": \"${state.selectedPeriod.name}\",")
            appendLine("    \"dateRange\": \"${jsonEscape(state.reportDateRangeLabel)}\",")
            appendLine("    \"generatedAt\": \"${isoFormatter.format(Date())}\"")
            appendLine("  },")
            appendLine("  \"summary\": {")
            appendLine("    \"totalDetections\": ${events.size},")
            appendLine("    \"uniqueApps\": ${events.mapNotNull { it.sourceApp }.distinct().size},")
            appendLine("    \"uniqueEntityTypes\": ${events.map { it.entityType }.distinct().size},")
            appendLine("    \"criticalCount\": ${events.count { it.severity == Severity.CRITICAL }},")
            appendLine("    \"highCount\": ${events.count { it.severity == Severity.HIGH }},")
            appendLine("    \"mediumCount\": ${events.count { it.severity == Severity.MEDIUM }},")
            val avgConf = if (events.isEmpty()) 0.0 else events.map { it.confidence }.average()
            appendLine("    \"averageConfidence\": ${String.format("%.4f", avgConf)},")
            appendLine("    \"averageRiskScore\": ${state.averageRiskScore}")
            appendLine("  },")
            appendLine("  \"entityTypeBreakdown\": [")
            state.entityTypeStats.forEachIndexed { idx, stat ->
                val isLast = idx == state.entityTypeStats.lastIndex
                appendLine("    {")
                appendLine("      \"entityType\": \"${stat.entityType.name}\",")
                appendLine("      \"displayName\": \"${jsonEscape(stat.entityType.displayName)}\",")
                appendLine("      \"count\": ${stat.count},")
                appendLine("      \"percentOfTotal\": ${String.format("%.4f", stat.percentOfTotal)},")
                appendLine("      \"averageConfidence\": ${String.format("%.4f", stat.averageConfidence)},")
                appendLine("      \"criticalCount\": ${stat.criticalCount},")
                appendLine("      \"highCount\": ${stat.highCount},")
                appendLine("      \"mediumCount\": ${stat.mediumCount}")
                append("    }")
                if (!isLast) appendLine(",") else appendLine()
            }
            appendLine("  ],")
            appendLine("  \"events\": [")
            events.sortedByDescending { it.timestamp }.forEachIndexed { idx, event ->
                val isLast = idx == events.lastIndex
                appendLine("    {")
                appendLine("      \"id\": \"${jsonEscape(event.id)}\",")
                appendLine("      \"timestamp\": ${event.timestamp},")
                appendLine("      \"timestampIso\": \"${isoFormatter.format(Date(event.timestamp))}\",")
                appendLine("      \"entityType\": \"${event.entityType.name}\",")
                appendLine("      \"entityTypeDisplay\": \"${jsonEscape(event.entityType.displayName)}\",")
                appendLine("      \"severity\": \"${event.severity.name}\",")
                appendLine("      \"confidence\": ${String.format("%.6f", event.confidence)},")
                appendLine("      \"inferenceTimeMs\": ${event.inferenceTimeMs},")
                appendLine("      \"actionTaken\": \"${event.actionTaken.name}\",")
                appendLine("      \"sourceApp\": ${if (event.sourceApp != null) "\"${jsonEscape(event.sourceApp)}\"" else "null"},")
                appendLine("      \"sourceAppName\": ${if (event.sourceAppName != null) "\"${jsonEscape(event.sourceAppName)}\"" else "null"}")
                append("    }")
                if (!isLast) appendLine(",") else appendLine()
            }
            appendLine("  ]")
            append("}")
        }
    }

    /**
     * Generates a plain-text representation of the report that approximates
     * a PDF layout. In a full implementation this would use a PDF library
     * such as iText or PdfDocument; here it returns structured text.
     *
     * @param events The filtered events to include.
     * @param state  The current UI state for metadata.
     * @return A formatted multi-line text string representing the report layout.
     */
    private fun exportAsPdfMarkup(
        events: List<DetectionEvent>,
        state: PrivacyReportUiState
    ): String {
        val displayFormatter = SimpleDateFormat(REPORT_HEADER_DATE_FORMAT, Locale.getDefault())
        val separator = "═".repeat(70)
        val thinSep = "─".repeat(70)

        return buildString {
            appendLine(separator)
            appendLine("  PRIVACYGUARD  —  PRIVACY DETECTION REPORT")
            appendLine(separator)
            appendLine()
            appendLine("  Report Period:   ${state.reportTitle}")
            appendLine("  Date Range:      ${state.reportDateRangeLabel}")
            appendLine("  Generated:       ${displayFormatter.format(Date())}")
            appendLine()
            appendLine(thinSep)
            appendLine("  EXECUTIVE SUMMARY")
            appendLine(thinSep)
            appendLine()
            appendLine("  Total Detections:       ${events.size}")
            appendLine("  Unique Source Apps:     ${events.mapNotNull { it.sourceApp }.distinct().size}")
            appendLine("  Unique PII Types:       ${events.map { it.entityType }.distinct().size}")
            appendLine("  Average Risk Score:     ${state.averageRiskScore}/100")
            appendLine("  Peak Risk Score:        ${state.peakRiskScore}/100")
            appendLine("  Average Confidence:     ${String.format("%.1f", state.averageConfidence * 100)}%")
            appendLine()

            // Severity breakdown
            state.severityBreakdown?.let { breakdown ->
                appendLine(thinSep)
                appendLine("  SEVERITY BREAKDOWN")
                appendLine(thinSep)
                appendLine()
                appendLine("  Critical:   ${breakdown.criticalCount.toString().padStart(5)} (${String.format("%5.1f", breakdown.criticalPercent * 100)}%)")
                appendLine("  High:       ${breakdown.highCount.toString().padStart(5)} (${String.format("%5.1f", breakdown.highPercent * 100)}%)")
                appendLine("  Medium:     ${breakdown.mediumCount.toString().padStart(5)} (${String.format("%5.1f", breakdown.mediumPercent * 100)}%)")
                appendLine()
            }

            // Entity type breakdown
            if (state.entityTypeStats.isNotEmpty()) {
                appendLine(thinSep)
                appendLine("  PII TYPE BREAKDOWN")
                appendLine(thinSep)
                appendLine()
                appendLine("  ${"Type".padEnd(25)} ${"Count".padStart(7)} ${"% Total".padStart(9)} ${"Avg Conf".padStart(10)}")
                appendLine("  ${"-".repeat(55)}")
                state.entityTypeStats.forEach { stat ->
                    appendLine(
                        "  ${stat.entityType.displayName.padEnd(25)} " +
                        "${stat.count.toString().padStart(7)} " +
                        "${String.format("%8.1f%%", stat.percentOfTotal * 100).padStart(9)} " +
                        "${String.format("%9.1f%%", stat.averageConfidence * 100).padStart(10)}"
                    )
                }
                appendLine()
            }

            // App breakdown
            if (state.appStats.isNotEmpty()) {
                appendLine(thinSep)
                appendLine("  SOURCE APPLICATION BREAKDOWN")
                appendLine(thinSep)
                appendLine()
                appendLine("  ${"App".padEnd(30)} ${"Count".padStart(7)} ${"% Total".padStart(9)} ${"Risk".padStart(8)}")
                appendLine("  ${"-".repeat(58)}")
                state.appStats.take(10).forEach { app ->
                    val riskLabel = app.maxSeverity.name.take(8).padStart(8)
                    appendLine(
                        "  ${app.displayName.take(30).padEnd(30)} " +
                        "${app.detectionCount.toString().padStart(7)} " +
                        "${String.format("%8.1f%%", app.percentOfTotal * 100).padStart(9)} " +
                        riskLabel
                    )
                }
                appendLine()
            }

            // Daily summary
            if (state.dailyStatPoints.isNotEmpty()) {
                appendLine(thinSep)
                appendLine("  DAILY DETECTION SUMMARY")
                appendLine(thinSep)
                appendLine()
                appendLine("  ${"Date".padEnd(12)} ${"Total".padStart(7)} ${"Critical".padStart(10)} ${"High".padStart(6)} ${"Medium".padStart(8)} ${"Risk".padStart(6)}")
                appendLine("  ${"-".repeat(53)}")
                state.dailyStatPoints.forEach { point ->
                    appendLine(
                        "  ${point.label.padEnd(12)} " +
                        "${point.count.toString().padStart(7)} " +
                        "${point.criticalCount.toString().padStart(10)} " +
                        "${point.highCount.toString().padStart(6)} " +
                        "${point.mediumCount.toString().padStart(8)} " +
                        "${point.riskScore.toString().padStart(6)}"
                    )
                }
                appendLine()
            }

            appendLine(separator)
            appendLine("  Generated by PrivacyGuard — on-device privacy protection")
            appendLine("  This report contains no raw PII data.")
            appendLine(separator)
        }
    }

    // ---------------------------------------------------------------------------
    // Share Report
    // ---------------------------------------------------------------------------

    /**
     * Generates a concise human-readable summary of the current report period
     * suitable for sharing via messaging apps, email, or notes.
     *
     * The share content is written to [PrivacyReportUiState.shareContent] for
     * the UI to deliver to Android's share sheet.
     */
    fun shareReport() {
        val state = _uiState.value
        if (!state.hasData) {
            _uiState.update {
                it.copy(shareContent = "PrivacyGuard: No detections recorded for ${state.selectedPeriod.displayName}.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSharing = true) }

            val content = generateShareableSummary(state)

            _uiState.update {
                it.copy(
                    shareContent = content,
                    isSharing = false
                )
            }
        }
    }

    /**
     * Clears the share content after the UI has delivered it to the share sheet.
     */
    fun clearShareContent() {
        _uiState.update { it.copy(shareContent = null) }
    }

    /**
     * Builds a concise but informative share string from the current UI state.
     *
     * @param state The current [PrivacyReportUiState].
     * @return A multi-line share-friendly string.
     */
    private fun generateShareableSummary(state: PrivacyReportUiState): String {
        val topType = state.topEntityType?.displayName ?: "various types"
        val breakdown = state.severityBreakdown

        return buildString {
            appendLine("PrivacyGuard Privacy Report — ${state.reportTitle}")
            appendLine()
            appendLine("Detections: ${state.totalDetections}")
            appendLine("Source Apps: ${state.uniqueAppsCount}")
            appendLine("Risk Score: ${state.averageRiskScore}/100")
            appendLine()
            if (breakdown != null && state.totalDetections > 0) {
                appendLine("Breakdown:")
                if (breakdown.criticalCount > 0) appendLine("  Critical: ${breakdown.criticalCount}")
                if (breakdown.highCount > 0) appendLine("  High: ${breakdown.highCount}")
                if (breakdown.mediumCount > 0) appendLine("  Medium: ${breakdown.mediumCount}")
                appendLine()
            }
            appendLine("Top PII Type: $topType")
            state.topRiskApp?.let { app ->
                appendLine("Most Active App: ${app.displayName} (${app.detectionCount} detections)")
            }
            appendLine()
            appendLine("Monitored by PrivacyGuard — on-device privacy protection.")
        }
    }

    // ---------------------------------------------------------------------------
    // Statistics Computation
    // ---------------------------------------------------------------------------

    /**
     * Computes per-entity-type statistics for the given list of events.
     *
     * Results are sorted by count descending so the most-detected types appear
     * first in charts and lists.
     *
     * @param currentEvents  Events in the current reporting period.
     * @param previousEvents Events in the previous equivalent period (for trend).
     * @return Sorted list of [EntityTypeStats] capped at [MAX_ENTITY_TYPE_STATS].
     */
    private fun computeEntityTypeStats(
        currentEvents: List<DetectionEvent>,
        previousEvents: List<DetectionEvent>
    ): List<EntityTypeStats> {
        if (currentEvents.isEmpty()) return emptyList()

        val total = currentEvents.size.toFloat()
        val grouped = currentEvents.groupBy { it.entityType }

        return grouped.entries
            .map { (entityType, events) ->
                val count = events.size
                val critical = events.count { it.severity == Severity.CRITICAL }
                val high = events.count { it.severity == Severity.HIGH }
                val medium = events.count { it.severity == Severity.MEDIUM }
                val avgConf = events.map { it.confidence }.average().toFloat()

                // Compute trend vs. previous period
                val prevCount = previousEvents.count { it.entityType == entityType }
                val trend = if (prevCount == 0) {
                    if (count > 0) 100f else 0f
                } else {
                    ((count - prevCount).toFloat() / prevCount) * 100f
                }

                EntityTypeStats(
                    entityType = entityType,
                    count = count,
                    percentOfTotal = count / total,
                    averageConfidence = avgConf,
                    criticalCount = critical,
                    highCount = high,
                    mediumCount = medium,
                    trend = trend
                )
            }
            .sortedByDescending { it.count }
            .take(MAX_ENTITY_TYPE_STATS)
    }

    /**
     * Computes per-application statistics from the given event list.
     *
     * Unknown-source events (where [DetectionEvent.sourceApp] is null) are
     * grouped under a synthetic "Unknown" entry.
     *
     * @param events Events to aggregate.
     * @return Sorted list of [AppStats] capped at [MAX_APP_STATS].
     */
    private fun computeAppStats(events: List<DetectionEvent>): List<AppStats> {
        if (events.isEmpty()) return emptyList()

        val total = events.size.toFloat()
        val grouped = events.groupBy { it.sourceApp ?: "unknown" }

        return grouped.entries
            .map { (packageName, appEvents) ->
                val critical = appEvents.count { it.severity == Severity.CRITICAL }
                val high = appEvents.count { it.severity == Severity.HIGH }
                val medium = appEvents.count { it.severity == Severity.MEDIUM }
                val avgConf = appEvents.map { it.confidence }.average().toFloat()
                val displayName = appEvents.firstOrNull()?.sourceAppName
                    ?: deriveDisplayName(packageName)
                val topTypes = appEvents
                    .groupBy { it.entityType }
                    .entries
                    .sortedByDescending { it.value.size }
                    .take(3)
                    .map { it.key }

                AppStats(
                    packageName = packageName,
                    displayName = displayName,
                    detectionCount = appEvents.size,
                    percentOfTotal = appEvents.size / total,
                    criticalCount = critical,
                    highCount = high,
                    mediumCount = medium,
                    topEntityTypes = topTypes,
                    firstDetectionMs = appEvents.minOfOrNull { it.timestamp } ?: 0L,
                    lastDetectionMs = appEvents.maxOfOrNull { it.timestamp } ?: 0L,
                    averageConfidence = avgConf
                )
            }
            .sortedByDescending { it.detectionCount }
            .take(MAX_APP_STATS)
    }

    /**
     * Generates one [DailyStatPoint] per calendar day within the period window.
     *
     * Days with no detections are included with zero counts so that charts
     * render a continuous x-axis.
     *
     * @param events   Events in the reporting period.
     * @param startMs  Period start boundary (epoch ms).
     * @param endMs    Period end boundary (epoch ms).
     * @return Ordered list of daily points from oldest to newest.
     */
    private fun computeDailyStatPoints(
        events: List<DetectionEvent>,
        startMs: Long,
        endMs: Long
    ): List<DailyStatPoint> {
        val points = mutableListOf<DailyStatPoint>()

        // Determine label format based on window size
        val windowDays = ((endMs - startMs) / MILLIS_PER_DAY).toInt() + 1
        val labelFormat = if (windowDays <= 7) DAY_OF_WEEK_FORMAT else MONTH_DAY_FORMAT
        val labelFormatter = SimpleDateFormat(labelFormat, Locale.getDefault())

        // Snap start to midnight UTC
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = startMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endDayMs = run {
            val endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = endMs
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            endCal.timeInMillis
        }

        while (cal.timeInMillis <= endDayMs) {
            val dayStart = cal.timeInMillis
            val dayEnd = dayStart + MILLIS_PER_DAY - 1

            val dayEvents = events.filter { it.timestamp in dayStart..dayEnd }
            val critical = dayEvents.count { it.severity == Severity.CRITICAL }
            val high = dayEvents.count { it.severity == Severity.HIGH }
            val medium = dayEvents.count { it.severity == Severity.MEDIUM }

            val riskScore = computeDailyRiskScore(critical, high, medium)
            val label = labelFormatter.format(Date(dayStart))

            points.add(
                DailyStatPoint(
                    epochDayMs = dayStart,
                    label = label,
                    count = dayEvents.size,
                    criticalCount = critical,
                    highCount = high,
                    mediumCount = medium,
                    riskScore = riskScore
                )
            )

            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return points
    }

    /**
     * Derives the risk trend line data from daily stat points.
     *
     * The risk trend applies a simple 3-day moving average to smooth the
     * per-day risk scores, reducing the visual noise of single-day spikes.
     *
     * @param dailyPoints The ordered list of daily stat points.
     * @return An equally-sized list of [RiskTrendPoint] with smoothed risk scores.
     */
    private fun computeRiskTrend(dailyPoints: List<DailyStatPoint>): List<RiskTrendPoint> {
        if (dailyPoints.isEmpty()) return emptyList()

        return dailyPoints.mapIndexed { index, point ->
            // 3-day moving average window
            val windowStart = max(0, index - 1)
            val windowEnd = min(dailyPoints.lastIndex, index + 1)
            val window = dailyPoints.subList(windowStart, windowEnd + 1)
            val smoothedRisk = window.map { it.riskScore }.average().roundToInt()

            RiskTrendPoint(
                epochMs = point.epochDayMs,
                label = point.label,
                riskScore = smoothedRisk
            )
        }
    }

    /**
     * Computes the severity breakdown (counts and percentages) for the given events.
     *
     * @param events The events to analyze.
     * @return A [SeverityBreakdown] instance, or null if [events] is empty.
     */
    private fun computeSeverityBreakdown(events: List<DetectionEvent>): SeverityBreakdown? {
        if (events.isEmpty()) return null

        val total = events.size
        val critical = events.count { it.severity == Severity.CRITICAL }
        val high = events.count { it.severity == Severity.HIGH }
        val medium = events.count { it.severity == Severity.MEDIUM }

        return SeverityBreakdown(
            criticalCount = critical,
            highCount = high,
            mediumCount = medium,
            totalCount = total,
            criticalPercent = critical.toFloat() / total,
            highPercent = high.toFloat() / total,
            mediumPercent = medium.toFloat() / total
        )
    }

    /**
     * Computes action distribution statistics for the given events.
     *
     * @param events The events to analyze.
     * @return An [ActionDistribution] instance, or null if [events] is empty.
     */
    private fun computeActionDistribution(events: List<DetectionEvent>): ActionDistribution? {
        if (events.isEmpty()) return null

        return ActionDistribution(
            clipboardClearedCount = events.count { it.actionTaken == UserAction.CLIPBOARD_CLEARED },
            dismissedCount = events.count { it.actionTaken == UserAction.DISMISSED },
            whitelistedCount = events.count { it.actionTaken == UserAction.WHITELISTED_APP },
            autoDismissedCount = events.count { it.actionTaken == UserAction.AUTO_DISMISSED },
            noActionCount = events.count { it.actionTaken == UserAction.NO_ACTION },
            totalCount = events.size
        )
    }

    /**
     * Computes a comparison between the current period and the previous equivalent window.
     *
     * @param currentEvents  Events in the current period.
     * @param previousEvents Events in the previous equivalent period.
     * @return A [WeeklyComparison] capturing the delta, or null if both are empty.
     */
    private fun computeWeeklyComparison(
        currentEvents: List<DetectionEvent>,
        previousEvents: List<DetectionEvent>
    ): WeeklyComparison? {
        val currentCount = currentEvents.size
        val prevCount = previousEvents.size

        if (currentCount == 0 && prevCount == 0) return null

        val countChange = if (prevCount == 0) {
            if (currentCount > 0) 100f else 0f
        } else {
            ((currentCount - prevCount).toFloat() / prevCount) * 100f
        }

        val currentRisk = computeAverageRiskScore(currentEvents)
        val prevRisk = computeAverageRiskScore(previousEvents)
        val riskChange = currentRisk - prevRisk

        return WeeklyComparison(
            currentPeriodCount = currentCount,
            previousPeriodCount = prevCount,
            countChangePercent = countChange,
            currentPeriodRiskScore = currentRisk,
            previousPeriodRiskScore = prevRisk,
            riskScoreChange = riskChange,
            isImproving = countChange <= 0f && riskChange <= 0
        )
    }

    /**
     * Computes an overall average risk score for the given event list.
     *
     * The risk score is a weighted sum based on severity distribution:
     * - Critical events contribute 25 points each (capped at 100)
     * - High events contribute 15 points each
     * - Medium events contribute 5 points each
     *
     * @param events The events to score.
     * @return An integer in the range [0, 100].
     */
    private fun computeAverageRiskScore(events: List<DetectionEvent>): Int {
        if (events.isEmpty()) return 0

        val critical = events.count { it.severity == Severity.CRITICAL }
        val high = events.count { it.severity == Severity.HIGH }
        val medium = events.count { it.severity == Severity.MEDIUM }

        val rawScore = critical * 25 + high * 15 + medium * 5
        return rawScore.coerceIn(0, 100)
    }

    /**
     * Computes the average model confidence across all events.
     *
     * @param events The events to average.
     * @return Mean confidence as a float in [0.0, 1.0], or 0.0 if empty.
     */
    private fun computeAverageConfidence(events: List<DetectionEvent>): Float {
        if (events.isEmpty()) return 0f
        return events.map { it.confidence }.average().toFloat()
    }

    /**
     * Computes a risk score for a single calendar day based on detection severity counts.
     *
     * @param critical Number of critical-severity detections on the day.
     * @param high     Number of high-severity detections.
     * @param medium   Number of medium-severity detections.
     * @return An integer in [0, 100].
     */
    private fun computeDailyRiskScore(critical: Int, high: Int, medium: Int): Int {
        val raw = BASE_DAILY_RISK +
            critical * CRITICAL_DAILY_PENALTY +
            high * HIGH_DAILY_PENALTY +
            medium * MEDIUM_DAILY_PENALTY
        return raw.coerceIn(0, 100)
    }

    // ---------------------------------------------------------------------------
    // Period Bounds
    // ---------------------------------------------------------------------------

    /**
     * Resolves the [startMs] and [endMs] epoch boundaries for the current
     * reporting period based on the UI state.
     *
     * @param state The current [PrivacyReportUiState].
     * @param now   The current epoch ms (used as the end boundary for non-CUSTOM periods).
     * @return A [Pair] of (startMs, endMs).
     */
    private fun getActivePeriodBounds(
        state: PrivacyReportUiState,
        now: Long
    ): Pair<Long, Long> {
        return when (state.selectedPeriod) {
            ReportPeriod.CUSTOM -> {
                if (state.isCustomRangeValid) {
                    Pair(state.customStartMs, state.customEndMs)
                } else {
                    // Fall back to the week window if custom range is not configured
                    Pair(now - MILLIS_PER_WEEK, now)
                }
            }
            else -> {
                Pair(now - state.selectedPeriod.durationMs, now)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Label and Title Generation
    // ---------------------------------------------------------------------------

    /**
     * Generates a human-readable report title that describes the selected period.
     *
     * @param period  The active report period.
     * @param startMs Period start epoch ms.
     * @param endMs   Period end epoch ms.
     * @return A title string, e.g., "Report: Last 7 Days" or "Report: Mar 1 – Mar 7".
     */
    private fun generateReportTitle(
        period: ReportPeriod,
        startMs: Long,
        endMs: Long
    ): String {
        if (period == ReportPeriod.CUSTOM) {
            val dateRangeFmt = SimpleDateFormat(REPORT_HEADER_DATE_FORMAT, Locale.getDefault())
            return "Custom Report: ${dateRangeFmt.format(Date(startMs))} – ${dateRangeFmt.format(Date(endMs))}"
        }
        return "Report: ${period.displayName}"
    }

    /**
     * Formats a period's start/end epoch pair into a compact date range label
     * suitable for display in the report header bar.
     *
     * @param startMs Period start (epoch ms).
     * @param endMs   Period end (epoch ms).
     * @return A string like "Mar 5 – Mar 12" or "Today".
     */
    private fun formatDateRange(startMs: Long, endMs: Long): String {
        val now = System.currentTimeMillis()
        val rangeFormatter = SimpleDateFormat(DATE_RANGE_FORMAT, Locale.getDefault())

        val startLabel = rangeFormatter.format(Date(startMs))
        val endLabel = rangeFormatter.format(Date(min(endMs, now)))

        return if (startLabel == endLabel) startLabel else "$startLabel – $endLabel"
    }

    // ---------------------------------------------------------------------------
    // Utility Helpers
    // ---------------------------------------------------------------------------

    /**
     * Derives a display name from a package name.
     *
     * @param packageName The Android package identifier.
     * @return A capitalized last segment of the package name.
     */
    private fun deriveDisplayName(packageName: String): String {
        if (packageName == "unknown") return "Unknown App"
        val last = packageName.substringAfterLast('.')
        return last.replaceFirstChar { it.uppercaseChar() }
    }

    /**
     * Escapes a field value for safe inclusion in a CSV cell.
     *
     * @param value The raw string value.
     * @return A CSV-safe string, quoted if necessary.
     */
    private fun csvEscape(value: String): String {
        if (value.contains(',') || value.contains('"') || value.contains('\n') || value.contains('\r')) {
            return "\"${value.replace("\"", "\"\"")}\""
        }
        return value
    }

    /**
     * Escapes a value for safe embedding in a JSON string.
     *
     * @param value The raw string value.
     * @return A JSON-safe escaped string (without surrounding quotes).
     */
    private fun jsonEscape(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    /**
     * Clears any active error message from the UI state.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ---------------------------------------------------------------------------
    // Auto-Refresh
    // ---------------------------------------------------------------------------

    /**
     * Starts a background coroutine that periodically re-fetches data from the
     * repository and refreshes all computed statistics.
     *
     * Only one auto-refresh job runs at a time; calling this method while a job
     * is already running first cancels the existing job.
     */
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(AUTO_REFRESH_INTERVAL_MS)
                if (!_uiState.value.isLoading && !_uiState.value.isRefreshing) {
                    refreshStats()
                }
            }
        }
    }

    /**
     * Cancels the auto-refresh background job.
     * Can be called to pause updates (e.g., when the screen is not visible).
     */
    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    /**
     * Restarts auto-refresh with a custom interval.
     *
     * @param intervalMs The refresh interval in milliseconds. Minimum 10 seconds.
     */
    fun setRefreshInterval(intervalMs: Long) {
        autoRefreshJob?.cancel()
        val coercedInterval = intervalMs.coerceAtLeast(10_000L)
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(coercedInterval)
                if (!_uiState.value.isLoading && !_uiState.value.isRefreshing) {
                    refreshStats()
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
}
