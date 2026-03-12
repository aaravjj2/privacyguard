package com.privacyguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives BOOT_COMPLETED broadcast and restarts the ClipboardMonitorService.
 * Ensures PII monitoring resumes after device reboot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ClipboardMonitorService.start(context)
        }
    }
}
