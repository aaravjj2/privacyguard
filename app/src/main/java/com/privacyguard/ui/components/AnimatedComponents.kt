package com.privacyguard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.ui.theme.*
import kotlin.math.*

// ---------------------------------------------------------------------------
// 1. AnimatedShield
// ---------------------------------------------------------------------------

/**
 * Animated shield composable that renders a shield shape on Canvas with
 * optional pulsing, rotation, and color-transition effects.
 *
 * Used on the dashboard to communicate protection status visually.
 *
 * @param isActive Whether the shield is in active-protection mode.
 * @param modifier Modifier for sizing and layout.
 * @param shieldColor Primary color of the shield body.
 * @param glowColor Color of the glow/pulse ring around the shield.
 * @param size The diameter of the shield canvas.
 * @param showPulse Whether to show the pulsing ring animation.
 * @param contentDescription Accessibility description for screen readers.
 */
@Composable
fun AnimatedShield(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    shieldColor: Color = if (isActive) ProtectionActive else ProtectionInactive,
    glowColor: Color = if (isActive) ProtectionActive.copy(alpha = 0.3f) else Color.Transparent,
    size: Dp = 120.dp,
    showPulse: Boolean = isActive,
    contentDescription: String = if (isActive) "Protection is active" else "Protection is inactive"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield_transition")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val animatedColor by animateColorAsState(
        targetValue = shieldColor,
        animationSpec = tween(durationMillis = 600),
        label = "shield_color"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "shield_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scaleAnim)
            .semantics {
                this.contentDescription = contentDescription
                this.stateDescription = if (isActive) "Active" else "Inactive"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val shieldWidth = this.size.width * 0.6f
            val shieldHeight = this.size.height * 0.7f

            // Pulse ring
            if (showPulse) {
                drawCircle(
                    color = glowColor.copy(alpha = pulseAlpha),
                    radius = cx * pulseScale,
                    center = Offset(cx, cy)
                )
            }

            // Outer glow orbit dots
            if (isActive) {
                for (i in 0 until 6) {
                    val angle = rotationAngle + (i * 60f)
                    val rad = Math.toRadians(angle.toDouble())
                    val orbitRadius = cx * 0.85f
                    val dotX = cx + orbitRadius * cos(rad).toFloat()
                    val dotY = cy + orbitRadius * sin(rad).toFloat()
                    drawCircle(
                        color = animatedColor.copy(alpha = 0.4f),
                        radius = 3.dp.toPx(),
                        center = Offset(dotX, dotY)
                    )
                }
            }

            // Shield body path
            val shieldPath = Path().apply {
                moveTo(cx, cy - shieldHeight / 2f)
                cubicTo(
                    cx + shieldWidth / 2f, cy - shieldHeight / 2f,
                    cx + shieldWidth / 2f, cy,
                    cx, cy + shieldHeight / 2f
                )
                cubicTo(
                    cx - shieldWidth / 2f, cy,
                    cx - shieldWidth / 2f, cy - shieldHeight / 2f,
                    cx, cy - shieldHeight / 2f
                )
                close()
            }

            drawPath(
                path = shieldPath,
                color = animatedColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = shieldPath,
                color = animatedColor.copy(alpha = 0.15f)
            )

            // Checkmark inside shield when active
            if (isActive) {
                val checkPath = Path().apply {
                    moveTo(cx - shieldWidth * 0.15f, cy)
                    lineTo(cx - shieldWidth * 0.02f, cy + shieldHeight * 0.1f)
                    lineTo(cx + shieldWidth * 0.18f, cy - shieldHeight * 0.12f)
                }
                drawPath(
                    path = checkPath,
                    color = animatedColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "AnimatedShield - Active")
@Composable
private fun AnimatedShieldActivePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            AnimatedShield(isActive = true)
        }
    }
}

@Preview(showBackground = true, name = "AnimatedShield - Inactive")
@Composable
private fun AnimatedShieldInactivePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            AnimatedShield(isActive = false)
        }
    }
}

// ---------------------------------------------------------------------------
// 2. PulsingDot
// ---------------------------------------------------------------------------

/**
 * A small pulsing dot indicator used for live-status displays.
 *
 * @param color The base color of the dot.
 * @param size The diameter of the dot.
 * @param isPulsing Whether the dot should animate.
 * @param contentDescription Accessibility label.
 */
@Composable
fun PulsingDot(
    color: Color = ProtectionActive,
    size: Dp = 12.dp,
    isPulsing: Boolean = true,
    contentDescription: String = "Status indicator"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_dot")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 0.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Box(
        modifier = Modifier
            .size(size * 2)
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer pulse ring
        if (isPulsing) {
            Box(
                modifier = Modifier
                    .size(size * scale)
                    .alpha(alpha * 0.4f)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        // Inner solid dot
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Preview(showBackground = true, name = "PulsingDot - Active")
@Composable
private fun PulsingDotActivePreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDot(color = ProtectionActive, isPulsing = true)
            Text("Active")
        }
    }
}

@Preview(showBackground = true, name = "PulsingDot - Inactive")
@Composable
private fun PulsingDotInactivePreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDot(color = ProtectionInactive, isPulsing = false)
            Text("Inactive")
        }
    }
}

// ---------------------------------------------------------------------------
// 3. AnimatedCounter
// ---------------------------------------------------------------------------

