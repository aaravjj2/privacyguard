package com.privacyguard.ui.alert

import com.privacyguard.ml.PIIAnalysisResult
import com.privacyguard.ml.Severity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Manages alert display logic with severity routing, queuing, and cooldown.
 */
class AlertManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    companion object {
        const val COOLDOWN_MS = 30_000L // 30 seconds cooldown per entity+app
        const val AUTO_DISMISS_MS = 10_000L // 10 seconds auto-dismiss for critical
        const val TOAST_DURATION_MS = 4_000L // 4 seconds for medium
    }

    private val _currentAlert = MutableStateFlow<AlertState>(AlertState.None)
    val currentAlert: StateFlow<AlertState> = _currentAlert.asStateFlow()

    private val alertQueue = ConcurrentLinkedQueue<QueuedAlert>()
    private val cooldownMap = mutableMapOf<String, Long>()
    private var autoDismissJob: Job? = null
    private var isProcessingQueue = false

    // Callback for alert actions
    var onClearClipboard: (() -> Unit)? = null
    var onShowOverlay: ((PIIAnalysisResult, String?) -> Unit)? = null
    var onShowBanner: ((PIIAnalysisResult, String?) -> Unit)? = null
    var onShowToast: ((PIIAnalysisResult, String?) -> Unit)? = null

    /**
     * Show an alert based on the analysis result severity.
     */
    fun show(result: PIIAnalysisResult, sourceApp: String?) {
        if (!result.hasSensitiveData()) return

        val severity = result.highestSeverity ?: return

        // Check cooldown
        val cooldownKey = "${result.entities.firstOrNull()?.entityType?.name}_$sourceApp"
        if (isOnCooldown(cooldownKey)) return

        // Set cooldown
        cooldownMap[cooldownKey] = System.currentTimeMillis() + COOLDOWN_MS

        // Queue the alert
        alertQueue.add(QueuedAlert(result, sourceApp, severity))

        // Process queue
        processNextAlert()
    }

    /**
     * Dismiss the current alert and process the next in queue.
     */
    fun dismiss() {
        autoDismissJob?.cancel()
        _currentAlert.value = AlertState.None
        isProcessingQueue = false

        // Process next in queue after a brief delay
        scope.launch {
            delay(300)
            processNextAlert()
        }
    }

    /**
     * Handle the "Clear Clipboard" action.
     */
    fun handleClearClipboard() {
        onClearClipboard?.invoke()
        dismiss()
    }

    /**
     * Handle the "Add to Whitelist" action.
     */
    fun handleWhitelist(sourceApp: String?, onWhitelist: (String) -> Unit) {
        sourceApp?.let { onWhitelist(it) }
        dismiss()
    }

    private fun processNextAlert() {
        if (isProcessingQueue || _currentAlert.value.isActive) return

        val next = alertQueue.poll() ?: return
        isProcessingQueue = true

        when (next.severity) {
            Severity.CRITICAL -> showCriticalOverlay(next.result, next.sourceApp)
            Severity.HIGH -> showHighBanner(next.result, next.sourceApp)
            Severity.MEDIUM -> showMediumToast(next.result, next.sourceApp)
        }
    }

    private fun showCriticalOverlay(result: PIIAnalysisResult, sourceApp: String?) {
        _currentAlert.value = AlertState.Critical(result, sourceApp)
        onShowOverlay?.invoke(result, sourceApp)

        // Auto-dismiss countdown
        autoDismissJob?.cancel()
        autoDismissJob = scope.launch {
            for (i in 10 downTo 1) {
                val current = _currentAlert.value
                if (current is AlertState.Critical) {
                    _currentAlert.value = current.copy(countdown = i)
                }
                delay(1000)
            }
            dismiss()
        }
    }

    private fun showHighBanner(result: PIIAnalysisResult, sourceApp: String?) {
        _currentAlert.value = AlertState.High(result, sourceApp)
        onShowBanner?.invoke(result, sourceApp)
    }

    private fun showMediumToast(result: PIIAnalysisResult, sourceApp: String?) {
        _currentAlert.value = AlertState.Medium(result, sourceApp)
        onShowToast?.invoke(result, sourceApp)

        // Auto-dismiss medium alerts
        autoDismissJob?.cancel()
        autoDismissJob = scope.launch {
            delay(TOAST_DURATION_MS)
            dismiss()
        }
    }

    private fun isOnCooldown(key: String): Boolean {
        val cooldownUntil = cooldownMap[key] ?: return false
        return if (System.currentTimeMillis() < cooldownUntil) {
            true
        } else {
            cooldownMap.remove(key)
            false
        }
    }

    fun clearCooldowns() {
        cooldownMap.clear()
    }

    fun getQueueSize(): Int = alertQueue.size

    private data class QueuedAlert(
        val result: PIIAnalysisResult,
        val sourceApp: String?,
        val severity: Severity
    )
}
