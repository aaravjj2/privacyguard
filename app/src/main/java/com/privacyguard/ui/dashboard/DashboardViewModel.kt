package com.privacyguard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.ModelState
import com.privacyguard.ml.PrivacyModel
import com.privacyguard.ml.Severity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * UI state representing the entire dashboard view.
 * Immutable data class supporting Compose recomposition.
 */
data class DashboardUiState(
    // Protection status
    val isProtectionActive: Boolean = false,
    val protectionScore: Int = 100,

    // Detection statistics
    val detectionsToday: Int = 0,
    val detectionsThisWeek: Int = 0,
    val detectionsThisMonth: Int = 0,
    val totalDetections: Long = 0L,
    val mostCommonType: EntityType? = null,
    val recentDetections: List<DetectionEvent> = emptyList(),

    // Detection breakdown by severity
    val criticalCount: Int = 0,
    val highCount: Int = 0,
    val mediumCount: Int = 0,

    // Model and inference state
    val inferenceLatencyMs: Long = 0L,
    val averageLatencyMs: Long = 0L,
    val modelState: ModelState = ModelState.Initializing,
    val totalInferences: Long = 0L,

    // Permissions
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayPermissionGranted: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,

    // UI state
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val lastRefreshTimestamp: Long = 0L
) {
    /**
     * The number of permissions that still need to be granted.
     */
    val missingPermissionsCount: Int
        get() = listOf(
            isAccessibilityEnabled,
            isOverlayPermissionGranted,
            isNotificationPermissionGranted
        ).count { !it }

    /**
     * Whether all required permissions are granted.
     */
    val allPermissionsGranted: Boolean
        get() = isAccessibilityEnabled && isOverlayPermissionGranted && isNotificationPermissionGranted

    /**
     * Whether the model is in a usable state.
     */
    val isModelOperational: Boolean
        get() = modelState is ModelState.Ready || modelState is ModelState.Running

    /**
     * Protection level label derived from the score.
     */
    val protectionLevel: ProtectionLevel
        get() = when {
            !isProtectionActive -> ProtectionLevel.OFF
            protectionScore >= 90 -> ProtectionLevel.EXCELLENT
            protectionScore >= 70 -> ProtectionLevel.GOOD
            protectionScore >= 50 -> ProtectionLevel.FAIR
            else -> ProtectionLevel.NEEDS_ATTENTION
        }
}

/**
 * Enum representing the qualitative protection level.
 */
enum class ProtectionLevel(val label: String) {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    NEEDS_ATTENTION("Needs Attention"),
    OFF("Off")
}

/**
 * ViewModel managing all dashboard state, including protection score
 * calculation, detection statistics, model state observation,
 * and timer-based periodic refresh.
 */
class DashboardViewModel : ViewModel() {

    companion object {
        /** Number of recent detections to show on the dashboard. */
        private const val RECENT_DETECTIONS_LIMIT = 5

        /** How often to refresh statistics (in milliseconds). */
        private const val REFRESH_INTERVAL_MS = 30_000L

        /** Millis in one day. */
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

        /** Millis in one week. */
        private const val MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY

        /** Millis in 30 days (approximate month). */
        private const val MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY

        /** Base score when monitoring is active with no detections. */
        private const val BASE_SCORE = 100

        /** Points deducted per critical detection in the last 24h. */
        private const val CRITICAL_PENALTY = 15

        /** Points deducted per high detection in the last 24h. */
        private const val HIGH_PENALTY = 8

        /** Points deducted per medium detection in the last 24h. */
        private const val MEDIUM_PENALTY = 3

        /** Score boost when all permissions are granted. */
        private const val PERMISSION_BOOST = 5

        /** Score penalty per missing permission. */
        private const val MISSING_PERMISSION_PENALTY = 10
    }

    private val model = PrivacyModel.getInstance()
    private var logRepository: EncryptedLogRepository? = null

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isMonitoringActive = MutableStateFlow(false)
    val isMonitoringActive: StateFlow<Boolean> = _isMonitoringActive.asStateFlow()

    // Tracks latency readings for computing average
    private val latencyHistory = mutableListOf<Long>()
    private val maxLatencyHistorySize = 50

    // Job for periodic refresh
    private var refreshJob: Job? = null

    init {
        observeModelState()
        observeInferenceLatency()
        startPeriodicRefresh()
    }

    // ---------------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------------

