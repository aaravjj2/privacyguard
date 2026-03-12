package com.privacyguard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Color utilities (local to this file)
// ---------------------------------------------------------------------------

/** Returns a color associated with the given [Severity] level. */
private fun Severity.toColor(): Color = when (this) {
    Severity.CRITICAL -> Color(0xFFE53935)
    Severity.HIGH     -> Color(0xFFF57C00)
    Severity.MEDIUM   -> Color(0xFFF9A825)
    else              -> Color(0xFF43A047)
}

/** Returns a color associated with the given [EntityType] for chart bars. */
private fun EntityType.toChartColor(): Color = when (this) {
    EntityType.CREDIT_CARD  -> Color(0xFFE53935)
    EntityType.SSN          -> Color(0xFFD81B60)
    EntityType.PASSWORD     -> Color(0xFF8E24AA)
    EntityType.API_KEY      -> Color(0xFF5E35B1)
    EntityType.EMAIL        -> Color(0xFF1E88E5)
    EntityType.PHONE        -> Color(0xFF039BE5)
    EntityType.MEDICAL_ID   -> Color(0xFF00ACC1)
    EntityType.ADDRESS      -> Color(0xFF43A047)
    EntityType.DATE_OF_BIRTH -> Color(0xFF7CB342)
    EntityType.PERSON_NAME  -> Color(0xFFF9A825)
    EntityType.UNKNOWN      -> Color(0xFF757575)
}

/** Returns an icon vector for the given [EntityType]. */
private fun EntityType.toIcon(): ImageVector = when (this) {
    EntityType.CREDIT_CARD   -> Icons.Filled.CreditCard
    EntityType.SSN           -> Icons.Filled.Badge
    EntityType.PASSWORD      -> Icons.Filled.Lock
    EntityType.API_KEY       -> Icons.Filled.Key
    EntityType.EMAIL         -> Icons.Filled.Email
    EntityType.PHONE         -> Icons.Filled.Phone
    EntityType.MEDICAL_ID    -> Icons.Filled.MedicalServices
    EntityType.ADDRESS       -> Icons.Filled.Home
    EntityType.DATE_OF_BIRTH -> Icons.Filled.Cake
    EntityType.PERSON_NAME   -> Icons.Filled.Person
    EntityType.UNKNOWN       -> Icons.Filled.HelpOutline
}

// ---------------------------------------------------------------------------
// Main PrivacyReportScreen
// ---------------------------------------------------------------------------

/**
 * The Privacy Report screen provides a comprehensive privacy analytics view
 * covering the selected time period. It includes:
 *
 * - Date range selector tabs (Today / 7 Days / 30 Days / Custom)
 * - Summary statistic cards with trend indicators
 * - A Canvas-rendered PII-type bar chart
 * - A Canvas-rendered risk trend line chart
 * - A ranked entity-type breakdown list with progress bars
 * - An app-by-app detection breakdown
 * - Export (PDF, CSV, JSON) and share controls
 * - Animated shimmer loading state and empty/error states
 *
 * @param viewModel         The [PrivacyReportViewModel] managing this screen's data.
 * @param onNavigateBack    Callback invoked when the back button is pressed.
 * @param onNavigateToEvent Callback invoked when the user taps a detection event.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyReportScreen(
    viewModel: PrivacyReportViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEvent: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Trigger load on first composition
    LaunchedEffect(Unit) {
        viewModel.loadReport()
    }

    // Consume share content
    LaunchedEffect(uiState.shareContent) {
        uiState.shareContent?.let { viewModel.clearShareContent() }
    }

    // Surface export errors as snackbars
    LaunchedEffect(uiState.exportStatus) {
        if (uiState.exportStatus is ReportExportStatus.Failed) {
            val status = uiState.exportStatus as ReportExportStatus.Failed
            snackbarHostState.showSnackbar(
                message = status.reason,
                duration = SnackbarDuration.Long
            )
            viewModel.clearExportStatus()
        }
    }

    Scaffold(
        topBar = {
            ReportTopAppBar(
                title = if (uiState.reportTitle.isEmpty()) "Privacy Report" else uiState.reportTitle,
                isRefreshing = uiState.isRefreshing,
                onNavigateBack = onNavigateBack,
                onRefresh = { viewModel.refreshStats() },
                onShare = { viewModel.shareReport() },
                onExport = { viewModel.showExportDialog() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                ReportShimmerLoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.errorMessage != null -> {
                ReportErrorStateScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadReport() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Date range picker row
                    item {
                        DateRangePickerRow(
                            selectedPeriod = uiState.selectedPeriod,
                            onPeriodSelected = { period ->
                                if (period == ReportPeriod.CUSTOM) {
                                    viewModel.showDateRangePicker()
                                } else {
                                    viewModel.setReportPeriod(period)
                                }
                            },
                            customRangeLabel = if (uiState.isCustomRangeValid) uiState.reportDateRangeLabel else null
                        )
                    }

                    // No data empty state
                    if (!uiState.hasData) {
                        item {
                            ReportEmptyStateSection(
                                period = uiState.selectedPeriod,
                                modifier = Modifier.fillParentMaxHeight(0.6f)
                            )
                        }
                    } else {
                        // Summary cards
                        item {
                            StatsSummarySection(uiState = uiState)
                        }

                        // Period comparison banner
                        uiState.weeklyComparison?.let { comparison ->
                            item {
                                PeriodComparisonBanner(
                                    comparison = comparison,
                                    period = uiState.selectedPeriod
                                )
                            }
                        }

                        // PII bar chart
                        if (uiState.entityTypeStats.isNotEmpty()) {
                            item {
                                PIITypeBarChartSection(
                                    stats = uiState.entityTypeStats,
                                    totalCount = uiState.totalDetections
                                )
                            }
                        }

                        // Risk trend chart
                        if (uiState.riskTrendPoints.size >= 2) {
                            item {
                                RiskTrendChartSection(
                                    trendPoints = uiState.riskTrendPoints,
                                    dailyPoints = uiState.dailyStatPoints
                                )
                            }
                        }

                        // Top entity types
                        if (uiState.entityTypeStats.isNotEmpty()) {
                            item {
                                TopEntityTypesSection(stats = uiState.entityTypeStats)
                            }
                        }

                        // App breakdown
                        if (uiState.appStats.isNotEmpty()) {
                            item {
                                AppBreakdownSection(
                                    appStats = uiState.appStats,
                                    onAppClicked = {}
                                )
                            }
                        }

                        // Export section
                        item {
                            ExportReportSection(
                                onExportPdf = { viewModel.exportReport(ReportExportFormat.PDF) },
                                onExportCsv = { viewModel.exportReport(ReportExportFormat.CSV) },
                                onExportJson = { viewModel.exportReport(ReportExportFormat.JSON) },
                                onShare = { viewModel.shareReport() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Export dialog
    if (uiState.showExportDialog) {
        ExportDialog(
            onExport = { format -> viewModel.exportReport(format) },
            onDismiss = { viewModel.dismissExportDialog() }
        )
    }

    // Export progress / success dialog
    when (val status = uiState.exportStatus) {
        is ReportExportStatus.Generating -> {
            ExportProgressDialog(
                format = status.format,
                progressPercent = status.progressPercent
            )
        }
        is ReportExportStatus.Ready -> {
            ExportSuccessDialog(
                filename = status.filename,
                eventCount = status.eventCount,
                format = status.format,
                onDismiss = { viewModel.clearExportStatus() }
            )
        }
        else -> { /* idle or failed — handled by snackbar */ }
    }

    // Custom date range picker dialog
    if (uiState.showDateRangePicker) {
        CustomDateRangePickerDialog(
            initialStartMs = uiState.customStartMs.takeIf { it > 0L }
                ?: (System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L),
            initialEndMs = uiState.customEndMs.takeIf { it > 0L }
                ?: System.currentTimeMillis(),
            onApply = { startMs, endMs -> viewModel.setCustomDateRange(startMs, endMs) },
            onDismiss = { viewModel.dismissDateRangePicker() }
        )
    }
}