/**
 * Animated counter that rolls up or down to the target value with
 * spring physics, designed for displaying detection counts.
 *
 * @param targetValue The numeric value to animate toward.
 * @param label An optional label displayed beneath the number.
 * @param modifier Modifier for layout.
 * @param textStyle Style for the counter text.
 * @param labelStyle Style for the label text.
 * @param prefix Optional prefix (e.g., "$" or "#").
 * @param suffix Optional suffix (e.g., "detections").
 * @param contentDescription Accessibility description.
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    label: String = "",
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
    labelStyle: TextStyle = MaterialTheme.typography.bodySmall,
    prefix: String = "",
    suffix: String = "",
    contentDescription: String = "$targetValue $label"
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "counter_value"
    )

    val scaleEffect by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "counter_scale"
    )

    Column(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$prefix$animatedValue$suffix",
            style = textStyle,
            modifier = Modifier.scale(scaleEffect)
        )
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = labelStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "AnimatedCounter - Basic")
@Composable
private fun AnimatedCounterBasicPreview() {
    MaterialTheme {
        AnimatedCounter(
            targetValue = 42,
            label = "Threats Blocked",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "AnimatedCounter - With Prefix")
@Composable
private fun AnimatedCounterPrefixPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            AnimatedCounter(targetValue = 128, label = "Scans", prefix = "#")
            AnimatedCounter(targetValue = 7, label = "Critical", prefix = "")
        }
    }
}

// ---------------------------------------------------------------------------
// 4. ShimmerBox
// ---------------------------------------------------------------------------

/**
 * Shimmer loading placeholder box with a horizontal gradient sweep.
 *
 * @param modifier Modifier controlling the size and shape.
 * @param shimmerColor Base color of the shimmer highlight.
 * @param baseColor Background color behind the shimmer.
 * @param durationMs Duration of one full shimmer sweep in milliseconds.
 * @param cornerRadius Corner radius of the shimmer box.
 * @param contentDescription Accessibility label.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shimmerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    baseColor: Color = MaterialTheme.colorScheme.surface,
    durationMs: Int = 1200,
    cornerRadius: Dp = 8.dp,
    contentDescription: String = "Loading"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }

    Canvas(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.stateDescription = "Loading"
            }
    ) {
        val width = size.width
        val height = size.height

        // Base rectangle
        drawRoundRect(
            color = baseColor,
            size = size,
            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
        )

        // Shimmer gradient overlay
        val shimmerStart = width * shimmerTranslate
        val shimmerWidth = width * 0.4f
        val brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                shimmerColor.copy(alpha = 0.4f),
                shimmerColor.copy(alpha = 0.7f),
                shimmerColor.copy(alpha = 0.4f),
                Color.Transparent
            ),
            startX = shimmerStart,
            endX = shimmerStart + shimmerWidth
        )

        drawRoundRect(
            brush = brush,
            size = size,
            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
        )
    }
}

/**
 * A shimmer loading placeholder that mimics a text line.
 */
@Composable
fun ShimmerTextLine(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 16.dp,
    cornerRadius: Dp = 4.dp,
    contentDescription: String = "Loading text"
) {
    ShimmerBox(
        modifier = modifier
            .width(width)
            .height(height),
        cornerRadius = cornerRadius,
        contentDescription = contentDescription
    )
}

/**
 * A shimmer loading placeholder that mimics an avatar/icon circle.
 */
@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    contentDescription: String = "Loading avatar"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_circle")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_circle_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
            .semantics {
                this.contentDescription = contentDescription
            }
    )
}

/**
 * A shimmer card placeholder mimicking a settings row or list item.
 */
@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier,
    contentDescription: String = "Loading list item"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerCircle(size = 40.dp, contentDescription = "Loading icon")
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                ShimmerTextLine(width = 140.dp, height = 14.dp, contentDescription = "Loading title")
                ShimmerTextLine(width = 200.dp, height = 10.dp, contentDescription = "Loading description")
            }
            ShimmerBox(
                modifier = Modifier.size(width = 48.dp, height = 24.dp),
                cornerRadius = 12.dp,
                contentDescription = "Loading control"
            )
        }
    }
}

@Preview(showBackground = true, name = "ShimmerBox - Variations")
@Composable
private fun ShimmerBoxPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentDescription = "Loading card"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerTextLine(width = 120.dp)
                ShimmerTextLine(width = 80.dp)
            }
            ShimmerCircle()
        }
    }
}

@Preview(showBackground = true, name = "ShimmerListItem")
@Composable
private fun ShimmerListItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerListItem()
            ShimmerListItem()
            ShimmerListItem()
        }
    }
}

// ---------------------------------------------------------------------------
// 5. AnimatedProgressBar
// ---------------------------------------------------------------------------

