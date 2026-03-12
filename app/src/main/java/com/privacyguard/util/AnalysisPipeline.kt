package com.privacyguard.util

import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.UserAction
import com.privacyguard.data.WhitelistManager
import com.privacyguard.ml.PIIAnalysisResult
import com.privacyguard.ml.PIIEntity
import com.privacyguard.ml.PrivacyModel
import com.privacyguard.ml.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Central analysis pipeline tying together monitoring, inference, alerting, and logging.
 *
 * Flow: whitelist check → debounce → inference → alert → log
 */
class AnalysisPipeline(
    private val model: PrivacyModel,
    private val whitelistManager: WhitelistManager,
    private val logRepository: EncryptedLogRepository,
    private val alertCallback: ((PIIAnalysisResult, String?) -> Unit)? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    debounceDelayMs: Long = Debouncer.DEFAULT_DELAY_MS
) {
    companion object {
        const val MIN_TEXT_LENGTH = 5
        const val MAX_TEXT_LENGTH = 10000
        const val CIRCUIT_BREAKER_THRESHOLD = 5
        const val CIRCUIT_BREAKER_RESET_MS = 60_000L
    }

    private val debouncer = Debouncer(scope, debounceDelayMs)

    // Circuit breaker state
    private var consecutiveFailures = 0
    private var circuitBreakerOpenUntil = 0L

    // Metrics
    private val _analysisEvents = MutableSharedFlow<AnalysisEvent>(extraBufferCapacity = 64)
    val analysisEvents: SharedFlow<AnalysisEvent> = _analysisEvents.asSharedFlow()

    /**
     * Process text from monitoring services.
     * This is the single entry point for all clipboard and accessibility events.
     */
    fun processText(text: String, sourceApp: String?) {
        // Whitelist check first
        if (!sourceApp.isNullOrEmpty() && whitelistManager.isWhitelisted(sourceApp)) {
            return
        }

        // Self-exclusion
        if (sourceApp == "com.privacyguard") {
            return
        }

        // Min/max length check
        if (text.length < MIN_TEXT_LENGTH) return
        val processedText = if (text.length > MAX_TEXT_LENGTH) {
            text.substring(0, MAX_TEXT_LENGTH)
        } else text

        // Circuit breaker check
        if (isCircuitBreakerOpen()) return

        // Debounce then analyze
        debouncer.debounce {
            analyzeAndAlert(processedText, sourceApp)
        }
    }

    /**
     * Run analysis immediately without debouncing (for testing).
     */
    suspend fun analyzeImmediate(text: String, sourceApp: String?): PIIAnalysisResult {
        if (!sourceApp.isNullOrEmpty() && whitelistManager.isWhitelisted(sourceApp)) {
            return PIIAnalysisResult.EMPTY
        }
        if (text.length < MIN_TEXT_LENGTH) return PIIAnalysisResult.EMPTY

        return try {
            val result = model.analyzeText(text)
            onAnalysisSuccess(result, sourceApp)
            result
        } catch (e: Exception) {
            onAnalysisFailure(e)
            PIIAnalysisResult.EMPTY
        }
    }

    private suspend fun analyzeAndAlert(text: String, sourceApp: String?) {
        try {
            val result = model.analyzeText(text)
            onAnalysisSuccess(result, sourceApp)
        } catch (e: Exception) {
            onAnalysisFailure(e)
        }
    }

    private fun onAnalysisSuccess(result: PIIAnalysisResult, sourceApp: String?) {
        consecutiveFailures = 0

        if (result.hasSensitiveData()) {
            // Fire alert
            alertCallback?.invoke(result, sourceApp)

            // Log each entity as a separate event
            result.entities.forEach { entity ->
                val event = DetectionEvent(
                    entityType = entity.entityType,
                    severity = entity.severity,
                    sourceApp = sourceApp,
                    confidence = entity.confidence,
                    inferenceTimeMs = result.inferenceTimeMs
                )
                logRepository.record(event)
            }

            scope.launch {
                _analysisEvents.emit(AnalysisEvent.Detection(result, sourceApp))
            }
        } else {
            scope.launch {
                _analysisEvents.emit(AnalysisEvent.Clean(result.inferenceTimeMs))
            }
        }
    }

    private fun onAnalysisFailure(e: Exception) {
        consecutiveFailures++
        if (consecutiveFailures >= CIRCUIT_BREAKER_THRESHOLD) {
            circuitBreakerOpenUntil = System.currentTimeMillis() + CIRCUIT_BREAKER_RESET_MS
        }
        scope.launch {
            _analysisEvents.emit(AnalysisEvent.Error(e.message ?: "Unknown error"))
        }
    }

    private fun isCircuitBreakerOpen(): Boolean {
        if (circuitBreakerOpenUntil <= 0) return false
        return if (System.currentTimeMillis() < circuitBreakerOpenUntil) {
            true
        } else {
            circuitBreakerOpenUntil = 0L
            consecutiveFailures = 0
            false
        }
    }

    fun getConsecutiveFailures(): Int = consecutiveFailures

    fun isCircuitBreakerTripped(): Boolean = isCircuitBreakerOpen()

    fun resetCircuitBreaker() {
        consecutiveFailures = 0
        circuitBreakerOpenUntil = 0L
    }

    fun cancel() {
        debouncer.cancel()
    }
}

sealed class AnalysisEvent {
    data class Detection(val result: PIIAnalysisResult, val sourceApp: String?) : AnalysisEvent()
    data class Clean(val inferenceTimeMs: Long) : AnalysisEvent()
    data class Error(val message: String) : AnalysisEvent()
}
