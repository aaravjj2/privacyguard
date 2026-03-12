package com.privacyguard.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.data.DetectionEvent
import com.privacyguard.ml.ModelState
import com.privacyguard.ml.Severity
import com.privacyguard.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PrivacyGuard") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Protection Score
            item {
                ProtectionScoreCard(
                    score = uiState.protectionScore,
                    isActive = uiState.isProtectionActive,
                    onToggle = { onToggleMonitoring(!uiState.isProtectionActive) }
                )
            }

            // Permission warnings
            if (!uiState.isAccessibilityEnabled) {
                item { PermissionWarningCard("Accessibility Service", "Enable accessibility service to monitor text fields across apps.") }
            }
            if (!uiState.isOverlayPermissionGranted) {
                item { PermissionWarningCard("Overlay Permission", "Grant overlay permission to show real-time PII alerts.") }
            }

            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Today",
                        value = uiState.detectionsToday.toString(),
                        icon = Icons.Default.Today
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "This Week",
                        value = uiState.detectionsThisWeek.toString(),
                        icon = Icons.Default.DateRange
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Latency",
                        value = "${uiState.inferenceLatencyMs}ms",
                        icon = Icons.Default.Speed
                    )
                }
            }

            // Model Status
            item {
                ModelStatusCard(uiState.modelState)
            }

            // Recent Detections Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Detections",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = onNavigateToHistory) {
                        Text("View All")
                    }
                }
            }

            // Recent Detection Cards
            if (uiState.recentDetections.isEmpty()) {
                item {
                    EmptyDetectionsCard()
                }
            } else {
                items(uiState.recentDetections) { event ->
                    DetectionEventCard(event)
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToWhitelist,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Manage Whitelist")
                    }
                    OutlinedButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View History")
                    }
                }
            }
        }
    }
}

@Composable
fun ProtectionScoreCard(
    score: Int,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "score"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Canvas(modifier = Modifier.size(150.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    // Background circle
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Score arc
                    val sweepAngle = (animatedScore / 100f) * 270f
                    val color = when {
                        animatedScore >= 80 -> ProtectionActive
                        animatedScore >= 50 -> AlertOrange
                        else -> AlertRed
                    }
                    drawArc(
                        color = color,
                        startAngle = 135f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) AlertRed else ProtectionActive
                )
            ) {
                Icon(
                    if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isActive) "Stop Protection" else "Start Protection")
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ModelStatusCard(state: ModelState) {
    val (statusText, statusColor) = when (state) {
        is ModelState.Initializing -> "Initializing..." to AlertYellow
        is ModelState.Ready -> "Model Ready" to ProtectionActive
        is ModelState.Running -> "Analyzing..." to TrustBlue
        is ModelState.Error -> "Error: ${state.message}" to AlertRed
        is ModelState.Closed -> "Model Closed" to ProtectionInactive
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(Modifier.width(8.dp))
            Text(statusText, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PermissionWarningCard(permission: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AlertOrange.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = AlertOrange)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(permission, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun DetectionEventCard(event: DetectionEvent) {
    val severityColor = when (event.severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
    }
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(severityColor)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.entityType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    event.sourceApp ?: "Unknown source",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                dateFormat.format(Date(event.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyDetectionsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = ProtectionActive
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "No detections yet",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Your clipboard is being monitored for sensitive data.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