    /**
     * Connect a log repository to the view model for detection observation.
     */
    fun setRepository(repo: EncryptedLogRepository) {
        if (logRepository == repo) return
        logRepository = repo
        observeDetections()
        _uiState.update { it.copy(isLoading = false) }
    }

    /**
     * Toggle the monitoring active state.
     */
    fun setMonitoringActive(active: Boolean) {
        _isMonitoringActive.value = active
        _uiState.update { it.copy(isProtectionActive = active) }
        recalculateProtectionScore()
    }

    /**
     * Update the accessibility service permission state.
     */
    fun setAccessibilityEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isAccessibilityEnabled = enabled) }
        recalculateProtectionScore()
    }

    /**
     * Update the overlay permission state.
     */
    fun setOverlayPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(isOverlayPermissionGranted = granted) }
        recalculateProtectionScore()
    }

    /**
     * Update the notification permission state.
     */
    fun setNotificationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(isNotificationPermissionGranted = granted) }
        recalculateProtectionScore()
    }

    /**
     * Force a refresh of all statistics from the repository.
     */
    fun refreshStats() {
        logRepository?.let { repo ->
            viewModelScope.launch {
                repo.getAllEvents().first().let { events ->
                    updateStatsFromEvents(events)
                }
            }
        }
    }

    /**
     * Retry initialization after an error state.
     */
    fun retryInitialization() {
        _uiState.update { it.copy(errorMessage = null, isLoading = true) }
        observeModelState()
        observeInferenceLatency()
        logRepository?.let { observeDetections() }

        viewModelScope.launch {
            delay(1000)
            if (_uiState.value.isLoading) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Model State Observation
    // ---------------------------------------------------------------------------

    private fun observeModelState() {
        viewModelScope.launch {
            model.modelState.collect { state ->
                _uiState.update { current ->
                    current.copy(
                        modelState = state,
                        errorMessage = when (state) {
                            is ModelState.Error -> state.message
                            else -> current.errorMessage
                        }
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Inference Latency Observation
    // ---------------------------------------------------------------------------

    private fun observeInferenceLatency() {
        viewModelScope.launch {
            model.lastInferenceLatencyMs.collect { latency ->
                if (latency > 0) {
                    trackLatency(latency)
                }
                _uiState.update {
                    it.copy(
                        inferenceLatencyMs = latency,
                        averageLatencyMs = calculateAverageLatency()
                    )
                }
            }
        }
        viewModelScope.launch {
            model.totalInferences.collect { total ->
                _uiState.update { it.copy(totalInferences = total) }
            }
        }
    }

    /**
     * Track a latency sample for running average computation.
     */
    private fun trackLatency(latencyMs: Long) {
        synchronized(latencyHistory) {
            latencyHistory.add(latencyMs)
            if (latencyHistory.size > maxLatencyHistorySize) {
                latencyHistory.removeAt(0)
            }
        }
    }

    /**
     * Calculate average inference latency from recent samples.
     */
    private fun calculateAverageLatency(): Long {
        return synchronized(latencyHistory) {
            if (latencyHistory.isEmpty()) 0L
            else latencyHistory.sum() / latencyHistory.size
        }
    }

    // ---------------------------------------------------------------------------
    // Detection Observation
    // ---------------------------------------------------------------------------

    private fun observeDetections() {
        viewModelScope.launch {
            logRepository?.getAllEvents()?.collect { events ->
                updateStatsFromEvents(events)
            }
        }
    }

    /**
     * Recompute all statistics from a fresh list of detection events.
     */
    private fun updateStatsFromEvents(events: List<DetectionEvent>) {
        val now = System.currentTimeMillis()

        val todayCutoff = now - MILLIS_PER_DAY
        val weekCutoff = now - MILLIS_PER_WEEK
        val monthCutoff = now - MILLIS_PER_MONTH

        val today = events.count { it.timestamp >= todayCutoff }
        val week = events.count { it.timestamp >= weekCutoff }
        val month = events.count { it.timestamp >= monthCutoff }

        val mostCommon = events
            .groupBy { it.entityType }
            .maxByOrNull { it.value.size }
            ?.key

        // Severity breakdown (last 24h for score calculation)
        val recentEvents = events.filter { it.timestamp >= todayCutoff }
        val criticalCount = recentEvents.count { it.severity == Severity.CRITICAL }
        val highCount = recentEvents.count { it.severity == Severity.HIGH }
        val mediumCount = recentEvents.count { it.severity == Severity.MEDIUM }

        val score = calculateProtectionScore(criticalCount, highCount, mediumCount)

        _uiState.update {
            it.copy(
                detectionsToday = today,
                detectionsThisWeek = week,
                detectionsThisMonth = month,
                mostCommonType = mostCommon,
                recentDetections = events.take(RECENT_DETECTIONS_LIMIT),
                protectionScore = score,
                totalDetections = events.size.toLong(),
                criticalCount = criticalCount,
                highCount = highCount,
                mediumCount = mediumCount,
                lastRefreshTimestamp = System.currentTimeMillis(),
                isLoading = false
            )
        }
    }

    // ---------------------------------------------------------------------------
    // Protection Score Calculation
    // ---------------------------------------------------------------------------

    /**
     * Calculate a protection score from 0-100 based on:
     * - Whether monitoring is active
     * - Recent detection severity counts
     * - Permission state
     */
    private fun calculateProtectionScore(
        criticalCount: Int = _uiState.value.criticalCount,
        highCount: Int = _uiState.value.highCount,
        mediumCount: Int = _uiState.value.mediumCount
    ): Int {
        if (!_isMonitoringActive.value) return 0

        var score = BASE_SCORE

        // Deduct for recent detections by severity
        score -= criticalCount * CRITICAL_PENALTY
        score -= highCount * HIGH_PENALTY
        score -= mediumCount * MEDIUM_PENALTY

        // Permission adjustments
        val currentState = _uiState.value
        val missingPermissions = listOf(
            currentState.isAccessibilityEnabled,
            currentState.isOverlayPermissionGranted,
            currentState.isNotificationPermissionGranted
        ).count { !it }

        if (missingPermissions == 0) {
            score += PERMISSION_BOOST
        } else {
            score -= missingPermissions * MISSING_PERMISSION_PENALTY
        }

        // Model state adjustment
        when (model.modelState.value) {
            is ModelState.Error -> score -= 20
            is ModelState.Closed -> score -= 15
            is ModelState.Initializing -> score -= 5
            else -> { /* no adjustment */ }
        }

        return score.coerceIn(0, 100)
    }

    /**
     * Recalculate protection score with current state values.
     */
    private fun recalculateProtectionScore() {
        val newScore = calculateProtectionScore()
        _uiState.update { it.copy(protectionScore = newScore) }
    }

    // ---------------------------------------------------------------------------
    // Periodic Refresh
    // ---------------------------------------------------------------------------

    /**
     * Start a coroutine that refreshes statistics at a fixed interval.
     * This ensures the dashboard stays current even if no new events arrive.
     */
    private fun startPeriodicRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(REFRESH_INTERVAL_MS)
                refreshStats()
                recalculateProtectionScore()
            }
        }
    }

    /**
     * Stop the periodic refresh coroutine.
     */
    fun stopPeriodicRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    /**
     * Restart the periodic refresh with a custom interval.
     */
    fun setRefreshInterval(intervalMs: Long) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(intervalMs.coerceAtLeast(5_000L))
                refreshStats()
                recalculateProtectionScore()
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Statistics Queries
    // ---------------------------------------------------------------------------

    /**
     * Get detection events grouped by entity type with counts.
     */
    fun getDetectionBreakdown(): Map<EntityType, Int> {
        return _uiState.value.recentDetections
            .groupBy { it.entityType }
            .mapValues { it.value.size }
    }

    /**
     * Get the average confidence across recent detections.
     */
    fun getAverageConfidence(): Float {
        val detections = _uiState.value.recentDetections
        if (detections.isEmpty()) return 0f
        return detections.map { it.confidence }.average().toFloat()
    }

    /**
     * Check whether the protection status has degraded since the last check.
     */
    fun hasProtectionDegraded(): Boolean {
        val state = _uiState.value
        return state.isProtectionActive && state.protectionScore < 50
    }

    /**
     * Get a summary string for the current protection state.
     */
    fun getProtectionSummary(): String {
        val state = _uiState.value
        return buildString {
            append("Score: ${state.protectionScore}/100")
            append(" | Level: ${state.protectionLevel.label}")
            append(" | Today: ${state.detectionsToday} detections")
            if (state.missingPermissionsCount > 0) {
                append(" | ${state.missingPermissionsCount} permissions missing")
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
