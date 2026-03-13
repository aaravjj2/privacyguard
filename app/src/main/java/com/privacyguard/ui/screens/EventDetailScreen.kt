@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
package com.privacyguard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.UserAction
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import com.privacyguard.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Data classes for screen state
// ---------------------------------------------------------------------------

/**
 * Represents the full UI state for the Event Detail screen.
 * Contains the event data, computed display values, and interaction state.
 */
data class EventDetailUiState(
    val event: DetectionEvent? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val redactedText: String = "",
    val originalTextLength: Int = 0,
    val tokenSpans: List<TokenSpanInfo> = emptyList(),
    val characterIndices: IntRange = IntRange.EMPTY,
    val timelineSteps: List<TimelineStep> = emptyList(),
    val similarDetections: List<DetectionEvent> = emptyList(),
    val sourceAppInfo: SourceAppDisplayInfo? = null,
    val showDeleteConfirmation: Boolean = false,
    val showFalsePositiveDialog: Boolean = false,
    val showWhitelistConfirmation: Boolean = false,
    val actionInProgress: EventAction? = null,
    val actionResult: ActionResult? = null,
    val isExpanded: Map<String, Boolean> = emptyMap()
)

/**
 * Information about a token span from the model inference.
 */
data class TokenSpanInfo(
    val tokenIndex: Int,
    val startChar: Int,
    val endChar: Int,
    val tokenText: String,
    val isEntity: Boolean,
    val confidence: Float
)

/**
 * A step in the detection event timeline.
 */
data class TimelineStep(
    val title: String,
    val description: String,
    val timestamp: Long,
    val icon: TimelineStepType,
    val isCompleted: Boolean,
    val isActive: Boolean = false
)

/**
 * Types of timeline steps for icon mapping.
 */
enum class TimelineStepType {
    DETECTED,
    ANALYZED,
    ALERTED,
    USER_ACTION,
    RESOLVED
}

/**
 * Display info for the source application.
 */
data class SourceAppDisplayInfo(
    val packageName: String,
    val displayName: String,
    val isWhitelisted: Boolean,
    val detectionCount: Int,
    val firstDetectionTimestamp: Long,
    val lastDetectionTimestamp: Long
)

/**
 * Actions that can be performed on an event.
 */
enum class EventAction {
    DELETE,
    REPORT_FALSE_POSITIVE,
    WHITELIST_SOURCE
}

/**
 * Result of an action performed on an event.
 */
data class ActionResult(
    val action: EventAction,
    val success: Boolean,
    val message: String
)

// ---------------------------------------------------------------------------
// Main Event Detail Screen
// ---------------------------------------------------------------------------

/**
 * Full detail screen for a single PII detection event.
 *
 * Displays comprehensive information about a detection event including:
 * - Entity type header with icon, severity badge, and confidence ring
 * - Redacted text display showing where PII was found
 * - Source application information card
 * - Timeline of the detection lifecycle
 * - Technical details (token spans, character indices, inference latency)
 * - Action buttons for delete, false positive reporting, and whitelisting
 * - Similar detections from the same source
 *
 * @param eventId The unique identifier of the detection event to display
 * @param viewModel The ViewModel managing this screen's state
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param onNavigateToEvent Callback to navigate to another event's detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEvent: (String) -> Unit
) {
    val vmState by viewModel.uiState.collectAsState()
    val uiState = EventDetailUiState(
        event = vmState.event,
        isLoading = vmState.isLoading,
        errorMessage = vmState.errorMessage,
        redactedText = vmState.redactedSummary,
        originalTextLength = 0,
        tokenSpans = emptyList(),
        characterIndices = IntRange.EMPTY,
        timelineSteps = emptyList(),
        similarDetections = vmState.relatedEvents,
        sourceAppInfo = null,
        showDeleteConfirmation = vmState.showDeleteConfirmation,
        showFalsePositiveDialog = vmState.showFalsePositiveDialog,
        showWhitelistConfirmation = vmState.showWhitelistConfirmation,
        actionInProgress = null,
        actionResult = vmState.actionMessage?.let { msg -> ActionResult(EventAction.DELETE, true, msg) },
        isExpanded = vmState.isExpanded
    )

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show action result as snackbar
    LaunchedEffect(uiState.actionResult) {
        uiState.actionResult?.let { result ->
            snackbarHostState.showSnackbar(
                message = result.message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearActionResult()
        }
    }

    Scaffold(
        topBar = {
            EventDetailTopBar(
                event = uiState.event,
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                EventDetailLoadingState(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.errorMessage != null -> {
                EventDetailErrorState(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadEvent(eventId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.event != null -> {
                EventDetailContent(
                    uiState = uiState,
                    onDeleteEvent = { viewModel.showDeleteConfirmation() },
                    onConfirmDelete = {
                        viewModel.deleteEvent()
                        onNavigateBack()
                    },
                    onDismissDelete = { viewModel.dismissDeleteConfirmation() },
                    onReportFalsePositive = { viewModel.showFalsePositiveDialog() },
                    onConfirmFalsePositive = { reason ->
                        viewModel.reportFalsePositive(reason)
                    },
                    onDismissFalsePositive = { viewModel.dismissFalsePositiveDialog() },
                    onWhitelistSource = { viewModel.showWhitelistConfirmation() },
                    onConfirmWhitelist = { viewModel.whitelistSource() },
                    onDismissWhitelist = { viewModel.dismissWhitelistConfirmation() },
                    onNavigateToEvent = onNavigateToEvent,
                    onToggleSection = { section -> viewModel.toggleSection(section) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteEvent()
                onNavigateBack()
            },
            onDismiss = { viewModel.dismissDeleteConfirmation() }
        )
    }

    // False positive dialog
    if (uiState.showFalsePositiveDialog) {
        FalsePositiveReportDialog(
            entityType = uiState.event?.entityType ?: EntityType.UNKNOWN,
            onSubmit = { reason -> viewModel.reportFalsePositive(reason) },
            onDismiss = { viewModel.dismissFalsePositiveDialog() }
        )
    }

    // Whitelist confirmation dialog
    if (uiState.showWhitelistConfirmation) {
        WhitelistConfirmationDialog(
            sourceAppName = uiState.sourceAppInfo?.displayName ?: "Unknown App",
            onConfirm = { viewModel.whitelistSource() },
            onDismiss = { viewModel.dismissWhitelistConfirmation() }
        )
    }
}

// ---------------------------------------------------------------------------
// Top Bar
// ---------------------------------------------------------------------------

/**
 * Top app bar for the event detail screen showing entity type and severity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDetailTopBar(
    event: DetectionEvent?,
    onNavigateBack: () -> Unit
) {
    val severityColor = event?.severity?.color ?: MaterialTheme.colorScheme.primary

    TopAppBar(
        title = {
            if (event != null) {
                Column {
                    Text(
                        text = event.entityType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Detection Event",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "Event Details",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.semantics {
                    contentDescription = "Navigate back"
                    role = Role.Button
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (event != null) {
                SeverityIndicatorChip(severity = event.severity)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = severityColor.copy(alpha = 0.08f)
        )
    )
}

/**
 * A chip showing the severity level with appropriate coloring.
 */
