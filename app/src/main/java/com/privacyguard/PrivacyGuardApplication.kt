package com.privacyguard

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class PrivacyGuardApplication : Application() {

    companion object {
        const val MONITORING_CHANNEL_ID = "privacyguard_monitoring"
        const val ALERT_CHANNEL_ID = "privacyguard_alerts"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val monitoringChannel = NotificationChannel(
            MONITORING_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_description)
            setShowBadge(false)
        }

        val alertChannel = NotificationChannel(
            ALERT_CHANNEL_ID,
            "PrivacyGuard Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts when sensitive data is detected"
            enableVibration(true)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(monitoringChannel)
        notificationManager.createNotificationChannel(alertChannel)
    }
}
