package com.privacyguard.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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

/**
 * Main dashboard screen showing the current protection status, detection
 * statistics, model state, permission prompts, and recent detection events.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToWhitelist: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onToggleMonitoring: (Boolean) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showModelDetails by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("PrivacyGuard")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.semantics {
                            contentDescription = "View detection history"
                        }
                    ) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.semantics {
                            contentDescription = "Open settings"
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                DashboardLoadingState(modifier = Modifier.padding(padding))
            }
            uiState.errorMessage != null -> {
                DashboardErrorState(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.retryInitialization() },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Protection Score Card with animated arc
                    item(key = "protection_score") {
                        ProtectionScoreCard(
                            score = uiState.protectionScore,
                            isActive = uiState.isProtectionActive,
                            onToggle = { onToggleMonitoring(!uiState.isProtectionActive) }
                        )
                    }

                    // Permission warning cards with animated visibility
                    item(key = "permission_accessibility") {
                        AnimatedVisibility(
                            visible = !uiState.isAccessibilityEnabled,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            PermissionWarningCard(
                                permission = "Accessibility Service",
                                message = "Enable accessibility service to monitor text fields across apps.",
                                icon = Icons.Default.Accessibility,
                                actionLabel = "Enable"
                            )
                        }
                    }

                    item(key = "permission_overlay") {
                        AnimatedVisibility(
                            visible = !uiState.isOverlayPermissionGranted,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            PermissionWarningCard(
                                permission = "Overlay Permission",
                                message = "Grant overlay permission to show real-time PII alerts.",
                                icon = Icons.Default.Layers,
                                actionLabel = "Grant"
                            )
                        }
                    }

                    item(key = "permission_notification") {
                        AnimatedVisibility(
                            visible = !uiState.isNotificationPermissionGranted,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            PermissionWarningCard(
                                permission = "Notification Permission",
                                message = "Allow notifications to receive alerts about detected PII.",
                                icon = Icons.Default.Notifications,
                                actionLabel = "Allow"
                            )
                        }
                    }

                    // Statistics Row
                    item(key = "stats_row") {
                        DashboardStatsSection(
                            detectionsToday = uiState.detectionsToday,
                            detectionsThisWeek = uiState.detectionsThisWeek,
                            totalDetections = uiState.totalDetections,
                            inferenceLatencyMs = uiState.inferenceLatencyMs
                        )
                    }

                    // Inference Latency Meter
                    item(key = "latency_meter") {
                        InferenceLatencyMeter(
                            latencyMs = uiState.inferenceLatencyMs,
                            totalInferences = uiState.totalInferences,
                            modelState = uiState.modelState,
                            onShowDetails = { showModelDetails = !showModelDetails }
                        )
                    }

                    // Expandable Model Details
                    item(key = "model_details") {
                        AnimatedVisibility(
                            visible = showModelDetails,
                            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                        ) {
                            ModelDetailsCard(
                                modelState = uiState.modelState,
                                totalInferences = uiState.totalInferences,
                                mostCommonType = uiState.mostCommonType
                            )
                        }
                    }

                    // Recent Detections Header
                    item(key = "recent_header") {
                        RecentDetectionsHeader(
                            count = uiState.recentDetections.size,
                            onViewAll = onNavigateToHistory
                        )
                    }

                    // Recent Detection Cards with animated appearance
                    if (uiState.recentDetections.isEmpty()) {
                        item(key = "empty_detections") {
                            EmptyDetectionsCard()
                        }
                    } else {
                        itemsIndexed(
                            uiState.recentDetections,
                            key = { _, event -> event.id }
                        ) { index, event ->
                            AnimatedDetectionEventCard(
                                event = event,
                                delayMs = index * 50
                            )
                        }
                    }

                    // Quick Actions Row
                    item(key = "quick_actions") {
                        QuickActionsSection(
                            onNavigateToWhitelist = onNavigateToWhitelist,
                            onNavigateToHistory = onNavigateToHistory
                        )
                    }

                    // Footer with version info
                    item(key = "footer") {
                        DashboardFooter()
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Protection Score Card
// ---------------------------------------------------------------------------

@Composable
fun ProtectionScoreCard(
    score: Int,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "score_animation"
    )

    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(600),
        label = "container_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Protection score $score out of 100, ${if (isActive) "active" else "inactive"}" },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .scale(pulseScale)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(160.dp)
                        .semantics { contentDescription = "Score arc showing $score percent" }
                ) {
                    val strokeWidth = 14.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Background track
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.25f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Gradient-like score arc
                    val sweepAngle = (animatedScore / 100f) * 270f
                    val arcColor = when {
                        animatedScore >= 80 -> ProtectionActive
                        animatedScore >= 50 -> AlertOrange
                        else -> AlertRed
                    }
                    drawArc(
                        color = arcColor,
                        startAngle = 135f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedScore.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isActive) "Protected" else "Inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score label text
            Text(
                text = when {
                    score >= 90 -> "Excellent Protection"
                    score >= 70 -> "Good Protection"
                    score >= 50 -> "Fair Protection"
                    score > 0 -> "Needs Attention"
                    else -> "Protection Off"
                },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) AlertRed else ProtectionActive
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(0.7f)
            ) {
                Icon(
                    if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isActive) "Stop Protection" else "Start Protection",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Statistics Section
// ---------------------------------------------------------------------------

@Composable
fun DashboardStatsSection(
    detectionsToday: Int,
    detectionsThisWeek: Int,
    totalDetections: Long,
    inferenceLatencyMs: Long
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Today",
                value = detectionsToday.toString(),
                icon = Icons.Default.Today,
                tintColor = TrustBlue
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "This Week",
                value = detectionsThisWeek.toString(),
                icon = Icons.Default.DateRange,
                tintColor = AlertOrange
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Total",
                value = totalDetections.toString(),
                icon = Icons.Default.Assessment,
                tintColor = SeverityMedium
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Latency",
                value = "${inferenceLatencyMs}ms",
                icon = Icons.Default.Speed,
                tintColor = ProtectionActive
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    tintColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier.semantics {
            contentDescription = "$title: $value"
        },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Inference Latency Meter
// ---------------------------------------------------------------------------

@Composable
fun InferenceLatencyMeter(
    latencyMs: Long,
    totalInferences: Long,
    modelState: ModelState,
    onShowDetails: () -> Unit
) {
    val latencyFraction = (latencyMs.toFloat() / 500f).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = latencyFraction,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "latency_bar"
    )

    val barColor by animateColorAsState(
        targetValue = when {
            latencyMs <= 50 -> ProtectionActive
            latencyMs <= 150 -> AlertYellow
            latencyMs <= 300 -> AlertOrange
            else -> AlertRed
        },
        animationSpec = tween(400),
        label = "latency_color"
    )

    val (statusText, statusColor) = when (modelState) {
        is ModelState.Initializing -> "Initializing..." to AlertYellow
        is ModelState.Ready -> "Ready" to ProtectionActive
        is ModelState.Running -> "Analyzing..." to TrustBlue
        is ModelState.Error -> "Error" to AlertRed
        is ModelState.Closed -> "Closed" to ProtectionInactive
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowDetails)
            .semantics { contentDescription = "Inference latency ${latencyMs}ms, model $statusText" },
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Model: $statusText",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "${totalInferences} inferences",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            // Latency bar
            Text(
                "Inference Latency: ${latencyMs}ms",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0ms", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("500ms", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Model Details Card (expandable)
// ---------------------------------------------------------------------------

@Composable
fun ModelDetailsCard(
    modelState: ModelState,
    totalInferences: Long,
    mostCommonType: EntityType?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Model Details",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            ModelDetailRow("Engine", "Melange On-Device NER")
            ModelDetailRow("Architecture", "Token Classification (BERT)")
            ModelDetailRow("Total Inferences", totalInferences.toString())
            ModelDetailRow("Most Detected Type", mostCommonType?.displayName ?: "None")
            ModelDetailRow("Status", when (modelState) {
                is ModelState.Initializing -> "Initializing"
                is ModelState.Ready -> "Ready"
                is ModelState.Running -> "Running"
                is ModelState.Error -> "Error: ${modelState.message}"
                is ModelState.Closed -> "Closed"
            })
        }
    }
}

@Composable
private fun ModelDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// ---------------------------------------------------------------------------
// Permission Warning Card
// ---------------------------------------------------------------------------

@Composable
fun PermissionWarningCard(
    permission: String,
    message: String,
    icon: ImageVector = Icons.Default.Warning,
    actionLabel: String = "Enable"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Warning: $permission required. $message" },
        colors = CardDefaults.cardColors(containerColor = AlertOrange.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = AlertOrange,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    permission,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(
                onClick = { /* handled by parent */ },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(actionLabel, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Recent Detections Header
// ---------------------------------------------------------------------------

@Composable
fun RecentDetectionsHeader(count: Int, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Recent Detections",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (count > 0) {
                Spacer(Modifier.width(8.dp))
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(
                        count.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        TextButton(onClick = onViewAll) {
            Text("View All")
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Detection Event Card with animated entry
// ---------------------------------------------------------------------------

@Composable
fun AnimatedDetectionEventCard(event: DetectionEvent, delayMs: Int = 0) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(event.id) {
        kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(300)
        )
    ) {
        DetectionEventCard(event)
    }
}

@Composable
fun DetectionEventCard(event: DetectionEvent) {
    val severityColor = when (event.severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${event.severity.displayName} detection: ${event.entityType.displayName} from ${event.sourceApp ?: "unknown"}"
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Severity indicator dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(severityColor)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.entityType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    buildString {
                        append(event.sourceAppName ?: event.sourceApp ?: "Unknown source")
                        append(" -- ")
                        append(event.actionTaken.displayName)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatRelativeTimestamp(event.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${(event.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = severityColor
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Quick Actions Section
// ---------------------------------------------------------------------------

@Composable
fun QuickActionsSection(
    onNavigateToWhitelist: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToWhitelist,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Whitelist", maxLines = 1)
            }
            OutlinedButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("History", maxLines = 1)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Empty Detections Card
// ---------------------------------------------------------------------------

@Composable
fun EmptyDetectionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = ProtectionActive
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "No detections yet",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Your clipboard and text fields are being monitored for sensitive data. You will see alerts here when PII is detected.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Loading State
// ---------------------------------------------------------------------------

@Composable
fun DashboardLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Initializing PrivacyGuard...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Loading model and scanning for permissions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Error State
// ---------------------------------------------------------------------------

@Composable
fun DashboardErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = AlertRed.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = AlertRed
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Something went wrong",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = onRetry, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dashboard Footer
// ---------------------------------------------------------------------------

@Composable
fun DashboardFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            "All processing happens on-device. No data is ever transmitted.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ---------------------------------------------------------------------------
// Utility functions
// ---------------------------------------------------------------------------

private fun formatRelativeTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

// ---------------------------------------------------------------------------
// Preview Composables
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Protection Score - Active")
@Composable
private fun ProtectionScoreCardActivePreview() {
    PrivacyGuardTheme {
        ProtectionScoreCard(score = 85, isActive = true, onToggle = {})
    }
}

@Preview(showBackground = true, name = "Protection Score - Inactive")
@Composable
private fun ProtectionScoreCardInactivePreview() {
    PrivacyGuardTheme {
        ProtectionScoreCard(score = 0, isActive = false, onToggle = {})
    }
}

@Preview(showBackground = true, name = "Stat Card")
@Composable
private fun StatCardPreview() {
    PrivacyGuardTheme {
        StatCard(
            title = "Today",
            value = "12",
            icon = Icons.Default.Today,
            tintColor = TrustBlue
        )
    }
}

@Preview(showBackground = true, name = "Empty Detections")
@Composable
private fun EmptyDetectionsCardPreview() {
    PrivacyGuardTheme {
        EmptyDetectionsCard()
    }
}

@Preview(showBackground = true, name = "Permission Warning")
@Composable
private fun PermissionWarningCardPreview() {
    PrivacyGuardTheme {
        PermissionWarningCard(
            permission = "Accessibility Service",
            message = "Enable accessibility service to monitor text fields.",
            icon = Icons.Default.Accessibility
        )
    }
}

@Preview(showBackground = true, name = "Detection Event Card")
@Composable
private fun DetectionEventCardPreview() {
    PrivacyGuardTheme {
        DetectionEventCard(
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                sourceApp = "com.example.app",
                sourceAppName = "Example App",
                actionTaken = UserAction.CLIPBOARD_CLEARED,
                confidence = 0.95f,
                inferenceTimeMs = 42L
            )
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun DashboardLoadingStatePreview() {
    PrivacyGuardTheme {
        DashboardLoadingState()
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun DashboardErrorStatePreview() {
    PrivacyGuardTheme {
        DashboardErrorState(
            message = "Failed to initialize the ML model. Please restart the app.",
            onRetry = {}
        )
    }
}
