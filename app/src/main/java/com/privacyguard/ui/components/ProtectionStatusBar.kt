package com.privacyguard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.ml.ModelState
import com.privacyguard.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

// ---------------------------------------------------------------------------
// Protection state enumeration
// ---------------------------------------------------------------------------

/**
 * Represents the overall protection state of the app, determining the
 * visual appearance and behavior of the [ProtectionStatusBar].
 */
enum class ProtectionState {
    /** All systems operational and actively protecting. */
    PROTECTED,
    /** Actively scanning text for PII entities. */
    SCANNING,
    /** A PII detection alert is active. */
    ALERT,
    /** Monitoring is paused by the user. */
    PAUSED,
    /** An error has occurred in one or more subsystems. */
    ERROR
}

/**
 * Display mode for the status bar.
 */
enum class StatusBarMode {
    /** Full expanded mode for the dashboard header. */
    FULL,
    /** Compact mini mode for use as an app bar element. */
    MINI
}

/**
 * Data class representing the current state of all monitored services.
 */
data class ServiceStatusInfo(
    val isClipboardMonitorActive: Boolean = false,
    val isAccessibilityServiceActive: Boolean = false,
    val modelState: ModelState = ModelState.Initializing,
    val protectionScore: Int = 0,
    val detectionsToday: Int = 0,
    val textsScannedToday: Int = 0,
    val protectionState: ProtectionState = ProtectionState.PAUSED
)

/**
 * Callback interface for quick actions on the status bar.
 */
data class StatusBarActions(
    val onPauseResume: () -> Unit = {},
    val onScanNow: () -> Unit = {},
    val onClearClipboard: () -> Unit = {},
    val onExpandCollapse: () -> Unit = {}
)

// ---------------------------------------------------------------------------
// Color and icon mapping for protection states
// ---------------------------------------------------------------------------

/**
 * Returns the primary color associated with each protection state.
 */
@Composable
fun protectionStateColor(state: ProtectionState): Color {
    return when (state) {
        ProtectionState.PROTECTED -> SuccessGreen
        ProtectionState.SCANNING -> TrustBlue
        ProtectionState.ALERT -> AlertRed
        ProtectionState.PAUSED -> ProtectionInactive
        ProtectionState.ERROR -> AlertRed
    }
}

/**
 * Returns a pair of gradient colors for the protection state.
 */
fun protectionStateGradient(state: ProtectionState): List<Color> {
    return when (state) {
        ProtectionState.PROTECTED -> listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.7f))
        ProtectionState.SCANNING -> listOf(TrustBlue, TrustBlueLight)
        ProtectionState.ALERT -> listOf(CriticalGradientStart, CriticalGradientEnd)
        ProtectionState.PAUSED -> listOf(ProtectionInactive, ProtectionInactive.copy(alpha = 0.7f))
        ProtectionState.ERROR -> listOf(AlertRed, AlertRedDark)
    }
}

/**
 * Returns the shield icon variant for each protection state.
 */
fun protectionStateIcon(state: ProtectionState): ImageVector {
    return when (state) {
        ProtectionState.PROTECTED -> Icons.Filled.Shield
        ProtectionState.SCANNING -> Icons.Filled.Security
        ProtectionState.ALERT -> Icons.Filled.GppBad
        ProtectionState.PAUSED -> Icons.Filled.PauseCircle
        ProtectionState.ERROR -> Icons.Filled.Error
    }
}

/**
 * Returns the status label text for each protection state.
 */
fun protectionStateLabel(state: ProtectionState): String {
    return when (state) {
        ProtectionState.PROTECTED -> "Protected"
        ProtectionState.SCANNING -> "Scanning..."
        ProtectionState.ALERT -> "Alert Active"
        ProtectionState.PAUSED -> "Paused"
        ProtectionState.ERROR -> "Error"
    }
}

// ---------------------------------------------------------------------------
// ProtectionStatusBar - Full mode
// ---------------------------------------------------------------------------