/**
 * Animated determinate progress bar with gradient fill and optional
 * label/percentage display.
 *
 * @param progress Current progress value in 0f..1f.
 * @param modifier Modifier for sizing and layout.
 * @param progressColor Color of the filled section.
 * @param trackColor Background track color.
 * @param height Height of the progress bar.
 * @param cornerRadius Corner radius of the bar.
 * @param showPercentage Whether to display the percentage text.
 * @param label Optional label displayed beside the bar.
 * @param animationDurationMs Duration of the progress animation.
 * @param contentDescription Accessibility description.
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = TrustBlue,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 8.dp,
    cornerRadius: Dp = 4.dp,
    showPercentage: Boolean = false,
    label: String = "",
    animationDurationMs: Int = 800,
    contentDescription: String = "Progress ${(progress * 100).toInt()}%"
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = EaseOutCubic
        ),
        label = "progress_animation"
    )

    Column(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = progress,
                range = 0f..1f
            )
        }
    ) {
        if (label.isNotEmpty() || showPercentage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (showPercentage) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = progressColor
                    )
                }
            }
        }

        val density = LocalDensity.current
        val cornerPx = with(density) { cornerRadius.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            // Track
            drawRoundRect(
                color = trackColor,
                size = size,
                cornerRadius = CornerRadius(cornerPx, cornerPx)
            )
            // Filled progress
            if (animatedProgress > 0f) {
                drawRoundRect(
                    color = progressColor,
                    size = Size(width = size.width * animatedProgress, height = size.height),
                    cornerRadius = CornerRadius(cornerPx, cornerPx)
                )
            }
        }
    }
}

/**
 * A segmented progress bar with multiple colored segments.
 */
@Composable
fun SegmentedProgressBar(
    segments: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    cornerRadius: Dp = 4.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    animationDurationMs: Int = 800,
    contentDescription: String = "Segmented progress"
) {
    val animatedSegments = segments.mapIndexed { index, (value, color) ->
        val animatedValue by animateFloatAsState(
            targetValue = value.coerceIn(0f, 1f),
            animationSpec = tween(
                durationMillis = animationDurationMs,
                delayMillis = index * 100,
                easing = EaseOutCubic
            ),
            label = "segment_$index"
        )
        animatedValue to color
    }

    val density = LocalDensity.current
    val cornerPx = with(density) { cornerRadius.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )

        var offset = 0f
        for ((value, color) in animatedSegments) {
            val segmentWidth = size.width * value
            if (segmentWidth > 0f) {
                drawRect(
                    color = color,
                    topLeft = Offset(offset, 0f),
                    size = Size(segmentWidth, size.height)
                )
            }
            offset += segmentWidth
        }
    }
}

@Preview(showBackground = true, name = "AnimatedProgressBar - Basic")
@Composable
private fun AnimatedProgressBarPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedProgressBar(
                progress = 0.7f,
                label = "Scan Progress",
                showPercentage = true
            )
            AnimatedProgressBar(
                progress = 0.3f,
                progressColor = AlertOrange,
                label = "Detection Confidence",
                showPercentage = true
            )
            AnimatedProgressBar(
                progress = 1.0f,
                progressColor = SuccessGreen,
                label = "Complete",
                showPercentage = true
            )
        }
    }
}