@Composable
private fun SeverityIndicatorChip(
    severity: Severity,
    modifier: Modifier = Modifier
) {
    val backgroundColor = severity.color.copy(alpha = 0.15f)
    val textColor = severity.color

    Surface(
        modifier = modifier
            .padding(end = 8.dp)
            .semantics {
                contentDescription = "Severity: ${severity.displayName}"
            },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = severity.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Loading State
// ---------------------------------------------------------------------------

/**
 * Loading placeholder for the event detail screen with shimmer animations.
 */
@Composable
private fun EventDetailLoadingState(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .semantics { contentDescription = "Loading event details" },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header placeholder
        ShimmerBlock(
            width = 200.dp,
            height = 32.dp,
            alpha = shimmerAlpha
        )
        ShimmerBlock(
            width = 120.dp,
            height = 20.dp,
            alpha = shimmerAlpha
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confidence ring placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = shimmerAlpha))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Card placeholders
        repeat(4) {
            ShimmerBlock(
                width = Dp.Unspecified,
                height = 100.dp,
                alpha = shimmerAlpha,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * A rectangular shimmer placeholder block used during loading.
 */
@Composable
private fun ShimmerBlock(
    width: Dp,
    height: Dp,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val widthModifier = if (width == Dp.Unspecified) {
        modifier.fillMaxWidth()
    } else {
        modifier.width(width)
    }

    Box(
        modifier = widthModifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
    )
}

// ---------------------------------------------------------------------------
// Error State
// ---------------------------------------------------------------------------

/**
 * Error state display with retry button.
 */
@Composable
private fun EventDetailErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Failed to Load Event",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.semantics {
                contentDescription = "Retry loading event"
                role = Role.Button
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

// ---------------------------------------------------------------------------
// Main Content
// ---------------------------------------------------------------------------

/**
 * The main scrollable content area for the event detail screen.
 * Organized into distinct sections, each in its own card.
 */
@Composable
private fun EventDetailContent(
    uiState: EventDetailUiState,
    onDeleteEvent: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onReportFalsePositive: () -> Unit,
    onConfirmFalsePositive: (String) -> Unit,
    onDismissFalsePositive: () -> Unit,
    onWhitelistSource: () -> Unit,
    onConfirmWhitelist: () -> Unit,
    onDismissWhitelist: () -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onToggleSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val event = uiState.event ?: return

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Entity Type Header with Confidence Ring
        item(key = "header") {
            EntityTypeHeaderSection(
                event = event,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }

        // Redacted Text Display
        item(key = "redacted_text") {
            RedactedTextSection(
                redactedText = uiState.redactedText,
                originalLength = uiState.originalTextLength,
                entityType = event.entityType,
                isExpanded = uiState.isExpanded["redacted_text"] == true,
                onToggleExpand = { onToggleSection("redacted_text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }

        // Source App Info Card
        item(key = "source_app") {
            SourceAppInfoSection(
                sourceInfo = uiState.sourceAppInfo,
                event = event,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }

        // Timeline Section
        item(key = "timeline") {
            TimelineSection(
                steps = uiState.timelineSteps,
                isExpanded = uiState.isExpanded["timeline"] != false,
                onToggleExpand = { onToggleSection("timeline") },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }

        // Technical Details Section
        item(key = "technical") {
            TechnicalDetailsSection(
                event = event,
                tokenSpans = uiState.tokenSpans,
                characterIndices = uiState.characterIndices,
                isExpanded = uiState.isExpanded["technical"] == true,
                onToggleExpand = { onToggleSection("technical") },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }

        // Action Buttons
        item(key = "actions") {
            ActionButtonsSection(
                event = event,
                sourceAppInfo = uiState.sourceAppInfo,
                actionInProgress = uiState.actionInProgress,
                onDeleteEvent = onDeleteEvent,
                onReportFalsePositive = onReportFalsePositive,
                onWhitelistSource = onWhitelistSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }

        // Similar Detections
        if (uiState.similarDetections.isNotEmpty()) {
            item(key = "similar_header") {
                SimilarDetectionsHeader(
                    count = uiState.similarDetections.size,
                    modifier = Modifier.animateItemPlacement()
                )
            }

            items(
                items = uiState.similarDetections,
                key = { it.id }
            ) { similarEvent ->
                SimilarDetectionCard(
                    event = similarEvent,
                    onClick = { onNavigateToEvent(similarEvent.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                )
            }
        }

        // Bottom spacing for navigation bar clearance
        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Entity Type Header Section
// ---------------------------------------------------------------------------

/**
 * Header section displaying the entity type with its icon, severity badge,
 * and an animated confidence ring showing the model's confidence score.
 */
@Composable
private fun EntityTypeHeaderSection(
    event: DetectionEvent,
    modifier: Modifier = Modifier
) {
    val severityColor = event.severity.color
    val animatedConfidence by animateFloatAsState(
        targetValue = event.confidence,
        animationSpec = tween(
            durationMillis = 1200,
            easing = EaseOutCubic
        ),
        label = "confidenceAnimation"
    )

    val headerScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "headerScale"
    )

    Card(
        modifier = modifier.scale(headerScale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = severityColor.copy(alpha = 0.06f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Entity type icon with animated background
            EntityTypeIconWithBackground(
                entityType = event.entityType,
                severity = event.severity,
                size = 72.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Entity type name
            Text(
                text = event.entityType.displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    heading()
                    contentDescription = "Entity type: ${event.entityType.displayName}"
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Severity label
            Text(
                text = "${event.severity.displayName} Severity",
                style = MaterialTheme.typography.titleSmall,
                color = severityColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Confidence Ring
            ConfidenceRingDisplay(
                confidence = animatedConfidence,
                severityColor = severityColor,
                size = 140.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timestamp
            Text(
                text = formatTimestamp(event.timestamp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics {
                    contentDescription = "Detected at ${formatTimestampAccessible(event.timestamp)}"
                }
            )
        }
    }
}

/**
 * An icon representing the entity type with an animated circular background.
 */
@Composable
private fun EntityTypeIconWithBackground(
    entityType: EntityType,
    severity: Severity,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val icon = getEntityTypeIcon(entityType)
    val backgroundColor = severity.color.copy(alpha = 0.15f)
    val iconColor = severity.color

    val pulseAnimation = rememberInfiniteTransition(label = "iconPulse")
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(backgroundColor)
            .semantics {
                contentDescription = "${entityType.displayName} icon"
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(size * 0.5f),
            tint = iconColor
        )
    }
}

/**
 * Maps entity types to their corresponding Material icons.
 */
private fun getEntityTypeIcon(entityType: EntityType): ImageVector {
    return when (entityType) {
        EntityType.CREDIT_CARD -> Icons.Filled.CreditCard
        EntityType.SSN -> Icons.Filled.Badge
        EntityType.PASSWORD -> Icons.Filled.Password
        EntityType.API_KEY -> Icons.Filled.Key
        EntityType.EMAIL -> Icons.Filled.Email
        EntityType.PHONE -> Icons.Filled.Phone
        EntityType.PERSON_NAME -> Icons.Filled.Person
        EntityType.ADDRESS -> Icons.Filled.LocationOn
        EntityType.DATE_OF_BIRTH -> Icons.Filled.CalendarMonth
        EntityType.MEDICAL_ID -> Icons.Filled.LocalHospital
        EntityType.UNKNOWN -> Icons.Filled.QuestionMark
    }
}

/**
 * Animated circular confidence ring showing model confidence percentage.
 *
 * Draws a circular arc whose sweep corresponds to the confidence value,
 * with the percentage displayed in the center.
 */
@Composable
private fun ConfidenceRingDisplay(
    confidence: Float,
    severityColor: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val percentage = (confidence * 100).roundToInt()
    val sweepAngle = confidence * 360f

    val backgroundTrackColor = MaterialTheme.colorScheme.surfaceVariant
    val confidenceLabel = "$percentage%"

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "Model confidence: $percentage percent"
            },
        contentAlignment = Alignment.Center
    ) {
        // Draw the ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val arcSize = Size(
                this.size.width - strokeWidth,
                this.size.height - strokeWidth
            )
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background track
            drawArc(
                color = backgroundTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Confidence arc
            drawArc(
                color = severityColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = confidenceLabel,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = severityColor
            )
            Text(
                text = "Confidence",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Redacted Text Section
// ---------------------------------------------------------------------------

/**
 * Section displaying the text that was analyzed, with sensitive portions
 * redacted and highlighted. The section can be expanded or collapsed.
 */
@Composable
private fun RedactedTextSection(
    redactedText: String,
    originalLength: Int,
    entityType: EntityType,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section header
            SectionHeaderWithToggle(
                title = "Detected Content",
                icon = Icons.Filled.TextSnippet,
                isExpanded = isExpanded,
                onToggle = onToggleExpand,
                badge = "$originalLength chars"
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Redacted text display with monospace font
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        val annotatedText = buildRedactedAnnotatedString(
                            redactedText = redactedText,
                            entityType = entityType
                        )

                        Text(
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier
                                .padding(12.dp)
                                .semantics {
                                    contentDescription =
                                        "Redacted text content. Sensitive data has been masked."
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Legend
                    RedactionLegend()
                }
            }

            // Collapsed preview
            if (!isExpanded && redactedText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = redactedText.take(80) + if (redactedText.length > 80) "..." else "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Builds an AnnotatedString where redacted portions are highlighted in red
 * and the rest of the text is displayed normally.
 */
@Composable
private fun buildRedactedAnnotatedString(
    redactedText: String,
    entityType: EntityType
): androidx.compose.ui.text.AnnotatedString {
    val redactedColor = entityType.severity.color
    val normalColor = MaterialTheme.colorScheme.onSurface

    return buildAnnotatedString {
        var i = 0
        while (i < redactedText.length) {
            if (redactedText[i] == '*') {
                // Find the end of the redacted block
                val start = i
                while (i < redactedText.length && redactedText[i] == '*') {
                    i++
                }
                withStyle(
                    SpanStyle(
                        color = redactedColor,
                        background = redactedColor.copy(alpha = 0.1f),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(redactedText.substring(start, i))
                }
            } else {
                withStyle(SpanStyle(color = normalColor)) {
                    append(redactedText[i])
                }
                i++
            }
        }
    }
}

/**
 * Legend explaining the redaction markers in the text display.
 */
@Composable
private fun RedactionLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem(
            color = SeverityCritical,
            label = "Redacted PII"
        )
        LegendItem(
            color = MaterialTheme.colorScheme.onSurface,
            label = "Safe content"
        )
    }
}

/**
 * A single legend item with a colored dot and label.
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "$label indicator"
        }
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Source App Info Section
// ---------------------------------------------------------------------------

/**
 * Card displaying information about the application that was the source
 * of the detected PII, including detection counts and whitelist status.
 */
@Composable
private fun SourceAppInfoSection(
    sourceInfo: SourceAppDisplayInfo?,
    event: DetectionEvent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics(mergeDescendants = true) {}
            ) {
                Icon(
                    imageVector = Icons.Filled.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Source Application",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App icon placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (sourceInfo?.displayName?.firstOrNull() ?: '?').toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sourceInfo?.displayName ?: event.sourceAppName ?: "Unknown App",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = sourceInfo?.packageName ?: event.sourceApp ?: "Unknown package",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Whitelist status badge
                if (sourceInfo?.isWhitelisted == true) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Trusted",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Stats row
            if (sourceInfo != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SourceAppStatItem(
                        label = "Detections",
                        value = sourceInfo.detectionCount.toString(),
                        icon = Icons.Filled.Warning
                    )
                    SourceAppStatItem(
                        label = "First Seen",
                        value = formatDateShort(sourceInfo.firstDetectionTimestamp),
                        icon = Icons.Filled.Schedule
                    )
                    SourceAppStatItem(
                        label = "Last Seen",
                        value = formatDateShort(sourceInfo.lastDetectionTimestamp),
                        icon = Icons.Filled.Update
                    )
                }
            }
        }
    }
}

/**
 * A single statistic item for the source app info card.
 */
@Composable
private fun SourceAppStatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "$label: $value"
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Timeline Section
// ---------------------------------------------------------------------------

/**
 * Timeline showing the lifecycle of the detection event, from initial
 * detection through analysis, alerting, and user action.
 */
@Composable
private fun TimelineSection(
    steps: List<TimelineStep>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeaderWithToggle(
                title = "Event Timeline",
                icon = Icons.Filled.Timeline,
                isExpanded = isExpanded,
                onToggle = onToggleExpand
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    steps.forEachIndexed { index, step ->
                        TimelineStepItem(
                            step = step,
                            isFirst = index == 0,
                            isLast = index == steps.lastIndex,
                            animationDelay = index * 100
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single step in the event timeline, with a connector line to the next step.
 */
@Composable
private fun TimelineStepItem(
    step: TimelineStep,
    isFirst: Boolean,
    isLast: Boolean,
    animationDelay: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val stepAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "stepAlpha"
    )

    val stepIcon = when (step.icon) {
        TimelineStepType.DETECTED -> Icons.Filled.Sensors
        TimelineStepType.ANALYZED -> Icons.Filled.Analytics
        TimelineStepType.ALERTED -> Icons.Filled.NotificationsActive
        TimelineStepType.USER_ACTION -> Icons.Filled.TouchApp
        TimelineStepType.RESOLVED -> Icons.Filled.CheckCircle
    }

    val stepColor = when {
        step.isActive -> MaterialTheme.colorScheme.primary
        step.isCompleted -> SuccessGreen
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(stepAlpha)
            .semantics(mergeDescendants = true) {
                contentDescription = buildString {
                    append("Step: ${step.title}. ")
                    append(step.description)
                    append(". ")
                    if (step.isCompleted) append("Completed. ")
                    if (step.isActive) append("Currently active. ")
                    append("Time: ${formatTimestampAccessible(step.timestamp)}")
                }
            },
        verticalAlignment = Alignment.Top
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Connector line above (if not first)
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(8.dp)
                        .background(stepColor.copy(alpha = 0.4f))
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Step dot
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(stepColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stepIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = stepColor
                )
            }

            // Connector line below (if not last)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(stepColor.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Step content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (step.isCompleted || step.isActive) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = formatTimeOnly(step.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isLast) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Technical Details Section
// ---------------------------------------------------------------------------

/**
 * Section showing technical details about the detection, including
 * token spans, character indices, and inference latency metrics.
 */
@Composable
private fun TechnicalDetailsSection(
    event: DetectionEvent,
    tokenSpans: List<TokenSpanInfo>,
    characterIndices: IntRange,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeaderWithToggle(
                title = "Technical Details",
                icon = Icons.Filled.Code,
                isExpanded = isExpanded,
                onToggle = onToggleExpand
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Inference Performance Card
                    TechnicalDetailCard(
                        title = "Inference Performance",
                        icon = Icons.Filled.Speed
                    ) {
                        TechnicalMetricRow(
                            label = "Inference Time",
                            value = "${event.inferenceTimeMs} ms"
                        )
                        TechnicalMetricRow(
                            label = "Model Confidence",
                            value = "${(event.confidence * 100).roundToInt()}%"
                        )
                        TechnicalMetricRow(
                            label = "Entity Label Index",
                            value = event.entityType.labelIndex.toString()
                        )

                        // Performance indicator
                        Spacer(modifier = Modifier.height(8.dp))
                        InferencePerformanceIndicator(
                            latencyMs = event.inferenceTimeMs
                        )
                    }

                    // Character Indices Card
                    if (!characterIndices.isEmpty()) {
                        TechnicalDetailCard(
                            title = "Character Indices",
                            icon = Icons.Filled.TextFields
                        ) {
                            TechnicalMetricRow(
                                label = "Start Index",
                                value = characterIndices.first.toString()
                            )
                            TechnicalMetricRow(
                                label = "End Index",
                                value = characterIndices.last.toString()
                            )
                            TechnicalMetricRow(
                                label = "Span Length",
                                value = "${characterIndices.last - characterIndices.first} chars"
                            )
                        }
                    }

                    // Token Spans Card
                    if (tokenSpans.isNotEmpty()) {
                        TechnicalDetailCard(
                            title = "Token Spans",
                            icon = Icons.Filled.DataArray
                        ) {
                            Text(
                                text = "${tokenSpans.size} tokens analyzed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Token list with horizontal scroll
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(tokenSpans) { token ->
                                    TokenSpanChip(token = token)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Token detail table
                            tokenSpans.filter { it.isEntity }.forEach { token ->
                                EntityTokenDetailRow(token = token)
                            }
                        }
                    }

                    // Detection Metadata Card
                    TechnicalDetailCard(
                        title = "Detection Metadata",
                        icon = Icons.Filled.Info
                    ) {
                        TechnicalMetricRow(
                            label = "Event ID",
                            value = event.id.take(12) + "..."
                        )
                        TechnicalMetricRow(
                            label = "Timestamp (epoch)",
                            value = event.timestamp.toString()
                        )
                        TechnicalMetricRow(
                            label = "Action Taken",
                            value = event.actionTaken.displayName
                        )
                        TechnicalMetricRow(
                            label = "Severity Level",
                            value = "${event.severity.displayName} (ordinal: ${event.severity.ordinal})"
                        )
                    }
                }
            }
        }
    }
}

/**
 * A card containing a group of related technical details.
 */
@Composable
private fun TechnicalDetailCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

/**
 * A row displaying a label-value pair for technical metrics.
 */
@Composable
private fun TechnicalMetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$label: $value"
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Visual indicator for inference performance based on latency thresholds.
 */
@Composable
private fun InferencePerformanceIndicator(
    latencyMs: Long
) {
    val (label, color) = when {
        latencyMs < 50 -> "Excellent" to SuccessGreen
        latencyMs < 100 -> "Good" to TrustBlue
        latencyMs < 200 -> "Acceptable" to AlertYellow
        else -> "Slow" to AlertRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Performance rating: $label at $latencyMs milliseconds"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Performance bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val fillFraction = (1f - (latencyMs.coerceAtMost(300) / 300f)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fillFraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * A chip displaying a single token from the model analysis.
 * Entity tokens are highlighted with the severity color.
 */
@Composable
private fun TokenSpanChip(token: TokenSpanInfo) {
    val backgroundColor = if (token.isEntity) {
        AlertRed.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (token.isEntity) {
        AlertRed
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        modifier = Modifier.semantics {
            contentDescription = buildString {
                append("Token ${token.tokenIndex}: ${token.tokenText}")
                if (token.isEntity) {
                    append(". Entity with ${(token.confidence * 100).roundToInt()}% confidence")
                }
            }
        }
    ) {
        Text(
            text = token.tokenText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

/**
 * Detail row for a token that was identified as part of an entity.
 */
@Composable
private fun EntityTokenDetailRow(token: TokenSpanInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Token #${token.tokenIndex} [${token.startChar}:${token.endChar}]",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${(token.confidence * 100).roundToInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AlertRed
        )
    }
}

// ---------------------------------------------------------------------------
// Action Buttons Section
// ---------------------------------------------------------------------------

/**
 * Section containing action buttons for interacting with the event:
 * delete, report false positive, and whitelist the source app.
 */
@Composable
private fun ActionButtonsSection(
    event: DetectionEvent,
    sourceAppInfo: SourceAppDisplayInfo?,
    actionInProgress: EventAction?,
    onDeleteEvent: () -> Unit,
    onReportFalsePositive: () -> Unit,
    onWhitelistSource: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Delete Event Button
            EventActionButton(
                text = "Delete Event",
                description = "Permanently remove this detection event from history",
                icon = Icons.Filled.Delete,
                onClick = onDeleteEvent,
                isLoading = actionInProgress == EventAction.DELETE,
                isDestructive = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Report False Positive Button
            EventActionButton(
                text = "Report False Positive",
                description = "Flag this detection as incorrect to improve future accuracy",
                icon = Icons.Filled.Flag,
                onClick = onReportFalsePositive,
                isLoading = actionInProgress == EventAction.REPORT_FALSE_POSITIVE,
                isDestructive = false,
                modifier = Modifier.fillMaxWidth()
            )

            // Whitelist Source App Button
            if (event.sourceApp != null && sourceAppInfo?.isWhitelisted != true) {
                EventActionButton(
                    text = "Whitelist ${sourceAppInfo?.displayName ?: "Source App"}",
                    description = "Stop monitoring this app for PII detections",
                    icon = Icons.Filled.VerifiedUser,
                    onClick = onWhitelistSource,
                    isLoading = actionInProgress == EventAction.WHITELIST_SOURCE,
                    isDestructive = false,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (sourceAppInfo?.isWhitelisted == true) {
                // Show already whitelisted indicator
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessGreen.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Source App Whitelisted",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = SuccessGreen
                            )
                            Text(
                                text = "This app is trusted and excluded from monitoring",
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

/**
 * A styled action button with icon, description text, and loading state.
 */
@Composable
private fun EventActionButton(
    text: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isLoading: Boolean,
    isDestructive: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isDestructive) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .clickable(enabled = !isLoading, onClick = onClick)
            .semantics {
                contentDescription = "$text. $description"
                role = Role.Button
                if (isLoading) {
                    disabled()
                    stateDescription = "Loading"
                }
            },
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = contentColor
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Similar Detections Section
// ---------------------------------------------------------------------------

/**
 * Header for the similar detections section showing the count.
 */
@Composable
private fun SimilarDetectionsHeader(
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Similar Detections",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * A compact card displaying a similar detection event.
 * Tapping navigates to that event's detail screen.
 */
@Composable
private fun SimilarDetectionCard(
    event: DetectionEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val severityColor = event.severity.color

    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = buildString {
                    append("Similar detection: ${event.entityType.displayName}")
                    append(", ${event.severity.displayName} severity")
                    append(", detected ${formatTimestampAccessible(event.timestamp)}")
                }
                role = Role.Button
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Entity type icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(severityColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getEntityTypeIcon(event.entityType),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = severityColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.entityType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatTimestamp(event.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(event.confidence * 100).roundToInt()}% confidence",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Severity indicator dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(severityColor)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Shared Section Components
// ---------------------------------------------------------------------------

/**
 * Reusable section header with expand/collapse toggle and optional badge.
 */
@Composable
private fun SectionHeaderWithToggle(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    badge: String? = null
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "rotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .semantics {
                contentDescription = "$title section. ${if (isExpanded) "Expanded" else "Collapsed"}. Double tap to toggle."
                role = Role.Button
                stateDescription = if (isExpanded) "Expanded" else "Collapsed"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        if (badge != null) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Dialogs
// ---------------------------------------------------------------------------

/**
 * Confirmation dialog for deleting a detection event.
 */
@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Delete Detection Event",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "This will permanently remove this detection event from your history. " +
                    "This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
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
 * Dialog for reporting a false positive detection.
 * Allows the user to select a reason and provide optional details.
 */
@Composable
private fun FalsePositiveReportDialog(
    entityType: EntityType,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var additionalDetails by remember { mutableStateOf("") }

    val reasons = listOf(
        "Not actually ${entityType.displayName}",
        "Test/sample data",
        "Already public information",
        "Intentionally shared",
        "Other"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Flag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Report False Positive",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Help improve detection accuracy by letting us know why " +
                        "this detection was incorrect.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Reason:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                OutlinedTextField(
                    value = additionalDetails,
                    onValueChange = { additionalDetails = it },
                    label = { Text("Additional details (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val report = buildString {
                        append(selectedReason ?: "Unspecified")
                        if (additionalDetails.isNotBlank()) {
                            append(": $additionalDetails")
                        }
                    }
                    onSubmit(report)
                },
                enabled = selectedReason != null
            ) {
                Text("Submit Report")
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
 * Confirmation dialog for adding a source app to the whitelist.
 */
@Composable
private fun WhitelistConfirmationDialog(
    sourceAppName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Whitelist Application",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Are you sure you want to whitelist \"$sourceAppName\"?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Once whitelisted, PII detections from this app will be " +
                        "ignored. You can remove it from the whitelist at any time " +
                        "in Settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = AlertYellow.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AlertYellow
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "This may reduce your overall protection coverage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AlertYellow
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Whitelist")
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
// Utility Functions
// ---------------------------------------------------------------------------

/**
 * Formats a timestamp into a human-readable date and time string.
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Formats a timestamp into an accessible string for screen readers.
 */
private fun formatTimestampAccessible(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Formats a timestamp into a short date string.
 */
private fun formatDateShort(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Formats a timestamp into a time-only string.
 */
private fun formatTimeOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Calculates a relative time string (e.g., "2 hours ago").
 */
private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
        diff < 604_800_000 -> "${diff / 86_400_000} days ago"
        else -> formatDateShort(timestamp)
    }
}

// ---------------------------------------------------------------------------
// Preview Parameter Provider
// ---------------------------------------------------------------------------

/**
 * Provides sample [DetectionEvent] instances for Compose previews.
 */
private class SampleEventProvider : PreviewParameterProvider<DetectionEvent> {
    override val values = sequenceOf(
        DetectionEvent(
            id = "preview-1",
            timestamp = System.currentTimeMillis() - 3_600_000,
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL,
            sourceApp = "com.example.shopping",
            sourceAppName = "ShopNow",
            actionTaken = UserAction.CLIPBOARD_CLEARED,
            confidence = 0.97f,
            inferenceTimeMs = 45L
        ),
        DetectionEvent(
            id = "preview-2",
            timestamp = System.currentTimeMillis() - 7_200_000,
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            sourceApp = "com.example.mail",
            sourceAppName = "QuickMail",
            actionTaken = UserAction.DISMISSED,
            confidence = 0.85f,
            inferenceTimeMs = 62L
        ),
        DetectionEvent(
            id = "preview-3",
            timestamp = System.currentTimeMillis() - 86_400_000,
            entityType = EntityType.PERSON_NAME,
            severity = Severity.MEDIUM,
            sourceApp = "com.example.social",
            sourceAppName = "SocialApp",
            actionTaken = UserAction.NO_ACTION,
            confidence = 0.72f,
            inferenceTimeMs = 38L
        ),
        DetectionEvent(
            id = "preview-4",
            timestamp = System.currentTimeMillis(),
            entityType = EntityType.SSN,
            severity = Severity.CRITICAL,
            sourceApp = "com.example.finance",
            sourceAppName = "FinanceTracker",
            actionTaken = UserAction.CLIPBOARD_CLEARED,
            confidence = 0.99f,
            inferenceTimeMs = 28L
        ),
        DetectionEvent(
            id = "preview-5",
            timestamp = System.currentTimeMillis() - 172_800_000,
            entityType = EntityType.API_KEY,
            severity = Severity.CRITICAL,
            sourceApp = "com.example.devtools",
            sourceAppName = "DevConsole",
            actionTaken = UserAction.WHITELISTED_APP,
            confidence = 0.91f,
            inferenceTimeMs = 55L
        ),
        DetectionEvent(
            id = "preview-6",
            timestamp = System.currentTimeMillis() - 43_200_000,
            entityType = EntityType.PHONE,
            severity = Severity.HIGH,
            sourceApp = "com.example.contacts",
            sourceAppName = "ContactSync",
            actionTaken = UserAction.AUTO_DISMISSED,
            confidence = 0.88f,
            inferenceTimeMs = 41L
        )
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Entity Type Header - Critical")
@Composable
private fun PreviewEntityTypeHeaderCritical() {
    PrivacyGuardTheme(dynamicColor = false) {
        EntityTypeHeaderSection(
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                confidence = 0.97f,
                inferenceTimeMs = 45L
            )
        )
    }
}

@Preview(showBackground = true, name = "Entity Type Header - High")
@Composable
private fun PreviewEntityTypeHeaderHigh() {
    PrivacyGuardTheme(dynamicColor = false) {
        EntityTypeHeaderSection(
            event = DetectionEvent(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                confidence = 0.85f,
                inferenceTimeMs = 62L
            )
        )
    }
}

@Preview(showBackground = true, name = "Redacted Text Section")
@Composable
private fun PreviewRedactedTextSection() {
    PrivacyGuardTheme(dynamicColor = false) {
        RedactedTextSection(
            redactedText = "My credit card number is ************1234 and my name is ******* *****.",
            originalLength = 72,
            entityType = EntityType.CREDIT_CARD,
            isExpanded = true,
            onToggleExpand = {}
        )
    }
}

@Preview(showBackground = true, name = "Source App Info")
@Composable
private fun PreviewSourceAppInfo() {
    PrivacyGuardTheme(dynamicColor = false) {
        SourceAppInfoSection(
            sourceInfo = SourceAppDisplayInfo(
                packageName = "com.example.shopping",
                displayName = "ShopNow",
                isWhitelisted = false,
                detectionCount = 5,
                firstDetectionTimestamp = System.currentTimeMillis() - 604_800_000,
                lastDetectionTimestamp = System.currentTimeMillis() - 3_600_000
            ),
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                sourceApp = "com.example.shopping",
                sourceAppName = "ShopNow",
                confidence = 0.97f,
                inferenceTimeMs = 45L
            )
        )
    }
}

@Preview(showBackground = true, name = "Timeline Section")
@Composable
private fun PreviewTimelineSection() {
    val now = System.currentTimeMillis()
    PrivacyGuardTheme(dynamicColor = false) {
        TimelineSection(
            steps = listOf(
                TimelineStep(
                    title = "PII Detected",
                    description = "Credit card number detected in clipboard",
                    timestamp = now - 5000,
                    icon = TimelineStepType.DETECTED,
                    isCompleted = true
                ),
                TimelineStep(
                    title = "Analysis Complete",
                    description = "Model inference completed in 45ms",
                    timestamp = now - 4000,
                    icon = TimelineStepType.ANALYZED,
                    isCompleted = true
                ),
                TimelineStep(
                    title = "Alert Shown",
                    description = "Critical overlay alert displayed to user",
                    timestamp = now - 3000,
                    icon = TimelineStepType.ALERTED,
                    isCompleted = true
                ),
                TimelineStep(
                    title = "User Action",
                    description = "Clipboard cleared by user",
                    timestamp = now - 1000,
                    icon = TimelineStepType.USER_ACTION,
                    isCompleted = true
                ),
                TimelineStep(
                    title = "Resolved",
                    description = "Event recorded and PII removed from memory",
                    timestamp = now,
                    icon = TimelineStepType.RESOLVED,
                    isCompleted = true,
                    isActive = true
                )
            ),
            isExpanded = true,
            onToggleExpand = {}
        )
    }
}

@Preview(showBackground = true, name = "Action Buttons")
@Composable
private fun PreviewActionButtons() {
    PrivacyGuardTheme(dynamicColor = false) {
        ActionButtonsSection(
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                sourceApp = "com.example.shopping",
                sourceAppName = "ShopNow",
                confidence = 0.97f,
                inferenceTimeMs = 45L
            ),
            sourceAppInfo = SourceAppDisplayInfo(
                packageName = "com.example.shopping",
                displayName = "ShopNow",
                isWhitelisted = false,
                detectionCount = 5,
                firstDetectionTimestamp = System.currentTimeMillis() - 604_800_000,
                lastDetectionTimestamp = System.currentTimeMillis() - 3_600_000
            ),
            actionInProgress = null,
            onDeleteEvent = {},
            onReportFalsePositive = {},
            onWhitelistSource = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun PreviewLoadingState() {
    PrivacyGuardTheme(dynamicColor = false) {
        EventDetailLoadingState()
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun PreviewErrorState() {
    PrivacyGuardTheme(dynamicColor = false) {
        EventDetailErrorState(
            message = "Event not found in the database. It may have been deleted.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Similar Detection Card")
@Composable
private fun PreviewSimilarDetectionCard() {
    PrivacyGuardTheme(dynamicColor = false) {
        SimilarDetectionCard(
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                sourceApp = "com.example.shopping",
                sourceAppName = "ShopNow",
                confidence = 0.93f,
                inferenceTimeMs = 52L,
                actionTaken = UserAction.DISMISSED
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Technical Details")
@Composable
private fun PreviewTechnicalDetails() {
    PrivacyGuardTheme(dynamicColor = false) {
        TechnicalDetailsSection(
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                confidence = 0.97f,
                inferenceTimeMs = 45L
            ),
            tokenSpans = listOf(
                TokenSpanInfo(0, 0, 2, "My", false, 0.01f),
                TokenSpanInfo(1, 3, 9, "credit", true, 0.95f),
                TokenSpanInfo(2, 10, 14, "card", true, 0.97f),
                TokenSpanInfo(3, 15, 17, "is", false, 0.02f),
                TokenSpanInfo(4, 18, 34, "4111111111111111", true, 0.99f)
            ),
            characterIndices = 18..34,
            isExpanded = true,
            onToggleExpand = {}
        )
    }
}
