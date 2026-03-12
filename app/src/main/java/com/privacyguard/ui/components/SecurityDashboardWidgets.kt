package com.privacyguard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.UserAction
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.ModelState
import com.privacyguard.ml.Severity
import com.privacyguard.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.*

// ==========================================================================
// Data classes for dashboard widgets
// ==========================================================================

/**
 * Data point for the threat level gauge.
 */
data class ThreatLevelData(
    val currentLevel: Float = 0f,
    val maxLevel: Float = 100f,
    val label: String = "Low",
    val criticalThreshold: Float = 80f,
    val highThreshold: Float = 60f,
    val mediumThreshold: Float = 40f
)

/**
 * A single item in the detection timeline.
 */
data class TimelineItem(
    val id: String = UUID.randomUUID().toString(),
    val entityType: EntityType = EntityType.UNKNOWN,
    val severity: Severity = Severity.MEDIUM,
    val timestamp: Long = System.currentTimeMillis(),
    val confidence: Float = 0f,
    val sourceAppName: String? = null
)

/**
 * A slice of the entity type distribution pie/donut chart.
 */
data class EntityDistributionSlice(
    val entityType: EntityType,
    val count: Int,
    val color: Color
)

/**
 * A single cell in the severity heatmap grid.
 */
data class HeatmapCell(
    val day: Int,
    val week: Int,
    val count: Int,
    val date: Long = 0L
)

/**
 * A data point in the protection score history line chart.
 */
data class ScoreHistoryPoint(
    val timestamp: Long,
    val score: Int
)

/**
 * A single statistic for the statistics grid.
 */
data class StatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val tintColor: Color,
    val delta: Float? = null,
    val deltaLabel: String? = null
)

/**
 * Performance metrics for the inference card.
 */
data class InferencePerformanceData(
    val averageLatencyMs: Long = 0L,
    val p50LatencyMs: Long = 0L,
    val p95LatencyMs: Long = 0L,
    val p99LatencyMs: Long = 0L,
    val totalInferences: Long = 0L,
    val modelState: ModelState = ModelState.Initializing,
    val latencyHistogram: List<Long> = emptyList(),
    val tokensPerSecond: Float = 0f
)

/**
 * Trend data for the privacy trend card.
 */
data class PrivacyTrendData(
    val label: String,
    val currentValue: Int,
    val previousValue: Int,
    val unit: String = ""
) {
    val percentageChange: Float
        get() = if (previousValue > 0) {
            ((currentValue - previousValue).toFloat() / previousValue * 100f)
        } else if (currentValue > 0) 100f else 0f

    val isImprovement: Boolean
        get() = currentValue <= previousValue
}

/**
 * A single badge for the quick stats row.
 */