@Preview(showBackground = true, name = "SegmentedProgressBar")
@Composable
private fun SegmentedProgressBarPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Threat Breakdown", style = MaterialTheme.typography.bodySmall)
            SegmentedProgressBar(
                segments = listOf(
                    0.4f to SeverityCritical,
                    0.25f to SeverityHigh,
                    0.2f to SeverityMedium,
                    0.15f to ProtectionActive
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 6. TypewriterText
// ---------------------------------------------------------------------------

/**
 * Text composable that reveals its content character by character,
 * simulating a typewriter effect.
 *
 * @param text The full text to reveal.
 * @param modifier Modifier for layout.
 * @param style The text style.
 * @param charDelayMs Delay between each character reveal.
 * @param startDelayMs Initial delay before typing begins.
 * @param onComplete Callback when all characters have been revealed.
 * @param cursor Optional cursor character shown at the typing position.
 * @param showCursor Whether to show a blinking cursor.
 * @param contentDescription Accessibility description.
 */
@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    charDelayMs: Long = 50L,
    startDelayMs: Long = 300L,
    onComplete: () -> Unit = {},
    cursor: Char = '|',
    showCursor: Boolean = true,
    contentDescription: String = text
) {
    var visibleChars by remember { mutableIntStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    LaunchedEffect(text) {
        visibleChars = 0
        isComplete = false
        kotlinx.coroutines.delay(startDelayMs)
        for (i in 1..text.length) {
            visibleChars = i
            kotlinx.coroutines.delay(charDelayMs)
        }
        isComplete = true
        onComplete()
    }

    val displayText = text.take(visibleChars)
    val cursorStr = if (showCursor && !isComplete) {
        cursor.toString()
    } else if (showCursor && isComplete) {
        // Blinking cursor after completion
        if (cursorAlpha > 0.5f) cursor.toString() else " "
    } else {
        ""
    }

    Text(
        text = "$displayText$cursorStr",
        style = style,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        }
    )
}

/**
 * Multi-line typewriter that reveals text line by line.
 */
@Composable
fun TypewriterTextMultiLine(
    lines: List<String>,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    lineDelayMs: Long = 800L,
    charDelayMs: Long = 40L,
    contentDescription: String = lines.joinToString(". ")
) {
    var currentLineIndex by remember { mutableIntStateOf(0) }
    var visibleLines by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(lines) {
        visibleLines = emptyList()
        currentLineIndex = 0
        for (i in lines.indices) {
            currentLineIndex = i
            val line = lines[i]
            val chars = StringBuilder()
            for (c in line) {
                chars.append(c)
                visibleLines = visibleLines.toMutableList().also {
                    if (it.size <= i) it.add(chars.toString())
                    else it[i] = chars.toString()
                }
                kotlinx.coroutines.delay(charDelayMs)
            }
            kotlinx.coroutines.delay(lineDelayMs)
        }
    }

    Column(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        visibleLines.forEach { line ->
            Text(text = line, style = style)
        }
    }
}

@Preview(showBackground = true, name = "TypewriterText")
@Composable
private fun TypewriterTextPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypewriterText(
                text = "Scanning for sensitive data...",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "TypewriterTextMultiLine")
@Composable
private fun TypewriterTextMultiLinePreview() {
    MaterialTheme {
        TypewriterTextMultiLine(
            lines = listOf(
                "Initializing on-device AI model...",
                "Scanning clipboard contents...",
                "No sensitive data detected.",
                "Protection is active."
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 7. WaveAnimation
// ---------------------------------------------------------------------------

/**
 * Animated sine-wave canvas composable often used for background decoration
 * or audio-level style indicators.
 *
 * @param modifier Modifier for layout and sizing.
 * @param waveColor Primary color of the wave stroke.
 * @param amplitude The wave amplitude in dp.
 * @param frequency Number of wave cycles visible.
 * @param strokeWidth Width of the wave stroke.
 * @param speed Speed multiplier for the animation.
 * @param layers Number of stacked wave layers.
 * @param contentDescription Accessibility label.
 */
@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    waveColor: Color = TrustBlue,
    amplitude: Dp = 20.dp,
    frequency: Float = 2f,
    strokeWidth: Dp = 2.dp,
    speed: Float = 1f,
    layers: Int = 3,
    contentDescription: String = "Wave animation"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (3000 / speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val density = LocalDensity.current
    val amplitudePx = with(density) { amplitude.toPx() }
    val strokePx = with(density) { strokeWidth.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(amplitude * 4)
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        val width = size.width
        val height = size.height
        val cy = height / 2f

        for (layer in 0 until layers) {
            val layerAlpha = 1f - (layer.toFloat() / layers.toFloat()) * 0.6f
            val layerAmplitude = amplitudePx * (1f - layer.toFloat() / layers.toFloat() * 0.3f)
            val layerPhaseOffset = layer * 0.6f

            val path = Path().apply {
                moveTo(0f, cy)
                var x = 0f
                while (x <= width) {
                    val y = cy + layerAmplitude * sin(
                        (x / width) * frequency * 2f * PI.toFloat() + phase + layerPhaseOffset
                    )
                    lineTo(x, y)
                    x += 2f
                }
            }

            drawPath(
                path = path,
                color = waveColor.copy(alpha = layerAlpha),
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "WaveAnimation")
@Composable
private fun WaveAnimationPreview() {
    MaterialTheme {
        WaveAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            waveColor = TrustBlue,
            layers = 3
        )
    }
}

// ---------------------------------------------------------------------------
// 8. BreathingCircle
// ---------------------------------------------------------------------------

/**
 * A circle that smoothly scales in and out, like breathing, used to
 * indicate a calm or idle state.
 *
 * @param color The circle color.
 * @param size The base size.
 * @param minScale Minimum scale factor.
 * @param maxScale Maximum scale factor.
 * @param durationMs One breathing cycle duration.
 * @param contentDescription Accessibility label.
 */
@Composable
fun BreathingCircle(
    color: Color = TrustBlueLight,
    size: Dp = 80.dp,
    minScale: Float = 0.85f,
    maxScale: Float = 1.15f,
    durationMs: Int = 3000,
    contentDescription: String = "Breathing indicator"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_alpha"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
            .semantics {
                this.contentDescription = contentDescription
            }
    )
}

@Preview(showBackground = true, name = "BreathingCircle")
@Composable
private fun BreathingCirclePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            BreathingCircle()
        }
    }
}

// ---------------------------------------------------------------------------
// 9. AnimatedBadge
// ---------------------------------------------------------------------------

/**
 * A badge that animates in with a bounce effect, typically used to display
 * counts on icons (e.g., notification count, threat count).
 *
 * @param count The number to display inside the badge.
 * @param modifier Modifier for layout.
 * @param badgeColor Badge background color.
 * @param textColor Badge text color.
 * @param maxDisplayCount Maximum count to show before displaying "+".
 * @param contentDescription Accessibility description.
 */
@Composable
fun AnimatedBadge(
    count: Int,
    modifier: Modifier = Modifier,
    badgeColor: Color = AlertRed,
    textColor: Color = Color.White,
    maxDisplayCount: Int = 99,
    contentDescription: String = "$count notifications"
) {
    val visible = count > 0

    val scaleAnim by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "badge_scale"
    )

    if (scaleAnim > 0f) {
        Box(
            modifier = modifier
                .scale(scaleAnim)
                .clip(CircleShape)
                .background(badgeColor)
                .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .semantics {
                    this.contentDescription = contentDescription
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > maxDisplayCount) "$maxDisplayCount+" else "$count",
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, name = "AnimatedBadge - Variations")
@Composable
private fun AnimatedBadgePreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box {
                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(32.dp))
                AnimatedBadge(count = 3, modifier = Modifier.align(Alignment.TopEnd))
            }
            Box {
                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(32.dp))
                AnimatedBadge(count = 128, modifier = Modifier.align(Alignment.TopEnd))
            }
            Box {
                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(32.dp))
                AnimatedBadge(count = 0, modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 10. ScanLineEffect
// ---------------------------------------------------------------------------

/**
 * A horizontal scan-line that sweeps vertically across its parent, used to
 * indicate an active scanning operation.
 *
 * @param modifier Modifier for sizing.
 * @param lineColor Color of the scan line.
 * @param glowColor Color of the glow behind the line.
 * @param speed Duration of one sweep in milliseconds.
 * @param lineWidth Width of the scan line.
 * @param isScanning Whether the animation is active.
 * @param contentDescription Accessibility label.
 */
@Composable
fun ScanLineEffect(
    modifier: Modifier = Modifier,
    lineColor: Color = TrustBlue,
    glowColor: Color = TrustBlueLight.copy(alpha = 0.2f),
    speed: Int = 2000,
    lineWidth: Dp = 2.dp,
    isScanning: Boolean = true,
    contentDescription: String = if (isScanning) "Scanning in progress" else "Scanner idle"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_line")

    val scanPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(speed, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_position"
    )

    val density = LocalDensity.current
    val lineWidthPx = with(density) { lineWidth.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        if (isScanning) {
            val y = size.height * scanPosition
            val glowHeight = 20.dp.toPx()

            // Glow region
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, glowColor, Color.Transparent),
                    startY = y - glowHeight,
                    endY = y + glowHeight
                ),
                topLeft = Offset(0f, (y - glowHeight).coerceAtLeast(0f)),
                size = Size(
                    size.width,
                    (glowHeight * 2).coerceAtMost(size.height - (y - glowHeight).coerceAtLeast(0f))
                )
            )

            // Scan line
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = lineWidthPx,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true, name = "ScanLineEffect")
@Composable
private fun ScanLineEffectPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
                .background(BackgroundLight, RoundedCornerShape(12.dp))
        ) {
            ScanLineEffect(modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Scanning clipboard...", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 11. AnimatedCheckmark
// ---------------------------------------------------------------------------

/**
 * An animated checkmark that draws itself on a Canvas with a satisfying
 * stroke animation, typically used after successful operations.
 *
 * @param isVisible Whether the checkmark is shown/animating.
 * @param modifier Modifier for sizing.
 * @param color Color of the checkmark stroke.
 * @param circleColor Color of the background circle.
 * @param size Size of the composable.
 * @param strokeWidth Width of the checkmark stroke.
 * @param animationDurationMs Duration of the draw animation.
 * @param contentDescription Accessibility label.
 */
@Composable
fun AnimatedCheckmark(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = SuccessGreen,
    circleColor: Color = SuccessGreen.copy(alpha = 0.1f),
    size: Dp = 64.dp,
    strokeWidth: Dp = 4.dp,
    animationDurationMs: Int = 600,
    contentDescription: String = "Success"
) {
    val progress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = EaseOutBack
        ),
        label = "checkmark_progress"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmark_scale"
    )

    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }

    Canvas(
        modifier = modifier
            .size(size)
            .scale(scaleAnim)
            .semantics {
                this.contentDescription = contentDescription
                if (isVisible) {
                    stateDescription = "Completed"
                }
            }
    ) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val radius = minOf(cx, cy) - strokePx

        // Background circle
        drawCircle(
            color = circleColor,
            radius = radius * progress,
            center = Offset(cx, cy)
        )

        // Circle outline
        drawCircle(
            color = color,
            radius = radius * progress,
            center = Offset(cx, cy),
            style = Stroke(width = strokePx)
        )

        // Checkmark path with progressive reveal
        if (progress > 0.3f) {
            val checkProgress = ((progress - 0.3f) / 0.7f).coerceIn(0f, 1f)
            val checkStartX = cx - radius * 0.3f
            val checkStartY = cy + radius * 0.05f
            val checkMidX = cx - radius * 0.05f
            val checkMidY = cy + radius * 0.3f
            val checkEndX = cx + radius * 0.35f
            val checkEndY = cy - radius * 0.25f

            val path = Path()
            path.moveTo(checkStartX, checkStartY)

            if (checkProgress <= 0.5f) {
                val t = checkProgress / 0.5f
                path.lineTo(
                    checkStartX + (checkMidX - checkStartX) * t,
                    checkStartY + (checkMidY - checkStartY) * t
                )
            } else {
                path.lineTo(checkMidX, checkMidY)
                val t = (checkProgress - 0.5f) / 0.5f
                path.lineTo(
                    checkMidX + (checkEndX - checkMidX) * t,
                    checkMidY + (checkEndY - checkMidY) * t
                )
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "AnimatedCheckmark - Visible")
@Composable
private fun AnimatedCheckmarkVisiblePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            AnimatedCheckmark(isVisible = true)
        }
    }
}

