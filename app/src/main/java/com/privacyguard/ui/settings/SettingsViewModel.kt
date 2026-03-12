package com.privacyguard.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.privacyguard.ml.EntityType
import com.privacyguard.util.ConfidenceThresholds
import com.privacyguard.util.Debouncer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.UUID

// ==========================================================================
// Settings UI State
// ==========================================================================

/**
 * Comprehensive UI state data class containing all settings fields organized
 * by category. This is the single source of truth for the settings screen's
 * state and drives all UI rendering.
 */
data class SettingsUiState(
    // -------------------------------------------------------------------------
    // Privacy Settings
    // -------------------------------------------------------------------------
    /** Detection sensitivity level. 0 = Low, 1 = Medium, 2 = High. */
    val detectionSensitivity: Int = 1,

    /** Auto-clear clipboard after PII detection. */
    val autoClearClipboard: Boolean = false,

    /** Delay in seconds before auto-clearing the clipboard. */
    val autoClearDelaySeconds: Int = 5,

    /** Alert type: 0 = Full overlay, 1 = Banner, 2 = Notification only. */
    val alertType: Int = 0,

    /** Whether to show alerts for medium severity detections. */
    val alertOnMediumSeverity: Boolean = true,

    /** Whether to show alerts for high severity detections. */
    val alertOnHighSeverity: Boolean = true,

    /** Whether to show alerts for critical severity detections. */
    val alertOnCriticalSeverity: Boolean = true,

    /** Whether clipboard monitoring is enabled. */
    val isClipboardMonitoringEnabled: Boolean = true,

    /** Whether text field monitoring via accessibility is enabled. */
    val isTextFieldMonitoringEnabled: Boolean = true,

    /** Per-entity type confidence thresholds. */
    val entityThresholds: Map<EntityType, Float> = defaultEntityThresholds(),

    /** Whether to enable regex pre-screening before ML inference. */
    val regexPreScreenEnabled: Boolean = true,

    // -------------------------------------------------------------------------
    // Notification Settings
    // -------------------------------------------------------------------------
    /** Whether notifications are enabled globally. */
    val notificationsEnabled: Boolean = true,

    /** Whether to play a sound on detection. */
    val notificationSoundEnabled: Boolean = true,

    /** Whether to vibrate on detection. */
    val notificationVibrationEnabled: Boolean = true,

    /** Notification channel for critical detections. */
    val criticalNotificationChannelEnabled: Boolean = true,

    /** Notification channel for high detections. */
    val highNotificationChannelEnabled: Boolean = true,

    /** Notification channel for medium detections. */
    val mediumNotificationChannelEnabled: Boolean = false,

    /** Whether to show detection details in the notification. */
    val showDetailsInNotification: Boolean = true,

    /** Whether to group multiple notifications. */
    val groupNotifications: Boolean = true,

    /** Quiet hours start (hour in 24h format, -1 = disabled). */
    val quietHoursStart: Int = -1,

    /** Quiet hours end (hour in 24h format). */
    val quietHoursEnd: Int = -1,

    // -------------------------------------------------------------------------
    // Appearance Settings
    // -------------------------------------------------------------------------
    /** Theme mode: 0 = System, 1 = Light, 2 = Dark. */
    val themeMode: Int = 0,

    /** Whether to use dynamic/Material You colors. */
    val useDynamicColor: Boolean = true,

    /** Language code (e.g., "en", "es", "de"). Empty = system default. */
    val languageCode: String = "",

    /** Font size scale. 0 = Small, 1 = Default, 2 = Large, 3 = Extra Large. */
    val fontSizeScale: Int = 1,

    /** Whether to show the compact status bar in mini mode. */
    val useCompactStatusBar: Boolean = false,

    /** Whether to show the detection timeline on the dashboard. */
    val showDetectionTimeline: Boolean = true,

    /** Whether to show the entity distribution chart on the dashboard. */
    val showEntityDistribution: Boolean = true,

    /** Whether to show performance metrics on the dashboard. */
    val showPerformanceMetrics: Boolean = true,

    // -------------------------------------------------------------------------
    // Storage Settings
    // -------------------------------------------------------------------------
    /** Retention period for detection events in days. 0 = keep forever. */
    val retentionPeriodDays: Int = 90,

    /** Maximum number of events to store. 0 = unlimited. */
    val maxStoredEvents: Int = 10_000,

    /** Default export format: "json", "csv", "txt". */
    val defaultExportFormat: String = "json",

    /** Whether to include metadata in exports. */
    val includeMetadataInExport: Boolean = true,

    /** Whether to anonymize data in exports. */
    val anonymizeExports: Boolean = false,

    /** Whether to auto-export on a schedule. */
    val autoExportEnabled: Boolean = false,

    /** Auto-export interval in days. */
    val autoExportIntervalDays: Int = 7,

    // -------------------------------------------------------------------------
    // Advanced Settings
    // -------------------------------------------------------------------------
    /** Debounce delay for text analysis in milliseconds. */
    val debounceDelayMs: Long = Debouncer.DEFAULT_DELAY_MS,

    /** Whether debug mode is enabled (shows extra logging). */
    val debugMode: Boolean = false,

    /** Whether performance mode is enabled (trades accuracy for speed). */
    val performanceMode: Boolean = false,

    /** Maximum sequence length for tokenization. */
    val maxSequenceLength: Int = 512,

    /** Whether to use NNAPI acceleration. */
    val useNnApiAcceleration: Boolean = true,

    /** Number of inference threads. */
    val inferenceThreadCount: Int = 4,

    /** Whether to enable background scanning. */
    val backgroundScanningEnabled: Boolean = true,

    /** Dashboard refresh interval in seconds. */
    val dashboardRefreshIntervalSeconds: Int = 30,

    // -------------------------------------------------------------------------
    // Security Settings
    // -------------------------------------------------------------------------
    /** Whether biometric authentication is required to open the app. */
    val biometricLockEnabled: Boolean = false,

    /** Whether to enable audit logging of all setting changes. */
    val auditLoggingEnabled: Boolean = true,

    /** Whether the encrypted log uses additional key rotation. */
    val keyRotationEnabled: Boolean = false,

    /** Key rotation interval in days. */
    val keyRotationIntervalDays: Int = 30,

    /** Whether to lock the app after a period of inactivity. */
    val autoLockEnabled: Boolean = false,

    /** Auto-lock timeout in minutes. */
    val autoLockTimeoutMinutes: Int = 5,

    /** Whether to obscure content in the app switcher. */
    val obscureInRecents: Boolean = true,

    // -------------------------------------------------------------------------
    // UI-only State (not persisted)
    // -------------------------------------------------------------------------
    /** Whether the settings have been modified since last save. */
    val isDirty: Boolean = false,

    /** Whether a save operation is in progress. */
    val isSaving: Boolean = false,

    /** Last save timestamp in milliseconds. */
    val lastSaveTimestamp: Long = 0L,

    /** Error message from the last operation, or null. */
    val errorMessage: String? = null,

    /** Whether the import/export dialog is visible. */
    val showImportExportDialog: Boolean = false,

    /** Whether the reset confirmation dialog is visible. */
    val showResetDialog: Boolean = false,

    /** Current expanded section key, or null if none is expanded. */
    val expandedSection: String? = null
) {
    /**
     * Returns true if all required security settings are enabled.
     */
    val isFullySecured: Boolean
        get() = biometricLockEnabled && auditLoggingEnabled && obscureInRecents

    /**
     * Returns a human-readable label for the current theme mode.
     */
    val themeModeLabel: String
        get() = when (themeMode) {
            0 -> "System"
            1 -> "Light"
            2 -> "Dark"
            else -> "System"
        }

    /**
     * Returns a human-readable label for the alert type.
     */
    val alertTypeLabel: String
        get() = when (alertType) {
            0 -> "Full Overlay"
            1 -> "Banner"
            2 -> "Notification Only"
            else -> "Full Overlay"
        }

    /**
     * Returns a human-readable label for the sensitivity level.
     */
    val sensitivityLabel: String
        get() = when (detectionSensitivity) {
            0 -> "Low"
            1 -> "Medium"
            2 -> "High"
            else -> "Medium"
        }

    /**
     * Returns a human-readable label for the font size scale.
     */
    val fontSizeLabel: String
        get() = when (fontSizeScale) {
            0 -> "Small"
            1 -> "Default"
            2 -> "Large"
            3 -> "Extra Large"
            else -> "Default"
        }

    /**
     * Returns a human-readable label for the retention period.
     */
    val retentionLabel: String
        get() = when (retentionPeriodDays) {
            0 -> "Forever"
            7 -> "1 Week"
            30 -> "30 Days"
            90 -> "90 Days"
            180 -> "6 Months"
            365 -> "1 Year"
            else -> "$retentionPeriodDays Days"
        }
}