/**
 * A comprehensive status bar/header component that displays the current
 * protection status of the PrivacyGuard app. Features include:
 *
 * - Animated shield icon that changes based on protection level
 * - Protection score ring (0-100) with gradient color progression
 * - Pulsing status text when actively scanning
 * - Service status indicators for clipboard, accessibility, and model state
 * - Quick action buttons for pause/resume, scan now, and clear clipboard
 * - Animated transitions between all protection states
 * - Supports full mode (dashboard header) and mini mode (app bar)
 * - Real-time stats ticker for detections today and texts scanned
 *
 * @param serviceStatus Current service status information.
 * @param actions Callback handlers for quick action buttons.
 * @param mode Display mode (FULL or MINI).
 * @param modifier Modifier for the status bar container.
 */
@Composable
fun ProtectionStatusBar(
    serviceStatus: ServiceStatusInfo,
    actions: StatusBarActions = StatusBarActions(),
    mode: StatusBarMode = StatusBarMode.FULL,
    modifier: Modifier = Modifier
) {
    when (mode) {
        StatusBarMode.FULL -> ProtectionStatusBarFull(
            serviceStatus = serviceStatus,
            actions = actions,
            modifier = modifier
        )
        StatusBarMode.MINI -> ProtectionStatusBarMini(
            serviceStatus = serviceStatus,
            actions = actions,
            modifier = modifier
        )
    }
}

// ---------------------------------------------------------------------------
// Full mode implementation
// ---------------------------------------------------------------------------

