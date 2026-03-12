package com.privacyguard.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.privacyguard.ml.EntityType
import com.privacyguard.ui.theme.*
import com.privacyguard.util.ConfidenceThresholds
import com.privacyguard.util.Debouncer

// ---------------------------------------------------------------------------
// Data classes for settings model
// ---------------------------------------------------------------------------

/**
 * Represents all user-configurable settings for the PrivacyGuard app.
 * Each field maps to a persistent preference.
 */
data class SettingsState(
    // Privacy Protection
    val isClipboardMonitoringEnabled: Boolean = true,
    val isTextFieldMonitoringEnabled: Boolean = true,
    val isScreenContentScanEnabled: Boolean = false,
    val clipboardAutoClearEnabled: Boolean = true,
    val clipboardAutoClearDelaySeconds: Int = 30,
    val sensitiveDataMaskingEnabled: Boolean = true,
    val alertOnScreenshot: Boolean = false,

    // Notifications
    val notificationsEnabled: Boolean = true,
    val criticalAlertsEnabled: Boolean = true,
    val highAlertsEnabled: Boolean = true,
    val mediumAlertsEnabled: Boolean = false,
    val silentModeEnabled: Boolean = false,
    val notificationSoundEnabled: Boolean = true,
    val notificationVibrateEnabled: Boolean = true,
    val alertStyle: String = "Overlay Banner",
    val maxAlertsPerHour: Int = 10,

    // Appearance
    val themeMode: String = "System Default",
    val dynamicColorsEnabled: Boolean = true,
    val compactModeEnabled: Boolean = false,
    val fontScale: Float = 1.0f,
    val accentColor: Color = TrustBlue,
    val showAnimations: Boolean = true,

    // Data & Storage
    val historyRetentionDays: Int = 30,
    val maxHistoryEntries: Int = 1000,
    val autoDeleteOldEntries: Boolean = true,
    val exportFormat: String = "JSON",
    val databaseEncryptionEnabled: Boolean = true,
    val cacheSize: String = "50 MB",

    // Advanced / Analysis
    val debounceDelayMs: Long = Debouncer.DEFAULT_DELAY_MS,
    val isPerformanceMetricsEnabled: Boolean = true,
    val detailedLoggingEnabled: Boolean = false,
    val modelPrecision: String = "FP16",
    val batchProcessingEnabled: Boolean = false,
    val backgroundScanInterval: Int = 15,
    val regexPreScreenerEnabled: Boolean = true,
    val inferenceThreads: Int = 2,

    // Security
    val appLockEnabled: Boolean = false,
    val biometricAuthEnabled: Boolean = false,
    val hideFromRecents: Boolean = false,
    val secureScreenEnabled: Boolean = true,
    val autoLockTimeoutMinutes: Int = 5,

    // Entity-specific thresholds
    val entityThresholds: Map<EntityType, Float> = EntityType.entries
        .filter { it != EntityType.UNKNOWN }
        .associateWith { ConfidenceThresholds.getThreshold(it) },

    // Monitored types
    val monitoredEntityTypes: Set<String> = setOf(
        "Credit Card", "SSN", "Password", "API Key", "Email", "Phone"
    )
)

/**
 * Identifiers for each expandable section in the settings screen.
 */
enum class SettingsSectionId {
    PRIVACY_PROTECTION,
    NOTIFICATIONS,
    APPEARANCE,
    DATA_STORAGE,
    ADVANCED,
    SECURITY,
    ABOUT
}

/**
 * Metadata for a settings section, including its title, icon, and description.
 */
data class SettingsSectionInfo(
    val id: SettingsSectionId,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val searchKeywords: List<String> = emptyList()
)

// All section definitions
private val allSections = listOf(
    SettingsSectionInfo(
        id = SettingsSectionId.PRIVACY_PROTECTION,
        title = "Privacy Protection",
        icon = Icons.Default.Shield,
        description = "Configure monitoring and detection features",
        searchKeywords = listOf(
            "clipboard", "text field", "monitoring", "screen", "auto clear",
            "masking", "screenshot", "privacy", "protection", "scan"
        )
    ),
    SettingsSectionInfo(
        id = SettingsSectionId.NOTIFICATIONS,
        title = "Notifications",
        icon = Icons.Default.Notifications,
        description = "Alert preferences and notification settings",
        searchKeywords = listOf(
            "notification", "alert", "sound", "vibrate", "silent",
            "critical", "overlay", "banner", "frequency"
        )
    ),
    SettingsSectionInfo(
        id = SettingsSectionId.APPEARANCE,
        title = "Appearance",
        icon = Icons.Default.Palette,
        description = "Theme, colors, and display options",
        searchKeywords = listOf(
            "theme", "dark", "light", "color", "accent", "font",
            "compact", "animation", "dynamic", "appearance"
        )
    ),
    SettingsSectionInfo(
        id = SettingsSectionId.DATA_STORAGE,
        title = "Data & Storage",
        icon = Icons.Default.Storage,
        description = "History retention, export, and database settings",
        searchKeywords = listOf(
            "data", "storage", "history", "retention", "export",
            "database", "encryption", "cache", "delete", "json", "csv"
        )
    ),
    SettingsSectionInfo(
        id = SettingsSectionId.ADVANCED,
        title = "Advanced",
        icon = Icons.Default.Tune,
        description = "Analysis engine, performance, and model settings",
        searchKeywords = listOf(
            "advanced", "debounce", "performance", "logging", "model",
            "precision", "batch", "background", "regex", "threads",
            "inference", "threshold", "confidence", "entity"
        )
    ),
    SettingsSectionInfo(
        id = SettingsSectionId.SECURITY,
        title = "Security",
        icon = Icons.Default.Lock,
        description = "App lock, biometrics, and screen security",
        searchKeywords = listOf(
            "security", "lock", "biometric", "fingerprint", "recents",
            "secure", "screen", "authentication", "timeout"
        )
    ),
    SettingsSectionInfo(
        id = SettingsSectionId.ABOUT,
        title = "About",
        icon = Icons.Default.Info,
        description = "App version, licenses, and support",
        searchKeywords = listOf(
            "about", "version", "license", "support", "privacy",
            "policy", "terms", "feedback", "rate"
        )
    )
)