// ---------------------------------------------------------------------------
// Top App Bar
// ---------------------------------------------------------------------------

/**
 * Top app bar for the Privacy Report screen.
 *
 * Includes a back navigation button, the report title, and action icons
 * for refresh, share, and export.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopAppBar(
    title: String,
    isRefreshing: Boolean,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onShare: () -> Unit,
    onExport: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Privacy Analytics",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share"
                )
            }
            IconButton(onClick = onExport) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Export"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// ---------------------------------------------------------------------------
// Date Range Picker Row
// ---------------------------------------------------------------------------

/**
 * Horizontally scrollable row of filter chips for the report period.
 * Renders one chip per [ReportPeriod] value.
 *
 * @param selectedPeriod    The currently active period.
 * @param onPeriodSelected  Callback invoked with the newly selected period.
 * @param customRangeLabel  Optional label string for the custom range chip;
 *                          if null the chip shows "Custom".
 */
@Composable
private fun DateRangePickerRow(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit,
    customRangeLabel: String?
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ReportPeriod.entries) { period ->
            val label = when {
                period == ReportPeriod.CUSTOM && customRangeLabel != null -> customRangeLabel
                else -> period.shortLabel
            }

            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                leadingIcon = if (period == ReportPeriod.CUSTOM) {
                    {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Custom Date Range Picker Dialog
// ---------------------------------------------------------------------------

/**
 * A dialog that lets the user pick a custom start and end date for the report.
 *
 * The current implementation uses a simplified month-day picker based on
 * offset from today. In a production build this would use a
 * [DateRangePicker] from Material3 when the API is stable.
 *
 * @param initialStartMs  Epoch ms to pre-populate the start date.
 * @param initialEndMs    Epoch ms to pre-populate the end date.
 * @param onApply         Callback invoked with (startMs, endMs) when confirmed.
 * @param onDismiss       Callback invoked when the dialog is dismissed.
 */
@Composable
private fun CustomDateRangePickerDialog(
    initialStartMs: Long,
    initialEndMs: Long,
    onApply: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    // Simple approach: offer preset offsets from "today"
    val presets = listOf(
        Pair("Yesterday", 1) ,
        Pair("Last 3 Days", 3),
        Pair("Last 7 Days", 7),
        Pair("Last 14 Days", 14),
        Pair("Last 30 Days", 30),
        Pair("Last 60 Days", 60),
        Pair("Last 90 Days", 90)
    )

    var selectedPresetIndex by remember { mutableIntStateOf(1) }
    val now = System.currentTimeMillis()
    val millisPerDay = 24 * 60 * 60 * 1000L

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Custom Date Range",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Select a time window for the report:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                presets.forEachIndexed { index, (label, days) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedPresetIndex = index }
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPresetIndex == index,
                            onClick = { selectedPresetIndex = index }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val start = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                .format(Date(now - days * millisPerDay))
                            val end = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                .format(Date(now))
                            Text(
                                text = "$start – $end",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val daysBack = presets[selectedPresetIndex].second.toLong()
                    val startMs = now - daysBack * millisPerDay
                    onApply(startMs, now)
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Statistics Summary Cards
// ---------------------------------------------------------------------------

/**
 * A 2×2 grid of summary statistic cards at the top of the report.
 *
 * Displays: total detections, unique apps, average risk score, and peak risk.
 */
@Composable
private fun StatsSummarySection(uiState: PrivacyReportUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ReportSectionHeader(
            title = "Summary",
            subtitle = uiState.effectiveDateRangeLabel
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatSummaryCard(
                modifier = Modifier.weight(1f),
                label = "Total Detections",
                value = uiState.totalDetections.toString(),
                icon = Icons.Filled.Shield,
                iconTint = MaterialTheme.colorScheme.primary,
                subtitle = "${uiState.uniqueEntityTypesCount} PII types"
            )
            StatSummaryCard(
                modifier = Modifier.weight(1f),
                label = "Source Apps",
                value = uiState.uniqueAppsCount.toString(),
                icon = Icons.Filled.Apps,
                iconTint = MaterialTheme.colorScheme.tertiary,
                subtitle = "apps detected"
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val avgRiskColor = when {
                uiState.averageRiskScore >= 70 -> Color(0xFFE53935)
                uiState.averageRiskScore >= 40 -> Color(0xFFF57C00)
                else -> Color(0xFF43A047)
            }
            StatSummaryCard(
                modifier = Modifier.weight(1f),
                label = "Avg Risk Score",
                value = "${uiState.averageRiskScore}/100",
                icon = Icons.Filled.Analytics,
                iconTint = avgRiskColor,
                subtitle = when {
                    uiState.averageRiskScore >= 70 -> "High risk"
                    uiState.averageRiskScore >= 40 -> "Moderate risk"
                    else -> "Low risk"
                }
            )
            StatSummaryCard(
                modifier = Modifier.weight(1f),
                label = "Avg Confidence",
                value = "${(uiState.averageConfidence * 100).roundToInt()}%",
                icon = Icons.Filled.Verified,
                iconTint = MaterialTheme.colorScheme.secondary,
                subtitle = "model confidence"
            )
        }
    }
}

/**
 * A single statistic card showing an icon, value, label, and subtitle.
 *
 * Uses a subtle elevation and background tint consistent with Material3.
 *
 * @param modifier  The modifier to apply to this card.
 * @param label     The name of the statistic (displayed above the value).
 * @param value     The primary numeric or text value to display prominently.
 * @param icon      The icon displayed to the left of the value.
 * @param iconTint  The tint color for the icon and value text.
 * @param subtitle  A secondary description line below the value.
 */
@Composable
private fun StatSummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    subtitle: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = iconTint,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Period Comparison Banner
// ---------------------------------------------------------------------------

/**
 * A horizontal banner card showing the change in detections vs. the previous
 * equivalent time window.
 *
 * @param comparison The [WeeklyComparison] data.
 * @param period     The active report period for label context.
 */
@Composable
private fun PeriodComparisonBanner(
    comparison: WeeklyComparison,
    period: ReportPeriod
) {
    val changeSign = if (comparison.countChangePercent > 0) "+" else ""
    val changeStr = "${changeSign}${comparison.countChangePercent.roundToInt()}%"

    val (bannerColor, changeColor, trendIcon, trendDesc) = if (comparison.isImproving) {
        Quadruple(
            Color(0xFF1B5E20).copy(alpha = 0.1f),
            Color(0xFF2E7D32),
            Icons.Filled.TrendingDown,
            "Fewer detections than the previous ${period.shortLabel.lowercase()}"
        )
    } else {
        Quadruple(
            Color(0xFFB71C1C).copy(alpha = 0.1f),
            Color(0xFFC62828),
            Icons.Filled.TrendingUp,
            "More detections than the previous ${period.shortLabel.lowercase()}"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bannerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = trendIcon,
                contentDescription = null,
                tint = changeColor,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trendDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${comparison.currentPeriodCount} now vs ${comparison.previousPeriodCount} before",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = changeStr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = changeColor
            )
        }
    }
}

/** Helper data class to allow destructuring of four values. */
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ---------------------------------------------------------------------------
// PII Type Bar Chart
// ---------------------------------------------------------------------------

/**
 * Section containing the PII-type breakdown bar chart.
 *
 * @param stats      The [EntityTypeStats] list sorted by count descending.
 * @param totalCount The total number of events in the period (for percentage labels).
 */
@Composable
private fun PIITypeBarChartSection(
    stats: List<EntityTypeStats>,
    totalCount: Int
) {
    ReportCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportSectionHeader(
                title = "PII Type Breakdown",
                subtitle = "$totalCount total detections",
                icon = Icons.Outlined.PieChart
            )

            PIITypeBarChart(
                stats = stats,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            // Legend
            BarChartLegend(stats = stats.take(6))
        }
    }
}

/**
 * Canvas-rendered horizontal bar chart showing PII type detection counts.
 *
 * Each bar:
 * - Is colored with the entity type's distinctive chart color
 * - Has rounded right corners
 * - Shows the count label at its right edge
 * - Has an animated entrance from width=0 to full width
 *
 * @param stats    Sorted list of entity type stats to render.
 * @param modifier Modifier for the Canvas composable.
 */
@Composable
private fun PIITypeBarChart(
    stats: List<EntityTypeStats>,
    modifier: Modifier = Modifier
) {
    // Entrance animation
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "barChartAnimation"
    )

    val displayStats = stats.take(8)
    val maxCount = displayStats.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    val density = LocalDensity.current

    val labelColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val countLabelSp = with(density) { 11.sp.toPx() }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val leftPadding = 140f
        val rightPadding = 50f
        val topPadding = 8f
        val bottomPadding = 16f

        val chartWidth = canvasWidth - leftPadding - rightPadding
        val chartHeight = canvasHeight - topPadding - bottomPadding

        val barSlotHeight = chartHeight / displayStats.size
        val barHeight = (barSlotHeight * 0.6f).coerceAtMost(32f)
        val barVerticalPadding = (barSlotHeight - barHeight) / 2f

        // Draw vertical grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val x = leftPadding + (chartWidth * i / gridLines)
            drawLine(
                color = gridColor,
                start = Offset(x, topPadding),
                end = Offset(x, topPadding + chartHeight),
                strokeWidth = 1f
            )
        }

        displayStats.forEachIndexed { index, stat ->
            val barTop = topPadding + index * barSlotHeight + barVerticalPadding
            val barMaxWidth = chartWidth * (stat.count.toFloat() / maxCount)
            val animatedBarWidth = barMaxWidth * animationProgress

            // Draw bar background (track)
            drawRoundRect(
                color = gridColor.copy(alpha = 0.5f),
                topLeft = Offset(leftPadding, barTop),
                size = Size(chartWidth, barHeight),
                cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
            )

            // Draw filled bar
            if (animatedBarWidth > 0f) {
                drawRoundRect(
                    color = stat.entityType.toChartColor(),
                    topLeft = Offset(leftPadding, barTop),
                    size = Size(animatedBarWidth, barHeight),
                    cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
                )
            }

            // Draw label on the left (entity type name)
            drawContext.canvas.nativeCanvas.drawText(
                stat.entityType.displayName.take(16),
                leftPadding - 8f,
                barTop + barHeight / 2f + countLabelSp / 3,
                android.graphics.Paint().apply {
                    color = labelColor.toArgb()
                    textSize = countLabelSp
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
            )

            // Draw count label to the right of the bar
            if (animationProgress >= 0.9f) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${stat.count}",
                    leftPadding + animatedBarWidth + 6f,
                    barTop + barHeight / 2f + countLabelSp / 3,
                    android.graphics.Paint().apply {
                        color = labelColor.copy(alpha = 0.7f).toArgb()
                        textSize = countLabelSp
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

/**
 * Color legend for the PII type bar chart.
 *
 * @param stats The entity type stats whose colors and names are shown.
 */
@Composable
private fun BarChartLegend(stats: List<EntityTypeStats>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        stats.forEach { stat ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(stat.entityType.toChartColor())
                )
                Text(
                    text = stat.entityType.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Risk Trend Line Chart
// ---------------------------------------------------------------------------

/**
 * Section card containing the risk trend line chart.
 *
 * @param trendPoints  The smoothed risk trend points for the line.
 * @param dailyPoints  The raw daily points used for annotation.
 */
@Composable
private fun RiskTrendChartSection(
    trendPoints: List<RiskTrendPoint>,
    dailyPoints: List<DailyStatPoint>
) {
    ReportCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportSectionHeader(
                title = "Risk Trend",
                subtitle = "Risk score over time (0–100)",
                icon = Icons.Outlined.ShowChart
            )

            RiskTrendLineChart(
                trendPoints = trendPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // Descriptive footnote
            val maxRisk = trendPoints.maxOfOrNull { it.riskScore } ?: 0
            val minRisk = trendPoints.minOfOrNull { it.riskScore } ?: 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RiskLegendItem(label = "Peak", value = maxRisk, isHighlighted = maxRisk >= 70)
                RiskLegendItem(label = "Lowest", value = minRisk, isHighlighted = false)
                val avg = trendPoints.map { it.riskScore }.average().roundToInt()
                RiskLegendItem(label = "Average", value = avg, isHighlighted = avg >= 50)
            }
        }
    }
}

/**
 * Canvas-rendered line chart showing the risk score trend over the report period.
 *
 * Features:
 * - Filled gradient area under the trend line
 * - Smooth cubic bezier path
 * - Horizontal reference lines at 25, 50, 75
 * - X-axis day labels
 * - Data point circles on each trend point
 * - Animated draw-in from left to right
 *
 * @param trendPoints The list of risk trend data points.
 * @param modifier    Modifier for the Canvas composable.
 */
@Composable
private fun RiskTrendLineChart(
    trendPoints: List<RiskTrendPoint>,
    modifier: Modifier = Modifier
) {
    if (trendPoints.isEmpty()) return

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "lineChartAnimation"
    )

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    val labelColor = MaterialTheme.colorScheme.onSurface
    val dangerColor = Color(0xFFE53935)
    val warningColor = Color(0xFFF57C00)
    val density = LocalDensity.current
    val labelSp = with(density) { 10.sp.toPx() }

    Canvas(modifier = modifier) {
        val canvasW = size.width
        val canvasH = size.height
        val leftPad = 36f
        val rightPad = 16f
        val topPad = 16f
        val bottomPad = 28f

        val chartW = canvasW - leftPad - rightPad
        val chartH = canvasH - topPad - bottomPad

        // Draw horizontal grid lines at 0, 25, 50, 75, 100
        val gridValues = listOf(0, 25, 50, 75, 100)
        gridValues.forEach { value ->
            val y = topPad + chartH * (1f - value / 100f)
            val gridLineColor = when {
                value == 75 -> warningColor.copy(alpha = 0.3f)
                value == 50 -> warningColor.copy(alpha = 0.15f)
                else -> gridColor
            }
            drawLine(
                color = gridLineColor,
                start = Offset(leftPad, y),
                end = Offset(leftPad + chartW, y),
                strokeWidth = if (value % 25 == 0) 1.5f else 0.8f,
                pathEffect = if (value == 0 || value == 100) null else PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )

            // Y-axis label
            drawContext.canvas.nativeCanvas.drawText(
                "$value",
                leftPad - 4f,
                y + labelSp / 3,
                android.graphics.Paint().apply {
                    color = labelColor.copy(alpha = 0.5f).toArgb()
                    textSize = labelSp * 0.9f
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
            )
        }

        // Compute screen coordinates for all data points
        val n = trendPoints.size
        val xStep = chartW / (n - 1).coerceAtLeast(1)

        data class Pt(val x: Float, val y: Float)

        val screenPoints = trendPoints.mapIndexed { i, point ->
            val x = leftPad + i * xStep
            val y = topPad + chartH * (1f - point.riskScore / 100f)
            Pt(x, y)
        }

        // Draw filled gradient area (clipped to animated progress)
        clipRect(right = leftPad + chartW * animProgress) {
            val areaPath = Path()
            areaPath.moveTo(screenPoints.first().x, topPad + chartH)
            screenPoints.forEach { pt -> areaPath.lineTo(pt.x, pt.y) }
            areaPath.lineTo(screenPoints.last().x, topPad + chartH)
            areaPath.close()

            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.25f),
                        lineColor.copy(alpha = 0.02f)
                    ),
                    startY = topPad,
                    endY = topPad + chartH
                )
            )

            // Draw the line itself
            val linePath = Path()
            screenPoints.forEachIndexed { i, pt ->
                if (i == 0) linePath.moveTo(pt.x, pt.y)
                else {
                    val prev = screenPoints[i - 1]
                    val cpX = (prev.x + pt.x) / 2f
                    linePath.cubicTo(cpX, prev.y, cpX, pt.y, pt.x, pt.y)
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )
        }

        // Draw data point circles (only when animation is complete)
        if (animProgress >= 0.98f) {
            screenPoints.forEachIndexed { i, pt ->
                val riskVal = trendPoints[i].riskScore
                val dotColor = when {
                    riskVal >= 70 -> dangerColor
                    riskVal >= 40 -> warningColor
                    else -> lineColor
                }
                drawCircle(
                    color = MaterialTheme.colorScheme.surface,
                    radius = 5f,
                    center = Offset(pt.x, pt.y)
                )
                drawCircle(
                    color = dotColor,
                    radius = 4f,
                    center = Offset(pt.x, pt.y)
                )
            }
        }

        // Draw x-axis labels (every other point for readability)
        val labelStep = if (n <= 7) 1 else if (n <= 14) 2 else 3
        screenPoints.forEachIndexed { i, pt ->
            if (i % labelStep == 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    trendPoints[i].label,
                    pt.x,
                    topPad + chartH + bottomPad - 4f,
                    android.graphics.Paint().apply {
                        color = labelColor.copy(alpha = 0.6f).toArgb()
                        textSize = labelSp
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

/**
 * A small labeled statistic chip used in the risk trend legend row.
 *
 * @param label         The stat label (e.g., "Peak", "Average").
 * @param value         The numeric score to display.
 * @param isHighlighted If true, renders the value in a warning/danger color.
 */
@Composable
private fun RiskLegendItem(label: String, value: Int, isHighlighted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isHighlighted) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ---------------------------------------------------------------------------
// Top Entity Types Section
// ---------------------------------------------------------------------------

/**
 * A ranked list section showing the top PII types detected in the period.
 *
 * Each row shows the entity type name, count, progress bar (relative to the
 * top entity), average confidence badge, and trend indicator.
 *
 * @param stats The entity type stats sorted by count descending.
 */
@Composable
private fun TopEntityTypesSection(stats: List<EntityTypeStats>) {
    ReportCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ReportSectionHeader(
                title = "Top Detected Types",
                subtitle = "Ranked by detection count",
                icon = Icons.Outlined.List
            )
            Spacer(Modifier.height(4.dp))

            val maxCount = stats.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

            stats.take(8).forEachIndexed { index, stat ->
                EntityTypeStatRow(
                    rank = index + 1,
                    stat = stat,
                    maxCount = maxCount
                )
                if (index < stats.take(8).lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

/**
 * A single row in the top entity types list.
 *
 * Layout:
 * ```
 * [rank] [icon] [name]                [trend] [count]
 *               [────────────────░░░░░░░░░░░░]  72%
 *               Avg conf: 87%  HIGH
 * ```
 *
 * @param rank     1-based rank of this entity type.
 * @param stat     The [EntityTypeStats] to display.
 * @param maxCount The maximum count across all displayed types (for bar scaling).
 */
@Composable
private fun EntityTypeStatRow(
    rank: Int,
    stat: EntityTypeStats,
    maxCount: Int
) {
    val barProgress by animateFloatAsState(
        targetValue = stat.count.toFloat() / maxCount,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "entityBarProgress_${stat.entityType.name}"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (rank <= 3) stat.entityType.toChartColor().copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) stat.entityType.toChartColor()
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Entity type icon
            Icon(
                imageVector = stat.entityType.toIcon(),
                contentDescription = null,
                tint = stat.entityType.toChartColor(),
                modifier = Modifier.size(18.dp)
            )

            // Entity type name
            Text(
                text = stat.entityType.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Trend indicator
            TrendIndicatorChip(trend = stat.trend)

            // Count
            Text(
                text = stat.count.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = stat.entityType.toChartColor()
            )
        }

        Spacer(Modifier.height(4.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { barProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = stat.entityType.toChartColor(),
            trackColor = stat.entityType.toChartColor().copy(alpha = 0.12f)
        )

        Spacer(Modifier.height(4.dp))

        // Metadata row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Avg conf: ${(stat.averageConfidence * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(stat.percentOfTotal * 100).roundToInt()}% of total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            // Severity badge
            SeverityBadge(severity = stat.dominantSeverity)
        }
    }
}

/**
 * A small inline badge showing the trend direction and magnitude.
 *
 * @param trend Percentage change (positive = up, negative = down).
 */
@Composable
private fun TrendIndicatorChip(trend: Float) {
    if (trend == 0f) return
    val isUp = trend > 0
    val color = if (isUp) Color(0xFFE53935) else Color(0xFF43A047)
    val icon = if (isUp) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
    val sign = if (isUp) "+" else ""
    val text = "$sign${trend.roundToInt()}%"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * A compact severity badge chip.
 *
 * @param severity The severity level to display.
 */
@Composable
private fun SeverityBadge(severity: Severity) {
    val color = severity.toColor()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.name,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

// ---------------------------------------------------------------------------
// App Breakdown Section
// ---------------------------------------------------------------------------

/**
 * Section displaying a list of [AppStats] cards, one per source application.
 *
 * @param appStats  The list of per-app statistics to display.
 * @param onAppClicked Callback invoked when an app row is tapped.
 */
@Composable
private fun AppBreakdownSection(
    appStats: List<AppStats>,
    onAppClicked: (AppStats) -> Unit
) {
    ReportCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ReportSectionHeader(
                title = "App Breakdown",
                subtitle = "${appStats.size} apps detected",
                icon = Icons.Outlined.Apps
            )
            Spacer(Modifier.height(4.dp))

            val maxCount = appStats.maxOfOrNull { it.detectionCount }?.coerceAtLeast(1) ?: 1

            appStats.take(10).forEachIndexed { index, app ->
                AppBreakdownCard(
                    app = app,
                    maxCount = maxCount,
                    onClick = { onAppClicked(app) }
                )
                if (index < appStats.take(10).lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            }

            if (appStats.size > 10) {
                TextButton(
                    onClick = { /* expand */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Show ${appStats.size - 10} more")
                }
            }
        }
    }
}

/**
 * A single app breakdown card row.
 *
 * Layout:
 * ```
 * [app icon letter] [app name]           [count] / [% total]
 *                   [───────────░░░░░░░░]  CRITICAL
 *                   C:2  H:3  M:5  | pkg.name
 * ```
 *
 * @param app      The [AppStats] to display.
 * @param maxCount Maximum detection count for scaling the progress bar.
 * @param onClick  Called when the user taps this row.
 */
@Composable
private fun AppBreakdownCard(
    app: AppStats,
    maxCount: Int,
    onClick: () -> Unit
) {
    val barProgress by animateFloatAsState(
        targetValue = app.detectionCount.toFloat() / maxCount,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "appBar_${app.packageName}"
    )
    val severityColor = app.maxSeverity.toColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // App "avatar" — first letter of display name
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(severityColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.displayName.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = severityColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${app.detectionCount} (${(app.percentOfTotal * 100).roundToInt()}%)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { barProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp)),
                    color = severityColor,
                    trackColor = severityColor.copy(alpha = 0.12f)
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (app.criticalCount > 0) {
                        Text(
                            text = "C:${app.criticalCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Severity.CRITICAL.toColor()
                        )
                    }
                    if (app.highCount > 0) {
                        Text(
                            text = "H:${app.highCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Severity.HIGH.toColor()
                        )
                    }
                    if (app.mediumCount > 0) {
                        Text(
                            text = "M:${app.mediumCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Severity.MEDIUM.toColor()
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (app.isWhitelisted) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Whitelisted",
                                tint = Color(0xFF43A047),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Whitelisted",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF43A047)
                            )
                        }
                    }
                    Text(
                        text = app.packageName.take(22),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Export Section
// ---------------------------------------------------------------------------

/**
 * An actions section at the bottom of the report for export and share controls.
 *
 * @param onExportPdf  Callback for PDF export button.
 * @param onExportCsv  Callback for CSV export button.
 * @param onExportJson Callback for JSON export button.
 * @param onShare      Callback for the share button.
 */
@Composable
private fun ExportReportSection(
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit,
    onShare: () -> Unit
) {
    ReportCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportSectionHeader(
                title = "Export Report",
                subtitle = "Save or share this report",
                icon = Icons.Outlined.Download
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExportFormatButton(
                    modifier = Modifier.weight(1f),
                    label = "PDF",
                    icon = Icons.Filled.PictureAsPdf,
                    tint = Color(0xFFE53935),
                    onClick = onExportPdf
                )
                ExportFormatButton(
                    modifier = Modifier.weight(1f),
                    label = "CSV",
                    icon = Icons.Filled.TableChart,
                    tint = Color(0xFF2E7D32),
                    onClick = onExportCsv
                )
                ExportFormatButton(
                    modifier = Modifier.weight(1f),
                    label = "JSON",
                    icon = Icons.Filled.Code,
                    tint = Color(0xFF1565C0),
                    onClick = onExportJson
                )
            }

            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Share Summary")
            }
        }
    }
}

/**
 * A compact format button used in the export section.
 *
 * @param modifier  Modifier for this button.
 * @param label     Format name (e.g., "PDF").
 * @param icon      Icon for this format.
 * @param tint      Accent color for the icon and text.
 * @param onClick   Callback when tapped.
 */
@Composable
private fun ExportFormatButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Export Dialog
// ---------------------------------------------------------------------------

/**
 * A full-featured format selection dialog for starting a report export.
 *
 * Displays a card for each [ReportExportFormat] showing its name, description,
 * and a selection indicator.
 *
 * @param onExport  Callback invoked with the selected [ReportExportFormat].
 * @param onDismiss Callback invoked when the dialog is cancelled.
 */
@Composable
private fun ExportDialog(
    onExport: (ReportExportFormat) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf<ReportExportFormat?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Export Report",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Choose an export format:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ReportExportFormat.entries.forEach { format ->
                    val isSelected = selectedFormat == format
                    val borderColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outlineVariant

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFormat = format },
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().let {
                            if (isSelected) {
                                androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            } else it
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedFormat = format }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = format.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = format.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedFormat?.let { onExport(it) }
                },
                enabled = selectedFormat != null
            ) {
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

/**
 * A minimal progress dialog shown while the export is being generated.
 *
 * @param format          The export format being processed.
 * @param progressPercent The current progress (0–100).
 */
@Composable
private fun ExportProgressDialog(
    format: ReportExportFormat,
    progressPercent: Int
) {
    AlertDialog(
        onDismissRequest = { /* Cannot dismiss during export */ },
        icon = {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
        },
        title = {
            Text(
                text = "Generating ${format.displayName}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Please wait while your report is prepared...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {}
    )
}

/**
 * A dialog shown when the export has successfully completed.
 *
 * @param filename   The output filename.
 * @param eventCount Number of events included in the export.
 * @param format     The format that was exported.
 * @param onDismiss  Callback when the dialog is dismissed.
 */
@Composable
private fun ExportSuccessDialog(
    filename: String,
    eventCount: Int,
    format: ReportExportFormat,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Export Complete",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Your ${format.displayName} has been generated successfully.",
                    style = MaterialTheme.typography.bodyMedium
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filename:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = filename,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Events included:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$eventCount",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Shimmer Loading State
// ---------------------------------------------------------------------------

/**
 * Animated shimmer loading skeleton shown while the report data is being fetched.
 *
 * Renders placeholder cards that mimic the shape and layout of the real content,
 * with a sweeping shimmer animation to indicate activity.
 *
 * @param modifier Modifier applied to the outer container.
 */
@Composable
private fun ReportShimmerLoadingScreen(modifier: Modifier = Modifier) {
    val shimmerBrush = rememberShimmerBrush()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date range chip row shimmer
        item {
            ShimmerRow(shimmerBrush = shimmerBrush, count = 4, chipHeight = 32.dp)
        }

        // Summary cards shimmer (2×2 grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.weight(1f), height = 96.dp)
                    ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.weight(1f), height = 96.dp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.weight(1f), height = 96.dp)
                    ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.weight(1f), height = 96.dp)
                }
            }
        }

        // Bar chart shimmer
        item {
            ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.fillMaxWidth(), height = 280.dp)
        }

        // Line chart shimmer
        item {
            ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.fillMaxWidth(), height = 220.dp)
        }

        // Top entity types shimmer
        item {
            ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.fillMaxWidth(), height = 360.dp)
        }

        // App breakdown shimmer
        item {
            ShimmerCard(shimmerBrush = shimmerBrush, modifier = Modifier.fillMaxWidth(), height = 280.dp)
        }
    }
}

/**
 * Computes a sweeping gradient [Brush] used to render the shimmer effect.
 *
 * The gradient animates from left edge to right edge continuously.
 */
@Composable
private fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerOffset, 0f),
        end = Offset(shimmerOffset + 600f, 300f)
    )
}