/**
 * Returns default confidence thresholds per entity type.
 */
private fun defaultEntityThresholds(): Map<EntityType, Float> {
    return mapOf(
        EntityType.CREDIT_CARD to 0.85f,
        EntityType.SSN to 0.85f,
        EntityType.PASSWORD to 0.80f,
        EntityType.API_KEY to 0.80f,
        EntityType.EMAIL to 0.75f,
        EntityType.PHONE to 0.75f,
        EntityType.PERSON_NAME to 0.70f,
        EntityType.ADDRESS to 0.70f,
        EntityType.DATE_OF_BIRTH to 0.70f,
        EntityType.MEDICAL_ID to 0.80f
    )
}

// ==========================================================================
// Settings categories for grouping
// ==========================================================================

/**
 * Enum of settings categories for section headers and navigation.
 */
enum class SettingsCategory(val displayName: String, val key: String) {
    PRIVACY("Privacy", "privacy"),
    NOTIFICATIONS("Notifications", "notifications"),
    APPEARANCE("Appearance", "appearance"),
    STORAGE("Storage", "storage"),
    ADVANCED("Advanced", "advanced"),
    SECURITY("Security", "security")
}

// ==========================================================================
// Audit log entry
// ==========================================================================

/**
 * An audit log entry recording a setting change.
 */
data class SettingsAuditEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val settingKey: String,
    val previousValue: String,
    val newValue: String,
    val category: String
)

// ==========================================================================
// Settings ViewModel
// ==========================================================================

/**
 * ViewModel for the Settings screen. Manages all settings state, persistence
 * using encrypted SharedPreferences, validation, conflict resolution,
 * default value management, reset functionality, and import/export of
 * settings configurations.
 *
 * All settings are persisted to encrypted shared preferences using the
 * AndroidX Security library. Changes are debounced to avoid excessive
 * disk writes.
 */