@Composable
private fun ProtectionStatusBarFull(
    serviceStatus: ServiceStatusInfo,
    actions: StatusBarActions,
    modifier: Modifier = Modifier
) {
    val state = serviceStatus.protectionState
    val stateColor = protectionStateColor(state)

    // Background color transition
    val containerColor by animateColorAsState(
        targetValue = stateColor.copy(alpha = 0.08f),
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "container_color"
    )

    // Border color transition
    val borderColor by animateColorAsState(
        targetValue = stateColor.copy(alpha = 0.3f),
        animationSpec = tween(600),
        label = "border_color"
    )

    val semanticDescription = buildString {
        append("Protection status: ${protectionStateLabel(state)}. ")
        append("Protection score: ${serviceStatus.protectionScore} out of 100. ")
        append("Detections today: ${serviceStatus.detectionsToday}. ")
        append("Texts scanned: ${serviceStatus.textsScannedToday}. ")
        if (!serviceStatus.isClipboardMonitorActive) append("Clipboard monitor inactive. ")
        if (!serviceStatus.isAccessibilityServiceActive) append("Accessibility service inactive. ")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = semanticDescription
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top row: animated shield + score ring
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Shield icon with animation
                AnimatedShieldIcon(
                    state = state,
                    size = 64.dp
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Protection score ring
                ProtectionScoreRing(
                    score = serviceStatus.protectionScore,
                    state = state,
                    size = 100.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status text with pulse animation
            AnimatedStatusText(state = state)

            Spacer(modifier = Modifier.height(16.dp))

            // Service status indicators
            ServiceStatusIndicators(serviceStatus = serviceStatus)

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time stats ticker
            RealTimeStatsTicker(
                detectionsToday = serviceStatus.detectionsToday,
                textsScannedToday = serviceStatus.textsScannedToday
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick action buttons
            QuickActionButtonsRow(
                state = state,
                actions = actions
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Mini mode implementation
// ---------------------------------------------------------------------------

@Composable
private fun ProtectionStatusBarMini(
    serviceStatus: ServiceStatusInfo,
    actions: StatusBarActions,
    modifier: Modifier = Modifier
) {
    val state = serviceStatus.protectionState
    val stateColor = protectionStateColor(state)

    val containerColor by animateColorAsState(
        targetValue = stateColor.copy(alpha = 0.1f),
        animationSpec = tween(400),
        label = "mini_container_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Protection: ${protectionStateLabel(state)}, Score: ${serviceStatus.protectionScore}"
            },
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini shield icon
            AnimatedShieldIcon(
                state = state,
                size = 28.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Status text and score
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = protectionStateLabel(state),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = stateColor
                )
                Text(
                    text = "Score: ${serviceStatus.protectionScore}/100 | ${serviceStatus.detectionsToday} detections today",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Mini service dots
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ServiceDot(
                    isActive = serviceStatus.isClipboardMonitorActive,
                    label = "Clipboard"
                )
                ServiceDot(
                    isActive = serviceStatus.isAccessibilityServiceActive,
                    label = "Accessibility"
                )
                ServiceDot(
                    isActive = serviceStatus.modelState is ModelState.Ready ||
                            serviceStatus.modelState is ModelState.Running,
                    label = "Model"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Pause/Resume icon button
            IconButton(
                onClick = actions.onPauseResume,
                modifier = Modifier
                    .size(32.dp)
                    .semantics {
                        contentDescription = if (state == ProtectionState.PAUSED) "Resume protection" else "Pause protection"
                        role = Role.Button
                    }
            ) {
                Icon(
                    imageVector = if (state == ProtectionState.PAUSED) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = stateColor
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Animated shield icon
// ---------------------------------------------------------------------------

/**
 * An animated shield icon that scales, rotates, and changes based on
 * the current protection state. Includes a pulse effect when protected
 * and a shake effect when in alert state.
 */
@Composable
fun AnimatedShieldIcon(
    state: ProtectionState,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val stateColor = protectionStateColor(state)

    // Pulse animation for protected state
    val pulseScale by rememberInfiniteTransition(label = "shield_pulse").animateFloat(
        initialValue = 1f,
        targetValue = when (state) {
            ProtectionState.PROTECTED -> 1.08f
            ProtectionState.SCANNING -> 1.05f
            ProtectionState.ALERT -> 1.12f
            else -> 1f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    ProtectionState.ALERT -> 600
                    ProtectionState.SCANNING -> 1000
                    else -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse_scale"
    )

    // Rotation for scanning state
    val rotationAngle by rememberInfiniteTransition(label = "shield_rotate").animateFloat(
        initialValue = 0f,
        targetValue = if (state == ProtectionState.SCANNING) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == ProtectionState.SCANNING) 3000 else 1,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shield_rotation"
    )

    // Animated icon transition
    val icon = protectionStateIcon(state)

    // Color animation for the icon
    val iconColor by animateColorAsState(
        targetValue = stateColor,
        animationSpec = tween(500),
        label = "shield_icon_color"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .scale(pulseScale)
            .semantics {
                contentDescription = "Shield icon: ${protectionStateLabel(state)}"
            }
    ) {
        // Glow circle behind the shield
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        stateColor.copy(alpha = 0.2f),
                        stateColor.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                ),
                radius = this.size.minDimension / 2
            )
        }

        // Shield icon with conditional rotation
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(size * 0.6f)
                .then(
                    if (state == ProtectionState.SCANNING)
                        Modifier.rotate(rotationAngle)
                    else Modifier
                ),
            tint = iconColor
        )
    }
}

// ---------------------------------------------------------------------------
// Protection score ring
// ---------------------------------------------------------------------------

/**
 * A circular ring showing the protection score from 0-100 with animated
 * fill and gradient color that transitions from red (0) through yellow (50)
 * to green (100).
 */
@Composable
fun ProtectionScoreRing(
    score: Int,
    state: ProtectionState,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    // Animated score value
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "score_ring_animation"
    )

    // Color based on score
    val scoreColor by animateColorAsState(
        targetValue = when {
            score >= 80 -> SuccessGreen
            score >= 60 -> AlertYellow
            score >= 40 -> AlertOrange
            else -> AlertRed
        },
        animationSpec = tween(600),
        label = "score_ring_color"
    )

    val backgroundTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "Protection score: $score out of 100"
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasStrokeWidth = strokeWidth.toPx()
            val arcSize = Size(
                this.size.width - canvasStrokeWidth,
                this.size.height - canvasStrokeWidth
            )
            val topLeft = Offset(canvasStrokeWidth / 2f, canvasStrokeWidth / 2f)

            // Background track (full circle)
            drawArc(
                color = backgroundTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = canvasStrokeWidth, cap = StrokeCap.Round)
            )

            // Score arc
            val sweepAngle = (animatedScore / 100f) * 360f
            drawArc(
                color = scoreColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = canvasStrokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${animatedScore.toInt()}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            Text(
                text = "/ 100",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Animated status text
// ---------------------------------------------------------------------------

/**
 * Status text that pulses when the system is actively scanning.
 * Includes a subtitle describing what is happening.
 */
@Composable
fun AnimatedStatusText(
    state: ProtectionState,
    modifier: Modifier = Modifier
) {
    val stateColor = protectionStateColor(state)

    // Pulse alpha for scanning state
    val pulseAlpha by rememberInfiniteTransition(label = "text_pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (state == ProtectionState.SCANNING) 0.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_pulse_alpha"
    )

    // Crossfade between status labels
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(tween(300)) + scaleIn(initialScale = 0.9f) togetherWith
                        fadeOut(tween(200)) + scaleOut(targetScale = 0.9f)
            },
            label = "status_text_transition"
        ) { targetState ->
            Text(
                text = protectionStateLabel(targetState),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = protectionStateColor(targetState).copy(
                    alpha = if (targetState == ProtectionState.SCANNING) pulseAlpha else 1f
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = "Status: ${protectionStateLabel(targetState)}"
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = when (state) {
                ProtectionState.PROTECTED -> "All systems operational. Your data is secure."
                ProtectionState.SCANNING -> "Analyzing text for PII entities..."
                ProtectionState.ALERT -> "Sensitive data detected! Review required."
                ProtectionState.PAUSED -> "Monitoring is paused. Tap to resume."
                ProtectionState.ERROR -> "A service has encountered an error."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

// ---------------------------------------------------------------------------
// Service status indicators
// ---------------------------------------------------------------------------

/**
 * A row of service status indicators showing the state of clipboard
 * monitoring, accessibility service, and the ML model.
 */
@Composable
fun ServiceStatusIndicators(
    serviceStatus: ServiceStatusInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append("Service status: ")
                    append("Clipboard monitor ${if (serviceStatus.isClipboardMonitorActive) "active" else "inactive"}. ")
                    append("Accessibility service ${if (serviceStatus.isAccessibilityServiceActive) "active" else "inactive"}. ")
                    append("Model ${modelStateLabel(serviceStatus.modelState)}. ")
                }
            },
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ServiceStatusChip(
            label = "Clipboard",
            icon = Icons.Filled.ContentPaste,
            isActive = serviceStatus.isClipboardMonitorActive
        )

        ServiceStatusChip(
            label = "Accessibility",
            icon = Icons.Filled.Accessibility,
            isActive = serviceStatus.isAccessibilityServiceActive
        )

        ServiceStatusChip(
            label = "Model",
            icon = Icons.Filled.Psychology,
            isActive = serviceStatus.modelState is ModelState.Ready ||
                    serviceStatus.modelState is ModelState.Running,
            statusLabel = modelStateLabel(serviceStatus.modelState)
        )
    }
}

/**
 * A single service status chip with an icon, label, and active/inactive indicator.
 */
@Composable
private fun ServiceStatusChip(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    statusLabel: String? = null,
    modifier: Modifier = Modifier
) {
    val chipColor by animateColorAsState(
        targetValue = if (isActive) SuccessGreen else ProtectionInactive,
        animationSpec = tween(400),
        label = "chip_color_$label"
    )

    Surface(
        modifier = modifier.semantics {
            contentDescription = "$label: ${statusLabel ?: if (isActive) "Active" else "Inactive"}"
        },
        shape = RoundedCornerShape(8.dp),
        color = chipColor.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(chipColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = chipColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = chipColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

/**
 * Small status dot indicator for the mini mode.
 */
@Composable
private fun ServiceDot(
    isActive: Boolean,
    label: String,
    modifier: Modifier = Modifier
) {
    val dotColor by animateColorAsState(
        targetValue = if (isActive) SuccessGreen else ProtectionInactive,
        animationSpec = tween(300),
        label = "dot_color_$label"
    )

    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(dotColor)
            .semantics {
                contentDescription = "$label: ${if (isActive) "Active" else "Inactive"}"
            }
    )
}

/**
 * Returns a human-readable label for the model state.
 */
private fun modelStateLabel(state: ModelState): String {
    return when (state) {
        is ModelState.Initializing -> "Initializing"
        is ModelState.Ready -> "Ready"
        is ModelState.Running -> "Running"
        is ModelState.Error -> "Error"
        is ModelState.Closed -> "Closed"
    }
}

// ---------------------------------------------------------------------------
// Real-time stats ticker
// ---------------------------------------------------------------------------

/**
 * A horizontal row of real-time statistics with animated counters,
 * showing detections today and texts scanned today.
 */
@Composable
fun RealTimeStatsTicker(
    detectionsToday: Int,
    textsScannedToday: Int,
    modifier: Modifier = Modifier
) {
    val animatedDetections by animateIntAsState(
        targetValue = detectionsToday,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "detection_counter"
    )

    val animatedScanned by animateIntAsState(
        targetValue = textsScannedToday,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "scanned_counter"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$detectionsToday detections today, $textsScannedToday texts scanned today"
            },
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatTickerItem(
            label = "Detections Today",
            value = animatedDetections.toString(),
            icon = Icons.Filled.Warning,
            tintColor = if (detectionsToday > 0) AlertOrange else SuccessGreen
        )

        // Vertical divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        StatTickerItem(
            label = "Texts Scanned",
            value = animatedScanned.toString(),
            icon = Icons.Filled.TextSnippet,
            tintColor = TrustBlue
        )
    }
}

/**
 * A single stat item within the [RealTimeStatsTicker].
 */
@Composable
private fun StatTickerItem(
    label: String,
    value: String,
    icon: ImageVector,
    tintColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics {
            contentDescription = "$label: $value"
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tintColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Quick action buttons row
// ---------------------------------------------------------------------------

/**
 * A row of quick action buttons: Pause/Resume, Scan Now, Clear Clipboard.
 * Button states change based on the current protection state.
 */
@Composable
fun QuickActionButtonsRow(
    state: ProtectionState,
    actions: StatusBarActions,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Quick actions" },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pause/Resume button
        val isPaused = state == ProtectionState.PAUSED

        FilledTonalButton(
            onClick = actions.onPauseResume,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = if (isPaused) "Resume protection" else "Pause protection"
                    role = Role.Button
                },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = if (isPaused) SuccessGreen.copy(alpha = 0.15f)
                else AlertOrange.copy(alpha = 0.15f)
            )
        ) {
            Icon(
                imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isPaused) SuccessGreen else AlertOrange
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isPaused) "Resume" else "Pause",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = if (isPaused) SuccessGreen else AlertOrange
            )
        }

        // Scan Now button
        FilledTonalButton(
            onClick = actions.onScanNow,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Scan clipboard now"
                    role = Role.Button
                },
            shape = RoundedCornerShape(10.dp),
            enabled = state != ProtectionState.SCANNING && state != ProtectionState.PAUSED,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = TrustBlue.copy(alpha = 0.15f)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TrustBlue
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Scan Now",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = TrustBlue
            )
        }

        // Clear Clipboard button
        FilledTonalButton(
            onClick = actions.onClearClipboard,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Clear clipboard contents"
                    role = Role.Button
                },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = AlertRed.copy(alpha = 0.1f)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.CleaningServices,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = AlertRed
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Clear",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = AlertRed
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Animated transition wrapper
// ---------------------------------------------------------------------------

/**
 * Wraps [ProtectionStatusBar] with an animated visibility transition
 * for use in screens where the status bar appears conditionally.
 */
@Composable
fun AnimatedProtectionStatusBar(
    visible: Boolean,
    serviceStatus: ServiceStatusInfo,
    actions: StatusBarActions = StatusBarActions(),
    mode: StatusBarMode = StatusBarMode.FULL,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(tween(300)) + shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    ) {
        ProtectionStatusBar(
            serviceStatus = serviceStatus,
            actions = actions,
            mode = mode,
            modifier = modifier
        )
    }
}

// ---------------------------------------------------------------------------
// Protection state transition card
// ---------------------------------------------------------------------------

/**
 * A card that displays a state transition message when the protection
 * state changes. Shows a brief notification-style card.
 */
@Composable
fun ProtectionStateTransitionCard(
    previousState: ProtectionState,
    currentState: ProtectionState,
    modifier: Modifier = Modifier
) {
    val show = previousState != currentState

    AnimatedVisibility(
        visible = show,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(400)
        ),
        exit = fadeOut(tween(300)) + slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(400)
        )
    ) {
        val stateColor = protectionStateColor(currentState)
        val transitionMessage = when (currentState) {
            ProtectionState.PROTECTED -> "Protection resumed successfully"
            ProtectionState.SCANNING -> "Scan initiated"
            ProtectionState.ALERT -> "PII detected - review required"
            ProtectionState.PAUSED -> "Protection paused"
            ProtectionState.ERROR -> "An error occurred"
        }

        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = stateColor.copy(alpha = 0.12f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = protectionStateIcon(currentState),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = stateColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = transitionMessage,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = stateColor
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Score ring with gradient (standalone)
// ---------------------------------------------------------------------------

/**
 * A standalone gradient score ring that transitions through red, yellow,
 * and green based on the score value. Suitable for inline usage.
 */
@Composable
fun GradientScoreRing(
    score: Int,
    size: Dp = 48.dp,
    strokeWidth: Dp = 5.dp,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "gradient_score"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "Score: $score"
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasStrokeWidth = strokeWidth.toPx()
            val arcSize = Size(
                this.size.width - canvasStrokeWidth,
                this.size.height - canvasStrokeWidth
            )
            val topLeft = Offset(canvasStrokeWidth / 2f, canvasStrokeWidth / 2f)

            // Background track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = canvasStrokeWidth, cap = StrokeCap.Round)
            )

            // Gradient arc using segments
            val totalSweep = (animatedScore / 100f) * 360f
            val segments = 60
            val segmentAngle = totalSweep / segments

            for (i in 0 until segments) {
                val progress = i.toFloat() / segments
                val segmentColor = when {
                    progress < 0.4f -> lerpColor(AlertRed, AlertYellow, progress / 0.4f)
                    progress < 0.7f -> lerpColor(AlertYellow, SuccessGreen, (progress - 0.4f) / 0.3f)
                    else -> SuccessGreen
                }

                drawArc(
                    color = segmentColor,
                    startAngle = -90f + (i * segmentAngle),
                    sweepAngle = segmentAngle + 1f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = canvasStrokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Text(
            text = "${animatedScore.toInt()}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Linearly interpolates between two colors.
 */
private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * f,
        green = start.green + (end.green - start.green) * f,
        blue = start.blue + (end.blue - start.blue) * f,
        alpha = start.alpha + (end.alpha - start.alpha) * f
    )
}

// ---------------------------------------------------------------------------
// Preview composables
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Status Bar - Protected (Full)")
@Composable
private fun ProtectionStatusBarProtectedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = true,
                modelState = ModelState.Ready,
                protectionScore = 92,
                detectionsToday = 3,
                textsScannedToday = 147,
                protectionState = ProtectionState.PROTECTED
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Scanning (Full)")
@Composable
private fun ProtectionStatusBarScanningPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = true,
                modelState = ModelState.Running,
                protectionScore = 85,
                detectionsToday = 5,
                textsScannedToday = 200,
                protectionState = ProtectionState.SCANNING
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Alert (Full)")
@Composable
private fun ProtectionStatusBarAlertPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = true,
                modelState = ModelState.Ready,
                protectionScore = 42,
                detectionsToday = 12,
                textsScannedToday = 89,
                protectionState = ProtectionState.ALERT
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Paused (Full)")
@Composable
private fun ProtectionStatusBarPausedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = false,
                isAccessibilityServiceActive = false,
                modelState = ModelState.Ready,
                protectionScore = 0,
                detectionsToday = 0,
                textsScannedToday = 0,
                protectionState = ProtectionState.PAUSED
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Error (Full)")
@Composable
private fun ProtectionStatusBarErrorPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = false,
                modelState = ModelState.Error("Model failed to load"),
                protectionScore = 25,
                detectionsToday = 1,
                textsScannedToday = 50,
                protectionState = ProtectionState.ERROR
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Mini Protected")
@Composable
private fun ProtectionStatusBarMiniProtectedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = true,
                modelState = ModelState.Ready,
                protectionScore = 92,
                detectionsToday = 3,
                textsScannedToday = 147,
                protectionState = ProtectionState.PROTECTED
            ),
            mode = StatusBarMode.MINI,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Mini Alert")
@Composable
private fun ProtectionStatusBarMiniAlertPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = true,
                modelState = ModelState.Ready,
                protectionScore = 42,
                detectionsToday = 12,
                textsScannedToday = 89,
                protectionState = ProtectionState.ALERT
            ),
            mode = StatusBarMode.MINI,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Status Bar - Mini Paused")
@Composable
private fun ProtectionStatusBarMiniPausedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ProtectionStatusBar(
            serviceStatus = ServiceStatusInfo(
                protectionState = ProtectionState.PAUSED
            ),
            mode = StatusBarMode.MINI,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Score Ring Standalone")
@Composable
private fun ProtectionScoreRingPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProtectionScoreRing(score = 95, state = ProtectionState.PROTECTED, size = 80.dp)
            ProtectionScoreRing(score = 60, state = ProtectionState.SCANNING, size = 80.dp)
            ProtectionScoreRing(score = 25, state = ProtectionState.ERROR, size = 80.dp)
        }
    }
}

@Preview(showBackground = true, name = "Gradient Score Rings")
@Composable
private fun GradientScoreRingPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientScoreRing(score = 100, size = 56.dp)
            GradientScoreRing(score = 75, size = 56.dp)
            GradientScoreRing(score = 50, size = 56.dp)
            GradientScoreRing(score = 25, size = 56.dp)
            GradientScoreRing(score = 0, size = 56.dp)
        }
    }
}