// ---------------------------------------------------------------------------
// Main Settings Screen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    isClipboardMonitoringEnabled: Boolean = true,
    isTextFieldMonitoringEnabled: Boolean = true,
    debounceDelayMs: Long = Debouncer.DEFAULT_DELAY_MS,
    isPerformanceMetricsEnabled: Boolean = true,
    onClipboardMonitoringChanged: (Boolean) -> Unit = {},
    onTextFieldMonitoringChanged: (Boolean) -> Unit = {},
    onDebounceDelayChanged: (Long) -> Unit = {},
    onPerformanceMetricsChanged: (Boolean) -> Unit = {},
    onResetSettings: () -> Unit = {}
) {
    var settingsState by remember {
        mutableStateOf(
            SettingsState(
                isClipboardMonitoringEnabled = isClipboardMonitoringEnabled,
                isTextFieldMonitoringEnabled = isTextFieldMonitoringEnabled,
                debounceDelayMs = debounceDelayMs,
                isPerformanceMetricsEnabled = isPerformanceMetricsEnabled
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    // Track which sections are expanded
    val expandedSections = remember {
        mutableStateMapOf<SettingsSectionId, Boolean>().apply {
            SettingsSectionId.entries.forEach { put(it, false) }
            // Expand first section by default
            put(SettingsSectionId.PRIVACY_PROTECTION, true)
        }
    }

    // Filter sections based on search query
    val filteredSections = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allSections
        } else {
            val query = searchQuery.lowercase()
            allSections.filter { section ->
                section.title.lowercase().contains(query) ||
                        section.description.lowercase().contains(query) ||
                        section.searchKeywords.any { it.contains(query) }
            }
        }
    }

    // Reset dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Reset All Settings",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "This will reset all settings to their default values, including " +
                            "monitoring preferences, notification settings, appearance, and " +
                            "confidence thresholds.\n\nYour detection history will not be affected.\n\n" +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsState = SettingsState()
                        onResetSettings()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset Everything", color = AlertRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearchBar) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search settings...") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Text("Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showSearchBar) {
                            showSearchBar = false
                            searchQuery = ""
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = if (showSearchBar) "Close search" else "Back"
                        )
                    }
                },
                actions = {
                    if (!showSearchBar) {
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search settings")
                        }
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(
                                Icons.Default.RestartAlt,
                                contentDescription = "Reset all settings",
                                tint = AlertRed
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Show search results summary when filtering
            if (searchQuery.isNotBlank()) {
                item(key = "search_summary") {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${filteredSections.size} section${if (filteredSections.size != 1) "s" else ""} matching \"$searchQuery\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: Privacy Protection
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.PRIVACY_PROTECTION }) {
                item(key = "header_privacy") {
                    SettingsSectionHeader(
                        info = allSections[0],
                        isExpanded = expandedSections[SettingsSectionId.PRIVACY_PROTECTION] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.PRIVACY_PROTECTION] =
                                !(expandedSections[SettingsSectionId.PRIVACY_PROTECTION] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.PRIVACY_PROTECTION] == true) {
                    item(key = "privacy_clipboard") {
                        SettingsToggleItem(
                            title = "Clipboard Monitoring",
                            description = "Monitor clipboard for sensitive data like credit cards and SSNs",
                            checked = settingsState.isClipboardMonitoringEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(isClipboardMonitoringEnabled = it)
                                onClipboardMonitoringChanged(it)
                            },
                            icon = Icons.Default.ContentPaste
                        )
                    }

                    item(key = "privacy_textfield") {
                        SettingsToggleItem(
                            title = "Text Field Monitoring",
                            description = "Monitor text fields across apps via Accessibility Service",
                            checked = settingsState.isTextFieldMonitoringEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(isTextFieldMonitoringEnabled = it)
                                onTextFieldMonitoringChanged(it)
                            },
                            icon = Icons.Default.TextFields
                        )
                    }

                    item(key = "privacy_screen_scan") {
                        SettingsToggleItem(
                            title = "Screen Content Scanning",
                            description = "Analyze visible screen content for sensitive data exposure",
                            checked = settingsState.isScreenContentScanEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(isScreenContentScanEnabled = it)
                            },
                            icon = Icons.Default.Phonelink
                        )
                    }

                    item(key = "privacy_auto_clear") {
                        SettingsToggleItem(
                            title = "Auto-Clear Clipboard",
                            description = "Automatically clear sensitive data from clipboard after detection",
                            checked = settingsState.clipboardAutoClearEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(clipboardAutoClearEnabled = it)
                            },
                            icon = Icons.Default.CleaningServices
                        )
                    }

                    if (settingsState.clipboardAutoClearEnabled) {
                        item(key = "privacy_auto_clear_delay") {
                            SettingsSliderItem(
                                title = "Auto-Clear Delay",
                                description = "Seconds before clipboard is cleared (${settingsState.clipboardAutoClearDelaySeconds}s)",
                                value = settingsState.clipboardAutoClearDelaySeconds.toFloat(),
                                onValueChange = {
                                    settingsState = settingsState.copy(clipboardAutoClearDelaySeconds = it.toInt())
                                },
                                valueRange = 5f..120f,
                                steps = 22,
                                icon = Icons.Default.Timer,
                                valueLabel = "${settingsState.clipboardAutoClearDelaySeconds}s"
                            )
                        }
                    }

                    item(key = "privacy_masking") {
                        SettingsToggleItem(
                            title = "Sensitive Data Masking",
                            description = "Replace detected PII with masked characters in the display",
                            checked = settingsState.sensitiveDataMaskingEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(sensitiveDataMaskingEnabled = it)
                            },
                            icon = Icons.Default.VisibilityOff
                        )
                    }

                    item(key = "privacy_screenshot") {
                        SettingsToggleItem(
                            title = "Screenshot Alert",
                            description = "Alert when a screenshot is taken while sensitive data is visible",
                            checked = settingsState.alertOnScreenshot,
                            onCheckedChange = {
                                settingsState = settingsState.copy(alertOnScreenshot = it)
                            },
                            icon = Icons.Default.Screenshot
                        )
                    }

                    item(key = "privacy_entity_types") {
                        SettingsMultiSelectItem(
                            title = "Monitored Entity Types",
                            description = "Select which data types to actively monitor",
                            options = listOf(
                                "Credit Card", "SSN", "Password", "API Key",
                                "Email", "Phone", "Person Name", "Address",
                                "Date of Birth", "Medical ID"
                            ),
                            selectedOptions = settingsState.monitoredEntityTypes,
                            onSelectionChanged = {
                                settingsState = settingsState.copy(monitoredEntityTypes = it)
                            },
                            icon = Icons.Default.Checklist
                        )
                    }

                    item(key = "privacy_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: Notifications
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.NOTIFICATIONS }) {
                item(key = "header_notifications") {
                    SettingsSectionHeader(
                        info = allSections[1],
                        isExpanded = expandedSections[SettingsSectionId.NOTIFICATIONS] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.NOTIFICATIONS] =
                                !(expandedSections[SettingsSectionId.NOTIFICATIONS] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.NOTIFICATIONS] == true) {
                    item(key = "notif_enabled") {
                        SettingsToggleItem(
                            title = "Notifications",
                            description = "Enable or disable all notifications",
                            checked = settingsState.notificationsEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(notificationsEnabled = it)
                            },
                            icon = Icons.Default.Notifications
                        )
                    }

                    if (settingsState.notificationsEnabled) {
                        item(key = "notif_critical") {
                            SettingsToggleItem(
                                title = "Critical Alerts",
                                description = "Alerts for credit cards, SSNs, and passwords",
                                checked = settingsState.criticalAlertsEnabled,
                                onCheckedChange = {
                                    settingsState = settingsState.copy(criticalAlertsEnabled = it)
                                },
                                icon = Icons.Default.Error,
                                iconTint = SeverityCritical
                            )
                        }

                        item(key = "notif_high") {
                            SettingsToggleItem(
                                title = "High Severity Alerts",
                                description = "Alerts for emails, phone numbers, and medical IDs",
                                checked = settingsState.highAlertsEnabled,
                                onCheckedChange = {
                                    settingsState = settingsState.copy(highAlertsEnabled = it)
                                },
                                icon = Icons.Default.Warning,
                                iconTint = SeverityHigh
                            )
                        }

                        item(key = "notif_medium") {
                            SettingsToggleItem(
                                title = "Medium Severity Alerts",
                                description = "Alerts for names, addresses, and dates of birth",
                                checked = settingsState.mediumAlertsEnabled,
                                onCheckedChange = {
                                    settingsState = settingsState.copy(mediumAlertsEnabled = it)
                                },
                                icon = Icons.Default.Info,
                                iconTint = SeverityMedium
                            )
                        }

                        item(key = "notif_silent") {
                            SettingsToggleItem(
                                title = "Silent Mode",
                                description = "Log detections without showing any notifications",
                                checked = settingsState.silentModeEnabled,
                                onCheckedChange = {
                                    settingsState = settingsState.copy(silentModeEnabled = it)
                                },
                                icon = Icons.Default.VolumeOff
                            )
                        }

                        if (!settingsState.silentModeEnabled) {
                            item(key = "notif_sound") {
                                SettingsToggleItem(
                                    title = "Notification Sound",
                                    description = "Play a sound when alerts are shown",
                                    checked = settingsState.notificationSoundEnabled,
                                    onCheckedChange = {
                                        settingsState = settingsState.copy(notificationSoundEnabled = it)
                                    },
                                    icon = Icons.Default.VolumeUp
                                )
                            }

                            item(key = "notif_vibrate") {
                                SettingsToggleItem(
                                    title = "Vibration",
                                    description = "Vibrate the device when alerts are shown",
                                    checked = settingsState.notificationVibrateEnabled,
                                    onCheckedChange = {
                                        settingsState = settingsState.copy(notificationVibrateEnabled = it)
                                    },
                                    icon = Icons.Default.Vibration
                                )
                            }
                        }

                        item(key = "notif_style") {
                            SettingsRadioGroupItem(
                                title = "Alert Style",
                                description = "How to display privacy alerts",
                                options = listOf("Overlay Banner", "Notification Only", "Silent Log"),
                                optionDescriptions = listOf(
                                    "Show a floating banner over other apps",
                                    "Send a system notification",
                                    "Record in history without any visual alert"
                                ),
                                selectedOption = settingsState.alertStyle,
                                onOptionSelected = {
                                    settingsState = settingsState.copy(alertStyle = it)
                                },
                                icon = Icons.Default.NotificationsActive
                            )
                        }

                        item(key = "notif_max_per_hour") {
                            SettingsStepperItem(
                                title = "Max Alerts Per Hour",
                                description = "Limit how many alerts can fire per hour to prevent fatigue",
                                value = settingsState.maxAlertsPerHour,
                                onValueChange = {
                                    settingsState = settingsState.copy(maxAlertsPerHour = it)
                                },
                                minValue = 1,
                                maxValue = 100,
                                stepSize = 1,
                                icon = Icons.Default.Speed
                            )
                        }
                    }

                    item(key = "notif_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: Appearance
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.APPEARANCE }) {
                item(key = "header_appearance") {
                    SettingsSectionHeader(
                        info = allSections[2],
                        isExpanded = expandedSections[SettingsSectionId.APPEARANCE] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.APPEARANCE] =
                                !(expandedSections[SettingsSectionId.APPEARANCE] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.APPEARANCE] == true) {
                    item(key = "appear_theme") {
                        SettingsDropdownItem(
                            title = "App Theme",
                            description = "Choose the visual appearance of the app",
                            options = listOf("System Default", "Light", "Dark"),
                            selectedOption = settingsState.themeMode,
                            onOptionSelected = {
                                settingsState = settingsState.copy(themeMode = it)
                            },
                            icon = Icons.Default.DarkMode
                        )
                    }

                    item(key = "appear_dynamic") {
                        SettingsToggleItem(
                            title = "Dynamic Colors",
                            description = "Use Material You dynamic colors from your wallpaper (Android 12+)",
                            checked = settingsState.dynamicColorsEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(dynamicColorsEnabled = it)
                            },
                            icon = Icons.Default.AutoAwesome
                        )
                    }

                    item(key = "appear_compact") {
                        SettingsToggleItem(
                            title = "Compact Mode",
                            description = "Reduce padding and spacing for smaller screens",
                            checked = settingsState.compactModeEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(compactModeEnabled = it)
                            },
                            icon = Icons.Default.ViewCompact
                        )
                    }

                    item(key = "appear_font") {
                        SettingsSliderItem(
                            title = "Font Scale",
                            description = "Adjust text size across the app",
                            value = settingsState.fontScale,
                            onValueChange = {
                                settingsState = settingsState.copy(fontScale = it)
                            },
                            valueRange = 0.8f..1.5f,
                            steps = 6,
                            icon = Icons.Default.TextIncrease,
                            valueLabel = "${"%.1f".format(settingsState.fontScale)}x"
                        )
                    }

                    item(key = "appear_accent") {
                        SettingsColorPickerItem(
                            title = "Accent Color",
                            description = "Choose the primary accent color for the app",
                            colors = listOf(
                                TrustBlue, TrustBlueDark, TrustBlueLight,
                                ProtectionActive, SuccessGreen,
                                AlertOrange, SeverityMedium
                            ),
                            selectedColor = settingsState.accentColor,
                            onColorSelected = {
                                settingsState = settingsState.copy(accentColor = it)
                            },
                            icon = Icons.Default.ColorLens
                        )
                    }

                    item(key = "appear_animations") {
                        SettingsToggleItem(
                            title = "Animations",
                            description = "Enable animated transitions and visual effects",
                            checked = settingsState.showAnimations,
                            onCheckedChange = {
                                settingsState = settingsState.copy(showAnimations = it)
                            },
                            icon = Icons.Default.Animation
                        )
                    }

                    item(key = "appear_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: Data & Storage
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.DATA_STORAGE }) {
                item(key = "header_data") {
                    SettingsSectionHeader(
                        info = allSections[3],
                        isExpanded = expandedSections[SettingsSectionId.DATA_STORAGE] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.DATA_STORAGE] =
                                !(expandedSections[SettingsSectionId.DATA_STORAGE] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.DATA_STORAGE] == true) {
                    item(key = "data_retention") {
                        SettingsSliderItem(
                            title = "History Retention",
                            description = "Number of days to keep detection history",
                            value = settingsState.historyRetentionDays.toFloat(),
                            onValueChange = {
                                settingsState = settingsState.copy(historyRetentionDays = it.toInt())
                            },
                            valueRange = 1f..365f,
                            steps = 0,
                            icon = Icons.Default.DateRange,
                            valueLabel = "${settingsState.historyRetentionDays} days"
                        )
                    }

                    item(key = "data_max_entries") {
                        SettingsStepperItem(
                            title = "Max History Entries",
                            description = "Maximum number of detection events to store",
                            value = settingsState.maxHistoryEntries,
                            onValueChange = {
                                settingsState = settingsState.copy(maxHistoryEntries = it)
                            },
                            minValue = 100,
                            maxValue = 10000,
                            stepSize = 100,
                            icon = Icons.Default.FormatListNumbered
                        )
                    }

                    item(key = "data_auto_delete") {
                        SettingsToggleItem(
                            title = "Auto-Delete Old Entries",
                            description = "Automatically remove entries older than retention period",
                            checked = settingsState.autoDeleteOldEntries,
                            onCheckedChange = {
                                settingsState = settingsState.copy(autoDeleteOldEntries = it)
                            },
                            icon = Icons.Default.AutoDelete
                        )
                    }

                    item(key = "data_export_format") {
                        SettingsDropdownItem(
                            title = "Export Format",
                            description = "File format for exporting detection history",
                            options = listOf("JSON", "CSV", "Plain Text"),
                            selectedOption = settingsState.exportFormat,
                            onOptionSelected = {
                                settingsState = settingsState.copy(exportFormat = it)
                            },
                            icon = Icons.Default.FileDownload
                        )
                    }

                    item(key = "data_encryption") {
                        SettingsToggleItem(
                            title = "Database Encryption",
                            description = "Encrypt the local detection database with AES-256",
                            checked = settingsState.databaseEncryptionEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(databaseEncryptionEnabled = it)
                            },
                            icon = Icons.Default.EnhancedEncryption
                        )
                    }

                    item(key = "data_cache") {
                        SettingsInfoItem(
                            title = "Cache Size",
                            value = settingsState.cacheSize,
                            icon = Icons.Default.Cached,
                            onClick = {}
                        )
                    }

                    item(key = "data_export_btn") {
                        SettingsActionItem(
                            title = "Export Detection History",
                            description = "Save all detection records to a file",
                            icon = Icons.Default.Upload,
                            onClick = { /* trigger export */ }
                        )
                    }

                    item(key = "data_clear_history") {
                        SettingsDangerItem(
                            title = "Clear Detection History",
                            description = "Permanently delete all detection records",
                            icon = Icons.Default.DeleteForever,
                            onClick = { /* clear history */ }
                        )
                    }

                    item(key = "data_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: Advanced
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.ADVANCED }) {
                item(key = "header_advanced") {
                    SettingsSectionHeader(
                        info = allSections[4],
                        isExpanded = expandedSections[SettingsSectionId.ADVANCED] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.ADVANCED] =
                                !(expandedSections[SettingsSectionId.ADVANCED] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.ADVANCED] == true) {
                    item(key = "adv_debounce") {
                        var currentDelay by remember {
                            mutableFloatStateOf(settingsState.debounceDelayMs.toFloat())
                        }
                        SettingsSliderItem(
                            title = "Debounce Delay",
                            description = "Wait time before analyzing text input",
                            value = currentDelay,
                            onValueChange = { currentDelay = it },
                            onValueChangeFinished = {
                                settingsState = settingsState.copy(debounceDelayMs = currentDelay.toLong())
                                onDebounceDelayChanged(currentDelay.toLong())
                            },
                            valueRange = Debouncer.MIN_DELAY_MS.toFloat()..Debouncer.MAX_DELAY_MS.toFloat(),
                            steps = 8,
                            icon = Icons.Default.Timer,
                            valueLabel = "${currentDelay.toLong()}ms"
                        )
                    }

                    item(key = "adv_performance") {
                        SettingsToggleItem(
                            title = "Performance Metrics",
                            description = "Show inference latency and model state on the dashboard",
                            checked = settingsState.isPerformanceMetricsEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(isPerformanceMetricsEnabled = it)
                                onPerformanceMetricsChanged(it)
                            },
                            icon = Icons.Default.Speed
                        )
                    }

                    item(key = "adv_logging") {
                        SettingsToggleItem(
                            title = "Detailed Logging",
                            description = "Enable verbose logging for debugging (may impact performance)",
                            checked = settingsState.detailedLoggingEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(detailedLoggingEnabled = it)
                            },
                            icon = Icons.Default.BugReport
                        )
                    }

                    item(key = "adv_precision") {
                        SettingsDropdownItem(
                            title = "Model Precision",
                            description = "Inference precision mode (lower = faster, higher = more accurate)",
                            options = listOf("FP32", "FP16", "INT8"),
                            selectedOption = settingsState.modelPrecision,
                            onOptionSelected = {
                                settingsState = settingsState.copy(modelPrecision = it)
                            },
                            icon = Icons.Default.Memory
                        )
                    }

                    item(key = "adv_batch") {
                        SettingsToggleItem(
                            title = "Batch Processing",
                            description = "Group multiple inputs for batch inference (reduces latency for rapid inputs)",
                            checked = settingsState.batchProcessingEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(batchProcessingEnabled = it)
                            },
                            icon = Icons.Default.DynamicFeed
                        )
                    }

                    item(key = "adv_background") {
                        SettingsSliderItem(
                            title = "Background Scan Interval",
                            description = "Minutes between background clipboard checks",
                            value = settingsState.backgroundScanInterval.toFloat(),
                            onValueChange = {
                                settingsState = settingsState.copy(backgroundScanInterval = it.toInt())
                            },
                            valueRange = 5f..60f,
                            steps = 10,
                            icon = Icons.Default.Update,
                            valueLabel = "${settingsState.backgroundScanInterval} min"
                        )
                    }

                    item(key = "adv_regex") {
                        SettingsToggleItem(
                            title = "Regex Pre-Screener",
                            description = "Use regex patterns to pre-filter text before ML inference",
                            checked = settingsState.regexPreScreenerEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(regexPreScreenerEnabled = it)
                            },
                            icon = Icons.Default.FilterAlt
                        )
                    }

                    item(key = "adv_threads") {
                        SettingsStepperItem(
                            title = "Inference Threads",
                            description = "Number of CPU threads for model inference",
                            value = settingsState.inferenceThreads,
                            onValueChange = {
                                settingsState = settingsState.copy(inferenceThreads = it)
                            },
                            minValue = 1,
                            maxValue = 8,
                            stepSize = 1,
                            icon = Icons.Default.DeveloperBoard
                        )
                    }

                    // Confidence thresholds sub-section
                    item(key = "adv_thresholds_header") {
                        ConfidenceThresholdsSubSection(
                            thresholds = settingsState.entityThresholds,
                            onThresholdChanged = { type, newThreshold ->
                                settingsState = settingsState.copy(
                                    entityThresholds = settingsState.entityThresholds.toMutableMap().apply {
                                        put(type, newThreshold)
                                    }
                                )
                                ConfidenceThresholds.setThreshold(type, newThreshold)
                            }
                        )
                    }

                    item(key = "adv_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: Security
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.SECURITY }) {
                item(key = "header_security") {
                    SettingsSectionHeader(
                        info = allSections[5],
                        isExpanded = expandedSections[SettingsSectionId.SECURITY] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.SECURITY] =
                                !(expandedSections[SettingsSectionId.SECURITY] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.SECURITY] == true) {
                    item(key = "sec_app_lock") {
                        SettingsToggleItem(
                            title = "App Lock",
                            description = "Require authentication to open PrivacyGuard",
                            checked = settingsState.appLockEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(appLockEnabled = it)
                            },
                            icon = Icons.Default.Lock
                        )
                    }

                    if (settingsState.appLockEnabled) {
                        item(key = "sec_biometric") {
                            SettingsToggleItem(
                                title = "Biometric Authentication",
                                description = "Use fingerprint or face recognition to unlock",
                                checked = settingsState.biometricAuthEnabled,
                                onCheckedChange = {
                                    settingsState = settingsState.copy(biometricAuthEnabled = it)
                                },
                                icon = Icons.Default.Fingerprint
                            )
                        }

                        item(key = "sec_timeout") {
                            SettingsSliderItem(
                                title = "Auto-Lock Timeout",
                                description = "Minutes of inactivity before auto-locking",
                                value = settingsState.autoLockTimeoutMinutes.toFloat(),
                                onValueChange = {
                                    settingsState = settingsState.copy(autoLockTimeoutMinutes = it.toInt())
                                },
                                valueRange = 1f..30f,
                                steps = 28,
                                icon = Icons.Default.LockClock,
                                valueLabel = "${settingsState.autoLockTimeoutMinutes} min"
                            )
                        }
                    }

                    item(key = "sec_recents") {
                        SettingsToggleItem(
                            title = "Hide from Recents",
                            description = "Hide PrivacyGuard from the recent apps list",
                            checked = settingsState.hideFromRecents,
                            onCheckedChange = {
                                settingsState = settingsState.copy(hideFromRecents = it)
                            },
                            icon = Icons.Default.VisibilityOff
                        )
                    }

                    item(key = "sec_screen") {
                        SettingsToggleItem(
                            title = "Secure Screen",
                            description = "Prevent screenshots and screen recording of the app",
                            checked = settingsState.secureScreenEnabled,
                            onCheckedChange = {
                                settingsState = settingsState.copy(secureScreenEnabled = it)
                            },
                            icon = Icons.Default.ScreenLockPortrait
                        )
                    }

                    item(key = "sec_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // SECTION: About
            // ---------------------------------------------------------------
            if (filteredSections.any { it.id == SettingsSectionId.ABOUT }) {
                item(key = "header_about") {
                    SettingsSectionHeader(
                        info = allSections[6],
                        isExpanded = expandedSections[SettingsSectionId.ABOUT] == true,
                        onToggle = {
                            expandedSections[SettingsSectionId.ABOUT] =
                                !(expandedSections[SettingsSectionId.ABOUT] ?: false)
                        }
                    )
                }

                if (expandedSections[SettingsSectionId.ABOUT] == true) {
                    item(key = "about_version") {
                        SettingsInfoItem(
                            title = "Version",
                            value = "1.0.0",
                            icon = Icons.Default.Info
                        )
                    }

                    item(key = "about_build") {
                        SettingsInfoItem(
                            title = "Build Number",
                            value = "2026.03.12.001",
                            icon = Icons.Default.Build
                        )
                    }

                    item(key = "about_model_version") {
                        SettingsInfoItem(
                            title = "ML Model Version",
                            value = "pii-detect-v1.0",
                            icon = Icons.Default.Memory
                        )
                    }

                    item(key = "about_model_status") {
                        SettingsInfoItem(
                            title = "Model Status",
                            value = "Loaded",
                            icon = Icons.Default.CheckCircle,
                            valueColor = SuccessGreen
                        )
                    }

                    item(key = "about_processing") {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = ProtectionActive,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "100% On-Device Processing",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = ProtectionActive
                                    )
                                    Text(
                                        "All analysis happens locally. No data is ever transmitted to any server.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item(key = "about_privacy_policy") {
                        SettingsActionItem(
                            title = "Privacy Policy",
                            description = "View our privacy policy",
                            icon = Icons.Default.Policy,
                            onClick = { /* open privacy policy */ }
                        )
                    }

                    item(key = "about_terms") {
                        SettingsActionItem(
                            title = "Terms of Service",
                            description = "View terms and conditions",
                            icon = Icons.Default.Description,
                            onClick = { /* open terms */ }
                        )
                    }

                    item(key = "about_licenses") {
                        SettingsActionItem(
                            title = "Open Source Licenses",
                            description = "View third-party software licenses",
                            icon = Icons.Default.Code,
                            onClick = { /* open licenses */ }
                        )
                    }

                    item(key = "about_feedback") {
                        SettingsActionItem(
                            title = "Send Feedback",
                            description = "Help us improve PrivacyGuard",
                            icon = Icons.Default.Feedback,
                            onClick = { /* open feedback */ }
                        )
                    }

                    item(key = "about_rate") {
                        SettingsActionItem(
                            title = "Rate on Play Store",
                            description = "If you enjoy PrivacyGuard, please leave a review",
                            icon = Icons.Default.Star,
                            onClick = { /* open store */ }
                        )
                    }

                    item(key = "about_spacer") {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // ---------------------------------------------------------------
            // Reset button at the bottom
            // ---------------------------------------------------------------
            item(key = "reset_all") {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AlertRed.copy(alpha = 0.5f))
                    )
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Reset All Settings to Default")
                }
            }

            // ---------------------------------------------------------------
            // Footer
            // ---------------------------------------------------------------
            item(key = "footer") {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "PrivacyGuard v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "All processing happens on-device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No data is ever transmitted.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Made with care for your privacy.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Section Header component
// ---------------------------------------------------------------------------

/**
 * Animated section header with icon, title, description, and expand/collapse.
 */
@Composable
fun SettingsSectionHeader(
    info: SettingsSectionInfo,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "section_header_chevron"
    )

    val bgAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0.08f else 0f,
        animationSpec = tween(200),
        label = "section_header_bg"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary.copy(alpha = bgAlpha),
        shape = RoundedCornerShape(12.dp),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .semantics {
                    contentDescription = "${info.title} section"
                    role = Role.Button
                    stateDescription = if (isExpanded) "Expanded" else "Collapsed"
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = info.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = info.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!isExpanded) {
                    Text(
                        text = info.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(22.dp)
                    .rotate(chevronRotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable setting item composables
// ---------------------------------------------------------------------------

/**
 * A toggle switch setting row with icon, title, and description.
 */
@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onCheckedChange(!checked) }
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "$title toggle"
                    role = Role.Switch
                    stateDescription = if (checked) "On" else "Off"
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) iconTint else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled
            )
        }
    }
}