class SettingsViewModel(
    private val prefs: SharedPreferences
) : ViewModel() {

    companion object {
        // Preference keys - Privacy
        private const val KEY_DETECTION_SENSITIVITY = "detection_sensitivity"
        private const val KEY_AUTO_CLEAR_CLIPBOARD = "auto_clear_clipboard"
        private const val KEY_AUTO_CLEAR_DELAY = "auto_clear_delay_seconds"
        private const val KEY_ALERT_TYPE = "alert_type"
        private const val KEY_ALERT_MEDIUM = "alert_on_medium"
        private const val KEY_ALERT_HIGH = "alert_on_high"
        private const val KEY_ALERT_CRITICAL = "alert_on_critical"
        private const val KEY_CLIPBOARD_MONITORING = "clipboard_monitoring"
        private const val KEY_TEXT_FIELD_MONITORING = "text_field_monitoring"
        private const val KEY_ENTITY_THRESHOLDS = "entity_thresholds_json"
        private const val KEY_REGEX_PRE_SCREEN = "regex_pre_screen"

        // Preference keys - Notifications
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
        private const val KEY_NOTIFICATION_VIBRATION = "notification_vibration"
        private const val KEY_CRITICAL_CHANNEL = "critical_channel"
        private const val KEY_HIGH_CHANNEL = "high_channel"
        private const val KEY_MEDIUM_CHANNEL = "medium_channel"
        private const val KEY_SHOW_DETAILS_NOTIF = "show_details_notification"
        private const val KEY_GROUP_NOTIFICATIONS = "group_notifications"
        private const val KEY_QUIET_HOURS_START = "quiet_hours_start"
        private const val KEY_QUIET_HOURS_END = "quiet_hours_end"

        // Preference keys - Appearance
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_LANGUAGE = "language_code"
        private const val KEY_FONT_SIZE = "font_size_scale"
        private const val KEY_COMPACT_STATUS_BAR = "compact_status_bar"
        private const val KEY_SHOW_TIMELINE = "show_timeline"
        private const val KEY_SHOW_DISTRIBUTION = "show_distribution"
        private const val KEY_SHOW_PERFORMANCE = "show_performance"

        // Preference keys - Storage
        private const val KEY_RETENTION_DAYS = "retention_days"
        private const val KEY_MAX_EVENTS = "max_events"
        private const val KEY_EXPORT_FORMAT = "export_format"
        private const val KEY_INCLUDE_METADATA = "include_metadata"
        private const val KEY_ANONYMIZE_EXPORTS = "anonymize_exports"
        private const val KEY_AUTO_EXPORT = "auto_export"
        private const val KEY_AUTO_EXPORT_INTERVAL = "auto_export_interval"

        // Preference keys - Advanced
        private const val KEY_DEBOUNCE_DELAY = "debounce_delay_ms"
        private const val KEY_DEBUG_MODE = "debug_mode"
        private const val KEY_PERFORMANCE_MODE = "performance_mode"
        private const val KEY_MAX_SEQ_LENGTH = "max_sequence_length"
        private const val KEY_NNAPI_ACCELERATION = "nnapi_acceleration"
        private const val KEY_INFERENCE_THREADS = "inference_threads"
        private const val KEY_BACKGROUND_SCANNING = "background_scanning"
        private const val KEY_REFRESH_INTERVAL = "dashboard_refresh_interval"

        // Preference keys - Security
        private const val KEY_BIOMETRIC_LOCK = "biometric_lock"
        private const val KEY_AUDIT_LOGGING = "audit_logging"
        private const val KEY_KEY_ROTATION = "key_rotation"
        private const val KEY_KEY_ROTATION_INTERVAL = "key_rotation_interval"
        private const val KEY_AUTO_LOCK = "auto_lock"
        private const val KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout"
        private const val KEY_OBSCURE_RECENTS = "obscure_recents"

        // Audit log key
        private const val KEY_AUDIT_LOG = "settings_audit_log"

        // Settings export/import key
        private const val KEY_SETTINGS_EXPORT = "settings_export_json"

        private val gson: Gson = GsonBuilder().create()

        /**
         * Create encrypted SharedPreferences for settings storage.
         */
        fun createEncryptedPreferences(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                "privacyguard_settings",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    // -------------------------------------------------------------------------
    // State management
    // -------------------------------------------------------------------------

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _auditLog = MutableStateFlow<List<SettingsAuditEntry>>(emptyList())
    val auditLog: StateFlow<List<SettingsAuditEntry>> = _auditLog.asStateFlow()

    init {
        loadAllSettings()
        loadAuditLog()
    }

    // -------------------------------------------------------------------------
    // Privacy settings
    // -------------------------------------------------------------------------

    fun setDetectionSensitivity(level: Int) {
        val clamped = level.coerceIn(0, 2)
        auditChange(KEY_DETECTION_SENSITIVITY, _uiState.value.detectionSensitivity.toString(), clamped.toString(), "Privacy")
        _uiState.update { it.copy(detectionSensitivity = clamped, isDirty = true) }
        persistInt(KEY_DETECTION_SENSITIVITY, clamped)
        applySensitivityPreset(clamped)
    }

    fun setAutoClearClipboard(enabled: Boolean) {
        auditChange(KEY_AUTO_CLEAR_CLIPBOARD, _uiState.value.autoClearClipboard.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(autoClearClipboard = enabled, isDirty = true) }
        persistBoolean(KEY_AUTO_CLEAR_CLIPBOARD, enabled)
    }

    fun setAutoClearDelaySeconds(seconds: Int) {
        val clamped = seconds.coerceIn(1, 30)
        auditChange(KEY_AUTO_CLEAR_DELAY, _uiState.value.autoClearDelaySeconds.toString(), clamped.toString(), "Privacy")
        _uiState.update { it.copy(autoClearDelaySeconds = clamped, isDirty = true) }
        persistInt(KEY_AUTO_CLEAR_DELAY, clamped)
    }

    fun setAlertType(type: Int) {
        val clamped = type.coerceIn(0, 2)
        auditChange(KEY_ALERT_TYPE, _uiState.value.alertType.toString(), clamped.toString(), "Privacy")
        _uiState.update { it.copy(alertType = clamped, isDirty = true) }
        persistInt(KEY_ALERT_TYPE, clamped)
    }

    fun setAlertOnMediumSeverity(enabled: Boolean) {
        auditChange(KEY_ALERT_MEDIUM, _uiState.value.alertOnMediumSeverity.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(alertOnMediumSeverity = enabled, isDirty = true) }
        persistBoolean(KEY_ALERT_MEDIUM, enabled)
    }

    fun setAlertOnHighSeverity(enabled: Boolean) {
        auditChange(KEY_ALERT_HIGH, _uiState.value.alertOnHighSeverity.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(alertOnHighSeverity = enabled, isDirty = true) }
        persistBoolean(KEY_ALERT_HIGH, enabled)
    }

    fun setAlertOnCriticalSeverity(enabled: Boolean) {
        auditChange(KEY_ALERT_CRITICAL, _uiState.value.alertOnCriticalSeverity.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(alertOnCriticalSeverity = enabled, isDirty = true) }
        persistBoolean(KEY_ALERT_CRITICAL, enabled)
    }

    fun setClipboardMonitoringEnabled(enabled: Boolean) {
        auditChange(KEY_CLIPBOARD_MONITORING, _uiState.value.isClipboardMonitoringEnabled.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(isClipboardMonitoringEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_CLIPBOARD_MONITORING, enabled)
    }

    fun setTextFieldMonitoringEnabled(enabled: Boolean) {
        auditChange(KEY_TEXT_FIELD_MONITORING, _uiState.value.isTextFieldMonitoringEnabled.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(isTextFieldMonitoringEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_TEXT_FIELD_MONITORING, enabled)
    }

    fun setEntityThreshold(entityType: EntityType, threshold: Float) {
        val clamped = threshold.coerceIn(0.5f, 1.0f)
        val currentThresholds = _uiState.value.entityThresholds.toMutableMap()
        val previousValue = currentThresholds[entityType] ?: 0.75f
        currentThresholds[entityType] = clamped

        auditChange(
            "threshold_${entityType.name}",
            previousValue.toString(),
            clamped.toString(),
            "Privacy"
        )

        _uiState.update { it.copy(entityThresholds = currentThresholds, isDirty = true) }
        persistString(KEY_ENTITY_THRESHOLDS, gson.toJson(currentThresholds.mapKeys { it.key.name }))
        ConfidenceThresholds.setThreshold(entityType, clamped)
    }

    fun setRegexPreScreenEnabled(enabled: Boolean) {
        auditChange(KEY_REGEX_PRE_SCREEN, _uiState.value.regexPreScreenEnabled.toString(), enabled.toString(), "Privacy")
        _uiState.update { it.copy(regexPreScreenEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_REGEX_PRE_SCREEN, enabled)
    }

    // -------------------------------------------------------------------------
    // Notification settings
    // -------------------------------------------------------------------------

    fun setNotificationsEnabled(enabled: Boolean) {
        auditChange(KEY_NOTIFICATIONS_ENABLED, _uiState.value.notificationsEnabled.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(notificationsEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
    }

    fun setNotificationSoundEnabled(enabled: Boolean) {
        auditChange(KEY_NOTIFICATION_SOUND, _uiState.value.notificationSoundEnabled.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(notificationSoundEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_NOTIFICATION_SOUND, enabled)
    }

    fun setNotificationVibrationEnabled(enabled: Boolean) {
        auditChange(KEY_NOTIFICATION_VIBRATION, _uiState.value.notificationVibrationEnabled.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(notificationVibrationEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_NOTIFICATION_VIBRATION, enabled)
    }

    fun setCriticalNotificationChannelEnabled(enabled: Boolean) {
        auditChange(KEY_CRITICAL_CHANNEL, _uiState.value.criticalNotificationChannelEnabled.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(criticalNotificationChannelEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_CRITICAL_CHANNEL, enabled)
    }

    fun setHighNotificationChannelEnabled(enabled: Boolean) {
        auditChange(KEY_HIGH_CHANNEL, _uiState.value.highNotificationChannelEnabled.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(highNotificationChannelEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_HIGH_CHANNEL, enabled)
    }

    fun setMediumNotificationChannelEnabled(enabled: Boolean) {
        auditChange(KEY_MEDIUM_CHANNEL, _uiState.value.mediumNotificationChannelEnabled.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(mediumNotificationChannelEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_MEDIUM_CHANNEL, enabled)
    }

    fun setShowDetailsInNotification(enabled: Boolean) {
        auditChange(KEY_SHOW_DETAILS_NOTIF, _uiState.value.showDetailsInNotification.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(showDetailsInNotification = enabled, isDirty = true) }
        persistBoolean(KEY_SHOW_DETAILS_NOTIF, enabled)
    }

    fun setGroupNotifications(enabled: Boolean) {
        auditChange(KEY_GROUP_NOTIFICATIONS, _uiState.value.groupNotifications.toString(), enabled.toString(), "Notifications")
        _uiState.update { it.copy(groupNotifications = enabled, isDirty = true) }
        persistBoolean(KEY_GROUP_NOTIFICATIONS, enabled)
    }

    fun setQuietHours(start: Int, end: Int) {
        auditChange(KEY_QUIET_HOURS_START, _uiState.value.quietHoursStart.toString(), start.toString(), "Notifications")
        _uiState.update { it.copy(quietHoursStart = start, quietHoursEnd = end, isDirty = true) }
        persistInt(KEY_QUIET_HOURS_START, start)
        persistInt(KEY_QUIET_HOURS_END, end)
    }

    // -------------------------------------------------------------------------
    // Appearance settings
    // -------------------------------------------------------------------------

    fun setThemeMode(mode: Int) {
        val clamped = mode.coerceIn(0, 2)
        auditChange(KEY_THEME_MODE, _uiState.value.themeMode.toString(), clamped.toString(), "Appearance")
        _uiState.update { it.copy(themeMode = clamped, isDirty = true) }
        persistInt(KEY_THEME_MODE, clamped)
    }

    fun setUseDynamicColor(enabled: Boolean) {
        auditChange(KEY_DYNAMIC_COLOR, _uiState.value.useDynamicColor.toString(), enabled.toString(), "Appearance")
        _uiState.update { it.copy(useDynamicColor = enabled, isDirty = true) }
        persistBoolean(KEY_DYNAMIC_COLOR, enabled)
    }

    fun setLanguageCode(code: String) {
        auditChange(KEY_LANGUAGE, _uiState.value.languageCode, code, "Appearance")
        _uiState.update { it.copy(languageCode = code, isDirty = true) }
        persistString(KEY_LANGUAGE, code)
    }

    fun setFontSizeScale(scale: Int) {
        val clamped = scale.coerceIn(0, 3)
        auditChange(KEY_FONT_SIZE, _uiState.value.fontSizeScale.toString(), clamped.toString(), "Appearance")
        _uiState.update { it.copy(fontSizeScale = clamped, isDirty = true) }
        persistInt(KEY_FONT_SIZE, clamped)
    }

    fun setUseCompactStatusBar(enabled: Boolean) {
        auditChange(KEY_COMPACT_STATUS_BAR, _uiState.value.useCompactStatusBar.toString(), enabled.toString(), "Appearance")
        _uiState.update { it.copy(useCompactStatusBar = enabled, isDirty = true) }
        persistBoolean(KEY_COMPACT_STATUS_BAR, enabled)
    }

    fun setShowDetectionTimeline(enabled: Boolean) {
        auditChange(KEY_SHOW_TIMELINE, _uiState.value.showDetectionTimeline.toString(), enabled.toString(), "Appearance")
        _uiState.update { it.copy(showDetectionTimeline = enabled, isDirty = true) }
        persistBoolean(KEY_SHOW_TIMELINE, enabled)
    }

    fun setShowEntityDistribution(enabled: Boolean) {
        auditChange(KEY_SHOW_DISTRIBUTION, _uiState.value.showEntityDistribution.toString(), enabled.toString(), "Appearance")
        _uiState.update { it.copy(showEntityDistribution = enabled, isDirty = true) }
        persistBoolean(KEY_SHOW_DISTRIBUTION, enabled)
    }

    fun setShowPerformanceMetrics(enabled: Boolean) {
        auditChange(KEY_SHOW_PERFORMANCE, _uiState.value.showPerformanceMetrics.toString(), enabled.toString(), "Appearance")
        _uiState.update { it.copy(showPerformanceMetrics = enabled, isDirty = true) }
        persistBoolean(KEY_SHOW_PERFORMANCE, enabled)
    }

    // -------------------------------------------------------------------------
    // Storage settings
    // -------------------------------------------------------------------------

    fun setRetentionPeriodDays(days: Int) {
        val clamped = days.coerceIn(0, 365)
        auditChange(KEY_RETENTION_DAYS, _uiState.value.retentionPeriodDays.toString(), clamped.toString(), "Storage")
        _uiState.update { it.copy(retentionPeriodDays = clamped, isDirty = true) }
        persistInt(KEY_RETENTION_DAYS, clamped)
    }

    fun setMaxStoredEvents(max: Int) {
        val clamped = max.coerceIn(0, 100_000)
        auditChange(KEY_MAX_EVENTS, _uiState.value.maxStoredEvents.toString(), clamped.toString(), "Storage")
        _uiState.update { it.copy(maxStoredEvents = clamped, isDirty = true) }
        persistInt(KEY_MAX_EVENTS, clamped)
    }

    fun setDefaultExportFormat(format: String) {
        val valid = if (format in listOf("json", "csv", "txt")) format else "json"
        auditChange(KEY_EXPORT_FORMAT, _uiState.value.defaultExportFormat, valid, "Storage")
        _uiState.update { it.copy(defaultExportFormat = valid, isDirty = true) }
        persistString(KEY_EXPORT_FORMAT, valid)
    }

    fun setIncludeMetadataInExport(enabled: Boolean) {
        auditChange(KEY_INCLUDE_METADATA, _uiState.value.includeMetadataInExport.toString(), enabled.toString(), "Storage")
        _uiState.update { it.copy(includeMetadataInExport = enabled, isDirty = true) }
        persistBoolean(KEY_INCLUDE_METADATA, enabled)
    }

    fun setAnonymizeExports(enabled: Boolean) {
        auditChange(KEY_ANONYMIZE_EXPORTS, _uiState.value.anonymizeExports.toString(), enabled.toString(), "Storage")
        _uiState.update { it.copy(anonymizeExports = enabled, isDirty = true) }
        persistBoolean(KEY_ANONYMIZE_EXPORTS, enabled)
    }

    fun setAutoExportEnabled(enabled: Boolean) {
        auditChange(KEY_AUTO_EXPORT, _uiState.value.autoExportEnabled.toString(), enabled.toString(), "Storage")
        _uiState.update { it.copy(autoExportEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_AUTO_EXPORT, enabled)
    }

    fun setAutoExportIntervalDays(days: Int) {
        val clamped = days.coerceIn(1, 90)
        auditChange(KEY_AUTO_EXPORT_INTERVAL, _uiState.value.autoExportIntervalDays.toString(), clamped.toString(), "Storage")
        _uiState.update { it.copy(autoExportIntervalDays = clamped, isDirty = true) }
        persistInt(KEY_AUTO_EXPORT_INTERVAL, clamped)
    }

    // -------------------------------------------------------------------------
    // Advanced settings
    // -------------------------------------------------------------------------

    fun setDebounceDelayMs(delayMs: Long) {
        val clamped = delayMs.coerceIn(Debouncer.MIN_DELAY_MS, Debouncer.MAX_DELAY_MS)
        auditChange(KEY_DEBOUNCE_DELAY, _uiState.value.debounceDelayMs.toString(), clamped.toString(), "Advanced")
        _uiState.update { it.copy(debounceDelayMs = clamped, isDirty = true) }
        persistLong(KEY_DEBOUNCE_DELAY, clamped)
    }

    fun setDebugMode(enabled: Boolean) {
        auditChange(KEY_DEBUG_MODE, _uiState.value.debugMode.toString(), enabled.toString(), "Advanced")
        _uiState.update { it.copy(debugMode = enabled, isDirty = true) }
        persistBoolean(KEY_DEBUG_MODE, enabled)
    }

    fun setPerformanceMode(enabled: Boolean) {
        auditChange(KEY_PERFORMANCE_MODE, _uiState.value.performanceMode.toString(), enabled.toString(), "Advanced")
        _uiState.update { it.copy(performanceMode = enabled, isDirty = true) }
        persistBoolean(KEY_PERFORMANCE_MODE, enabled)

        if (enabled) {
            resolvePerformanceModeConflicts()
        }
    }

    fun setMaxSequenceLength(length: Int) {
        val clamped = length.coerceIn(64, 512)
        auditChange(KEY_MAX_SEQ_LENGTH, _uiState.value.maxSequenceLength.toString(), clamped.toString(), "Advanced")
        _uiState.update { it.copy(maxSequenceLength = clamped, isDirty = true) }
        persistInt(KEY_MAX_SEQ_LENGTH, clamped)
    }

    fun setUseNnApiAcceleration(enabled: Boolean) {
        auditChange(KEY_NNAPI_ACCELERATION, _uiState.value.useNnApiAcceleration.toString(), enabled.toString(), "Advanced")
        _uiState.update { it.copy(useNnApiAcceleration = enabled, isDirty = true) }
        persistBoolean(KEY_NNAPI_ACCELERATION, enabled)
    }

    fun setInferenceThreadCount(count: Int) {
        val clamped = count.coerceIn(1, 8)
        auditChange(KEY_INFERENCE_THREADS, _uiState.value.inferenceThreadCount.toString(), clamped.toString(), "Advanced")
        _uiState.update { it.copy(inferenceThreadCount = clamped, isDirty = true) }
        persistInt(KEY_INFERENCE_THREADS, clamped)
    }

    fun setBackgroundScanningEnabled(enabled: Boolean) {
        auditChange(KEY_BACKGROUND_SCANNING, _uiState.value.backgroundScanningEnabled.toString(), enabled.toString(), "Advanced")
        _uiState.update { it.copy(backgroundScanningEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_BACKGROUND_SCANNING, enabled)
    }

    fun setDashboardRefreshIntervalSeconds(seconds: Int) {
        val clamped = seconds.coerceIn(5, 300)
        auditChange(KEY_REFRESH_INTERVAL, _uiState.value.dashboardRefreshIntervalSeconds.toString(), clamped.toString(), "Advanced")
        _uiState.update { it.copy(dashboardRefreshIntervalSeconds = clamped, isDirty = true) }
        persistInt(KEY_REFRESH_INTERVAL, clamped)
    }

    // -------------------------------------------------------------------------
    // Security settings
    // -------------------------------------------------------------------------

    fun setBiometricLockEnabled(enabled: Boolean) {
        auditChange(KEY_BIOMETRIC_LOCK, _uiState.value.biometricLockEnabled.toString(), enabled.toString(), "Security")
        _uiState.update { it.copy(biometricLockEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_BIOMETRIC_LOCK, enabled)
    }

    fun setAuditLoggingEnabled(enabled: Boolean) {
        auditChange(KEY_AUDIT_LOGGING, _uiState.value.auditLoggingEnabled.toString(), enabled.toString(), "Security")
        _uiState.update { it.copy(auditLoggingEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_AUDIT_LOGGING, enabled)
    }

    fun setKeyRotationEnabled(enabled: Boolean) {
        auditChange(KEY_KEY_ROTATION, _uiState.value.keyRotationEnabled.toString(), enabled.toString(), "Security")
        _uiState.update { it.copy(keyRotationEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_KEY_ROTATION, enabled)
    }

    fun setKeyRotationIntervalDays(days: Int) {
        val clamped = days.coerceIn(7, 365)
        auditChange(KEY_KEY_ROTATION_INTERVAL, _uiState.value.keyRotationIntervalDays.toString(), clamped.toString(), "Security")
        _uiState.update { it.copy(keyRotationIntervalDays = clamped, isDirty = true) }
        persistInt(KEY_KEY_ROTATION_INTERVAL, clamped)
    }

    fun setAutoLockEnabled(enabled: Boolean) {
        auditChange(KEY_AUTO_LOCK, _uiState.value.autoLockEnabled.toString(), enabled.toString(), "Security")
        _uiState.update { it.copy(autoLockEnabled = enabled, isDirty = true) }
        persistBoolean(KEY_AUTO_LOCK, enabled)
    }

    fun setAutoLockTimeoutMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(1, 60)
        auditChange(KEY_AUTO_LOCK_TIMEOUT, _uiState.value.autoLockTimeoutMinutes.toString(), clamped.toString(), "Security")
        _uiState.update { it.copy(autoLockTimeoutMinutes = clamped, isDirty = true) }
        persistInt(KEY_AUTO_LOCK_TIMEOUT, clamped)
    }

    fun setObscureInRecents(enabled: Boolean) {
        auditChange(KEY_OBSCURE_RECENTS, _uiState.value.obscureInRecents.toString(), enabled.toString(), "Security")
        _uiState.update { it.copy(obscureInRecents = enabled, isDirty = true) }
        persistBoolean(KEY_OBSCURE_RECENTS, enabled)
    }

    // -------------------------------------------------------------------------
    // UI-only state management
    // -------------------------------------------------------------------------

    fun setExpandedSection(key: String?) {
        _uiState.update { it.copy(expandedSection = if (it.expandedSection == key) null else key) }
    }

    fun showResetDialog(show: Boolean) {
        _uiState.update { it.copy(showResetDialog = show) }
    }

    fun showImportExportDialog(show: Boolean) {
        _uiState.update { it.copy(showImportExportDialog = show) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // -------------------------------------------------------------------------
    // Reset to defaults
    // -------------------------------------------------------------------------

    /**
     * Resets all settings to their default values and clears the audit log.
     */
    fun resetToDefaults() {
        auditChange("ALL_SETTINGS", "custom", "defaults", "System")

        val defaults = SettingsUiState()
        _uiState.value = defaults.copy(
            isDirty = false,
            lastSaveTimestamp = System.currentTimeMillis(),
            showResetDialog = false
        )

        viewModelScope.launch {
            prefs.edit().clear().apply()
            persistAllSettings(defaults)

            // Reset confidence thresholds in the global utility
            defaults.entityThresholds.forEach { (type, threshold) ->
                ConfidenceThresholds.setThreshold(type, threshold)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Import / Export settings
    // -------------------------------------------------------------------------

    /**
     * Exports all current settings as a JSON string.
     */
    fun exportSettingsJson(): String {
        val state = _uiState.value
        val exportMap = mapOf(
            "version" to "1.0",
            "timestamp" to System.currentTimeMillis(),
            "privacy" to mapOf(
                "detectionSensitivity" to state.detectionSensitivity,
                "autoClearClipboard" to state.autoClearClipboard,
                "autoClearDelaySeconds" to state.autoClearDelaySeconds,
                "alertType" to state.alertType,
                "alertOnMedium" to state.alertOnMediumSeverity,
                "alertOnHigh" to state.alertOnHighSeverity,
                "alertOnCritical" to state.alertOnCriticalSeverity,
                "clipboardMonitoring" to state.isClipboardMonitoringEnabled,
                "textFieldMonitoring" to state.isTextFieldMonitoringEnabled,
                "regexPreScreen" to state.regexPreScreenEnabled,
                "thresholds" to state.entityThresholds.mapKeys { it.key.name }
            ),
            "notifications" to mapOf(
                "enabled" to state.notificationsEnabled,
                "sound" to state.notificationSoundEnabled,
                "vibration" to state.notificationVibrationEnabled,
                "criticalChannel" to state.criticalNotificationChannelEnabled,
                "highChannel" to state.highNotificationChannelEnabled,
                "mediumChannel" to state.mediumNotificationChannelEnabled,
                "showDetails" to state.showDetailsInNotification,
                "groupNotifications" to state.groupNotifications,
                "quietHoursStart" to state.quietHoursStart,
                "quietHoursEnd" to state.quietHoursEnd
            ),
            "appearance" to mapOf(
                "themeMode" to state.themeMode,
                "dynamicColor" to state.useDynamicColor,
                "language" to state.languageCode,
                "fontSizeScale" to state.fontSizeScale,
                "compactStatusBar" to state.useCompactStatusBar,
                "showTimeline" to state.showDetectionTimeline,
                "showDistribution" to state.showEntityDistribution,
                "showPerformance" to state.showPerformanceMetrics
            ),
            "storage" to mapOf(
                "retentionDays" to state.retentionPeriodDays,
                "maxEvents" to state.maxStoredEvents,
                "exportFormat" to state.defaultExportFormat,
                "includeMetadata" to state.includeMetadataInExport,
                "anonymize" to state.anonymizeExports,
                "autoExport" to state.autoExportEnabled,
                "autoExportInterval" to state.autoExportIntervalDays
            ),
            "advanced" to mapOf(
                "debounceDelay" to state.debounceDelayMs,
                "debugMode" to state.debugMode,
                "performanceMode" to state.performanceMode,
                "maxSeqLength" to state.maxSequenceLength,
                "nnapi" to state.useNnApiAcceleration,
                "inferenceThreads" to state.inferenceThreadCount,
                "backgroundScanning" to state.backgroundScanningEnabled,
                "refreshInterval" to state.dashboardRefreshIntervalSeconds
            ),
            "security" to mapOf(
                "biometricLock" to state.biometricLockEnabled,
                "auditLogging" to state.auditLoggingEnabled,
                "keyRotation" to state.keyRotationEnabled,
                "keyRotationInterval" to state.keyRotationIntervalDays,
                "autoLock" to state.autoLockEnabled,
                "autoLockTimeout" to state.autoLockTimeoutMinutes,
                "obscureRecents" to state.obscureInRecents
            )
        )
        return gson.toJson(exportMap)
    }

    /**
     * Imports settings from a JSON string. Returns true on success.
     */
    fun importSettingsJson(json: String): Boolean {
        return try {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val importMap: Map<String, Any> = gson.fromJson(json, mapType)

            val version = importMap["version"] as? String
            if (version == null || version != "1.0") {
                _uiState.update { it.copy(errorMessage = "Unsupported settings version: $version") }
                return false
            }

            // Apply privacy settings
            @Suppress("UNCHECKED_CAST")
            val privacy = importMap["privacy"] as? Map<String, Any>
            privacy?.let {
                (it["detectionSensitivity"] as? Double)?.toInt()?.let { v -> setDetectionSensitivity(v) }
                (it["autoClearClipboard"] as? Boolean)?.let { v -> setAutoClearClipboard(v) }
                (it["autoClearDelaySeconds"] as? Double)?.toInt()?.let { v -> setAutoClearDelaySeconds(v) }
                (it["alertType"] as? Double)?.toInt()?.let { v -> setAlertType(v) }
                (it["clipboardMonitoring"] as? Boolean)?.let { v -> setClipboardMonitoringEnabled(v) }
                (it["textFieldMonitoring"] as? Boolean)?.let { v -> setTextFieldMonitoringEnabled(v) }
            }

            // Apply appearance settings
            @Suppress("UNCHECKED_CAST")
            val appearance = importMap["appearance"] as? Map<String, Any>
            appearance?.let {
                (it["themeMode"] as? Double)?.toInt()?.let { v -> setThemeMode(v) }
                (it["dynamicColor"] as? Boolean)?.let { v -> setUseDynamicColor(v) }
                (it["language"] as? String)?.let { v -> setLanguageCode(v) }
                (it["fontSizeScale"] as? Double)?.toInt()?.let { v -> setFontSizeScale(v) }
            }

            auditChange("ALL_SETTINGS", "previous", "imported", "System")
            _uiState.update { it.copy(isDirty = false, lastSaveTimestamp = System.currentTimeMillis()) }
            true
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Import failed: ${e.message}") }
            false
        }
    }

    // -------------------------------------------------------------------------
    // Setting validation and conflict resolution
    // -------------------------------------------------------------------------

    /**
     * Applies sensitivity preset by adjusting thresholds.
     */
    private fun applySensitivityPreset(level: Int) {
        val thresholdMultiplier = when (level) {
            0 -> 1.1f   // Low - higher thresholds (fewer detections)
            1 -> 1.0f   // Medium - default thresholds
            2 -> 0.85f  // High - lower thresholds (more detections)
            else -> 1.0f
        }

        val defaultThresholds = defaultEntityThresholds()
        val adjustedThresholds = defaultThresholds.mapValues { (_, defaultValue) ->
            (defaultValue * thresholdMultiplier).coerceIn(0.5f, 1.0f)
        }

        _uiState.update { it.copy(entityThresholds = adjustedThresholds) }
        persistString(KEY_ENTITY_THRESHOLDS, gson.toJson(adjustedThresholds.mapKeys { it.key.name }))

        adjustedThresholds.forEach { (type, threshold) ->
            ConfidenceThresholds.setThreshold(type, threshold)
        }
    }

    /**
     * Resolves setting conflicts when performance mode is enabled.
     * Performance mode reduces sequence length and increases debounce delay.
     */
    private fun resolvePerformanceModeConflicts() {
        _uiState.update {
            it.copy(
                maxSequenceLength = 256.coerceAtMost(it.maxSequenceLength),
                debounceDelayMs = 500L.coerceAtLeast(it.debounceDelayMs)
            )
        }
        persistInt(KEY_MAX_SEQ_LENGTH, _uiState.value.maxSequenceLength)
        persistLong(KEY_DEBOUNCE_DELAY, _uiState.value.debounceDelayMs)
    }

    // -------------------------------------------------------------------------
    // Audit logging
    // -------------------------------------------------------------------------

    /**
     * Records a setting change in the audit log if audit logging is enabled.
     */
    private fun auditChange(key: String, previousValue: String, newValue: String, category: String) {
        if (!_uiState.value.auditLoggingEnabled) return
        if (previousValue == newValue) return

        val entry = SettingsAuditEntry(
            settingKey = key,
            previousValue = previousValue,
            newValue = newValue,
            category = category
        )

        val currentLog = _auditLog.value.toMutableList()
        currentLog.add(0, entry)

        // Keep only the last 500 entries
        if (currentLog.size > 500) {
            currentLog.subList(500, currentLog.size).clear()
        }

        _auditLog.value = currentLog
        persistAuditLog(currentLog)
    }

    /**
     * Clears the audit log.
     */
    fun clearAuditLog() {
        _auditLog.value = emptyList()
        prefs.edit().remove(KEY_AUDIT_LOG).apply()
    }

    /**
     * Returns the audit log as a formatted string for export.
     */
    fun formatAuditLogForExport(): String {
        return buildString {
            appendLine("PrivacyGuard Settings Audit Log")
            appendLine("================================")
            _auditLog.value.forEach { entry ->
                val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date(entry.timestamp))
                appendLine("[$date] [${entry.category}] ${entry.settingKey}: ${entry.previousValue} -> ${entry.newValue}")
            }
        }
    }

    // -------------------------------------------------------------------------
    // Persistence helpers
    // -------------------------------------------------------------------------

    private fun persistBoolean(key: String, value: Boolean) {
        viewModelScope.launch { prefs.edit().putBoolean(key, value).apply() }
    }

    private fun persistInt(key: String, value: Int) {
        viewModelScope.launch { prefs.edit().putInt(key, value).apply() }
    }

    private fun persistLong(key: String, value: Long) {
        viewModelScope.launch { prefs.edit().putLong(key, value).apply() }
    }

    private fun persistString(key: String, value: String) {
        viewModelScope.launch { prefs.edit().putString(key, value).apply() }
    }

    private fun persistAuditLog(log: List<SettingsAuditEntry>) {
        viewModelScope.launch {
            prefs.edit().putString(KEY_AUDIT_LOG, gson.toJson(log)).apply()
        }
    }

    private fun persistAllSettings(state: SettingsUiState) {
        prefs.edit().apply {
            putInt(KEY_DETECTION_SENSITIVITY, state.detectionSensitivity)
            putBoolean(KEY_AUTO_CLEAR_CLIPBOARD, state.autoClearClipboard)
            putInt(KEY_AUTO_CLEAR_DELAY, state.autoClearDelaySeconds)
            putInt(KEY_ALERT_TYPE, state.alertType)
            putBoolean(KEY_ALERT_MEDIUM, state.alertOnMediumSeverity)
            putBoolean(KEY_ALERT_HIGH, state.alertOnHighSeverity)
            putBoolean(KEY_ALERT_CRITICAL, state.alertOnCriticalSeverity)
            putBoolean(KEY_CLIPBOARD_MONITORING, state.isClipboardMonitoringEnabled)
            putBoolean(KEY_TEXT_FIELD_MONITORING, state.isTextFieldMonitoringEnabled)
            putString(KEY_ENTITY_THRESHOLDS, gson.toJson(state.entityThresholds.mapKeys { it.key.name }))
            putBoolean(KEY_REGEX_PRE_SCREEN, state.regexPreScreenEnabled)

            putBoolean(KEY_NOTIFICATIONS_ENABLED, state.notificationsEnabled)
            putBoolean(KEY_NOTIFICATION_SOUND, state.notificationSoundEnabled)
            putBoolean(KEY_NOTIFICATION_VIBRATION, state.notificationVibrationEnabled)
            putBoolean(KEY_CRITICAL_CHANNEL, state.criticalNotificationChannelEnabled)
            putBoolean(KEY_HIGH_CHANNEL, state.highNotificationChannelEnabled)
            putBoolean(KEY_MEDIUM_CHANNEL, state.mediumNotificationChannelEnabled)
            putBoolean(KEY_SHOW_DETAILS_NOTIF, state.showDetailsInNotification)
            putBoolean(KEY_GROUP_NOTIFICATIONS, state.groupNotifications)
            putInt(KEY_QUIET_HOURS_START, state.quietHoursStart)
            putInt(KEY_QUIET_HOURS_END, state.quietHoursEnd)

            putInt(KEY_THEME_MODE, state.themeMode)
            putBoolean(KEY_DYNAMIC_COLOR, state.useDynamicColor)
            putString(KEY_LANGUAGE, state.languageCode)
            putInt(KEY_FONT_SIZE, state.fontSizeScale)
            putBoolean(KEY_COMPACT_STATUS_BAR, state.useCompactStatusBar)
            putBoolean(KEY_SHOW_TIMELINE, state.showDetectionTimeline)
            putBoolean(KEY_SHOW_DISTRIBUTION, state.showEntityDistribution)
            putBoolean(KEY_SHOW_PERFORMANCE, state.showPerformanceMetrics)

            putInt(KEY_RETENTION_DAYS, state.retentionPeriodDays)
            putInt(KEY_MAX_EVENTS, state.maxStoredEvents)
            putString(KEY_EXPORT_FORMAT, state.defaultExportFormat)
            putBoolean(KEY_INCLUDE_METADATA, state.includeMetadataInExport)
            putBoolean(KEY_ANONYMIZE_EXPORTS, state.anonymizeExports)
            putBoolean(KEY_AUTO_EXPORT, state.autoExportEnabled)
            putInt(KEY_AUTO_EXPORT_INTERVAL, state.autoExportIntervalDays)

            putLong(KEY_DEBOUNCE_DELAY, state.debounceDelayMs)
            putBoolean(KEY_DEBUG_MODE, state.debugMode)
            putBoolean(KEY_PERFORMANCE_MODE, state.performanceMode)
            putInt(KEY_MAX_SEQ_LENGTH, state.maxSequenceLength)
            putBoolean(KEY_NNAPI_ACCELERATION, state.useNnApiAcceleration)
            putInt(KEY_INFERENCE_THREADS, state.inferenceThreadCount)
            putBoolean(KEY_BACKGROUND_SCANNING, state.backgroundScanningEnabled)
            putInt(KEY_REFRESH_INTERVAL, state.dashboardRefreshIntervalSeconds)

            putBoolean(KEY_BIOMETRIC_LOCK, state.biometricLockEnabled)
            putBoolean(KEY_AUDIT_LOGGING, state.auditLoggingEnabled)
            putBoolean(KEY_KEY_ROTATION, state.keyRotationEnabled)
            putInt(KEY_KEY_ROTATION_INTERVAL, state.keyRotationIntervalDays)
            putBoolean(KEY_AUTO_LOCK, state.autoLockEnabled)
            putInt(KEY_AUTO_LOCK_TIMEOUT, state.autoLockTimeoutMinutes)
            putBoolean(KEY_OBSCURE_RECENTS, state.obscureInRecents)
        }.apply()
    }

    // -------------------------------------------------------------------------
    // Load settings from SharedPreferences
    // -------------------------------------------------------------------------

    private fun loadAllSettings() {
        val defaults = SettingsUiState()
        val thresholdsJson = prefs.getString(KEY_ENTITY_THRESHOLDS, null)
        val entityThresholds = if (thresholdsJson != null) {
            try {
                val mapType = object : TypeToken<Map<String, Float>>() {}.type
                val nameMap: Map<String, Float> = gson.fromJson(thresholdsJson, mapType)
                nameMap.mapNotNull { (name, value) ->
                    try {
                        EntityType.valueOf(name) to value
                    } catch (_: Exception) {
                        null
                    }
                }.toMap()
            } catch (_: Exception) {
                defaults.entityThresholds
            }
        } else {
            defaults.entityThresholds
        }

        _uiState.value = SettingsUiState(
            detectionSensitivity = prefs.getInt(KEY_DETECTION_SENSITIVITY, defaults.detectionSensitivity),
            autoClearClipboard = prefs.getBoolean(KEY_AUTO_CLEAR_CLIPBOARD, defaults.autoClearClipboard),
            autoClearDelaySeconds = prefs.getInt(KEY_AUTO_CLEAR_DELAY, defaults.autoClearDelaySeconds),
            alertType = prefs.getInt(KEY_ALERT_TYPE, defaults.alertType),
            alertOnMediumSeverity = prefs.getBoolean(KEY_ALERT_MEDIUM, defaults.alertOnMediumSeverity),
            alertOnHighSeverity = prefs.getBoolean(KEY_ALERT_HIGH, defaults.alertOnHighSeverity),
            alertOnCriticalSeverity = prefs.getBoolean(KEY_ALERT_CRITICAL, defaults.alertOnCriticalSeverity),
            isClipboardMonitoringEnabled = prefs.getBoolean(KEY_CLIPBOARD_MONITORING, defaults.isClipboardMonitoringEnabled),
            isTextFieldMonitoringEnabled = prefs.getBoolean(KEY_TEXT_FIELD_MONITORING, defaults.isTextFieldMonitoringEnabled),
            entityThresholds = entityThresholds,
            regexPreScreenEnabled = prefs.getBoolean(KEY_REGEX_PRE_SCREEN, defaults.regexPreScreenEnabled),

            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, defaults.notificationsEnabled),
            notificationSoundEnabled = prefs.getBoolean(KEY_NOTIFICATION_SOUND, defaults.notificationSoundEnabled),
            notificationVibrationEnabled = prefs.getBoolean(KEY_NOTIFICATION_VIBRATION, defaults.notificationVibrationEnabled),
            criticalNotificationChannelEnabled = prefs.getBoolean(KEY_CRITICAL_CHANNEL, defaults.criticalNotificationChannelEnabled),
            highNotificationChannelEnabled = prefs.getBoolean(KEY_HIGH_CHANNEL, defaults.highNotificationChannelEnabled),
            mediumNotificationChannelEnabled = prefs.getBoolean(KEY_MEDIUM_CHANNEL, defaults.mediumNotificationChannelEnabled),
            showDetailsInNotification = prefs.getBoolean(KEY_SHOW_DETAILS_NOTIF, defaults.showDetailsInNotification),
            groupNotifications = prefs.getBoolean(KEY_GROUP_NOTIFICATIONS, defaults.groupNotifications),
            quietHoursStart = prefs.getInt(KEY_QUIET_HOURS_START, defaults.quietHoursStart),
            quietHoursEnd = prefs.getInt(KEY_QUIET_HOURS_END, defaults.quietHoursEnd),

            themeMode = prefs.getInt(KEY_THEME_MODE, defaults.themeMode),
            useDynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, defaults.useDynamicColor),
            languageCode = prefs.getString(KEY_LANGUAGE, defaults.languageCode) ?: "",
            fontSizeScale = prefs.getInt(KEY_FONT_SIZE, defaults.fontSizeScale),
            useCompactStatusBar = prefs.getBoolean(KEY_COMPACT_STATUS_BAR, defaults.useCompactStatusBar),
            showDetectionTimeline = prefs.getBoolean(KEY_SHOW_TIMELINE, defaults.showDetectionTimeline),
            showEntityDistribution = prefs.getBoolean(KEY_SHOW_DISTRIBUTION, defaults.showEntityDistribution),
            showPerformanceMetrics = prefs.getBoolean(KEY_SHOW_PERFORMANCE, defaults.showPerformanceMetrics),

            retentionPeriodDays = prefs.getInt(KEY_RETENTION_DAYS, defaults.retentionPeriodDays),
            maxStoredEvents = prefs.getInt(KEY_MAX_EVENTS, defaults.maxStoredEvents),
            defaultExportFormat = prefs.getString(KEY_EXPORT_FORMAT, defaults.defaultExportFormat) ?: "json",
            includeMetadataInExport = prefs.getBoolean(KEY_INCLUDE_METADATA, defaults.includeMetadataInExport),
            anonymizeExports = prefs.getBoolean(KEY_ANONYMIZE_EXPORTS, defaults.anonymizeExports),
            autoExportEnabled = prefs.getBoolean(KEY_AUTO_EXPORT, defaults.autoExportEnabled),
            autoExportIntervalDays = prefs.getInt(KEY_AUTO_EXPORT_INTERVAL, defaults.autoExportIntervalDays),

            debounceDelayMs = prefs.getLong(KEY_DEBOUNCE_DELAY, defaults.debounceDelayMs),
            debugMode = prefs.getBoolean(KEY_DEBUG_MODE, defaults.debugMode),
            performanceMode = prefs.getBoolean(KEY_PERFORMANCE_MODE, defaults.performanceMode),
            maxSequenceLength = prefs.getInt(KEY_MAX_SEQ_LENGTH, defaults.maxSequenceLength),
            useNnApiAcceleration = prefs.getBoolean(KEY_NNAPI_ACCELERATION, defaults.useNnApiAcceleration),
            inferenceThreadCount = prefs.getInt(KEY_INFERENCE_THREADS, defaults.inferenceThreadCount),
            backgroundScanningEnabled = prefs.getBoolean(KEY_BACKGROUND_SCANNING, defaults.backgroundScanningEnabled),
            dashboardRefreshIntervalSeconds = prefs.getInt(KEY_REFRESH_INTERVAL, defaults.dashboardRefreshIntervalSeconds),

            biometricLockEnabled = prefs.getBoolean(KEY_BIOMETRIC_LOCK, defaults.biometricLockEnabled),
            auditLoggingEnabled = prefs.getBoolean(KEY_AUDIT_LOGGING, defaults.auditLoggingEnabled),
            keyRotationEnabled = prefs.getBoolean(KEY_KEY_ROTATION, defaults.keyRotationEnabled),
            keyRotationIntervalDays = prefs.getInt(KEY_KEY_ROTATION_INTERVAL, defaults.keyRotationIntervalDays),
            autoLockEnabled = prefs.getBoolean(KEY_AUTO_LOCK, defaults.autoLockEnabled),
            autoLockTimeoutMinutes = prefs.getInt(KEY_AUTO_LOCK_TIMEOUT, defaults.autoLockTimeoutMinutes),
            obscureInRecents = prefs.getBoolean(KEY_OBSCURE_RECENTS, defaults.obscureInRecents),

            isDirty = false,
            lastSaveTimestamp = System.currentTimeMillis()
        )

        // Sync confidence thresholds to global utility
        entityThresholds.forEach { (type, threshold) ->
            ConfidenceThresholds.setThreshold(type, threshold)
        }
    }

    private fun loadAuditLog() {
        val logJson = prefs.getString(KEY_AUDIT_LOG, null) ?: return
        try {
            val listType = object : TypeToken<List<SettingsAuditEntry>>() {}.type
            val log: List<SettingsAuditEntry> = gson.fromJson(logJson, listType)
            _auditLog.value = log
        } catch (_: Exception) {
            _auditLog.value = emptyList()
        }
    }
}

// ==========================================================================
// ViewModelFactory
// ==========================================================================

/**
 * Factory for creating [SettingsViewModel] instances with the required
 * encrypted SharedPreferences dependency.
 */
class SettingsViewModelFactory(
    private val prefs: SharedPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
