package com.privacyguard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.ModelState
import com.privacyguard.ml.PrivacyModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isProtectionActive: Boolean = false,
    val protectionScore: Int = 100,
    val detectionsToday: Int = 0,
    val detectionsThisWeek: Int = 0,
    val mostCommonType: EntityType? = null,
    val recentDetections: List<DetectionEvent> = emptyList(),
    val inferenceLatencyMs: Long = 0L,
    val modelState: ModelState = ModelState.Initializing,
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayPermissionGranted: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,
    val totalDetections: Long = 0L,
    val totalInferences: Long = 0L
)

class DashboardViewModel : ViewModel() {

    private val model = PrivacyModel.getInstance()
    private var logRepository: EncryptedLogRepository? = null

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isMonitoringActive = MutableStateFlow(false)
    val isMonitoringActive: StateFlow<Boolean> = _isMonitoringActive.asStateFlow()

    init {
        observeModelState()
        observeInferenceLatency()
    }

    fun setRepository(repo: EncryptedLogRepository) {
        logRepository = repo
        observeDetections()
    }

    fun setMonitoringActive(active: Boolean) {
        _isMonitoringActive.value = active
        _uiState.update { it.copy(isProtectionActive = active) }
    }

    fun setAccessibilityEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isAccessibilityEnabled = enabled) }
    }

    fun setOverlayPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(isOverlayPermissionGranted = granted) }
    }

    fun setNotificationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(isNotificationPermissionGranted = granted) }
    }

    private fun observeModelState() {
        viewModelScope.launch {
            model.modelState.collect { state ->
                _uiState.update { it.copy(modelState = state) }
            }
        }
    }

    private fun observeInferenceLatency() {
        viewModelScope.launch {
            model.lastInferenceLatencyMs.collect { latency ->
                _uiState.update { it.copy(inferenceLatencyMs = latency) }
            }
        }
        viewModelScope.launch {
            model.totalInferences.collect { total ->
                _uiState.update { it.copy(totalInferences = total) }
            }
        }
    }

    private fun observeDetections() {
        viewModelScope.launch {
            logRepository?.getAllEvents()?.collect { events ->
                val today = events.count {
                    it.timestamp >= System.currentTimeMillis() - 24 * 60 * 60 * 1000
                }
                val week = events.count {
                    it.timestamp >= System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
                }
                val mostCommon = events
                    .groupBy { it.entityType }
                    .maxByOrNull { it.value.size }
                    ?.key

                val score = calculateProtectionScore(events)

                _uiState.update {
                    it.copy(
                        detectionsToday = today,
                        detectionsThisWeek = week,
                        mostCommonType = mostCommon,
                        recentDetections = events.take(5),
                        protectionScore = score,
                        totalDetections = events.size.toLong()
                    )
                }
            }
        }
    }

    private fun calculateProtectionScore(events: List<DetectionEvent>): Int {
        if (!_isMonitoringActive.value) return 0
        val recentCount = events.count {
            it.timestamp >= System.currentTimeMillis() - 24 * 60 * 60 * 1000
        }
        return (100 - (recentCount * 5)).coerceIn(0, 100)
    }
}
