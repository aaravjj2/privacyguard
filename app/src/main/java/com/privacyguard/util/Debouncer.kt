package com.privacyguard.util

import kotlinx.coroutines.*

/**
 * Coroutine-based debouncer for throttling rapid-fire events.
 * Only executes the action after the specified delay of inactivity.
 */
class Debouncer(
    private val scope: CoroutineScope,
    private val delayMs: Long = DEFAULT_DELAY_MS
) {
    companion object {
        const val DEFAULT_DELAY_MS = 800L
        const val MIN_DELAY_MS = 200L
        const val MAX_DELAY_MS = 2000L
    }

    private var job: Job? = null
    private var pendingAction: (suspend () -> Unit)? = null

    /**
     * Debounce the given action. Cancels any previously pending action.
     */
    fun debounce(action: suspend () -> Unit) {
        job?.cancel()
        pendingAction = action
        job = scope.launch {
            delay(delayMs)
            action()
            pendingAction = null
        }
    }

    /**
     * Cancel any pending debounced action.
     */
    fun cancel() {
        job?.cancel()
        job = null
        pendingAction = null
    }

    /**
     * Check if there's a pending action waiting to execute.
     */
    fun hasPending(): Boolean = job?.isActive == true

    /**
     * Force-execute the pending action immediately, canceling the delay.
     */
    fun flush() {
        val action = pendingAction
        cancel()
        if (action != null) {
            scope.launch { action() }
        }
    }
}
