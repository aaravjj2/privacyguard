package com.privacyguard.ml

/**
 * Sealed class representing the current state of the ML model.
 */
sealed class ModelState {
    data object Initializing : ModelState()
    data object Ready : ModelState()
    data object Running : ModelState()
    data class Error(val message: String, val cause: Throwable? = null) : ModelState()
    data object Closed : ModelState()

    val isReady: Boolean get() = this is Ready
    val isRunning: Boolean get() = this is Running
    val isError: Boolean get() = this is Error
    val canRunInference: Boolean get() = this is Ready
}
