package com.privacyguard.ui.alert

import com.privacyguard.ml.PIIAnalysisResult

/**
 * Sealed class representing the current alert state.
 */
sealed class AlertState {
    data class Critical(
        val result: PIIAnalysisResult,
        val sourceApp: String?,
        val countdown: Int = 10
    ) : AlertState()

    data class High(
        val result: PIIAnalysisResult,
        val sourceApp: String?
    ) : AlertState()

    data class Medium(
        val result: PIIAnalysisResult,
        val sourceApp: String?
    ) : AlertState()

    data object None : AlertState()

    val isActive: Boolean get() = this !is None
}