@Preview(showBackground = true, name = "AnimatedCheckmark - Hidden")
@Composable
private fun AnimatedCheckmarkHiddenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            AnimatedCheckmark(isVisible = false)
        }
    }
}

// ---------------------------------------------------------------------------
// 12. SpinnerIndicator
// ---------------------------------------------------------------------------

/**
 * Custom spinning loading indicator drawn on Canvas with segmented arcs.
 *
 * @param modifier Modifier for sizing.
 * @param color Primary spinner color.
 * @param size Diameter of the spinner.
 * @param strokeWidth Stroke width of the arcs.
 * @param segmentCount Number of arc segments.
 * @param contentDescription Accessibility label.
 */
@Composable
fun SpinnerIndicator(
    modifier: Modifier = Modifier,
    color: Color = TrustBlue,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    segmentCount: Int = 8,
    contentDescription: String = "Loading"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_rotation"
    )

    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }

    Canvas(
        modifier = modifier
            .size(size)
            .rotate(rotation)
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = "Loading"
            }
    ) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val radius = minOf(cx, cy) - strokePx

        val arcAngle = 360f / segmentCount
        val gapAngle = arcAngle * 0.3f
        val sweepAngle = arcAngle - gapAngle

        for (i in 0 until segmentCount) {
            val startAngle = i * arcAngle
            val alpha = (i + 1).toFloat() / segmentCount.toFloat()

            drawArc(
                color = color.copy(alpha = alpha),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}

@Preview(showBackground = true, name = "SpinnerIndicator")
@Composable
private fun SpinnerIndicatorPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpinnerIndicator(size = 32.dp)
            SpinnerIndicator(size = 48.dp, color = AlertRed)
            SpinnerIndicator(size = 64.dp, color = SuccessGreen, segmentCount = 12)
        }
    }
}