@Preview(showBackground = true, name = "Service Status Indicators")
@Composable
private fun ServiceStatusIndicatorsPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        ServiceStatusIndicators(
            serviceStatus = ServiceStatusInfo(
                isClipboardMonitorActive = true,
                isAccessibilityServiceActive = false,
                modelState = ModelState.Ready
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "State Transition Card")
@Composable
private fun ProtectionStateTransitionCardPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProtectionStateTransitionCard(
                previousState = ProtectionState.PAUSED,
                currentState = ProtectionState.PROTECTED
            )
            ProtectionStateTransitionCard(
                previousState = ProtectionState.PROTECTED,
                currentState = ProtectionState.ALERT
            )
            ProtectionStateTransitionCard(
                previousState = ProtectionState.PROTECTED,
                currentState = ProtectionState.ERROR
            )
        }
    }
}

@Preview(showBackground = true, name = "Real-Time Stats Ticker")
@Composable
private fun RealTimeStatsTickerPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        RealTimeStatsTicker(
            detectionsToday = 7,
            textsScannedToday = 234,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Quick Action Buttons - Protected")
@Composable
private fun QuickActionButtonsProtectedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        QuickActionButtonsRow(
            state = ProtectionState.PROTECTED,
            actions = StatusBarActions(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Quick Action Buttons - Paused")
@Composable
private fun QuickActionButtonsPausedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        QuickActionButtonsRow(
            state = ProtectionState.PAUSED,
            actions = StatusBarActions(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Animated Shield Icons")
@Composable
private fun AnimatedShieldIconsPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedShieldIcon(state = ProtectionState.PROTECTED, size = 48.dp)
            AnimatedShieldIcon(state = ProtectionState.SCANNING, size = 48.dp)
            AnimatedShieldIcon(state = ProtectionState.ALERT, size = 48.dp)
            AnimatedShieldIcon(state = ProtectionState.PAUSED, size = 48.dp)
            AnimatedShieldIcon(state = ProtectionState.ERROR, size = 48.dp)
        }
    }
}
