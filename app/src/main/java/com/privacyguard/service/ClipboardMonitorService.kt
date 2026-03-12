package com.privacyguard.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.privacyguard.PrivacyGuardApplication
import com.privacyguard.R
import com.privacyguard.ml.PrivacyModel
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.WhitelistManager
import com.privacyguard.util.AnalysisPipeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Foreground service that monitors the Android clipboard for PII.
 * Uses START_STICKY to survive process kills.
 */
class ClipboardMonitorService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        private const val MIN_CLIP_LENGTH = 5
        private const val MAX_CLIP_LENGTH = 10000

        fun start(context: Context) {
            val intent = Intent(context, ClipboardMonitorService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ClipboardMonitorService::class.java)
            context.stopService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var clipboardManager: ClipboardManager? = null
    private var pipeline: AnalysisPipeline? = null

    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        onClipChanged()
    }

    override fun onCreate() {
        super.onCreate()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        registerClipListener()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        unregisterClipListener()
        pipeline?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun registerClipListener() {
        clipboardManager?.addPrimaryClipChangedListener(clipListener)
    }

    private fun unregisterClipListener() {
        clipboardManager?.removePrimaryClipChangedListener(clipListener)
    }

    private fun onClipChanged() {
        val clip = clipboardManager?.primaryClip ?: return
        if (clip.itemCount == 0) return

        for (i in 0 until clip.itemCount) {
            val text = clip.getItemAt(i)?.text?.toString() ?: continue

            if (text.isBlank() || text.length < MIN_CLIP_LENGTH) continue

            val processedText = if (text.length > MAX_CLIP_LENGTH) {
                text.substring(0, MAX_CLIP_LENGTH)
            } else text

            val sourceApp = clip.description?.label?.toString()

            pipeline?.processText(processedText, sourceApp)
        }
    }

    fun setPipeline(analysisPipeline: AnalysisPipeline) {
        this.pipeline = analysisPipeline
    }

    fun clearClipboard() {
        clipboardManager?.clearPrimaryClip()
    }

    private fun createNotification(): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(this, PrivacyGuardApplication.MONITORING_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
