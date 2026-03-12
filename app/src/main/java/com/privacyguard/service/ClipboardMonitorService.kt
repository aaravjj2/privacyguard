package com.privacyguard.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.privacyguard.PrivacyGuardApplication
import com.privacyguard.R
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.WhitelistManager
import com.privacyguard.ml.PrivacyModel
import com.privacyguard.ui.alert.AlertManager
import com.privacyguard.util.AnalysisPipeline
import com.privacyguard.util.RegexScreener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Foreground service that monitors the Android clipboard for PII.
 *
 * Implementation details:
 * - Uses START_STICKY to survive process kills and OOM
 * - Runs a persistent low-priority notification (silent)
 * - Registers ClipboardManager.OnPrimaryClipChangedListener
 * - Handles multiple ClipData items per clip event
 * - Guards against null/blank/short/long clipboard content
 * - Tracks source app from clip description label
 * - Supports clearing clipboard as a user protective action
 * - Integrates with RegexScreener for pre-filtering before ML inference
 * - Provides monitoring statistics via StateFlow
 * - Deduplicates rapid duplicate clip events
 * - Acquires partial wake lock during analysis to prevent CPU sleep
 */
class ClipboardMonitorService : Service() {

    companion object {
        private const val TAG = "ClipboardMonitor"
        const val NOTIFICATION_ID = 1001
        private const val MIN_CLIP_LENGTH = 5
        private const val MAX_CLIP_LENGTH = 10000
        private const val DUPLICATE_WINDOW_MS = 2000L
        private const val WAKELOCK_HOLD_MS = 10_000L

        const val ACTION_START = "com.privacyguard.ACTION_START_MONITORING"
        const val ACTION_STOP = "com.privacyguard.ACTION_STOP_MONITORING"
        const val ACTION_CLEAR_CLIPBOARD = "com.privacyguard.ACTION_CLEAR_CLIPBOARD"

        @Volatile
        private var isRunning = false

        fun isServiceRunning(): Boolean = isRunning

        fun start(context: Context) {
            val intent = Intent(context, ClipboardMonitorService::class.java).apply {
                action = ACTION_START
            }
            try {
                context.startForegroundService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground service", e)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ClipboardMonitorService::class.java))
        }

        fun requestClipboardClear(context: Context) {
            val intent = Intent(context, ClipboardMonitorService::class.java).apply {
                action = ACTION_CLEAR_CLIPBOARD
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send clear action", e)
            }
        }
    }

    // Coroutine scope bound to service lifecycle
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // System services
    private var clipboardManager: ClipboardManager? = null
    private var wakeLock: PowerManager.WakeLock? = null

    // Analysis pipeline
    private var pipeline: AnalysisPipeline? = null
    private var alertManager: AlertManager? = null

    // Statistics
    private val _clipboardReads = MutableStateFlow(0L)
    val clipboardReads: StateFlow<Long> = _clipboardReads.asStateFlow()

    private val _piiDetections = MutableStateFlow(0L)
    val piiDetections: StateFlow<Long> = _piiDetections.asStateFlow()

    private val _lastClipTimestamp = MutableStateFlow(0L)
    val lastClipTimestamp: StateFlow<Long> = _lastClipTimestamp.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    // Duplicate detection
    private var lastClipHash: Int = 0
    private var lastClipTime: Long = 0

    // Clip changed listener
    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        onClipChanged()
    }

    // ==================
    // LIFECYCLE
    // ==================

    override fun onCreate() {
        super.onCreate()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PrivacyGuard::ClipMonitor")
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                START_NOT_STICKY
            }
            ACTION_CLEAR_CLIPBOARD -> {
                performClipboardClear()
                START_STICKY
            }
            else -> {
                beginMonitoring()
                START_STICKY
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopMonitoring()
        releaseWakeLockSafe()
        serviceScope.cancel()
        isRunning = false
        _isActive.value = false
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
    }

    // ==================
    // MONITORING
    // ==================

