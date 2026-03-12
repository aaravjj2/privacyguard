package com.privacyguard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.ui.theme.*
import kotlin.math.*

// ==========================================================================
// Data classes for chart components
// ==========================================================================

/**
 * A single data point for line and area charts.
 */
data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val label: String = ""
)

/**
 * A data series for multi-series line charts.
 */
data class ChartSeries(
    val name: String,
    val points: List<ChartDataPoint>,
    val color: Color,
    val fillColor: Color? = null
)

/**
 * Configuration for chart appearance.
 */
data class ChartConfig(
    val showGrid: Boolean = true,
    val showAxisLabels: Boolean = true,
    val showLegend: Boolean = true,
    val showDataPoints: Boolean = true,
    val showValueLabels: Boolean = false,
    val animationDurationMs: Int = 1200,
    val gridColor: Color = Color.LightGray.copy(alpha = 0.2f),
    val axisColor: Color = Color.Gray.copy(alpha = 0.4f),
    val lineWidth: Float = 2.5f,
    val pointRadius: Float = 4f,
    val gridLineCount: Int = 5
)

/**
 * A single bar in a bar chart.
 */
data class BarData(
    val label: String,
    val value: Float,
    val color: Color = TrustBlue,
    val secondaryValue: Float? = null,
    val secondaryColor: Color? = null
)

/**
 * A grouped bar set for grouped bar charts.
 */
data class GroupedBarData(
    val groupLabel: String,
    val bars: List<BarData>
)

/**
 * A slice for pie charts.
 */
data class PieSlice(
    val label: String,
    val value: Float,
    val color: Color
)

/**
 * A dimension for radar charts.
 */
data class RadarDimension(
    val label: String,
    val value: Float,
    val maxValue: Float = 100f
)

/**
 * Touch inspection data returned when the user taps on a chart.
 */
data class ChartInspection(
    val seriesName: String = "",
    val label: String = "",
    val value: Float = 0f,
    val position: Offset = Offset.Zero
)

// ==========================================================================
// 1. LineChart
// ==========================================================================

/**
 * A multi-series line chart with animated drawing, grid lines, axis labels,
 * touch-to-inspect, and legend. Supports multiple data series overlaid
 * on the same axes.
 *
 * @param series List of data series to display.
 * @param config Chart configuration options.
 * @param height Height of the chart area.
 * @param onInspect Callback with inspection data when user taps the chart.
 * @param modifier Optional modifier.
 */
