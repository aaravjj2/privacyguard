package com.privacyguard.ui.alert

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry

/**
 * Service that displays a Compose-based overlay window for CRITICAL severity alerts.
 * Uses SYSTEM_ALERT_WINDOW permission to show over other apps.
 */
class AlertOverlayService : Service() {

    companion object {
        private const val EXTRA_SOURCE_APP = "source_app"
        private const val EXTRA_ENTITY_TYPE = "entity_type"
        private const val EXTRA_SEVERITY = "severity"

        fun start(context: Context, sourceApp: String?, entityType: String, severity: String) {
            val intent = Intent(context, AlertOverlayService::class.java).apply {
                putExtra(EXTRA_SOURCE_APP, sourceApp)
                putExtra(EXTRA_ENTITY_TYPE, entityType)
                putExtra(EXTRA_SEVERITY, severity)
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AlertOverlayService::class.java))
        }
    }

    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sourceApp = intent?.getStringExtra(EXTRA_SOURCE_APP)
        val entityType = intent?.getStringExtra(EXTRA_ENTITY_TYPE) ?: "Unknown"
        val severity = intent?.getStringExtra(EXTRA_SEVERITY) ?: "CRITICAL"

        showOverlay(sourceApp, entityType, severity)

        return START_NOT_STICKY
    }

    private fun showOverlay(sourceApp: String?, entityType: String, severity: String) {
        removeOverlay()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }

        try {
            overlayView = ComposeView(this)
            windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            // Permission not granted or other error
            stopSelf()
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) { }
        }
        overlayView = null
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }
}