/**
 * A slider setting row with icon, title, description, and value label.
 */
@Composable
fun SettingsSliderItem(
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueLabel: String = ""
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (valueLabel.isNotEmpty()) {
                    Text(
                        text = valueLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
                steps = steps
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatRangeValue(valueRange.start),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatRangeValue(valueRange.endInclusive),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatRangeValue(value: Float): String {
    return if (value == value.toLong().toFloat()) value.toLong().toString()
    else "%.1f".format(value)
}

/**
 * A dropdown selector setting row.
 */
@Composable
fun SettingsDropdownItem(
    title: String,
    description: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedOption,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = option,
                                    fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                                )
                                if (option == selectedOption) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * A radio group setting row.
 */
@Composable
fun SettingsRadioGroupItem(
    title: String,
    description: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    optionDescriptions: List<String> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "radio_expand"
    )

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isExpanded) description else "Current: $selectedOption",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    options.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOptionSelected(option) }
                                .then(
                                    if (option == selectedOption)
                                        Modifier.background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        )
                                    else Modifier
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (option == selectedOption),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                                )
                                if (optionDescriptions.size > index) {
                                    Text(
                                        text = optionDescriptions[index],
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A numeric stepper setting row.
 */
@Composable
fun SettingsStepperItem(
    title: String,
    description: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    minValue: Int = 0,
    maxValue: Int = 100,
    stepSize: Int = 1
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledIconButton(
                    onClick = { onValueChange((value - stepSize).coerceAtLeast(minValue)) },
                    enabled = value > minValue,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.defaultMinSize(minWidth = 36.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                FilledIconButton(
                    onClick = { onValueChange((value + stepSize).coerceAtMost(maxValue)) },
                    enabled = value < maxValue,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/**
 * A read-only info row displaying a label and value.
 */
@Composable
fun SettingsInfoItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                fontWeight = FontWeight.SemiBold
            )
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * An action/navigation setting item with chevron.
 */
@Composable
fun SettingsActionItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * A danger/destructive action item with confirmation.
 */
@Composable
fun SettingsDangerItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmation by remember { mutableStateOf(false) }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm $title") },
            text = { Text("$description\n\nThis action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onClick()
                    showConfirmation = false
                }) {
                    Text("Confirm", color = AlertRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { showConfirmation = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AlertRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = AlertRed
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AlertRed.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * A multi-select setting row.
 */
@Composable
fun SettingsMultiSelectItem(
    title: String,
    description: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "multiselect_expand"
    )

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isExpanded) description else "${selectedOptions.size}/${options.size} selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${selectedOptions.size}/${options.size}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    options.forEach { option ->
                        val isSelected = option in selectedOptions
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val newSet = selectedOptions.toMutableSet()
                                    if (isSelected) newSet.remove(option) else newSet.add(option)
                                    onSelectionChanged(newSet)
                                }
                                .then(
                                    if (isSelected)
                                        Modifier.background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        )
                                    else Modifier
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A color picker setting row.
 */
@Composable
fun SettingsColorPickerItem(
    title: String,
    description: String,
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { color ->
                    val isSelected = color == selectedColor
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) Modifier.border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                                else Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                            )
                            .clickable { onColorSelected(color) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Confidence Thresholds Sub-section
// ---------------------------------------------------------------------------

@Composable
fun ConfidenceThresholdsSubSection(
    thresholds: Map<EntityType, Float>,
    onThresholdChanged: (EntityType, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "thresholds_expand"
    )

    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { isExpanded = !isExpanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Confidence Thresholds",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Per-entity type detection sensitivity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(tween(300)) + fadeIn(tween(300)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                thresholds.forEach { (entityType, threshold) ->
                    var currentThreshold by remember(entityType) { mutableFloatStateOf(threshold) }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (entityType.severity.displayName) {
                                                    "Critical" -> SeverityCritical
                                                    "High" -> SeverityHigh
                                                    else -> SeverityMedium
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        entityType.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    "${(currentThreshold * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = currentThreshold,
                                onValueChange = { currentThreshold = it },
                                onValueChangeFinished = {
                                    onThresholdChanged(entityType, currentThreshold)
                                },
                                valueRange = 0.5f..1.0f
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Border import helper (used by color picker)
// ---------------------------------------------------------------------------

private fun Modifier.border(
    width: androidx.compose.ui.unit.Dp,
    color: Color,
    shape: androidx.compose.ui.graphics.Shape
): Modifier = this.then(
    androidx.compose.foundation.border(width, color, shape)
)

// ---------------------------------------------------------------------------
// Preview Composables (8+)
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "SettingsScreen - Full")
@Composable
private fun SettingsScreenFullPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}

@Preview(showBackground = true, name = "SettingsSectionHeader - Expanded")
@Composable
private fun SettingsSectionHeaderExpandedPreview() {
    MaterialTheme {
        SettingsSectionHeader(
            info = allSections[0],
            isExpanded = true,
            onToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsSectionHeader - Collapsed")
@Composable
private fun SettingsSectionHeaderCollapsedPreview() {
    MaterialTheme {
        SettingsSectionHeader(
            info = allSections[1],
            isExpanded = false,
            onToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsToggleItem - On")
@Composable
private fun SettingsToggleItemOnPreview() {
    MaterialTheme {
        SettingsToggleItem(
            title = "Clipboard Monitoring",
            description = "Monitor clipboard for sensitive data",
            checked = true,
            onCheckedChange = {},
            icon = Icons.Default.ContentPaste,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsToggleItem - Off")
@Composable
private fun SettingsToggleItemOffPreview() {
    MaterialTheme {
        SettingsToggleItem(
            title = "Text Field Monitoring",
            description = "Monitor text fields via Accessibility Service",
            checked = false,
            onCheckedChange = {},
            icon = Icons.Default.TextFields,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsSliderItem")
@Composable
private fun SettingsSliderItemPreview() {
    MaterialTheme {
        SettingsSliderItem(
            title = "Debounce Delay",
            description = "Wait time before analyzing text",
            value = 800f,
            onValueChange = {},
            valueRange = 200f..2000f,
            steps = 8,
            icon = Icons.Default.Timer,
            valueLabel = "800ms",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsDropdownItem")
@Composable
private fun SettingsDropdownItemPreview() {
    MaterialTheme {
        SettingsDropdownItem(
            title = "App Theme",
            description = "Choose visual appearance",
            options = listOf("System Default", "Light", "Dark"),
            selectedOption = "System Default",
            onOptionSelected = {},
            icon = Icons.Default.DarkMode,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsStepperItem")
@Composable
private fun SettingsStepperItemPreview() {
    MaterialTheme {
        SettingsStepperItem(
            title = "Max Alerts Per Hour",
            description = "Limit alert frequency",
            value = 10,
            onValueChange = {},
            minValue = 1,
            maxValue = 50,
            icon = Icons.Default.Speed,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SettingsInfoItem")
@Composable
private fun SettingsInfoItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsInfoItem(
                title = "Version",
                value = "1.0.0",
                icon = Icons.Default.Info
            )
            SettingsInfoItem(
                title = "Model Status",
                value = "Loaded",
                icon = Icons.Default.CheckCircle,
                valueColor = SuccessGreen
            )
        }
    }
}

@Preview(showBackground = true, name = "SettingsDangerItem")
@Composable
private fun SettingsDangerItemPreview() {
    MaterialTheme {
        SettingsDangerItem(
            title = "Clear History",
            description = "Delete all detection records",
            icon = Icons.Default.DeleteForever,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Settings - All Sections Overview")
@Composable
private fun SettingsAllSectionsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            allSections.forEach { section ->
                SettingsSectionHeader(
                    info = section,
                    isExpanded = false,
                    onToggle = {}
                )
            }
        }
    }
}