@Composable
fun LineChart(
    series: List<ChartSeries>,
    config: ChartConfig = ChartConfig(),
    height: Dp = 200.dp,
    onInspect: (ChartInspection?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (series.isEmpty() || series.all { it.points.isEmpty() }) {
        ChartEmptyState(height = height, modifier = modifier)
        return
    }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDurationMs, easing = FastOutSlowInEasing),
        label = "line_chart_animation"
    )

    var inspectionState by remember { mutableStateOf<ChartInspection?>(null) }
    val textMeasurer = rememberTextMeasurer()

    // Compute bounds
    val allPoints = series.flatMap { it.points }
    val minX = allPoints.minOfOrNull { it.x } ?: 0f
    val maxX = allPoints.maxOfOrNull { it.x } ?: 1f
    val minY = allPoints.minOfOrNull { it.y } ?: 0f
    val maxY = allPoints.maxOfOrNull { it.y } ?: 1f
    val yRange = (maxY - minY).coerceAtLeast(1f)
    val xRange = (maxX - minX).coerceAtLeast(1f)

    val semanticDescription = buildString {
        append("Line chart with ${series.size} series. ")
        series.forEach { s ->
            append("${s.name}: ${s.points.size} points. ")
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Legend
        if (config.showLegend && series.size > 1) {
            ChartLegend(series = series)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                .semantics { contentDescription = semanticDescription }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val chartPadding = 40.dp.toPx()
                        val chartWidth = size.width - chartPadding * 2
                        val chartHeight = size.height - chartPadding * 2

                        // Find nearest point
                        var nearest: ChartInspection? = null
                        var nearestDist = Float.MAX_VALUE

                        series.forEach { s ->
                            s.points.forEach { p ->
                                val px = chartPadding + ((p.x - minX) / xRange) * chartWidth
                                val py = chartPadding + chartHeight - ((p.y - minY) / yRange) * chartHeight
                                val dist = sqrt((offset.x - px).pow(2) + (offset.y - py).pow(2))
                                if (dist < nearestDist && dist < 50.dp.toPx()) {
                                    nearestDist = dist
                                    nearest = ChartInspection(
                                        seriesName = s.name,
                                        label = p.label,
                                        value = p.y,
                                        position = Offset(px, py)
                                    )
                                }
                            }
                        }

                        inspectionState = nearest
                        onInspect(nearest)
                    }
                }
        ) {
            val chartPadding = 40.dp.toPx()
            val chartWidth = size.width - chartPadding * 2
            val chartHeight = size.height - chartPadding * 2

            // Grid lines
            if (config.showGrid) {
                drawChartGrid(
                    chartPadding = chartPadding,
                    chartWidth = chartWidth,
                    chartHeight = chartHeight,
                    gridColor = config.gridColor,
                    gridLineCount = config.gridLineCount
                )
            }

            // Axis labels
            if (config.showAxisLabels) {
                for (i in 0..config.gridLineCount) {
                    val yValue = minY + (yRange * i / config.gridLineCount)
                    val y = chartPadding + chartHeight - (chartHeight * i / config.gridLineCount)

                    val textResult = textMeasurer.measure(
                        text = formatAxisValue(yValue),
                        style = TextStyle(fontSize = 9.sp, color = Color.Gray)
                    )
                    drawText(
                        textLayoutResult = textResult,
                        topLeft = Offset(2.dp.toPx(), y - textResult.size.height / 2)
                    )
                }
            }

            // Draw each series
            series.forEach { s ->
                val points = s.points.sortedBy { it.x }
                if (points.size < 2) return@forEach

                val screenPoints = points.map { p ->
                    Offset(
                        x = chartPadding + ((p.x - minX) / xRange) * chartWidth,
                        y = chartPadding + chartHeight - ((p.y - minY) / yRange) * chartHeight
                    )
                }

                val visibleCount = (screenPoints.size * animationProgress).toInt().coerceAtLeast(1)
                val visiblePoints = screenPoints.take(visibleCount)

                // Fill area under curve
                s.fillColor?.let { fill ->
                    if (visiblePoints.size >= 2) {
                        val fillPath = Path().apply {
                            moveTo(visiblePoints.first().x, chartPadding + chartHeight)
                            visiblePoints.forEach { lineTo(it.x, it.y) }
                            lineTo(visiblePoints.last().x, chartPadding + chartHeight)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(fill, Color.Transparent),
                                startY = 0f,
                                endY = chartPadding + chartHeight
                            )
                        )
                    }
                }

                // Draw line
                if (visiblePoints.size >= 2) {
                    val linePath = Path().apply {
                        moveTo(visiblePoints.first().x, visiblePoints.first().y)
                        for (i in 1 until visiblePoints.size) {
                            lineTo(visiblePoints[i].x, visiblePoints[i].y)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = s.color,
                        style = Stroke(
                            width = config.lineWidth.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw data points
                if (config.showDataPoints) {
                    visiblePoints.forEach { point ->
                        drawCircle(
                            color = s.color,
                            radius = config.pointRadius.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = (config.pointRadius * 0.5f).dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            // Draw inspection indicator
            inspectionState?.let { inspection ->
                // Vertical line
                drawLine(
                    color = Color.Gray.copy(alpha = 0.5f),
                    start = Offset(inspection.position.x, chartPadding),
                    end = Offset(inspection.position.x, chartPadding + chartHeight),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
                )

                // Highlight circle
                drawCircle(
                    color = Color.White,
                    radius = 8.dp.toPx(),
                    center = inspection.position
                )
                drawCircle(
                    color = TrustBlue,
                    radius = 5.dp.toPx(),
                    center = inspection.position
                )

                // Value label
                val labelText = "${inspection.label}: ${formatAxisValue(inspection.value)}"
                val labelResult = textMeasurer.measure(
                    text = labelText,
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )

                val labelX = (inspection.position.x - labelResult.size.width / 2)
                    .coerceIn(chartPadding, size.width - chartPadding - labelResult.size.width)
                val labelY = inspection.position.y - labelResult.size.height - 12.dp.toPx()

                drawRoundRect(
                    color = Color.DarkGray,
                    topLeft = Offset(labelX - 4.dp.toPx(), labelY - 2.dp.toPx()),
                    size = Size(
                        labelResult.size.width + 8.dp.toPx(),
                        labelResult.size.height + 4.dp.toPx()
                    ),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(labelX, labelY)
                )
            }
        }
    }
}

// ==========================================================================
// 2. BarChart
// ==========================================================================

/**
 * A bar chart supporting both vertical and horizontal orientations,
 * with animated bars and value labels.
 *
 * @param bars List of bar data items.
 * @param config Chart configuration.
 * @param height Height of the chart area.
 * @param isHorizontal If true, renders horizontal bars.
 * @param modifier Optional modifier.
 */
@Composable
fun BarChart(
    bars: List<BarData>,
    config: ChartConfig = ChartConfig(),
    height: Dp = 200.dp,
    isHorizontal: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (bars.isEmpty()) {
        ChartEmptyState(height = height, modifier = modifier)
        return
    }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDurationMs, easing = FastOutSlowInEasing),
        label = "bar_chart_animation"
    )

    val textMeasurer = rememberTextMeasurer()
    val maxValue = bars.maxOf { it.value }.coerceAtLeast(1f)

    val semanticDescription = buildString {
        append("Bar chart with ${bars.size} bars. ")
        bars.forEach { bar ->
            append("${bar.label}: ${formatAxisValue(bar.value)}. ")
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
            .semantics { contentDescription = semanticDescription }
    ) {
        val chartPadding = 40.dp.toPx()
        val chartWidth = size.width - chartPadding * 2
        val chartHeight = size.height - chartPadding * 2

        // Grid
        if (config.showGrid) {
            drawChartGrid(chartPadding, chartWidth, chartHeight, config.gridColor, config.gridLineCount)
        }

        if (isHorizontal) {
            // Horizontal bars
            val barHeight = (chartHeight / bars.size) * 0.7f
            val barGap = (chartHeight / bars.size) * 0.3f

            bars.forEachIndexed { index, bar ->
                val barWidth = (bar.value / maxValue) * chartWidth * animationProgress
                val y = chartPadding + index * (barHeight + barGap) + barGap / 2

                // Bar
                drawRoundRect(
                    color = bar.color,
                    topLeft = Offset(chartPadding, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                // Label
                val labelResult = textMeasurer.measure(
                    text = bar.label,
                    style = TextStyle(fontSize = 9.sp, color = Color.Gray)
                )
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(2.dp.toPx(), y + barHeight / 2 - labelResult.size.height / 2)
                )

                // Value label
                if (config.showValueLabels) {
                    val valueResult = textMeasurer.measure(
                        text = formatAxisValue(bar.value),
                        style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = bar.color)
                    )
                    drawText(
                        textLayoutResult = valueResult,
                        topLeft = Offset(chartPadding + barWidth + 4.dp.toPx(), y + barHeight / 2 - valueResult.size.height / 2)
                    )
                }
            }
        } else {
            // Vertical bars
            val barWidth = (chartWidth / bars.size) * 0.6f
            val barGap = (chartWidth / bars.size) * 0.4f

            bars.forEachIndexed { index, bar ->
                val barHeightVal = (bar.value / maxValue) * chartHeight * animationProgress
                val x = chartPadding + index * (barWidth + barGap) + barGap / 2
                val y = chartPadding + chartHeight - barHeightVal

                // Bar
                drawRoundRect(
                    color = bar.color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeightVal),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Stacked secondary bar
                bar.secondaryValue?.let { secVal ->
                    val secHeight = (secVal / maxValue) * chartHeight * animationProgress
                    drawRoundRect(
                        color = bar.secondaryColor ?: bar.color.copy(alpha = 0.5f),
                        topLeft = Offset(x, y - secHeight),
                        size = Size(barWidth, secHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }

                // Label at bottom
                val labelResult = textMeasurer.measure(
                    text = bar.label,
                    style = TextStyle(fontSize = 8.sp, color = Color.Gray)
                )
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(
                        x + barWidth / 2 - labelResult.size.width / 2,
                        chartPadding + chartHeight + 4.dp.toPx()
                    )
                )

                // Value label on top
                if (config.showValueLabels) {
                    val valueResult = textMeasurer.measure(
                        text = formatAxisValue(bar.value),
                        style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = bar.color)
                    )
                    drawText(
                        textLayoutResult = valueResult,
                        topLeft = Offset(
                            x + barWidth / 2 - valueResult.size.width / 2,
                            y - valueResult.size.height - 2.dp.toPx()
                        )
                    )
                }
            }

            // Y-axis labels
            if (config.showAxisLabels) {
                for (i in 0..config.gridLineCount) {
                    val yValue = maxValue * i / config.gridLineCount
                    val y = chartPadding + chartHeight - (chartHeight * i / config.gridLineCount)
                    val textResult = textMeasurer.measure(
                        text = formatAxisValue(yValue),
                        style = TextStyle(fontSize = 8.sp, color = Color.Gray)
                    )
                    drawText(
                        textLayoutResult = textResult,
                        topLeft = Offset(2.dp.toPx(), y - textResult.size.height / 2)
                    )
                }
            }
        }
    }
}

// ==========================================================================
// 3. PieChart (Donut style)
// ==========================================================================

/**
 * A donut-style pie chart with animated segments, labels, interactive
 * selection, and a center label.
 *
 * @param slices List of pie slices to render.
 * @param config Chart configuration.
 * @param size Diameter of the chart.
 * @param strokeWidth Width of the donut ring.
 * @param centerLabel Optional label displayed at the center.
 * @param onSliceSelected Callback when a slice is tapped.
 * @param modifier Optional modifier.
 */
@Composable
fun PieChart(
    slices: List<PieSlice>,
    config: ChartConfig = ChartConfig(),
    size: Dp = 200.dp,
    strokeWidth: Dp = 36.dp,
    centerLabel: String? = null,
    onSliceSelected: (PieSlice?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (slices.isEmpty()) {
        ChartEmptyState(height = size, modifier = modifier)
        return
    }

    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    var selectedSliceIndex by remember { mutableIntStateOf(-1) }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDurationMs, easing = FastOutSlowInEasing),
        label = "pie_chart_animation"
    )

    val semanticDescription = buildString {
        append("Pie chart. ")
        slices.forEach { slice ->
            val pct = if (total > 0) (slice.value / total * 100).toInt() else 0
            append("${slice.label}: $pct%. ")
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            Canvas(
                modifier = Modifier
                    .size(size)
                    .semantics { contentDescription = semanticDescription }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val center = Offset(this.size.width / 2f, this.size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            val distance = sqrt(dx * dx + dy * dy)
                            val outerRadius = this.size.minDimension / 2f
                            val innerRadius = outerRadius - strokeWidth.toPx()

                            if (distance in innerRadius..outerRadius) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f
                                angle = (angle + 90f) % 360f

                                var currentAngle = 0f
                                slices.forEachIndexed { index, slice ->
                                    val sweep = (slice.value / total) * 360f
                                    if (angle >= currentAngle && angle < currentAngle + sweep) {
                                        selectedSliceIndex = if (selectedSliceIndex == index) -1 else index
                                        onSliceSelected(if (selectedSliceIndex >= 0) slices[selectedSliceIndex] else null)
                                        return@detectTapGestures
                                    }
                                    currentAngle += sweep
                                }
                            } else {
                                selectedSliceIndex = -1
                                onSliceSelected(null)
                            }
                        }
                    }
            ) {
                val canvasStrokeWidth = strokeWidth.toPx()
                val canvasSize = this.size.minDimension
                val radius = (canvasSize - canvasStrokeWidth) / 2f
                val center = Offset(this.size.width / 2f, this.size.height / 2f)

                if (total == 0f) {
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = canvasStrokeWidth)
                    )
                    return@Canvas
                }

                var startAngle = -90f
                slices.forEachIndexed { index, slice ->
                    val sweep = (slice.value / total) * 360f * animationProgress
                    val isSelected = index == selectedSliceIndex

                    val adjustedStrokeWidth = if (isSelected) canvasStrokeWidth + 6.dp.toPx() else canvasStrokeWidth

                    drawArc(
                        color = if (isSelected) slice.color else slice.color.copy(alpha = 0.85f),
                        startAngle = startAngle,
                        sweepAngle = sweep - 1f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = adjustedStrokeWidth, cap = StrokeCap.Butt)
                    )

                    startAngle += sweep
                }
            }

            // Center label
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (selectedSliceIndex >= 0 && selectedSliceIndex < slices.size) {
                    val selectedSlice = slices[selectedSliceIndex]
                    val pct = if (total > 0) (selectedSlice.value / total * 100).toInt() else 0
                    Text(
                        text = "$pct%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = selectedSlice.color
                    )
                    Text(
                        text = selectedSlice.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = centerLabel ?: total.toInt().toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (centerLabel != null) "" else "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Legend
        if (config.showLegend) {
            Spacer(modifier = Modifier.height(12.dp))
            PieChartLegend(slices = slices, total = total, selectedIndex = selectedSliceIndex)
        }
    }
}

@Composable
private fun PieChartLegend(
    slices: List<PieSlice>,
    total: Float,
    selectedIndex: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        slices.forEachIndexed { index, slice ->
            val pct = if (total > 0) (slice.value / total * 100).toInt() else 0
            val isSelected = index == selectedIndex

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) slice.color.copy(alpha = 0.08f) else Color.Transparent,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(slice.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = slice.color
                )
            }
        }
    }
}