// ---------------------------------------------------------------------------
// 13. RadarSweep
// ---------------------------------------------------------------------------

/**
 * Radar-style sweep animation drawn on Canvas, used to indicate active
 * threat scanning.
 *
 * @param modifier Modifier for sizing.
 * @param sweepColor Color of the radar sweep.
 * @param gridColor Color of the concentric grid circles.
 * @param dotColor Color of detected dots.
 * @param size Size of the radar canvas.
 * @param isActive Whether the radar is sweeping.
 * @param detectedDots List of normalized (0..1, 0..1) dot positions.
 * @param contentDescription Accessibility label.
 */
@Composable
fun RadarSweep(
    modifier: Modifier = Modifier,
    sweepColor: Color = TrustBlue,
    gridColor: Color = TrustBlueLight.copy(alpha = 0.15f),
    dotColor: Color = AlertRed,
    size: Dp = 200.dp,
    isActive: Boolean = true,
    detectedDots: List<Pair<Float, Float>> = emptyList(),
    contentDescription: String = "Radar sweep"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val maxRadius = minOf(cx, cy) * 0.9f

        // Grid circles
        for (i in 1..4) {
            drawCircle(
                color = gridColor,
                radius = maxRadius * (i / 4f),
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Cross hair lines
        drawLine(
            color = gridColor,
            start = Offset(cx - maxRadius, cy),
            end = Offset(cx + maxRadius, cy),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = gridColor,
            start = Offset(cx, cy - maxRadius),
            end = Offset(cx, cy + maxRadius),
            strokeWidth = 1.dp.toPx()
        )

        // Sweep arc
        if (isActive) {
            val sweepArcAngle = 45f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        sweepColor.copy(alpha = 0.3f),
                        sweepColor.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                startAngle = sweepAngle - sweepArcAngle,
                sweepAngle = sweepArcAngle,
                useCenter = true,
                topLeft = Offset(cx - maxRadius, cy - maxRadius),
                size = Size(maxRadius * 2, maxRadius * 2)
            )

            // Sweep line
            val rad = Math.toRadians(sweepAngle.toDouble())
            val endX = cx + maxRadius * cos(rad).toFloat()
            val endY = cy + maxRadius * sin(rad).toFloat()
            drawLine(
                color = sweepColor,
                start = Offset(cx, cy),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Detected dots
        for ((nx, ny) in detectedDots) {
            val dotX = cx + (nx - 0.5f) * maxRadius * 2f
            val dotY = cy + (ny - 0.5f) * maxRadius * 2f
            drawCircle(
                color = dotColor,
                radius = 4.dp.toPx(),
                center = Offset(dotX, dotY)
            )
            drawCircle(
                color = dotColor.copy(alpha = 0.3f),
                radius = 8.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }

        // Center dot
        drawCircle(
            color = sweepColor,
            radius = 3.dp.toPx(),
            center = Offset(cx, cy)
        )
    }
}

@Preview(showBackground = true, name = "RadarSweep")
@Composable
private fun RadarSweepPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            RadarSweep(
                detectedDots = listOf(
                    0.3f to 0.4f,
                    0.7f to 0.3f,
                    0.6f to 0.7f
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 14. AnimatedGradientBorder
// ---------------------------------------------------------------------------

/**
 * A card with an animated gradient border that rotates around the edges.
 *
 * @param modifier Modifier for sizing.
 * @param borderWidth Width of the gradient border.
 * @param gradientColors Colors for the rotating gradient.
 * @param cornerRadius Corner radius of the card.
 * @param durationMs Duration of one full rotation.
 * @param content Content inside the bordered card.
 */
@Composable
fun AnimatedGradientBorder(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    gradientColors: List<Color> = listOf(TrustBlue, TrustBlueLight, ProtectionActive, TrustBlue),
    cornerRadius: Dp = 16.dp,
    durationMs: Int = 3000,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_border")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_angle"
    )

    val density = LocalDensity.current
    val borderPx = with(density) { borderWidth.toPx() }
    val cornerPx = with(density) { cornerRadius.toPx() }

    Box(modifier = modifier) {
        // Border layer
        Canvas(modifier = Modifier.matchParentSize()) {
            rotate(angle) {
                drawRoundRect(
                    brush = Brush.sweepGradient(gradientColors),
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    size = size,
                    style = Stroke(width = borderPx)
                )
            }
        }

        // Content layer with inner padding
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(borderWidth)
                .clip(RoundedCornerShape(cornerRadius - borderWidth)),
            content = content
        )
    }
}

@Preview(showBackground = true, name = "AnimatedGradientBorder")
@Composable
private fun AnimatedGradientBorderPreview() {
    MaterialTheme {
        AnimatedGradientBorder(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Protected Content",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 15. CountdownTimer
// ---------------------------------------------------------------------------

/**
 * Circular countdown timer that draws an arc depleting over time.
 *
 * @param totalSeconds Total seconds for the countdown.
 * @param remainingSeconds Current remaining seconds.
 * @param modifier Modifier for sizing.
 * @param size Diameter of the timer.
 * @param activeColor Color of the remaining arc.
 * @param trackColor Color of the depleted arc.
 * @param strokeWidth Width of the arc stroke.
 * @param contentDescription Accessibility label.
 * @param content Center content (typically the time display).
 */
@Composable
fun CountdownTimer(
    totalSeconds: Int,
    remainingSeconds: Int,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    activeColor: Color = TrustBlue,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp = 6.dp,
    contentDescription: String = "$remainingSeconds seconds remaining",
    content: @Composable BoxScope.() -> Unit = {
        Text(
            text = "$remainingSeconds",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = activeColor
        )
    }
) {
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300, easing = LinearEasing),
        label = "countdown_progress"
    )

    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val arcSize = Size(
                this.size.width - strokePx,
                this.size.height - strokePx
            )
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active arc
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        content()
    }
}

@Preview(showBackground = true, name = "CountdownTimer")
@Composable
private fun CountdownTimerPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CountdownTimer(totalSeconds = 60, remainingSeconds = 45)
            CountdownTimer(
                totalSeconds = 60,
                remainingSeconds = 15,
                activeColor = AlertOrange
            )
            CountdownTimer(
                totalSeconds = 60,
                remainingSeconds = 5,
                activeColor = AlertRed
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 16. FadeInColumn
// ---------------------------------------------------------------------------

/**
 * A column whose children fade and slide in sequentially.
 *
 * @param modifier Modifier for layout.
 * @param delayPerItemMs Stagger delay between each child animation.
 * @param durationMs Duration of each item's animation.
 * @param content Column content.
 */
@Composable
fun FadeInColumn(
    modifier: Modifier = Modifier,
    delayPerItemMs: Int = 100,
    durationMs: Int = 400,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(modifier = modifier) {
        content()
    }
}

/**
 * Wraps a single item with a staggered fade-in and slide-up animation.
 *
 * @param index The item index (used to calculate delay).
 * @param delayPerItemMs Stagger delay.
 * @param durationMs Animation duration.
 * @param content The content to animate.
 */
@Composable
fun FadeInItem(
    index: Int,
    delayPerItemMs: Int = 100,
    durationMs: Int = 400,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * delayPerItemMs).toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMs)
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(durationMs)
        )
    ) {
        content()
    }
}