/**
 * A rounded rectangle placeholder card with the shimmer brush applied.
 *
 * @param shimmerBrush The animated brush to use as the background.
 * @param modifier     Modifier for the placeholder box.
 * @param height       Height of the placeholder.
 * @param cornerRadius The corner radius in dp.
 */
@Composable
private fun ShimmerCard(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    cornerRadius: Dp = 16.dp
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush)
    )
}

/**
 * A row of shimmer chip placeholders.
 *
 * @param shimmerBrush The animated brush.
 * @param count        Number of chips to render.
 * @param chipHeight   Height of each chip.
 */
@Composable
private fun ShimmerRow(
    shimmerBrush: Brush,
    count: Int,
    chipHeight: Dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { i ->
            val widthDp = if (i == 0) 70.dp else if (i == 1) 80.dp else 90.dp
            Box(
                modifier = Modifier
                    .width(widthDp)
                    .height(chipHeight)
                    .clip(RoundedCornerShape(chipHeight / 2))
                    .background(shimmerBrush)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Empty State Screen
// ---------------------------------------------------------------------------

/**
 * Empty state shown when the selected period has no detection events.
 *
 * @param period   The active period to display in the message.
 * @param modifier Modifier applied to the container.
 */
@Composable
private fun ReportEmptyStateSection(
    period: ReportPeriod,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated shield icon
        val pulsation by rememberInfiniteTransition(label = "emptyPulse")
            .animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "emptyIconPulse"
            )

        Icon(
            imageVector = Icons.Filled.Shield,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer(scaleX = pulsation, scaleY = pulsation)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "All Clear",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "No privacy detections recorded for ${period.displayName.lowercase()}.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "PrivacyGuard is actively monitoring your clipboard for PII. " +
                "When detections occur, they will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Tip card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Tip: Try extending the time range to 30 days to see historical data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Error State Screen
// ---------------------------------------------------------------------------

/**
 * Error state shown when the report fails to load.
 *
 * @param message  The error message to display.
 * @param onRetry  Callback for the retry button.
 * @param modifier Modifier applied to the outer container.
 */
@Composable
private fun ReportErrorStateScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Failed to Load Report",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable Components
// ---------------------------------------------------------------------------

/**
 * Section header composable used consistently throughout the report.
 *
 * @param title    The section heading text.
 * @param subtitle Optional descriptive subheading.
 * @param icon     Optional icon displayed to the left of the title.
 */
@Composable
private fun ReportSectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * A consistently styled card container used for all report sections.
 *
 * @param modifier The modifier to apply.
 * @param content  The card's composable content.
 */
@Composable
private fun ReportCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = content
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Stats Summary Card - Light")
@Composable
private fun StatSummaryCardPreview() {
    MaterialTheme {
        Surface {
            StatSummaryCard(
                label = "Total Detections",
                value = "142",
                icon = Icons.Filled.Shield,
                iconTint = MaterialTheme.colorScheme.primary,
                subtitle = "7 PII types"
            )
        }
    }
}

@Preview(showBackground = true, name = "Entity Type Stat Row")
@Composable
private fun EntityTypeStatRowPreview() {
    MaterialTheme {
        Surface {
            EntityTypeStatRow(
                rank = 1,
                stat = EntityTypeStats(
                    entityType = EntityType.EMAIL,
                    count = 34,
                    percentOfTotal = 0.42f,
                    averageConfidence = 0.89f,
                    criticalCount = 0,
                    highCount = 34,
                    mediumCount = 0,
                    trend = 15f
                ),
                maxCount = 34
            )
        }
    }
}

@Preview(showBackground = true, name = "Severity Badge")
@Composable
private fun SeverityBadgePreview() {
    MaterialTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SeverityBadge(severity = Severity.CRITICAL)
                SeverityBadge(severity = Severity.HIGH)
                SeverityBadge(severity = Severity.MEDIUM)
            }
        }
    }
}

@Preview(showBackground = true, name = "Export Format Button")
@Composable
private fun ExportFormatButtonPreview() {
    MaterialTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExportFormatButton(
                    modifier = Modifier.width(80.dp),
                    label = "PDF",
                    icon = Icons.Filled.PictureAsPdf,
                    tint = Color(0xFFE53935),
                    onClick = {}
                )
                ExportFormatButton(
                    modifier = Modifier.width(80.dp),
                    label = "CSV",
                    icon = Icons.Filled.TableChart,
                    tint = Color(0xFF2E7D32),
                    onClick = {}
                )
                ExportFormatButton(
                    modifier = Modifier.width(80.dp),
                    label = "JSON",
                    icon = Icons.Filled.Code,
                    tint = Color(0xFF1565C0),
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Trend Indicator Chips")
@Composable
private fun TrendIndicatorChipPreview() {
    MaterialTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrendIndicatorChip(trend = 22f)
                TrendIndicatorChip(trend = -15f)
                TrendIndicatorChip(trend = 0f)
            }
        }
    }
}

@Preview(showBackground = true, name = "Shimmer Card")
@Composable
private fun ShimmerCardPreview() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val brush = rememberShimmerBrush()
                ShimmerCard(shimmerBrush = brush, modifier = Modifier.fillMaxWidth(), height = 80.dp)
                ShimmerCard(shimmerBrush = brush, modifier = Modifier.fillMaxWidth(), height = 120.dp)
            }
        }
    }
}
