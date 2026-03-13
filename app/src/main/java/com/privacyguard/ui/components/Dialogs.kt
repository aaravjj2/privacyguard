@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.privacyguard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.UserAction
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.ModelState
import com.privacyguard.ml.Severity
import com.privacyguard.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ==========================================================================
// 1. PII Alert Dialog
// ==========================================================================

/**
 * Data class representing a PII alert for the dialog.
 */
data class PIIAlertData(
    val entityType: EntityType = EntityType.UNKNOWN,
    val severity: Severity = Severity.MEDIUM,
    val confidence: Float = 0f,
    val sourceAppName: String? = null,
    val sourceAppPackage: String? = null,
    val maskedText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val detectionId: String = UUID.randomUUID().toString()
)

/**
 * Actions the user can take in response to a PII alert.
 */
sealed class PIIAlertAction {
    data object Dismiss : PIIAlertAction()
    data object ClearClipboard : PIIAlertAction()
    data class AddToWhitelist(val sourceApp: String?) : PIIAlertAction()
}

/**
 * A full-featured PII alert dialog showing detected PII details with
 * severity coloring, entity information, masked text, and action buttons.
 *
 * @param alert The PII alert data to display.
 * @param onAction Callback for user actions.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PIIAlertDialog(
    alert: PIIAlertData,
    onAction: (PIIAlertAction) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val severityColor = when (alert.severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        modifier = Modifier.semantics {
            contentDescription = "${alert.severity.displayName} PII alert: ${alert.entityType.displayName} detected"
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(severityColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.GppBad,
                    contentDescription = null,
                    tint = severityColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PII Detected",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                SeverityBadge(severity = alert.severity)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Entity type info
                DialogInfoRow(
                    icon = entityTypeIcon(alert.entityType),
                    label = "Type",
                    value = alert.entityType.displayName,
                    valueColor = severityColor
                )

                // Confidence
                DialogInfoRow(
                    icon = Icons.Filled.Assessment,
                    label = "Confidence",
                    value = "${(alert.confidence * 100).toInt()}%"
                )

                // Source app
                alert.sourceAppName?.let {
                    DialogInfoRow(
                        icon = Icons.Filled.Apps,
                        label = "Source",
                        value = it
                    )
                }

                // Masked text
                if (alert.maskedText.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = alert.maskedText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Warning message
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = severityColor.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = severityColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (alert.severity) {
                                Severity.CRITICAL -> "Critical PII detected. This data should be handled with extreme care. Consider clearing the clipboard immediately."
                                Severity.HIGH -> "High sensitivity PII detected. Review the source and consider clearing the clipboard."
                                Severity.MEDIUM -> "Moderate sensitivity PII detected. Monitor the source application."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = severityColor
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAction(PIIAlertAction.ClearClipboard)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = severityColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    Icons.Filled.CleaningServices,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Clipboard")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (alert.sourceAppPackage != null) {
                    TextButton(
                        onClick = {
                            onAction(PIIAlertAction.AddToWhitelist(alert.sourceAppPackage))
                            onDismiss()
                        }
                    ) {
                        Text("Whitelist App")
                    }
                }
                TextButton(onClick = {
                    onAction(PIIAlertAction.Dismiss)
                    onDismiss()
                }) {
                    Text("Dismiss")
                }
            }
        }
    )
}

/**
 * A single info row within a dialog body.
 */
@Composable
private fun DialogInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$label: $value" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

// ==========================================================================
// 2. Confirm Delete Dialog
// ==========================================================================

/**
 * A confirmation dialog for deleting detection events, featuring an
 * undo timer countdown that allows cancellation before permanent deletion.
 *
 * @param eventCount Number of events to be deleted.
 * @param onConfirm Callback when deletion is confirmed.
 * @param onDismiss Callback when the dialog is dismissed/cancelled.
 * @param countdownSeconds Duration of the undo countdown timer.
 */