data class QuickStatBadge(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

// ==========================================================================
// 1. ThreatLevelGauge
// ==========================================================================

/**
 * A semi-circular gauge with animated needle showing the current threat level.
 * The gauge transitions through green (low), yellow (medium), orange (high),
 * and red (critical) zones with labeled thresholds.
 *
 * @param data The threat level data to display.
 * @param size The diameter of the gauge.
 * @param modifier Optional modifier for the gauge container.
 */
@Composable
fun ThreatLevelGauge(
    data: ThreatLevelData,
    size: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(
        targetValue = data.currentLevel,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "gauge_needle"
    )

    val needleColor by animateColorAsState(
        targetValue = when {
            data.currentLevel >= data.criticalThreshold -> SeverityCritical
            data.currentLevel >= data.highThreshold -> SeverityHigh
            data.currentLevel >= data.mediumThreshold -> SeverityMedium
            else -> SuccessGreen
        },
        animationSpec = tween(600),
        label = "gauge_needle_color"
    )

    val textMeasurer = rememberTextMeasurer()

    val gaugeSemanticDescription = buildString {
        append("Threat level gauge: ${data.currentLevel.toInt()} out of ${data.maxLevel.toInt()}. ")
        append("Level: ${data.label}.")
    }

    Column(
        modifier = modifier
            .width(size)
            .semantics { contentDescription = gaugeSemanticDescription },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(size, size / 2 + 20.dp)
        ) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val strokeWidth = 24.dp.toPx()
            val padding = strokeWidth / 2 + 4.dp.toPx()

            val arcRect = Rect(
                left = padding,
                top = padding,
                right = canvasWidth - padding,
                bottom = canvasHeight * 2 - padding * 2
            )

            // Draw arc zones
            val totalSweep = 180f

            // Green zone (0 - medium threshold)
            val greenSweep = (data.mediumThreshold / data.maxLevel) * totalSweep
            drawArc(
                color = SuccessGreen.copy(alpha = 0.3f),
                startAngle = 180f,
                sweepAngle = greenSweep,
                useCenter = false,
                topLeft = Offset(arcRect.left, arcRect.top),
                size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            // Yellow zone (medium - high threshold)
            val yellowSweep = ((data.highThreshold - data.mediumThreshold) / data.maxLevel) * totalSweep
            drawArc(
                color = SeverityMedium.copy(alpha = 0.3f),
                startAngle = 180f + greenSweep,
                sweepAngle = yellowSweep,
                useCenter = false,
                topLeft = Offset(arcRect.left, arcRect.top),
                size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            // Orange zone (high - critical threshold)
            val orangeSweep = ((data.criticalThreshold - data.highThreshold) / data.maxLevel) * totalSweep
            drawArc(
                color = SeverityHigh.copy(alpha = 0.3f),
                startAngle = 180f + greenSweep + yellowSweep,
                sweepAngle = orangeSweep,
                useCenter = false,
                topLeft = Offset(arcRect.left, arcRect.top),
                size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            // Red zone (critical - max)
            val redSweep = ((data.maxLevel - data.criticalThreshold) / data.maxLevel) * totalSweep
            drawArc(
                color = SeverityCritical.copy(alpha = 0.3f),
                startAngle = 180f + greenSweep + yellowSweep + orangeSweep,
                sweepAngle = redSweep,
                useCenter = false,
                topLeft = Offset(arcRect.left, arcRect.top),
                size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            // Filled progress arc
            val progressSweep = (animatedLevel / data.maxLevel) * totalSweep
            drawArc(
                color = needleColor,
                startAngle = 180f,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = Offset(arcRect.left, arcRect.top),
                size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw needle
            val centerX = canvasWidth / 2
            val centerY = canvasHeight - padding
            val needleLength = (canvasWidth / 2) - padding - strokeWidth
            val needleAngle = 180f + (animatedLevel / data.maxLevel) * 180f
            val radians = Math.toRadians(needleAngle.toDouble())

            val endX = centerX + needleLength * cos(radians).toFloat()
            val endY = centerY + needleLength * sin(radians).toFloat()

            // Needle line
            drawLine(
                color = needleColor,
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center dot
            drawCircle(
                color = needleColor,
                radius = 6.dp.toPx(),
                center = Offset(centerX, centerY)
            )

            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Value label
        Text(
            text = "${animatedLevel.toInt()}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = needleColor
        )

        Text(
            text = data.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Threshold labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
            Text("${data.mediumThreshold.toInt()}", style = MaterialTheme.typography.labelSmall, color = SeverityMedium)
            Text("${data.highThreshold.toInt()}", style = MaterialTheme.typography.labelSmall, color = SeverityHigh)
            Text("${data.criticalThreshold.toInt()}", style = MaterialTheme.typography.labelSmall, color = SeverityCritical)
            Text("${data.maxLevel.toInt()}", style = MaterialTheme.typography.labelSmall, color = SeverityCritical)
        }
    }
}

// ==========================================================================
// 2. DetectionTimeline
// ==========================================================================

/**
 * A horizontal scrollable timeline showing recent detections with time markers.
 * Each detection is represented as a dot on the timeline with severity coloring.
 *
 * @param items List of timeline items to display.
 * @param onItemClick Callback when a timeline item is tapped.
 * @param modifier Optional modifier.
 */
@Composable
fun DetectionTimeline(
    items: List<TimelineItem>,
    onItemClick: (TimelineItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Detection timeline with ${items.size} events"
            }
    ) {
        Text(
            text = "Detection Timeline",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (items.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No detections yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    TimelineItemCard(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == items.lastIndex,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItemCard(
    item: TimelineItem,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val severityColor = when (item.severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(item.id) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(4.dp)
                .semantics {
                    contentDescription = "${item.entityType.displayName} detected ${formatTimelineTime(item.timestamp)}"
                    role = Role.Button
                }
        ) {
            // Severity dot with icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(severityColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = entityTypeIcon(item.entityType),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = severityColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Entity type label
            Text(
                text = item.entityType.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Confidence
            Text(
                text = "${(item.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = severityColor,
                fontWeight = FontWeight.Bold
            )

            // Time label
            Text(
                text = formatTimelineTime(item.timestamp),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun formatTimelineTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
        else -> {
            val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

// ==========================================================================
// 3. EntityTypeDistribution (Donut Chart)
// ==========================================================================

/**
 * A donut chart showing the distribution of detections by entity type.
 * Uses Canvas for custom rendering with animated segments.
 *
 * @param slices List of distribution slices to display.
 * @param size Diameter of the donut chart.
 * @param modifier Optional modifier.
 */
@Composable
fun EntityTypeDistribution(
    slices: List<EntityDistributionSlice>,
    size: Dp = 180.dp,
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.count }.toFloat()
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "donut_animation"
    )

    val semanticDescription = buildString {
        append("Entity type distribution chart. ")
        slices.forEach { slice ->
            val percentage = if (total > 0) (slice.count / total * 100).toInt() else 0
            append("${slice.entityType.displayName}: ${slice.count} (${percentage}%). ")
        }
    }

    Column(
        modifier = modifier
            .semantics { contentDescription = semanticDescription },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detection Distribution",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donut chart
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(size)
            ) {
                Canvas(modifier = Modifier.size(size)) {
                    val strokeWidth = 32.dp.toPx()
                    val canvasSize = this.size.minDimension
                    val radius = (canvasSize - strokeWidth) / 2
                    val center = Offset(this.size.width / 2, this.size.height / 2)

                    if (total == 0f) {
                        // Empty state
                        drawCircle(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )
                    } else {
                        var currentAngle = -90f
                        slices.forEach { slice ->
                            val sweep = (slice.count / total) * 360f * animationProgress
                            drawArc(
                                color = slice.color,
                                startAngle = currentAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = Offset(
                                    center.x - radius,
                                    center.y - radius
                                ),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            currentAngle += sweep
                        }
                    }
                }

                // Center text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = total.toInt().toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                slices.sortedByDescending { it.count }.forEach { slice ->
                    val percentage = if (total > 0) (slice.count / total * 100).toInt() else 0
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.semantics {
                            contentDescription = "${slice.entityType.displayName}: ${slice.count} detections, $percentage percent"
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(slice.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slice.entityType.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 100.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = slice.color
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================================
// 4. SeverityHeatmap
// ==========================================================================

/**
 * A weekly heatmap grid (7 columns x 4 rows) with color intensity based
 * on the detection count for each day. Similar to GitHub's contribution graph.
 *
 * @param cells List of heatmap cells to render.
 * @param modifier Optional modifier.
 */
@Composable
fun SeverityHeatmap(
    cells: List<HeatmapCell>,
    modifier: Modifier = Modifier
) {
    val maxCount = cells.maxOfOrNull { it.count } ?: 1
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "heatmap_animation"
    )

    val semanticDescription = buildString {
        append("Severity heatmap showing detection frequency over the past 4 weeks. ")
        val totalDetections = cells.sumOf { it.count }
        append("Total: $totalDetections detections.")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = semanticDescription }
    ) {
        Text(
            text = "Detection Heatmap",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Heatmap grid (4 weeks x 7 days)
        for (week in 0 until 4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0 until 7) {
                    val cell = cells.find { it.week == week && it.day == day }
                    val count = cell?.count ?: 0
                    val intensity = if (maxCount > 0) {
                        (count.toFloat() / maxCount * animationProgress).coerceIn(0f, 1f)
                    } else 0f

                    val cellColor = when {
                        count == 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        intensity < 0.25f -> SuccessGreen.copy(alpha = 0.4f)
                        intensity < 0.5f -> AlertYellow.copy(alpha = 0.5f)
                        intensity < 0.75f -> AlertOrange.copy(alpha = 0.6f)
                        else -> SeverityCritical.copy(alpha = 0.7f)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(cellColor)
                            .semantics {
                                contentDescription = "$count detections"
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { level ->
                val legendColor = when {
                    level == 0f -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    level < 0.25f -> SuccessGreen.copy(alpha = 0.4f)
                    level < 0.5f -> AlertYellow.copy(alpha = 0.5f)
                    level < 0.75f -> AlertOrange.copy(alpha = 0.6f)
                    else -> SeverityCritical.copy(alpha = 0.7f)
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(legendColor)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================================================
// 5. ProtectionScoreHistory
// ==========================================================================

/**
 * A line chart showing protection score over time for the last 7 or 30 days.
 * Uses Canvas for custom rendering with animated drawing.
 *
 * @param dataPoints List of score history data points.
 * @param height Height of the chart area.
 * @param modifier Optional modifier.
 */
@Composable
fun ProtectionScoreHistory(
    dataPoints: List<ScoreHistoryPoint>,
    height: Dp = 150.dp,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "score_history_animation"
    )

    val latestScore = dataPoints.lastOrNull()?.score ?: 0
    val averageScore = if (dataPoints.isNotEmpty()) {
        dataPoints.map { it.score }.average().toInt()
    } else 0

    val semanticDescription = buildString {
        append("Protection score history chart. ")
        append("Latest score: $latestScore. ")
        append("Average: $averageScore. ")
        append("${dataPoints.size} data points.")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = semanticDescription }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Score History",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Avg: $averageScore",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Latest: $latestScore",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        latestScore >= 80 -> SuccessGreen
                        latestScore >= 50 -> AlertOrange
                        else -> AlertRed
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (dataPoints.size < 2) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Not enough data for chart",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val lineColor = TrustBlue
            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            val fillColor = TrustBlue.copy(alpha = 0.1f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val padding = 8.dp.toPx()

                val chartWidth = canvasWidth - padding * 2
                val chartHeight = canvasHeight - padding * 2

                // Grid lines
                for (i in 0..4) {
                    val y = padding + (chartHeight * i / 4)
                    drawLine(
                        color = gridColor,
                        start = Offset(padding, y),
                        end = Offset(canvasWidth - padding, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Build path
                val points = dataPoints.mapIndexed { index, point ->
                    val x = padding + (chartWidth * index / (dataPoints.size - 1).coerceAtLeast(1))
                    val y = padding + chartHeight * (1f - point.score / 100f)
                    Offset(x, y)
                }

                // Draw animated path
                val visibleCount = (points.size * animationProgress).toInt().coerceAtLeast(1)
                val visiblePoints = points.take(visibleCount)

                // Area fill
                if (visiblePoints.size >= 2) {
                    val fillPath = Path().apply {
                        moveTo(visiblePoints.first().x, canvasHeight - padding)
                        visiblePoints.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                        lineTo(visiblePoints.last().x, canvasHeight - padding)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(fillColor, Color.Transparent),
                            startY = 0f,
                            endY = canvasHeight
                        )
                    )

                    // Line
                    val linePath = Path().apply {
                        moveTo(visiblePoints.first().x, visiblePoints.first().y)
                        for (i in 1 until visiblePoints.size) {
                            lineTo(visiblePoints[i].x, visiblePoints[i].y)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Dots
                    visiblePoints.forEach { point ->
                        drawCircle(
                            color = lineColor,
                            radius = 3.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 1.5.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================================
// 6. StatisticsGrid
// ==========================================================================

/**
 * A responsive grid of stat cards with animated counters. Shows key
 * statistics in a visually appealing grid layout.
 *
 * @param stats List of stat items to display.
 * @param columns Number of columns in the grid.
 * @param modifier Optional modifier.
 */
@Composable
fun StatisticsGrid(
    stats: List<StatItem>,
    columns: Int = 2,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Statistics grid with ${stats.size} metrics"
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Render grid rows
        stats.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    AnimatedStatCard(
                        stat = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row is not complete
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * A single animated stat card within the statistics grid.
 */
@Composable
private fun AnimatedStatCard(
    stat: StatItem,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(stat.label) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.9f, animationSpec = tween(400))
    ) {
        Card(
            modifier = modifier
                .semantics {
                    contentDescription = buildString {
                        append("${stat.label}: ${stat.value}")
                        stat.deltaLabel?.let { append(". $it") }
                    }
                },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.tintColor,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Delta indicator
                stat.delta?.let { delta ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (delta >= 0) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (delta <= 0) SuccessGreen else AlertRed
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stat.deltaLabel ?: "${abs(delta).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = if (delta <= 0) SuccessGreen else AlertRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================================
// 7. InferencePerformanceCard
// ==========================================================================

/**
 * A detailed card showing ML model inference performance metrics including
 * average latency, percentiles, and a latency histogram.
 *
 * @param data Performance data to display.
 * @param modifier Optional modifier.
 */
@Composable
fun InferencePerformanceCard(
    data: InferencePerformanceData,
    modifier: Modifier = Modifier
) {
    val semanticDescription = buildString {
        append("Inference performance. ")
        append("Average latency: ${data.averageLatencyMs} milliseconds. ")
        append("P95: ${data.p95LatencyMs} milliseconds. ")
        append("Total inferences: ${data.totalInferences}. ")
        append("Tokens per second: ${data.tokensPerSecond.toInt()}.")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = semanticDescription },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inference Performance",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                // Model state badge
                val (stateLabel, stateColor) = when (data.modelState) {
                    is ModelState.Ready -> "Ready" to SuccessGreen
                    is ModelState.Running -> "Running" to TrustBlue
                    is ModelState.Initializing -> "Loading" to AlertYellow
                    is ModelState.Error -> "Error" to AlertRed
                    is ModelState.Closed -> "Closed" to ProtectionInactive
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stateColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(stateColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stateLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = stateColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Latency metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LatencyMetric("Avg", "${data.averageLatencyMs}ms")
                LatencyMetric("P50", "${data.p50LatencyMs}ms")
                LatencyMetric("P95", "${data.p95LatencyMs}ms")
                LatencyMetric("P99", "${data.p99LatencyMs}ms")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Latency histogram
            if (data.latencyHistogram.isNotEmpty()) {
                LatencyHistogram(
                    values = data.latencyHistogram,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total inferences: ${data.totalInferences}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${data.tokensPerSecond.toInt()} tok/s",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TrustBlue
                )
            }
        }
    }
}

@Composable
private fun LatencyMetric(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics {
            contentDescription = "$label latency: $value"
        }
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

/**
 * A small horizontal bar histogram for latency values.
 */
@Composable
private fun LatencyHistogram(
    values: List<Long>,
    modifier: Modifier = Modifier
) {
    val maxVal = values.maxOrNull()?.toFloat() ?: 1f
    val barColor = TrustBlue

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "histogram_animation"
    )

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        val barWidth = size.width / values.size
        val maxHeight = size.height - 4.dp.toPx()

        values.forEachIndexed { index, value ->
            val barHeight = (value / maxVal) * maxHeight * animationProgress
            val x = index * barWidth

            val intensity = (value / maxVal).coerceIn(0f, 1f)
            val color = when {
                intensity < 0.3f -> SuccessGreen
                intensity < 0.6f -> AlertYellow
                intensity < 0.8f -> AlertOrange
                else -> AlertRed
            }

            drawRoundRect(
                color = color.copy(alpha = 0.7f),
                topLeft = Offset(x + 1.dp.toPx(), size.height - barHeight),
                size = Size(barWidth - 2.dp.toPx(), barHeight),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }
    }
}

// ==========================================================================
// 8. PrivacyTrendCard
// ==========================================================================

/**
 * A card showing privacy trend data with up/down indicators and
 * percentage change from the previous period.
 *
 * @param trends List of trend data items.
 * @param modifier Optional modifier.
 */
@Composable
fun PrivacyTrendCard(
    trends: List<PrivacyTrendData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Privacy trends showing ${trends.size} metrics"
            },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Privacy Trends",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            trends.forEachIndexed { index, trend ->
                TrendRow(trend = trend)
                if (index < trends.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendRow(trend: PrivacyTrendData) {
    val percentChange = trend.percentageChange
    val isImprovement = trend.isImprovement
    val trendColor = if (isImprovement) SuccessGreen else AlertRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append("${trend.label}: ${trend.currentValue}${trend.unit}. ")
                    append(if (isImprovement) "Improved" else "Worsened")
                    append(" by ${abs(percentChange).toInt()} percent.")
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trend.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${trend.previousValue}${trend.unit} -> ${trend.currentValue}${trend.unit}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Trend indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (percentChange <= 0) Icons.Filled.TrendingDown else Icons.Filled.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = trendColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${if (percentChange > 0) "+" else ""}${percentChange.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = trendColor
            )
        }
    }
}

// ==========================================================================
// 9. QuickStatsRow
// ==========================================================================

/**
 * A horizontal scrollable row of mini stat badges. Each badge shows
 * an icon, label, and value in a compact format.
 *
 * @param badges List of quick stat badges.
 * @param modifier Optional modifier.
 */
@Composable
fun QuickStatsRow(
    badges: List<QuickStatBadge>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Quick statistics with ${badges.size} metrics"
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(badges) { badge ->
            QuickStatBadgeChip(badge = badge)
        }
    }
}

@Composable
private fun QuickStatBadgeChip(badge: QuickStatBadge) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = badge.color.copy(alpha = 0.1f),
        modifier = Modifier.semantics {
            contentDescription = "${badge.label}: ${badge.value}"
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = badge.color
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = badge.value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badge.color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = badge.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

// ==========================================================================
// Composite Dashboard Section
// ==========================================================================

/**
 * A ready-to-use composite widget that combines multiple dashboard widgets
 * into a single cohesive section. Ideal for quick integration.
 */
@Composable
fun DashboardWidgetSection(
    threatLevel: ThreatLevelData = ThreatLevelData(),
    timelineItems: List<TimelineItem> = emptyList(),
    entityDistribution: List<EntityDistributionSlice> = emptyList(),
    heatmapCells: List<HeatmapCell> = emptyList(),
    scoreHistory: List<ScoreHistoryPoint> = emptyList(),
    stats: List<StatItem> = emptyList(),
    performanceData: InferencePerformanceData = InferencePerformanceData(),
    trends: List<PrivacyTrendData> = emptyList(),
    quickStats: List<QuickStatBadge> = emptyList(),
    onTimelineItemClick: (TimelineItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (quickStats.isNotEmpty()) {
            QuickStatsRow(badges = quickStats)
        }

        if (stats.isNotEmpty()) {
            StatisticsGrid(stats = stats)
        }

        if (entityDistribution.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                EntityTypeDistribution(
                    slices = entityDistribution,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (timelineItems.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                DetectionTimeline(
                    items = timelineItems,
                    onItemClick = onTimelineItemClick,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (scoreHistory.size >= 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                ProtectionScoreHistory(
                    dataPoints = scoreHistory,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (heatmapCells.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                SeverityHeatmap(
                    cells = heatmapCells,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        InferencePerformanceCard(data = performanceData)

        if (trends.isNotEmpty()) {
            PrivacyTrendCard(trends = trends)
        }
    }
}

// ==========================================================================
// Sample data generators for previews
// ==========================================================================

private fun sampleEntityDistribution(): List<EntityDistributionSlice> = listOf(
    EntityDistributionSlice(EntityType.CREDIT_CARD, 12, SeverityCritical),
    EntityDistributionSlice(EntityType.SSN, 5, AlertRedDark),
    EntityDistributionSlice(EntityType.EMAIL, 23, SeverityHigh),
    EntityDistributionSlice(EntityType.PHONE, 18, AlertOrange),
    EntityDistributionSlice(EntityType.PERSON_NAME, 31, SeverityMedium),
    EntityDistributionSlice(EntityType.ADDRESS, 8, TrustBlue),
    EntityDistributionSlice(EntityType.PASSWORD, 3, TrustBlueDark),
    EntityDistributionSlice(EntityType.API_KEY, 2, ProtectionInactive)
)

private fun sampleTimelineItems(): List<TimelineItem> = listOf(
    TimelineItem(entityType = EntityType.CREDIT_CARD, severity = Severity.CRITICAL, confidence = 0.97f, sourceAppName = "Shop", timestamp = System.currentTimeMillis() - 300_000L),
    TimelineItem(entityType = EntityType.EMAIL, severity = Severity.HIGH, confidence = 0.88f, sourceAppName = "Mail", timestamp = System.currentTimeMillis() - 1_800_000L),
    TimelineItem(entityType = EntityType.PHONE, severity = Severity.HIGH, confidence = 0.78f, sourceAppName = "SMS", timestamp = System.currentTimeMillis() - 3_600_000L),
    TimelineItem(entityType = EntityType.PERSON_NAME, severity = Severity.MEDIUM, confidence = 0.65f, sourceAppName = "Notes", timestamp = System.currentTimeMillis() - 7_200_000L),
    TimelineItem(entityType = EntityType.SSN, severity = Severity.CRITICAL, confidence = 0.92f, sourceAppName = "Forms", timestamp = System.currentTimeMillis() - 14_400_000L),
    TimelineItem(entityType = EntityType.ADDRESS, severity = Severity.MEDIUM, confidence = 0.70f, sourceAppName = "Maps", timestamp = System.currentTimeMillis() - 28_800_000L),
    TimelineItem(entityType = EntityType.PASSWORD, severity = Severity.CRITICAL, confidence = 0.85f, sourceAppName = "Browser", timestamp = System.currentTimeMillis() - 43_200_000L)
)

private fun sampleHeatmapCells(): List<HeatmapCell> {
    val cells = mutableListOf<HeatmapCell>()
    val random = Random(42)
    for (week in 0 until 4) {
        for (day in 0 until 7) {
            cells.add(HeatmapCell(day = day, week = week, count = random.nextInt(8)))
        }
    }
    return cells
}

private fun sampleScoreHistory(): List<ScoreHistoryPoint> {
    val now = System.currentTimeMillis()
    return listOf(
        ScoreHistoryPoint(now - 6 * 86_400_000L, 85),
        ScoreHistoryPoint(now - 5 * 86_400_000L, 82),
        ScoreHistoryPoint(now - 4 * 86_400_000L, 90),
        ScoreHistoryPoint(now - 3 * 86_400_000L, 78),
        ScoreHistoryPoint(now - 2 * 86_400_000L, 65),
        ScoreHistoryPoint(now - 1 * 86_400_000L, 88),
        ScoreHistoryPoint(now, 92)
    )
}

private fun sampleStats(): List<StatItem> = listOf(
    StatItem("Today", "7", Icons.Filled.Today, TrustBlue, delta = -30f, deltaLabel = "-30%"),
    StatItem("This Week", "34", Icons.Filled.DateRange, AlertOrange, delta = 12f, deltaLabel = "+12%"),
    StatItem("Total", "1,247", Icons.Filled.Assessment, SeverityMedium),
    StatItem("Latency", "42ms", Icons.Filled.Speed, SuccessGreen, delta = -15f, deltaLabel = "-15%")
)

private fun samplePerformanceData(): InferencePerformanceData = InferencePerformanceData(
    averageLatencyMs = 42,
    p50LatencyMs = 38,
    p95LatencyMs = 85,
    p99LatencyMs = 120,
    totalInferences = 1247,
    modelState = ModelState.Ready,
    latencyHistogram = listOf(35, 38, 42, 40, 55, 38, 30, 45, 85, 42, 38, 36, 120, 40, 38, 42, 35, 50, 45, 38),
    tokensPerSecond = 156.5f
)

private fun sampleTrends(): List<PrivacyTrendData> = listOf(
    PrivacyTrendData("Daily Detections", 7, 10),
    PrivacyTrendData("Critical Alerts", 1, 3),
    PrivacyTrendData("Avg Confidence", 89, 85, "%"),
    PrivacyTrendData("Texts Scanned", 234, 198)
)

private fun sampleQuickStats(): List<QuickStatBadge> = listOf(
    QuickStatBadge("today", "7", Icons.Filled.Today, TrustBlue),
    QuickStatBadge("critical", "1", Icons.Filled.Warning, SeverityCritical),
    QuickStatBadge("scanned", "234", Icons.Filled.TextSnippet, SuccessGreen),
    QuickStatBadge("latency", "42ms", Icons.Filled.Speed, AlertOrange),
    QuickStatBadge("model", "Ready", Icons.Filled.Psychology, SuccessGreen)
)

// ==========================================================================
// Preview composables
// ==========================================================================

@Preview(showBackground = true, name = "Threat Level Gauge - Low")
@Composable
private fun ThreatLevelGaugeLowPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ThreatLevelGauge(
            data = ThreatLevelData(currentLevel = 15f, label = "Low"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Threat Level Gauge - Medium")
@Composable
private fun ThreatLevelGaugeMediumPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ThreatLevelGauge(
            data = ThreatLevelData(currentLevel = 50f, label = "Medium"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Threat Level Gauge - Critical")
@Composable
private fun ThreatLevelGaugeCriticalPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ThreatLevelGauge(
            data = ThreatLevelData(currentLevel = 88f, label = "Critical"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Detection Timeline")
@Composable
private fun DetectionTimelinePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        DetectionTimeline(
            items = sampleTimelineItems(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Detection Timeline - Empty")
@Composable
private fun DetectionTimelineEmptyPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        DetectionTimeline(
            items = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Entity Type Distribution")
@Composable
private fun EntityTypeDistributionPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        EntityTypeDistribution(
            slices = sampleEntityDistribution(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Entity Type Distribution - Empty")
@Composable
private fun EntityTypeDistributionEmptyPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        EntityTypeDistribution(
            slices = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Severity Heatmap")
@Composable
private fun SeverityHeatmapPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        SeverityHeatmap(
            cells = sampleHeatmapCells(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Protection Score History")
@Composable
private fun ProtectionScoreHistoryPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionScoreHistory(
            dataPoints = sampleScoreHistory(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Protection Score History - Insufficient Data")
@Composable
private fun ProtectionScoreHistoryInsufficientPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionScoreHistory(
            dataPoints = listOf(ScoreHistoryPoint(System.currentTimeMillis(), 85)),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Statistics Grid")
@Composable
private fun StatisticsGridPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        StatisticsGrid(
            stats = sampleStats(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Inference Performance Card")
@Composable
private fun InferencePerformanceCardPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        InferencePerformanceCard(
            data = samplePerformanceData(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Inference Performance - Error")
@Composable
private fun InferencePerformanceErrorPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        InferencePerformanceCard(
            data = InferencePerformanceData(
                modelState = ModelState.Error("OOM"),
                averageLatencyMs = 0,
                totalInferences = 42
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Privacy Trend Card")
@Composable
private fun PrivacyTrendCardPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PrivacyTrendCard(
            trends = sampleTrends(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Quick Stats Row")
@Composable
private fun QuickStatsRowPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        QuickStatsRow(
            badges = sampleQuickStats(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Full Dashboard Widget Section")
@Composable
private fun DashboardWidgetSectionPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        DashboardWidgetSection(
            threatLevel = ThreatLevelData(currentLevel = 35f, label = "Moderate"),
            timelineItems = sampleTimelineItems().take(4),
            entityDistribution = sampleEntityDistribution().take(5),
            heatmapCells = sampleHeatmapCells(),
            scoreHistory = sampleScoreHistory(),
            stats = sampleStats(),
            performanceData = samplePerformanceData(),
            trends = sampleTrends(),
            quickStats = sampleQuickStats(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Threat Level Gauge - Zero")
@Composable
private fun ThreatLevelGaugeZeroPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ThreatLevelGauge(
            data = ThreatLevelData(currentLevel = 0f, label = "None"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Threat Level Gauge - High")
@Composable
private fun ThreatLevelGaugeHighPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ThreatLevelGauge(
            data = ThreatLevelData(currentLevel = 72f, label = "High"),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Statistics Grid - 3 Columns")
@Composable
private fun StatisticsGridThreeColumnsPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        StatisticsGrid(
            stats = sampleStats() + listOf(
                StatItem("Cleared", "14", Icons.Filled.CleaningServices, AlertRed),
                StatItem("Whitelisted", "6", Icons.Filled.VerifiedUser, SuccessGreen)
            ),
            columns = 3,
            modifier = Modifier.padding(16.dp)
        )
    }
}