// ==========================================================================
// 4. AreaChart
// ==========================================================================

/**
 * A filled area chart with gradient fill under the curve, supporting
 * multiple series with configurable appearance.
 *
 * @param series List of data series.
 * @param config Chart configuration.
 * @param height Height of the chart area.
 * @param modifier Optional modifier.
 */
@Composable
fun AreaChart(
    series: List<ChartSeries>,
    config: ChartConfig = ChartConfig(),
    height: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    // Use LineChart with fill colors set
    val filledSeries = series.map { s ->
        s.copy(fillColor = s.fillColor ?: s.color.copy(alpha = 0.2f))
    }

    LineChart(
        series = filledSeries,
        config = config.copy(showDataPoints = false),
        height = height,
        modifier = modifier
    )
}

// ==========================================================================
// 5. RadarChart
// ==========================================================================

/**
 * A radar (spider web) chart for multi-dimensional data visualization.
 * Used for showing privacy scores across multiple dimensions.
 *
 * @param dimensions List of radar dimensions with values.
 * @param fillColor Fill color for the data area.
 * @param strokeColor Stroke color for the data outline.
 * @param size Diameter of the chart.
 * @param config Chart configuration.
 * @param modifier Optional modifier.
 */
@Composable
fun RadarChart(
    dimensions: List<RadarDimension>,
    fillColor: Color = TrustBlue.copy(alpha = 0.2f),
    strokeColor: Color = TrustBlue,
    size: Dp = 200.dp,
    config: ChartConfig = ChartConfig(),
    modifier: Modifier = Modifier
) {
    if (dimensions.size < 3) {
        ChartEmptyState(height = size, message = "Need 3+ dimensions", modifier = modifier)
        return
    }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDurationMs, easing = FastOutSlowInEasing),
        label = "radar_animation"
    )

    val textMeasurer = rememberTextMeasurer()
    val count = dimensions.size

    val semanticDescription = buildString {
        append("Radar chart with ${dimensions.size} dimensions. ")
        dimensions.forEach { d ->
            append("${d.label}: ${d.value.toInt()} of ${d.maxValue.toInt()}. ")
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .semantics { contentDescription = semanticDescription }
        ) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxRadius = (this.size.minDimension / 2f) - 30.dp.toPx()
            val angleStep = (2 * PI / count).toFloat()

            // Draw web rings
            if (config.showGrid) {
                for (ring in 1..4) {
                    val ringRadius = maxRadius * ring / 4
                    val ringPath = Path().apply {
                        for (i in 0 until count) {
                            val angle = -PI / 2 + i * angleStep
                            val x = center.x + ringRadius * cos(angle).toFloat()
                            val y = center.y + ringRadius * sin(angle).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(
                        path = ringPath,
                        color = config.gridColor,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Draw axis lines
                for (i in 0 until count) {
                    val angle = -PI / 2 + i * angleStep
                    val endX = center.x + maxRadius * cos(angle).toFloat()
                    val endY = center.y + maxRadius * sin(angle).toFloat()
                    drawLine(
                        color = config.gridColor,
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Draw data polygon
            val dataPath = Path().apply {
                dimensions.forEachIndexed { i, dim ->
                    val normalizedValue = (dim.value / dim.maxValue).coerceIn(0f, 1f) * animationProgress
                    val angle = -PI / 2 + i * angleStep
                    val r = maxRadius * normalizedValue
                    val x = center.x + r * cos(angle).toFloat()
                    val y = center.y + r * sin(angle).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }

            drawPath(path = dataPath, color = fillColor)
            drawPath(
                path = dataPath,
                color = strokeColor,
                style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
            )

            // Draw data points
            if (config.showDataPoints) {
                dimensions.forEachIndexed { i, dim ->
                    val normalizedValue = (dim.value / dim.maxValue).coerceIn(0f, 1f) * animationProgress
                    val angle = -PI / 2 + i * angleStep
                    val r = maxRadius * normalizedValue
                    val x = center.x + r * cos(angle).toFloat()
                    val y = center.y + r * sin(angle).toFloat()

                    drawCircle(color = strokeColor, radius = 4.dp.toPx(), center = Offset(x, y))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
                }
            }

            // Draw labels
            dimensions.forEachIndexed { i, dim ->
                val angle = -PI / 2 + i * angleStep
                val labelRadius = maxRadius + 18.dp.toPx()
                val x = center.x + labelRadius * cos(angle).toFloat()
                val y = center.y + labelRadius * sin(angle).toFloat()

                val labelResult = textMeasurer.measure(
                    text = dim.label,
                    style = TextStyle(fontSize = 9.sp, color = Color.Gray)
                )
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(
                        x - labelResult.size.width / 2,
                        y - labelResult.size.height / 2
                    )
                )
            }
        }
    }
}

// ==========================================================================
// 6. SparkLine
// ==========================================================================

/**
 * A minimal inline line chart for use within stat cards.
 * No axes, grid, or labels -- just a small trend line.
 *
 * @param values List of values to plot.
 * @param color Line color.
 * @param height Height of the sparkline.
 * @param strokeWidth Line stroke width.
 * @param showGradient Whether to show a gradient fill under the line.
 * @param modifier Optional modifier.
 */
@Composable
fun SparkLine(
    values: List<Float>,
    color: Color = TrustBlue,
    height: Dp = 32.dp,
    strokeWidth: Dp = 2.dp,
    showGradient: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (values.size < 2) return

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "sparkline_animation"
    )

    val minVal = values.min()
    val maxVal = values.max()
    val range = (maxVal - minVal).coerceAtLeast(0.001f)

    val trend = values.last() - values.first()
    val trendDescription = if (trend >= 0) "upward trend" else "downward trend"

    Canvas(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .semantics {
                contentDescription = "Sparkline with ${values.size} points, $trendDescription"
            }
    ) {
        val padding = 2.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        val points = values.mapIndexed { index, value ->
            Offset(
                x = padding + (chartWidth * index / (values.size - 1)),
                y = padding + chartHeight * (1f - (value - minVal) / range)
            )
        }

        val visibleCount = (points.size * animationProgress).toInt().coerceAtLeast(2)
        val visiblePoints = points.take(visibleCount)

        // Gradient fill
        if (showGradient && visiblePoints.size >= 2) {
            val fillPath = Path().apply {
                moveTo(visiblePoints.first().x, size.height)
                visiblePoints.forEach { lineTo(it.x, it.y) }
                lineTo(visiblePoints.last().x, size.height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = size.height
                )
            )
        }

        // Line
        if (visiblePoints.size >= 2) {
            val linePath = Path().apply {
                moveTo(visiblePoints.first().x, visiblePoints.first().y)
                for (i in 1 until visiblePoints.size) {
                    lineTo(visiblePoints[i].x, visiblePoints[i].y)
                }
            }
            drawPath(
                path = linePath,
                color = color,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // End dot
        if (visiblePoints.isNotEmpty()) {
            val lastPoint = visiblePoints.last()
            drawCircle(color = color, radius = 3.dp.toPx(), center = lastPoint)
            drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = lastPoint)
        }
    }
}

// ==========================================================================
// 7. ProgressRing
// ==========================================================================

/**
 * A circular progress ring with animated fill and a center label.
 *
 * @param progress Progress value from 0f to 1f.
 * @param color Ring fill color.
 * @param trackColor Ring track color.
 * @param size Diameter of the ring.
 * @param strokeWidth Width of the ring stroke.
 * @param centerLabel Label to display at the center.
 * @param centerSubLabel Optional sub-label below the center label.
 * @param modifier Optional modifier.
 */
@Composable
fun ProgressRing(
    progress: Float,
    color: Color = TrustBlue,
    trackColor: Color = Color.LightGray.copy(alpha = 0.2f),
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    centerLabel: String = "",
    centerSubLabel: String = "",
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress_ring_animation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "$centerLabel: ${(progress * 100).toInt()} percent"
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasStrokeWidth = strokeWidth.toPx()
            val radius = (this.size.minDimension - canvasStrokeWidth) / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Track
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = canvasStrokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = animatedProgress * 360f
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = canvasStrokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (centerSubLabel.isNotEmpty()) {
                Text(
                    text = centerSubLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }
    }
}

// ==========================================================================
// Shared chart utilities
// ==========================================================================

/**
 * Draws grid lines within a chart canvas.
 */
private fun DrawScope.drawChartGrid(
    chartPadding: Float,
    chartWidth: Float,
    chartHeight: Float,
    gridColor: Color,
    gridLineCount: Int
) {
    // Horizontal grid lines
    for (i in 0..gridLineCount) {
        val y = chartPadding + (chartHeight * i / gridLineCount)
        drawLine(
            color = gridColor,
            start = Offset(chartPadding, y),
            end = Offset(chartPadding + chartWidth, y),
            strokeWidth = 1f
        )
    }

    // Vertical grid lines
    for (i in 0..gridLineCount) {
        val x = chartPadding + (chartWidth * i / gridLineCount)
        drawLine(
            color = gridColor,
            start = Offset(x, chartPadding),
            end = Offset(x, chartPadding + chartHeight),
            strokeWidth = 1f
        )
    }
}

/**
 * Formats a numeric value for axis labels.
 */
private fun formatAxisValue(value: Float): String {
    return when {
        value >= 1_000_000 -> "${(value / 1_000_000).toInt()}M"
        value >= 1_000 -> "${(value / 1_000).toInt()}K"
        value == value.toInt().toFloat() -> value.toInt().toString()
        else -> String.format("%.1f", value)
    }
}

/**
 * Empty state placeholder for charts with no data.
 */
@Composable
private fun ChartEmptyState(
    height: Dp,
    message: String = "No data available",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A horizontal legend row for chart series.
 */
@Composable
private fun ChartLegend(series: List<ChartSeries>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        series.forEach { s ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(s.color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = s.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================================================
// Sample data for previews
// ==========================================================================

private fun sampleLineChartSeries(): List<ChartSeries> = listOf(
    ChartSeries(
        name = "Protection Score",
        points = listOf(
            ChartDataPoint(0f, 85f, "Mon"),
            ChartDataPoint(1f, 82f, "Tue"),
            ChartDataPoint(2f, 90f, "Wed"),
            ChartDataPoint(3f, 78f, "Thu"),
            ChartDataPoint(4f, 65f, "Fri"),
            ChartDataPoint(5f, 88f, "Sat"),
            ChartDataPoint(6f, 92f, "Sun")
        ),
        color = TrustBlue,
        fillColor = TrustBlue.copy(alpha = 0.15f)
    ),
    ChartSeries(
        name = "Detections",
        points = listOf(
            ChartDataPoint(0f, 3f, "Mon"),
            ChartDataPoint(1f, 5f, "Tue"),
            ChartDataPoint(2f, 2f, "Wed"),
            ChartDataPoint(3f, 8f, "Thu"),
            ChartDataPoint(4f, 12f, "Fri"),
            ChartDataPoint(5f, 4f, "Sat"),
            ChartDataPoint(6f, 1f, "Sun")
        ),
        color = AlertOrange,
        fillColor = AlertOrange.copy(alpha = 0.1f)
    )
)

private fun sampleBarData(): List<BarData> = listOf(
    BarData("Mon", 3f, TrustBlue),
    BarData("Tue", 5f, TrustBlue),
    BarData("Wed", 2f, TrustBlue),
    BarData("Thu", 8f, AlertOrange),
    BarData("Fri", 12f, AlertRed),
    BarData("Sat", 4f, TrustBlue),
    BarData("Sun", 1f, SuccessGreen)
)

private fun sampleStackedBarData(): List<BarData> = listOf(
    BarData("Mon", 3f, SeverityCritical, secondaryValue = 5f, secondaryColor = SeverityHigh),
    BarData("Tue", 5f, SeverityCritical, secondaryValue = 3f, secondaryColor = SeverityHigh),
    BarData("Wed", 2f, SeverityCritical, secondaryValue = 8f, secondaryColor = SeverityHigh),
    BarData("Thu", 8f, SeverityCritical, secondaryValue = 2f, secondaryColor = SeverityHigh),
    BarData("Fri", 12f, SeverityCritical, secondaryValue = 6f, secondaryColor = SeverityHigh)
)

private fun samplePieSlices(): List<PieSlice> = listOf(
    PieSlice("Credit Card", 12f, SeverityCritical),
    PieSlice("Email", 23f, SeverityHigh),
    PieSlice("Phone", 18f, AlertOrange),
    PieSlice("Name", 31f, SeverityMedium),
    PieSlice("Address", 8f, TrustBlue),
    PieSlice("Other", 5f, ProtectionInactive)
)

private fun sampleRadarDimensions(): List<RadarDimension> = listOf(
    RadarDimension("Detection", 85f),
    RadarDimension("Response", 70f),
    RadarDimension("Coverage", 92f),
    RadarDimension("Accuracy", 88f),
    RadarDimension("Speed", 75f),
    RadarDimension("Privacy", 95f)
)

// ==========================================================================
// Preview composables
// ==========================================================================

@Preview(showBackground = true, name = "Line Chart - Multi Series")
@Composable
private fun LineChartPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        LineChart(
            series = sampleLineChartSeries(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Line Chart - Single Series")
@Composable
private fun LineChartSingleSeriesPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        LineChart(
            series = sampleLineChartSeries().take(1),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Line Chart - Empty")
@Composable
private fun LineChartEmptyPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        LineChart(
            series = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Bar Chart - Vertical")
@Composable
private fun BarChartVerticalPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        BarChart(
            bars = sampleBarData(),
            config = ChartConfig(showValueLabels = true),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Bar Chart - Horizontal")
@Composable
private fun BarChartHorizontalPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        BarChart(
            bars = sampleBarData(),
            config = ChartConfig(showValueLabels = true),
            isHorizontal = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Bar Chart - Stacked")
@Composable
private fun BarChartStackedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        BarChart(
            bars = sampleStackedBarData(),
            config = ChartConfig(showValueLabels = true),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Pie Chart - Donut")
@Composable
private fun PieChartPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PieChart(
            slices = samplePieSlices(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Pie Chart - Empty")
@Composable
private fun PieChartEmptyPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PieChart(
            slices = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Area Chart")
@Composable
private fun AreaChartPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        AreaChart(
            series = listOf(
                ChartSeries(
                    name = "Score",
                    points = listOf(
                        ChartDataPoint(0f, 85f), ChartDataPoint(1f, 72f),
                        ChartDataPoint(2f, 90f), ChartDataPoint(3f, 68f),
                        ChartDataPoint(4f, 75f), ChartDataPoint(5f, 82f),
                        ChartDataPoint(6f, 95f)
                    ),
                    color = SuccessGreen
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Radar Chart")
@Composable
private fun RadarChartPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        RadarChart(
            dimensions = sampleRadarDimensions(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "SparkLine")
@Composable
private fun SparkLinePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Detections", style = MaterialTheme.typography.labelSmall)
                        Text("7 today", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    SparkLine(
                        values = listOf(3f, 5f, 2f, 8f, 12f, 4f, 7f),
                        color = AlertOrange,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Score", style = MaterialTheme.typography.labelSmall)
                        Text("92", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    SparkLine(
                        values = listOf(85f, 82f, 90f, 78f, 65f, 88f, 92f),
                        color = SuccessGreen,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Progress Rings")
@Composable
private fun ProgressRingPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgressRing(
                progress = 0.92f,
                color = SuccessGreen,
                centerLabel = "92%",
                centerSubLabel = "Score"
            )
            ProgressRing(
                progress = 0.65f,
                color = AlertOrange,
                centerLabel = "65%",
                centerSubLabel = "Conf."
            )
            ProgressRing(
                progress = 0.25f,
                color = AlertRed,
                centerLabel = "25%",
                centerSubLabel = "Risk"
            )
        }
    }
}

@Preview(showBackground = true, name = "Progress Ring - Large")
@Composable
private fun ProgressRingLargePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProgressRing(
            progress = 0.78f,
            color = TrustBlue,
            size = 120.dp,
            strokeWidth = 12.dp,
            centerLabel = "78%",
            centerSubLabel = "Protected",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Chart Composition - Stats Dashboard")
@Composable
private fun ChartCompositionPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Privacy Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                LineChart(
                    series = sampleLineChartSeries().take(1),
                    config = ChartConfig(showLegend = false),
                    height = 150.dp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProgressRing(
                            progress = 0.92f,
                            color = SuccessGreen,
                            size = 64.dp,
                            strokeWidth = 6.dp,
                            centerLabel = "92",
                            centerSubLabel = "Score"
                        )
                    }
                }
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProgressRing(
                            progress = 0.35f,
                            color = AlertOrange,
                            size = 64.dp,
                            strokeWidth = 6.dp,
                            centerLabel = "35",
                            centerSubLabel = "Alerts"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Bar Chart - Empty")
@Composable
private fun BarChartEmptyPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        BarChart(
            bars = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Radar Chart - Fewer Dimensions")
@Composable
private fun RadarChartFewerDimensionsPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        RadarChart(
            dimensions = listOf(
                RadarDimension("Detection", 90f),
                RadarDimension("Speed", 65f),
                RadarDimension("Accuracy", 80f)
            ),
            fillColor = SuccessGreen.copy(alpha = 0.2f),
            strokeColor = SuccessGreen,
            modifier = Modifier.padding(16.dp)
        )
    }
}