@Composable
fun ConfirmDeleteDialog(
    eventCount: Int = 1,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    countdownSeconds: Int = 5
) {
    var remainingSeconds by remember { mutableIntStateOf(countdownSeconds) }
    var isCountingDown by remember { mutableStateOf(false) }

    LaunchedEffect(isCountingDown) {
        if (isCountingDown) {
            while (remainingSeconds > 0) {
                kotlinx.coroutines.delay(1000L)
                remainingSeconds--
            }
            onConfirm()
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (isCountingDown) {
                isCountingDown = false
                remainingSeconds = countdownSeconds
            }
            onDismiss()
        },
        modifier = Modifier.semantics {
            contentDescription = "Confirm deletion of $eventCount events"
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AlertRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Delete ${if (eventCount > 1) "$eventCount Events" else "Event"}?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "This will permanently remove ${if (eventCount > 1) "these $eventCount detection events" else "this detection event"} from the encrypted log.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = AlertRed.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AlertRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "This action cannot be undone.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AlertRed
                        )
                    }
                }

                // Countdown indicator
                AnimatedVisibility(
                    visible = isCountingDown,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = remainingSeconds.toFloat() / countdownSeconds,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AlertRed,
                            trackColor = AlertRed.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Deleting in ${remainingSeconds}s... Tap Cancel to stop.",
                            style = MaterialTheme.typography.labelSmall,
                            color = AlertRed,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isCountingDown) {
                Button(
                    onClick = { isCountingDown = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        isCountingDown = false
                        remainingSeconds = countdownSeconds
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            }
        },
        dismissButton = {
            if (!isCountingDown) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

// ==========================================================================
// 3. Export Data Dialog
// ==========================================================================

/**
 * Supported export formats.
 */
enum class ExportFormat(val displayName: String, val extension: String) {
    CSV("CSV", ".csv"),
    JSON("JSON", ".json"),
    TEXT("Plain Text", ".txt")
}

/**
 * Data class for export configuration.
 */
data class ExportConfig(
    val format: ExportFormat = ExportFormat.JSON,
    val includeEntityTypes: Set<EntityType> = EntityType.entries.toSet(),
    val dateRangeLabel: String = "All Time",
    val includeMetadata: Boolean = true,
    val anonymize: Boolean = false
)

/**
 * A dialog for configuring data export with format selection, date range
 * picker, and entity type filtering.
 *
 * @param totalEvents Total number of events available for export.
 * @param onExport Callback with the selected export configuration.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun ExportDataDialog(
    totalEvents: Int = 0,
    onExport: (ExportConfig) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var selectedFormat by remember { mutableStateOf(ExportFormat.JSON) }
    var selectedDateRange by remember { mutableStateOf("All Time") }
    var includeMetadata by remember { mutableStateOf(true) }
    var anonymize by remember { mutableStateOf(false) }
    var selectedEntityTypes by remember { mutableStateOf(EntityType.entries.toSet()) }
    var showEntityFilter by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Export data dialog. $totalEvents events available."
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.FileDownload,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Export Detection Data",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "$totalEvents events available for export",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Format selection
                Text(
                    text = "Export Format",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    ExportFormat.entries.forEach { format ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedFormat == format,
                                    onClick = { selectedFormat = format },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFormat == format,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = format.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Export as ${format.extension} file",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Divider()

                // Date range selection
                Text(
                    text = "Date Range",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All Time", "Last 7 Days", "Last 30 Days", "Today").forEach { range ->
                        FilterChip(
                            selected = selectedDateRange == range,
                            onClick = { selectedDateRange = range },
                            label = {
                                Text(
                                    text = range,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Date range: $range"
                                role = Role.RadioButton
                            }
                        )
                    }
                }

                Divider()

                // Entity type filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEntityFilter = !showEntityFilter },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Entity Types",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${selectedEntityTypes.size} of ${EntityType.entries.size} selected",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (showEntityFilter) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(
                    visible = showEntityFilter,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        EntityType.entries.filter { it != EntityType.UNKNOWN }.forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedEntityTypes = if (selectedEntityTypes.contains(type)) {
                                            selectedEntityTypes - type
                                        } else {
                                            selectedEntityTypes + type
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedEntityTypes.contains(type),
                                    onCheckedChange = { checked ->
                                        selectedEntityTypes = if (checked) {
                                            selectedEntityTypes + type
                                        } else {
                                            selectedEntityTypes - type
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = type.displayName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Divider()

                // Options
                Text(
                    text = "Options",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { includeMetadata = !includeMetadata },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = includeMetadata,
                        onCheckedChange = { includeMetadata = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text("Include Metadata", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text("Add inference time, model version", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { anonymize = !anonymize },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = anonymize,
                        onCheckedChange = { anonymize = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text("Anonymize Data", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text("Remove source app identifiers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onExport(
                        ExportConfig(
                            format = selectedFormat,
                            includeEntityTypes = selectedEntityTypes,
                            dateRangeLabel = selectedDateRange,
                            includeMetadata = includeMetadata,
                            anonymize = anonymize
                        )
                    )
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================================================
// 4. Privacy Report Dialog
// ==========================================================================

/**
 * Data class for privacy report summary.
 */
data class PrivacyReportData(
    val totalDetections: Int = 0,
    val criticalCount: Int = 0,
    val highCount: Int = 0,
    val mediumCount: Int = 0,
    val topEntityType: EntityType? = null,
    val protectionScore: Int = 0,
    val riskLevel: String = "Low",
    val recommendations: List<String> = emptyList(),
    val periodLabel: String = "Last 30 Days"
)

/**
 * A dialog showing a summary privacy report with detection statistics,
 * risk assessment, and actionable recommendations.
 *
 * @param report The privacy report data.
 * @param onDismiss Callback when the dialog is dismissed.
 * @param onExport Callback to export the report.
 */
@Composable
fun PrivacyReportDialog(
    report: PrivacyReportData,
    onDismiss: () -> Unit = {},
    onExport: () -> Unit = {}
) {
    val riskColor = when (report.riskLevel.lowercase()) {
        "critical" -> SeverityCritical
        "high" -> SeverityHigh
        "medium" -> SeverityMedium
        else -> SuccessGreen
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Privacy report for ${report.periodLabel}. Risk level: ${report.riskLevel}."
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Assessment,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Privacy Report",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = report.periodLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Risk level badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = riskColor.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Risk Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = report.riskLevel.uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = riskColor
                        )
                        Text(
                            text = "Protection Score: ${report.protectionScore}/100",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Detection summary
                Text(
                    text = "Detection Summary",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReportStatChip("Total", report.totalDetections.toString(), TrustBlue)
                    ReportStatChip("Critical", report.criticalCount.toString(), SeverityCritical)
                    ReportStatChip("High", report.highCount.toString(), SeverityHigh)
                    ReportStatChip("Medium", report.mediumCount.toString(), SeverityMedium)
                }

                // Most common type
                report.topEntityType?.let {
                    DialogInfoRow(
                        icon = entityTypeIcon(it),
                        label = "Most Common",
                        value = it.displayName
                    )
                }

                // Recommendations
                if (report.recommendations.isNotEmpty()) {
                    Divider()
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    report.recommendations.forEach { recommendation ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = AlertYellow
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = recommendation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onExport()
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ReportStatChip(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics {
            contentDescription = "$label: $value"
        }
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

// ==========================================================================
// 5. Onboarding Permission Dialog
// ==========================================================================

/**
 * A single permission step in the onboarding flow.
 */
data class PermissionStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isGranted: Boolean,
    val permissionKey: String
)

/**
 * A step-by-step onboarding dialog explaining each required permission
 * with clear descriptions of why it is needed.
 *
 * @param steps List of permission steps.
 * @param currentStep Current step index (0-based).
 * @param onGrantPermission Callback when user taps to grant a permission.
 * @param onSkip Callback when user skips a permission.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun OnboardingPermissionDialog(
    steps: List<PermissionStep>,
    currentStep: Int = 0,
    onGrantPermission: (String) -> Unit = {},
    onSkip: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val step = steps.getOrNull(currentStep) ?: return
    val totalSteps = steps.size
    val progress = (currentStep + 1).toFloat() / totalSteps

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false),
        modifier = Modifier.semantics {
            contentDescription = "Permission setup step ${currentStep + 1} of $totalSteps: ${step.title}"
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(TrustBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (step.isGranted) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = TrustBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Step ${currentStep + 1} of $totalSteps",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = TrustBlue,
                    trackColor = TrustBlue.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All data is processed on-device. No information leaves your phone.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (step.isGranted) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Permission granted",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!step.isGranted) {
                Button(
                    onClick = { onGrantPermission(step.permissionKey) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Grant Permission")
                }
            } else {
                Button(
                    onClick = onSkip,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (currentStep < totalSteps - 1) "Next" else "Done")
                }
            }
        },
        dismissButton = {
            if (!step.isGranted) {
                TextButton(onClick = onSkip) {
                    Text("Skip")
                }
            }
        }
    )
}

// ==========================================================================
// 6. Settings Confirmation Dialog
// ==========================================================================

/**
 * A dialog for confirming destructive settings changes such as clearing
 * history, resetting all settings, or disabling monitoring.
 *
 * @param title The title of the confirmation.
 * @param message Descriptive message explaining the action.
 * @param confirmLabel Label for the confirm button.
 * @param isDestructive Whether this is a destructive action (colors button red).
 * @param onConfirm Callback when the user confirms.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun SettingsConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String = "Confirm",
    isDestructive: Boolean = true,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Confirmation: $title"
        },
        icon = {
            Icon(
                imageVector = if (isDestructive) Icons.Filled.Warning else Icons.Filled.Info,
                contentDescription = null,
                tint = if (isDestructive) AlertOrange else TrustBlue,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) AlertRed else TrustBlue
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================================================
// 7. Model Status Dialog
// ==========================================================================

/**
 * Data class for model status information.
 */
data class ModelStatusData(
    val modelName: String = "Melange On-Device NER",
    val architecture: String = "Token Classification (BERT)",
    val version: String = "1.0.0",
    val modelState: ModelState = ModelState.Initializing,
    val modelSizeMb: Float = 28.5f,
    val vocabSize: Int = 30522,
    val maxSequenceLength: Int = 512,
    val totalInferences: Long = 0L,
    val averageLatencyMs: Long = 0L,
    val tokensPerSecond: Float = 0f,
    val lastLoadTimeMs: Long = 0L,
    val quantizationType: String = "INT8",
    val accelerator: String = "NNAPI"
)

/**
 * A detailed dialog showing model information, version, performance
 * statistics, and configuration details.
 *
 * @param data Model status data.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun ModelStatusDialog(
    data: ModelStatusData,
    onDismiss: () -> Unit = {}
) {
    val (stateLabel, stateColor) = when (data.modelState) {
        is ModelState.Ready -> "Ready" to SuccessGreen
        is ModelState.Running -> "Running" to TrustBlue
        is ModelState.Initializing -> "Initializing" to AlertYellow
        is ModelState.Error -> "Error" to AlertRed
        is ModelState.Closed -> "Closed" to ProtectionInactive
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Model status: $stateLabel. ${data.modelName}."
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(stateColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = null,
                    tint = stateColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Model Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stateColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(stateColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stateLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = stateColor
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Model Info section
                DialogSectionHeader("Model Information")
                ModelDetailRow("Name", data.modelName)
                ModelDetailRow("Architecture", data.architecture)
                ModelDetailRow("Version", data.version)
                ModelDetailRow("Size", "${data.modelSizeMb} MB")
                ModelDetailRow("Quantization", data.quantizationType)
                ModelDetailRow("Accelerator", data.accelerator)

                Spacer(modifier = Modifier.height(8.dp))

                // Configuration section
                DialogSectionHeader("Configuration")
                ModelDetailRow("Vocab Size", data.vocabSize.toString())
                ModelDetailRow("Max Sequence", "${data.maxSequenceLength} tokens")

                Spacer(modifier = Modifier.height(8.dp))

                // Performance section
                DialogSectionHeader("Performance")
                ModelDetailRow("Total Inferences", data.totalInferences.toString())
                ModelDetailRow("Avg Latency", "${data.averageLatencyMs}ms")
                ModelDetailRow("Throughput", "${data.tokensPerSecond.toInt()} tok/s")
                ModelDetailRow("Load Time", "${data.lastLoadTimeMs}ms")

                // Error message if in error state
                if (data.modelState is ModelState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = AlertRed.copy(alpha = 0.08f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Error Details",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                            Text(
                                text = (data.modelState as ModelState.Error).message,
                                style = MaterialTheme.typography.bodySmall,
                                color = AlertRed
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun DialogSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = TrustBlue,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ModelDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .semantics { contentDescription = "$label: $value" },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// ==========================================================================
// 8. Clear History Dialog
// ==========================================================================

/**
 * A dialog confirming the clearing of all detection history, showing
 * the event count and a prominent warning that it cannot be undone.
 *
 * @param eventCount Number of events that will be cleared.
 * @param onConfirm Callback when clearing is confirmed.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun ClearHistoryDialog(
    eventCount: Int,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Clear all $eventCount detection events from history"
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AlertRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteSweep,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Clear All History",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Event count highlight
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = eventCount.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                        Text(
                            text = "detection events will be permanently deleted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Warning
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = AlertRed.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = AlertRed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "This cannot be undone",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                            Text(
                                text = "All detection events will be removed from the encrypted log. Statistics will be reset.",
                                style = MaterialTheme.typography.labelSmall,
                                color = AlertRed.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear All History")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================================================
// 9. About Dialog
// ==========================================================================

/**
 * An about dialog showing app version, open source licenses information,
 * and a privacy policy link.
 *
 * @param appVersion The current app version string.
 * @param buildNumber The build number.
 * @param onPrivacyPolicy Callback to open the privacy policy.
 * @param onLicenses Callback to open open source licenses.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun AboutDialog(
    appVersion: String = "1.0.0",
    buildNumber: String = "1",
    onPrivacyPolicy: () -> Unit = {},
    onLicenses: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "About PrivacyGuard version $appVersion"
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PrivacyGuard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version $appVersion (Build $buildNumber)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "On-device PII detection and privacy protection for Android. All processing happens locally on your device -- no data is ever transmitted.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Divider()

                // Links
                AboutLinkRow(
                    icon = Icons.Filled.PrivacyTip,
                    label = "Privacy Policy",
                    onClick = onPrivacyPolicy
                )

                AboutLinkRow(
                    icon = Icons.Filled.Description,
                    label = "Open Source Licenses",
                    onClick = onLicenses
                )

                Divider()

                // Credits
                Text(
                    text = "Built with",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    listOf(
                        "Jetpack Compose (Material 3)",
                        "ONNX Runtime (On-device ML)",
                        "AndroidX Security (Encryption)",
                        "Kotlin Coroutines & Flow"
                    ).forEach { library ->
                        Text(
                            text = library,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun AboutLinkRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = label
                role = Role.Button
            },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TrustBlue
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TrustBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================================================
// 10. Feedback Dialog
// ==========================================================================

/**
 * Category types for feedback.
 */
enum class FeedbackCategory(val displayName: String) {
    BUG("Bug Report"),
    FEATURE("Feature Request"),
    FALSE_POSITIVE("False Positive"),
    PERFORMANCE("Performance Issue"),
    OTHER("Other")
}

/**
 * A feedback dialog with text input and category selection.
 *
 * @param onSubmit Callback with the feedback category and text.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun FeedbackDialog(
    onSubmit: (FeedbackCategory, String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(FeedbackCategory.BUG) }
    var feedbackText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Send feedback dialog"
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Feedback,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "Send Feedback",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeedbackCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }

                // Feedback text input
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(
                            "Describe your feedback...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 6
                )

                Text(
                    text = "${feedbackText.length}/500 characters",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (feedbackText.length > 500)
                        AlertRed else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(selectedCategory, feedbackText)
                    onDismiss()
                },
                enabled = feedbackText.isNotBlank() && feedbackText.length <= 500,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================================================
// 11. Error Dialog
// ==========================================================================

/**
 * An error dialog with error details, copy stack trace button, and retry option.
 *
 * @param title Error title.
 * @param message Error description.
 * @param stackTrace Optional stack trace for debugging.
 * @param onRetry Callback for the retry action.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun ErrorDialog(
    title: String = "An Error Occurred",
    message: String = "",
    stackTrace: String? = null,
    onRetry: (() -> Unit)? = null,
    onDismiss: () -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    var showStackTrace by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = "Error: $title. $message"
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AlertRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = AlertRed,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = AlertRed
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Stack trace section
                if (stackTrace != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showStackTrace = !showStackTrace }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Error Details",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = if (showStackTrace) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(
                        visible = showStackTrace,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = stackTrace,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 15,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                TextButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(stackTrace))
                                        copied = true
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (copied) "Copied" else "Copy Stack Trace",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (onRetry != null) {
                Button(
                    onClick = {
                        onRetry()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retry")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (onRetry != null) "Cancel" else "Close")
            }
        }
    )
}

// ==========================================================================
// Preview composables
// ==========================================================================

@Preview(showBackground = true, name = "PII Alert Dialog - Critical")
@Composable
private fun PIIAlertDialogCriticalPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIAlertDialog(
            alert = PIIAlertData(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                confidence = 0.97f,
                sourceAppName = "Shopping App",
                sourceAppPackage = "com.example.shop",
                maskedText = "****-****-****-4242"
            )
        )
    }
}

@Preview(showBackground = true, name = "PII Alert Dialog - High")
@Composable
private fun PIIAlertDialogHighPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIAlertDialog(
            alert = PIIAlertData(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                confidence = 0.88f,
                sourceAppName = "Email App",
                maskedText = "j***@***.com"
            )
        )
    }
}

@Preview(showBackground = true, name = "Confirm Delete Dialog")
@Composable
private fun ConfirmDeleteDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ConfirmDeleteDialog(eventCount = 5)
    }
}

@Preview(showBackground = true, name = "Confirm Delete - Single")
@Composable
private fun ConfirmDeleteDialogSinglePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ConfirmDeleteDialog(eventCount = 1)
    }
}

@Preview(showBackground = true, name = "Export Data Dialog")
@Composable
private fun ExportDataDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ExportDataDialog(totalEvents = 1247)
    }
}

@Preview(showBackground = true, name = "Privacy Report Dialog")
@Composable
private fun PrivacyReportDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PrivacyReportDialog(
            report = PrivacyReportData(
                totalDetections = 87,
                criticalCount = 5,
                highCount = 18,
                mediumCount = 64,
                topEntityType = EntityType.EMAIL,
                protectionScore = 78,
                riskLevel = "Medium",
                recommendations = listOf(
                    "Enable accessibility service for broader text monitoring",
                    "Review critical detections and clear sensitive clipboard data",
                    "Consider whitelisting trusted password manager apps"
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "Onboarding Permission Dialog")
@Composable
private fun OnboardingPermissionDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        OnboardingPermissionDialog(
            steps = listOf(
                PermissionStep(
                    title = "Accessibility Service",
                    description = "PrivacyGuard needs accessibility access to monitor text fields across apps for PII. This allows real-time detection of sensitive data entry.",
                    icon = Icons.Filled.Accessibility,
                    isGranted = false,
                    permissionKey = "accessibility"
                ),
                PermissionStep(
                    title = "Overlay Permission",
                    description = "Display alert overlays when PII is detected.",
                    icon = Icons.Filled.Layers,
                    isGranted = true,
                    permissionKey = "overlay"
                ),
                PermissionStep(
                    title = "Notifications",
                    description = "Receive notifications about detections.",
                    icon = Icons.Filled.Notifications,
                    isGranted = false,
                    permissionKey = "notification"
                )
            ),
            currentStep = 0
        )
    }
}

@Preview(showBackground = true, name = "Settings Confirmation Dialog")
@Composable
private fun SettingsConfirmationDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        SettingsConfirmationDialog(
            title = "Disable Clipboard Monitoring?",
            message = "Disabling clipboard monitoring will stop PII detection for clipboard content. You can re-enable this in settings at any time.",
            confirmLabel = "Disable"
        )
    }
}

@Preview(showBackground = true, name = "Model Status Dialog - Ready")
@Composable
private fun ModelStatusDialogReadyPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ModelStatusDialog(
            data = ModelStatusData(
                modelState = ModelState.Ready,
                totalInferences = 1247,
                averageLatencyMs = 42,
                tokensPerSecond = 156.5f,
                lastLoadTimeMs = 1850
            )
        )
    }
}

@Preview(showBackground = true, name = "Model Status Dialog - Error")
@Composable
private fun ModelStatusDialogErrorPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ModelStatusDialog(
            data = ModelStatusData(
                modelState = ModelState.Error("Out of memory: failed to allocate tensor buffer"),
                totalInferences = 42,
                averageLatencyMs = 0,
                tokensPerSecond = 0f
            )
        )
    }
}

@Preview(showBackground = true, name = "Clear History Dialog")
@Composable
private fun ClearHistoryDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ClearHistoryDialog(eventCount = 1247)
    }
}

@Preview(showBackground = true, name = "About Dialog")
@Composable
private fun AboutDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        AboutDialog(
            appVersion = "1.0.0",
            buildNumber = "42"
        )
    }
}

@Preview(showBackground = true, name = "Feedback Dialog")
@Composable
private fun FeedbackDialogPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        FeedbackDialog()
    }
}

@Preview(showBackground = true, name = "Error Dialog - With Stack Trace")
@Composable
private fun ErrorDialogWithStackTracePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ErrorDialog(
            title = "Model Initialization Failed",
            message = "The NER model could not be loaded. This may be due to insufficient memory or a corrupted model file.",
            stackTrace = "java.lang.OutOfMemoryError: Failed to allocate a 28MB tensor\n\tat ai.onnxruntime.OnnxTensor.createTensor(OnnxTensor.java:42)\n\tat com.privacyguard.ml.PrivacyModel.init(PrivacyModel.kt:85)\n\tat com.privacyguard.ml.PrivacyModel.getInstance(PrivacyModel.kt:28)",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Error Dialog - Simple")
@Composable
private fun ErrorDialogSimplePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ErrorDialog(
            title = "Network Error",
            message = "Could not check for model updates. Please check your internet connection.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Error Dialog - No Retry")
@Composable
private fun ErrorDialogNoRetryPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ErrorDialog(
            title = "Unsupported Device",
            message = "This device does not support the required hardware acceleration for on-device inference."
        )
    }
}