    private fun beginMonitoring() {
        startForeground(NOTIFICATION_ID, buildNotification("PrivacyGuard Active", "Clipboard monitoring is running"))
        clipboardManager?.addPrimaryClipChangedListener(clipListener)
        isRunning = true
        _isActive.value = true
        Log.d(TAG, "Monitoring started")
    }

    private fun stopMonitoring() {
        try {
            clipboardManager?.removePrimaryClipChangedListener(clipListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing clip listener", e)
        }
        pipeline?.cancel()
        isRunning = false
        _isActive.value = false
        Log.d(TAG, "Monitoring stopped")
    }

    /**
     * Main handler for clipboard change events.
     */
    private fun onClipChanged() {
        val clip = clipboardManager?.primaryClip ?: return
        if (clip.itemCount == 0) return

        _lastClipTimestamp.value = System.currentTimeMillis()
        _clipboardReads.value += 1

        acquireWakeLockSafe()

        for (i in 0 until clip.itemCount) {
            processClipItem(clip, i)
        }

        // Release wake lock after short delay
        serviceScope.launch {
            delay(WAKELOCK_HOLD_MS)
            releaseWakeLockSafe()
        }
    }

    private fun processClipItem(clip: ClipData, index: Int) {
        val text = clip.getItemAt(index)?.text?.toString() ?: return

        // Guard: blank or too short
        if (text.isBlank() || text.length < MIN_CLIP_LENGTH) return

        // Deduplication
        val hash = text.hashCode()
        val now = System.currentTimeMillis()
        if (hash == lastClipHash && (now - lastClipTime) < DUPLICATE_WINDOW_MS) return
        lastClipHash = hash
        lastClipTime = now

        // Truncate if too long
        val processed = if (text.length > MAX_CLIP_LENGTH) {
            Log.w(TAG, "Truncating clipboard text from ${text.length} to $MAX_CLIP_LENGTH chars")
            text.substring(0, MAX_CLIP_LENGTH)
        } else text

        // Regex pre-screen: skip ML inference if no PII patterns detected
        if (!RegexScreener.containsPotentialPII(processed)) {
            Log.d(TAG, "Pre-screen negative for clip of ${processed.length} chars")
            return
        }

        val sourceApp = try {
            clip.description?.label?.toString()
        } catch (e: Exception) {
            null
        }

        Log.d(TAG, "Processing clip: ${processed.length} chars from ${sourceApp ?: "unknown"}")
        pipeline?.processText(processed, sourceApp)
    }

    // ==================
    // CLIPBOARD ACTIONS
    // ==================

    fun performClipboardClear() {
        try {
            clipboardManager?.clearPrimaryClip()
            Log.d(TAG, "Clipboard cleared successfully")
        } catch (e: Exception) {
            try {
                clipboardManager?.setPrimaryClip(ClipData.newPlainText("", ""))
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to clear clipboard", e2)
            }
        }
    }

    fun clearClipboard() = performClipboardClear()

    // ==================
    // DEPENDENCY INJECTION
    // ==================

    fun setPipeline(analysisPipeline: AnalysisPipeline) {
        this.pipeline = analysisPipeline
    }

    fun setAlertManager(manager: AlertManager) {
        this.alertManager = manager
    }

    // ==================
    // WAKE LOCK
    // ==================

    private fun acquireWakeLockSafe() {
        try {
            wakeLock?.let { if (!it.isHeld) it.acquire(WAKELOCK_HOLD_MS) }
        } catch (e: Exception) {
            Log.e(TAG, "Wake lock acquire failed", e)
        }
    }

    private fun releaseWakeLockSafe() {
        try {
            wakeLock?.let { if (it.isHeld) it.release() }
        } catch (e: Exception) {
            Log.e(TAG, "Wake lock release failed", e)
        }
    }

    // ==================
    // NOTIFICATION
    // ==================

    private fun buildNotification(title: String, text: String): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val stopIntent = Intent(this, ClipboardMonitorService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, PrivacyGuardApplication.MONITORING_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPending)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
    }

    @Suppress("unused")
    private fun createNotification(): Notification {
        return buildNotification(
            getString(R.string.notification_title),
            getString(R.string.notification_text)
        )
    }
}
