package com.privacyguard.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.privacyguard.ml.EntityType
import com.privacyguard.ui.theme.*
import com.privacyguard.util.ConfidenceThresholds
import com.privacyguard.util.Debouncer

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
    var showResetDialog by remember { mutableStateOf(false) }
    var showThresholds by remember { mutableStateOf(false) }
    var currentDebounceDelay by remember { mutableStateOf(debounceDelayMs.toFloat()) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all settings to their default values? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onResetSettings()
                    showResetDialog = false
                }) { Text("Reset", color = AlertRed) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Monitoring Section
            item {
                Text(
                    "Monitoring",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingsToggleItem(
                    title = "Clipboard Monitoring",
                    description = "Monitor clipboard for sensitive data like credit cards and SSNs",
                    checked = isClipboardMonitoringEnabled,
                    onCheckedChange = onClipboardMonitoringChanged
                )
            }

            item {
                SettingsToggleItem(
                    title = "Text Field Monitoring",
                    description = "Monitor text fields across apps via Accessibility Service",
                    checked = isTextFieldMonitoringEnabled,
                    onCheckedChange = onTextFieldMonitoringChanged
                )
            }

            // Analysis Section
            item {
                Text(
                    "Analysis",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Debounce Delay", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text(
                            "Wait time before analyzing text (${currentDebounceDelay.toLong()}ms)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Slider(
                            value = currentDebounceDelay,
                            onValueChange = { currentDebounceDelay = it },
                            onValueChangeFinished = { onDebounceDelayChanged(currentDebounceDelay.toLong()) },
                            valueRange = Debouncer.MIN_DELAY_MS.toFloat()..Debouncer.MAX_DELAY_MS.toFloat(),
                            steps = 8
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${Debouncer.MIN_DELAY_MS}ms", style = MaterialTheme.typography.labelSmall)
                            Text("${Debouncer.MAX_DELAY_MS}ms", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Confidence Thresholds
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showThresholds = !showThresholds }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Confidence Thresholds", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text("Per-entity type detection sensitivity", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(
                            if (showThresholds) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
            }

            if (showThresholds) {
                val thresholdTypes = listOf(
                    EntityType.CREDIT_CARD, EntityType.SSN, EntityType.PASSWORD,
                    EntityType.API_KEY, EntityType.EMAIL, EntityType.PHONE,
                    EntityType.PERSON_NAME, EntityType.ADDRESS
                )

                items(thresholdTypes.size) { index ->
                    val type = thresholdTypes[index]
                    var threshold by remember {
                        mutableStateOf(ConfidenceThresholds.getThreshold(type))
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(type.displayName, style = MaterialTheme.typography.bodyMedium)
                                Text("${(threshold * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = threshold,
                                onValueChange = { threshold = it },
                                onValueChangeFinished = {
                                    ConfidenceThresholds.setThreshold(type, threshold)
                                },
                                valueRange = 0.5f..1.0f
                            )
                        }
                    }
                }
            }

            // Display Section
            item {
                Text(
                    "Display",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                SettingsToggleItem(
                    title = "Performance Metrics",
                    description = "Show inference latency and model state on the dashboard",
                    checked = isPerformanceMetricsEnabled,
                    onCheckedChange = onPerformanceMetricsChanged
                )
            }

            // Reset
            item {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Reset All Settings to Default")
                }
            }

            // Version info
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "PrivacyGuard v1.0.0\nAll processing happens on-device. No data is ever transmitted.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