@Preview(showBackground = true, name = "FadeInColumn")
@Composable
private fun FadeInColumnPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until 5) {
                FadeInItem(index = i) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Item $i",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 17. AnimatedExpandableCard
// ---------------------------------------------------------------------------

/**
 * A card that can expand/collapse its content with smooth animation.
 *
 * @param title The card header title.
 * @param subtitle Optional subtitle.
 * @param icon Optional leading icon.
 * @param modifier Modifier for layout.
 * @param isExpanded Whether the card is expanded.
 * @param onToggle Callback for expand/collapse toggle.
 * @param expandedContent Content shown when expanded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedExpandableCard(
    title: String,
    subtitle: String = "",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onToggle: () -> Unit = {},
    expandedContent: @Composable ColumnScope.() -> Unit = {}
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "expand_arrow_rotation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 0.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (subtitle.isNotEmpty()) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationAngle)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    content = expandedContent
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "AnimatedExpandableCard - Collapsed")
@Composable
private fun AnimatedExpandableCardCollapsedPreview() {
    MaterialTheme {
        AnimatedExpandableCard(
            title = "Advanced Settings",
            subtitle = "Tap to expand",
            icon = Icons.Default.Settings,
            modifier = Modifier.padding(16.dp),
            isExpanded = false
        )
    }
}

@Preview(showBackground = true, name = "AnimatedExpandableCard - Expanded")
@Composable
private fun AnimatedExpandableCardExpandedPreview() {
    MaterialTheme {
        AnimatedExpandableCard(
            title = "Advanced Settings",
            subtitle = "Configuration options",
            icon = Icons.Default.Settings,
            modifier = Modifier.padding(16.dp),
            isExpanded = true
        ) {
            Text("Setting 1: Enabled")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Setting 2: Auto")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Setting 3: 500ms")
        }
    }
}

// ---------------------------------------------------------------------------
// 18. StatusTransitionIcon
// ---------------------------------------------------------------------------

/**
 * An icon that cross-fades and scales between different states, used for
 * status transitions (e.g., scanning -> success -> error).
 *
 * @param currentIcon The currently displayed icon.
 * @param currentColor The current icon tint.
 * @param modifier Modifier for sizing.
 * @param size Icon size.
 * @param contentDescription Accessibility label.
 */
@Composable
fun StatusTransitionIcon(
    currentIcon: ImageVector,
    currentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    contentDescription: String = ""
) {
    val animatedColor by animateColorAsState(
        targetValue = currentColor,
        animationSpec = tween(400),
        label = "status_icon_color"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "status_icon_scale"
    )

    AnimatedContent(
        targetState = currentIcon,
        transitionSpec = {
            (fadeIn(tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300)
            )).togetherWith(
                fadeOut(tween(300)) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(300)
                )
            )
        },
        label = "status_icon_transition",
        modifier = modifier
    ) { icon ->
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = animatedColor,
            modifier = Modifier
                .size(size)
                .scale(scaleAnim)
        )
    }
}

@Preview(showBackground = true, name = "StatusTransitionIcon")
@Composable
private fun StatusTransitionIconPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusTransitionIcon(
                currentIcon = Icons.Default.Shield,
                currentColor = ProtectionActive,
                contentDescription = "Active"
            )
            StatusTransitionIcon(
                currentIcon = Icons.Default.Warning,
                currentColor = AlertOrange,
                contentDescription = "Warning"
            )
            StatusTransitionIcon(
                currentIcon = Icons.Default.Error,
                currentColor = AlertRed,
                contentDescription = "Error"
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 19. ParticleField
// ---------------------------------------------------------------------------

/**
 * Data class representing a single particle in the particle field.
 */
data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val speedX: Float,
    val speedY: Float
)

/**
 * A canvas-drawn particle field with floating dots, used for decorative
 * backgrounds or ambient status display.
 *
 * @param modifier Modifier for sizing.
 * @param particleColor Color of the particles.
 * @param particleCount Number of particles.
 * @param maxRadius Maximum particle radius.
 * @param contentDescription Accessibility label.
 */
@Composable
fun ParticleField(
    modifier: Modifier = Modifier,
    particleColor: Color = TrustBlueLight,
    particleCount: Int = 30,
    maxRadius: Float = 4f,
    contentDescription: String = "Ambient particle effect"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    val particles = remember {
        List(particleCount) {
            Particle(
                x = Math.random().toFloat(),
                y = Math.random().toFloat(),
                radius = (Math.random() * maxRadius + 1f).toFloat(),
                alpha = (Math.random() * 0.5f + 0.2f).toFloat(),
                speedX = (Math.random() * 0.02f - 0.01f).toFloat(),
                speedY = (Math.random() * 0.02f - 0.01f).toFloat()
            )
        }
    }

    Canvas(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        for (particle in particles) {
            val px = ((particle.x + particle.speedX * time) % 1f) * size.width
            val py = ((particle.y + particle.speedY * time) % 1f) * size.height

            drawCircle(
                color = particleColor.copy(alpha = particle.alpha),
                radius = particle.radius.dp.toPx(),
                center = Offset(
                    px.coerceIn(0f, size.width),
                    py.coerceIn(0f, size.height)
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "ParticleField")
@Composable
private fun ParticleFieldPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(BackgroundDark)
        ) {
            ParticleField(
                modifier = Modifier.fillMaxSize(),
                particleColor = TrustBlueLight
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 20. AnimatedVisibilityToggle
// ---------------------------------------------------------------------------

/**
 * A utility composable that wraps content in an AnimatedVisibility with
 * customizable enter/exit transitions.
 *
 * @param visible Whether the content is visible.
 * @param modifier Modifier for layout.
 * @param enterDurationMs Enter animation duration.
 * @param exitDurationMs Exit animation duration.
 * @param slideDirection Direction of the slide animation.
 * @param content Content to animate.
 */
@Composable
fun AnimatedVisibilityToggle(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enterDurationMs: Int = 300,
    exitDurationMs: Int = 200,
    slideDirection: SlideDirection = SlideDirection.DOWN,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val slideOffset: (Int) -> Int = when (slideDirection) {
        SlideDirection.UP -> { height -> -height / 3 }
        SlideDirection.DOWN -> { height -> height / 3 }
        SlideDirection.LEFT -> { width -> -width / 3 }
        SlideDirection.RIGHT -> { width -> width / 3 }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(enterDurationMs)) + slideInVertically(
            initialOffsetY = slideOffset,
            animationSpec = tween(enterDurationMs)
        ),
        exit = fadeOut(tween(exitDurationMs)) + slideOutVertically(
            targetOffsetY = slideOffset,
            animationSpec = tween(exitDurationMs)
        ),
        content = content
    )
}

/**
 * Slide direction for AnimatedVisibilityToggle.
 */
enum class SlideDirection {
    UP, DOWN, LEFT, RIGHT
}

@Preview(showBackground = true, name = "AnimatedVisibilityToggle")
@Composable
private fun AnimatedVisibilityTogglePreview() {
    MaterialTheme {
        var visible by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { visible = !visible }) {
                Text(if (visible) "Hide" else "Show")
            }
            AnimatedVisibilityToggle(visible = visible) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "This content animates in and out",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// All Animated Components Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "All Animated Components Gallery")
@Composable
private fun AnimatedComponentsGalleryPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Animated Components Gallery",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedShield(isActive = true, size = 60.dp)
                PulsingDot(color = ProtectionActive)
                AnimatedBadge(count = 5)
            }

            AnimatedProgressBar(
                progress = 0.65f,
                label = "Model Loading",
                showPercentage = true
            )

            AnimatedCounter(targetValue = 99, label = "Threats Detected")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AnimatedCheckmark(isVisible = true, size = 40.dp)
                SpinnerIndicator(size = 40.dp)
                BreathingCircle(size = 40.dp)
            }
        }
    }
}
