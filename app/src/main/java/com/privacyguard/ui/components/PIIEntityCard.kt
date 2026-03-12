package com.privacyguard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.util.concurrent.TimeUnit

// ---------------------------------------------------------------------------
// Data models for PIIEntityCard
// ---------------------------------------------------------------------------

/**
 * Extended data class representing a single PII entity detection with all
 * metadata needed for the full card display. This augments [DetectionEvent]
 * with display-specific fields such as raw text snippets and token spans.
 */
data class PIIEntityDisplayData(
    val id: String = UUID.randomUUID().toString(),
    val entityType: EntityType = EntityType.UNKNOWN,
    val severity: Severity = Severity.MEDIUM,
    val confidence: Float = 0f,
    val rawText: String = "",
    val maskedText: String = "",
    val sourceAppPackage: String? = null,
    val sourceAppName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val actionTaken: UserAction = UserAction.NO_ACTION,
    val inferenceTimeMs: Long = 0L,
    val tokenSpans: List<TokenSpan> = emptyList(),
    val startCharIndex: Int = 0,
    val endCharIndex: Int = 0,
    val validationDetails: List<ValidationDetail> = emptyList(),
    val isWhitelisted: Boolean = false,
    val isFalsePositive: Boolean = false,
    val contextWindow: String = ""
)

/**
 * Represents a single token span within the detected entity.
 */
data class TokenSpan(
    val token: String,
    val startIndex: Int,
    val endIndex: Int,
    val label: String,
    val score: Float
)

/**
 * A single validation check result for the detected entity.
 */
data class ValidationDetail(
    val checkName: String,
    val passed: Boolean,
    val description: String
)

/**
 * Actions the user can take on a PII entity card.
 */
sealed class PIICardAction {
    data class CopyMasked(val entityId: String) : PIICardAction()
    data class Delete(val entityId: String) : PIICardAction()
    data class AddToWhitelist(val entityId: String, val sourceApp: String?) : PIICardAction()
    data class ReportFalsePositive(val entityId: String) : PIICardAction()
    data class ViewDetails(val entityId: String) : PIICardAction()
}

// ---------------------------------------------------------------------------
// Severity color mapping
// ---------------------------------------------------------------------------

/**
 * Returns the border color for the card based on severity.
 * CRITICAL=red, HIGH=orange, MEDIUM=yellow, LOW=green (using SuccessGreen).
 */
@Composable
fun severityBorderColor(severity: Severity): Color {
    return when (severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
    }
}

/**
 * Returns a gradient brush for the severity.
 */
fun severityGradientBrush(severity: Severity): Brush {
    return when (severity) {
        Severity.CRITICAL -> Brush.verticalGradient(
            listOf(CriticalGradientStart, CriticalGradientEnd)
        )
        Severity.HIGH -> Brush.verticalGradient(
            listOf(HighGradientStart, HighGradientEnd)
        )
        Severity.MEDIUM -> Brush.verticalGradient(
            listOf(AlertYellow, AlertYellow.copy(alpha = 0.7f))
        )
    }
}

/**
 * Returns the Material icon for a given entity type.
 */
fun entityTypeIcon(entityType: EntityType): ImageVector {
    return when (entityType) {
        EntityType.CREDIT_CARD -> Icons.Filled.CreditCard
        EntityType.SSN -> Icons.Filled.Badge
        EntityType.PASSWORD -> Icons.Filled.Key
        EntityType.API_KEY -> Icons.Filled.VpnKey
        EntityType.EMAIL -> Icons.Filled.Email
        EntityType.PHONE -> Icons.Filled.Phone
        EntityType.PERSON_NAME -> Icons.Filled.Person
        EntityType.ADDRESS -> Icons.Filled.LocationOn
        EntityType.DATE_OF_BIRTH -> Icons.Filled.Cake
        EntityType.MEDICAL_ID -> Icons.Filled.MedicalServices
        EntityType.UNKNOWN -> Icons.Filled.HelpOutline
    }
}

// ---------------------------------------------------------------------------
// Shimmer loading effect
// ---------------------------------------------------------------------------

/**
 * Creates an infinite shimmer animation that can be applied as a background
 * modifier to any composable to indicate a loading state.
 */
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return this.then(
        Modifier.drawBehind {
            val brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(translateAnim - 200f, 0f),
                end = Offset(translateAnim + 200f, size.height)
            )
            drawRect(brush = brush)
        }
    )
}

// ---------------------------------------------------------------------------
// PIIEntityCard - Main composable
// ---------------------------------------------------------------------------

/**
 * A highly detailed, expandable card component for displaying a single PII
 * entity detection. Features include:
 *
 * - Severity-colored left border strip
 * - Entity type icon with display name
 * - Animated confidence bar
 * - Masked raw text display
 * - Source app info
 * - Relative and absolute timestamps
 * - Expandable detail section with token spans and validation info
 * - Action buttons for copy, delete, whitelist, and false positive reporting
 * - Full accessibility support with content descriptions and roles
 * - Animated expand/collapse, fade in, and scale transitions
 *
 * @param data The PII entity display data to render.
 * @param onAction Callback when the user triggers a card action.
 * @param modifier Optional modifier for the card container.
 * @param isLoading If true, renders a shimmer placeholder instead of real content.
 * @param animationDelayMs Delay in milliseconds before the card fades in.
 * @param initiallyExpanded Whether the detail section starts expanded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PIIEntityCard(
    data: PIIEntityDisplayData,
    onAction: (PIICardAction) -> Unit = {},
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    animationDelayMs: Int = 0,
    initiallyExpanded: Boolean = false
) {
    // Visibility state for fade-in animation
    var isVisible by remember { mutableStateOf(animationDelayMs == 0) }
    LaunchedEffect(data.id) {
        if (animationDelayMs > 0) {
            kotlinx.coroutines.delay(animationDelayMs.toLong())
        }
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400, easing = FastOutSlowInEasing)) +
                slideInVertically(
                    initialOffsetY = { it / 5 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) +
                scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ),
        exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
    ) {
        if (isLoading) {
            PIIEntityCardShimmer(modifier = modifier)
        } else {
            PIIEntityCardContent(
                data = data,
                onAction = onAction,
                modifier = modifier,
                initiallyExpanded = initiallyExpanded
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Card content (non-loading state)
// ---------------------------------------------------------------------------

@Composable
private fun PIIEntityCardContent(
    data: PIIEntityDisplayData,
    onAction: (PIICardAction) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val borderColor = severityBorderColor(data.severity)
    val borderWidth = 4.dp

    // Scale animation when card is tapped
    val scaleState by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    // Animated rotation for expand/collapse chevron
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "chevron_rotation"
    )

    // Animated container color when expanded
    val containerColor by animateColorAsState(
        targetValue = if (isExpanded)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "container_color"
    )

    val cardSemanticDescription = buildString {
        append("${data.severity.displayName} severity ${data.entityType.displayName} detection. ")
        append("Confidence ${(data.confidence * 100).toInt()} percent. ")
        data.sourceAppName?.let { append("Source: $it. ") }
        append("Detected ${formatRelativeTime(data.timestamp)}. ")
        if (isExpanded) append("Expanded. ") else append("Collapsed. Double tap to expand. ")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleState)
            .semantics {
                contentDescription = cardSemanticDescription
                role = Role.Button
                stateDescription = if (isExpanded) "Expanded" else "Collapsed"
            },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Severity-colored left border strip
            Box(
                modifier = Modifier
                    .width(borderWidth)
                    .fillMaxHeight()
                    .background(
                        brush = severityGradientBrush(data.severity),
                        shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 16.dp, top = 14.dp, bottom = 8.dp)
            ) {
                // Header row: icon, entity name, severity badge, expand button
                PIIEntityCardHeader(
                    data = data,
                    isExpanded = isExpanded,
                    chevronRotation = chevronRotation,
                    onToggleExpand = { isExpanded = !isExpanded }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Confidence bar
                ConfidenceBar(
                    confidence = data.confidence,
                    severityColor = borderColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Masked text display
                MaskedTextDisplay(
                    maskedText = data.maskedText,
                    entityType = data.entityType
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Source app and timestamp row
                SourceAndTimestampRow(
                    sourceAppName = data.sourceAppName,
                    sourceAppPackage = data.sourceAppPackage,
                    timestamp = data.timestamp,
                    inferenceTimeMs = data.inferenceTimeMs
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons row
                PIIEntityActionButtons(
                    data = data,
                    onAction = onAction
                )

                // Expandable detailed breakdown section
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(tween(300)) + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ),
                    exit = fadeOut(tween(200)) + shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                ) {
                    PIIEntityDetailSection(data = data)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Card header composable
// ---------------------------------------------------------------------------

@Composable
private fun PIIEntityCardHeader(
    data: PIIEntityDisplayData,
    isExpanded: Boolean,
    chevronRotation: Float,
    onToggleExpand: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Entity type icon with colored background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(severityBorderColor(data.severity).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = entityTypeIcon(data.entityType),
                contentDescription = null,
                tint = severityBorderColor(data.severity),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Entity type display name and severity label
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.entityType.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = data.severity.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = severityBorderColor(data.severity),
                fontWeight = FontWeight.Medium
            )
        }

        // Severity badge chip
        SeverityBadge(severity = data.severity)

        Spacer(modifier = Modifier.width(4.dp))

        // Expand/collapse button
        IconButton(
            onClick = onToggleExpand,
            modifier = Modifier
                .size(32.dp)
                .semantics {
                    contentDescription = if (isExpanded) "Collapse details" else "Expand details"
                    role = Role.Button
                }
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Severity badge
// ---------------------------------------------------------------------------

@Composable
fun SeverityBadge(severity: Severity) {
    val backgroundColor = severityBorderColor(severity).copy(alpha = 0.12f)
    val textColor = severityBorderColor(severity)

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        modifier = Modifier.semantics {
            contentDescription = "Severity: ${severity.displayName}"
        }
    ) {
        Text(
            text = severity.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            letterSpacing = 0.5.sp
        )
    }
}

// ---------------------------------------------------------------------------
// Confidence bar with animated fill
// ---------------------------------------------------------------------------

/**
 * An animated horizontal confidence bar that fills proportionally to the
 * detection confidence level. Includes a percentage label.
 */
@Composable
fun ConfidenceBar(
    confidence: Float,
    severityColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    showLabel: Boolean = true
) {
    val animatedConfidence by animateFloatAsState(
        targetValue = confidence,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "confidence_animation"
    )

    val barColor by animateColorAsState(
        targetValue = when {
            confidence >= 0.9f -> SeverityCritical
            confidence >= 0.7f -> SeverityHigh
            confidence >= 0.5f -> SeverityMedium
            else -> SuccessGreen
        },
        animationSpec = tween(500),
        label = "bar_color"
    )

    val confidencePercent = (animatedConfidence * 100).toInt()

    Column(modifier = modifier.fillMaxWidth()) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Confidence",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${confidencePercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .semantics {
                    contentDescription = "Confidence level: $confidencePercent percent"
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedConfidence.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(height / 2))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(barColor.copy(alpha = 0.7f), barColor)
                        )
                    )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Masked text display
// ---------------------------------------------------------------------------

/**
 * Displays the masked version of the detected PII text in a monospace
 * styled container. Shows the entity type label alongside the masked value.
 */
@Composable
fun MaskedTextDisplay(
    maskedText: String,
    entityType: EntityType,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Masked text: $maskedText"
            },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
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
                text = maskedText.ifEmpty { maskDefaultText(entityType) },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Provides a default masked text example based on entity type.
 */
private fun maskDefaultText(entityType: EntityType): String {
    return when (entityType) {
        EntityType.CREDIT_CARD -> "****-****-****-1234"
        EntityType.SSN -> "***-**-6789"
        EntityType.PASSWORD -> "**********"
        EntityType.API_KEY -> "sk-****...****xY9z"
        EntityType.EMAIL -> "j***@***.com"
        EntityType.PHONE -> "(***) ***-4567"
        EntityType.PERSON_NAME -> "J*** D**"
        EntityType.ADDRESS -> "*** M*** St, ***"
        EntityType.DATE_OF_BIRTH -> "**//**/19**"
        EntityType.MEDICAL_ID -> "MED-****-****"
        EntityType.UNKNOWN -> "***hidden***"
    }
}

// ---------------------------------------------------------------------------
// Source app and timestamp row
// ---------------------------------------------------------------------------

@Composable
private fun SourceAndTimestampRow(
    sourceAppName: String?,
    sourceAppPackage: String?,
    timestamp: Long,
    inferenceTimeMs: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append("Source: ${sourceAppName ?: sourceAppPackage ?: "Unknown"}. ")
                    append("Detected ${formatRelativeTime(timestamp)}. ")
                    append("Inference time: ${inferenceTimeMs} milliseconds.")
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Source app info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Apps,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = sourceAppName ?: sourceAppPackage ?: "Unknown source",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Timestamps
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatRelativeTime(timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatAbsoluteTime(timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Action buttons
// ---------------------------------------------------------------------------

@Composable
private fun PIIEntityActionButtons(
    data: PIIEntityDisplayData,
    onAction: (PIICardAction) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Delete confirmation snackbar-like inline warning
    AnimatedVisibility(
        visible = showDeleteConfirmation,
        enter = fadeIn(tween(200)) + expandVertically(),
        exit = fadeOut(tween(200)) + shrinkVertically()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(8.dp),
            color = AlertRed.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delete this detection?",
                    style = MaterialTheme.typography.labelSmall,
                    color = AlertRed,
                    fontWeight = FontWeight.Medium
                )
                Row {
                    TextButton(
                        onClick = { showDeleteConfirmation = false },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(
                        onClick = {
                            onAction(PIICardAction.Delete(data.id))
                            showDeleteConfirmation = false
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Delete",
                            style = MaterialTheme.typography.labelSmall,
                            color = AlertRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Action buttons row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Actions for this detection" },
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Copy masked text
        PIIActionButton(
            icon = Icons.Filled.ContentCopy,
            label = "Copy",
            contentDescription = "Copy masked text to clipboard",
            onClick = {
                clipboardManager.setText(AnnotatedString(data.maskedText))
                onAction(PIICardAction.CopyMasked(data.id))
            }
        )

        // Delete detection
        PIIActionButton(
            icon = Icons.Filled.Delete,
            label = "Delete",
            contentDescription = "Delete this detection event",
            tintColor = AlertRed.copy(alpha = 0.8f),
            onClick = { showDeleteConfirmation = true }
        )

        // Add to whitelist
        PIIActionButton(
            icon = if (data.isWhitelisted) Icons.Filled.PlaylistAddCheck else Icons.Filled.PlaylistAdd,
            label = if (data.isWhitelisted) "Listed" else "Whitelist",
            contentDescription = if (data.isWhitelisted) "Already whitelisted" else "Add source app to whitelist",
            enabled = !data.isWhitelisted,
            onClick = {
                onAction(PIICardAction.AddToWhitelist(data.id, data.sourceAppPackage))
            }
        )

        // Report false positive
        PIIActionButton(
            icon = if (data.isFalsePositive) Icons.Filled.FlagCircle else Icons.Outlined.Flag,
            label = if (data.isFalsePositive) "Reported" else "False +",
            contentDescription = if (data.isFalsePositive) "Already reported as false positive" else "Report as false positive",
            enabled = !data.isFalsePositive,
            onClick = {
                onAction(PIICardAction.ReportFalsePositive(data.id))
            }
        )
    }
}

/**
 * A small vertically-stacked action button with icon and label used
 * within the [PIIEntityCard] action row.
 */
@Composable
private fun PIIActionButton(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    tintColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
                if (!enabled) disabled()
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (enabled) tintColor else tintColor.copy(alpha = 0.38f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) tintColor else tintColor.copy(alpha = 0.38f),
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Expandable detail section
// ---------------------------------------------------------------------------

/**
 * The expandable detail section that shows token spans, character indices,
 * and validation details when the card is expanded.
 */
@Composable
private fun PIIEntityDetailSection(data: PIIEntityDisplayData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Divider(
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            text = "Detection Details",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Character indices
        DetailInfoRow(
            label = "Character Range",
            value = "${data.startCharIndex} - ${data.endCharIndex}",
            icon = Icons.Filled.TextFields
        )

        // Inference time
        DetailInfoRow(
            label = "Inference Time",
            value = "${data.inferenceTimeMs}ms",
            icon = Icons.Filled.Speed
        )

        // Action taken
        DetailInfoRow(
            label = "Action Taken",
            value = data.actionTaken.displayName,
            icon = Icons.Filled.TouchApp
        )

        // Entity ID (truncated)
        DetailInfoRow(
            label = "Detection ID",
            value = data.id.take(8) + "...",
            icon = Icons.Filled.Fingerprint
        )

        // Token spans section
        if (data.tokenSpans.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Token Spans",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            data.tokenSpans.forEach { span ->
                TokenSpanRow(span = span)
            }
        }

        // Validation details section
        if (data.validationDetails.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Validation Checks",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            data.validationDetails.forEach { detail ->
                ValidationDetailRow(detail = detail)
            }
        }

        // Context window
        if (data.contextWindow.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Context",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Text(
                    text = data.contextWindow,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

/**
 * A single row displaying a labeled info detail within the expanded section.
 */
@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics { contentDescription = "$label: $value" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * A single row displaying a token span in the expanded details section.
 */
@Composable
private fun TokenSpanRow(span: TokenSpan) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .semantics {
                contentDescription = "Token: ${span.token}, label: ${span.label}, score: ${(span.score * 100).toInt()} percent"
            },
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Token text
            Text(
                text = span.token,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Label tag
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = TrustBlue.copy(alpha = 0.1f)
            ) {
                Text(
                    text = span.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TrustBlue,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Score
            Text(
                text = "${(span.score * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Indices
            Text(
                text = "[${span.startIndex}:${span.endIndex}]",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
        }
    }
}

/**
 * A single row displaying a validation check result.
 */
@Composable
private fun ValidationDetailRow(detail: ValidationDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .semantics {
                contentDescription = "${detail.checkName}: ${if (detail.passed) "passed" else "failed"}. ${detail.description}"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (detail.passed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (detail.passed) SuccessGreen else AlertRed
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.checkName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Shimmer loading placeholder
// ---------------------------------------------------------------------------

/**
 * A shimmer placeholder card shown while PII entity data is loading.
 * Matches the structure of [PIIEntityCardContent] for a consistent layout shift.
 */
@Composable
fun PIIEntityCardShimmer(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading detection event" },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Shimmer border strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(140.dp)
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Header shimmer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confidence bar shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Text shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Source/timestamp shimmer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Compact variant for list views
// ---------------------------------------------------------------------------

/**
 * A compact, non-expandable version of [PIIEntityCard] suitable for use in
 * dense list views such as detection history.
 */
@Composable
fun PIIEntityCardCompact(
    data: PIIEntityDisplayData,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val borderColor = severityBorderColor(data.severity)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "${data.severity.displayName} ${data.entityType.displayName} detection, confidence ${(data.confidence * 100).toInt()} percent"
                role = Role.Button
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Severity strip
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(56.dp)
                    .background(borderColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = entityTypeIcon(data.entityType),
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.entityType.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = data.sourceAppName ?: "Unknown",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${(data.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = borderColor
                    )
                    Text(
                        text = formatRelativeTime(data.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Batch selection variant
// ---------------------------------------------------------------------------

/**
 * A variant of the compact card that supports selection via a checkbox,
 * used in batch operations such as bulk delete.
 */
@Composable
fun PIIEntityCardSelectable(
    data: PIIEntityDisplayData,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "selection_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = buildString {
                    if (isSelected) append("Selected. ")
                    append("${data.entityType.displayName} detection")
                }
                role = Role.Checkbox
                stateDescription = if (isSelected) "Selected" else "Not selected"
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Severity dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(severityBorderColor(data.severity))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.entityType.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${data.severity.displayName} - ${(data.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatRelativeTime(data.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Helper: Construct PIIEntityDisplayData from DetectionEvent
// ---------------------------------------------------------------------------

/**
 * Extension function to create a [PIIEntityDisplayData] from a [DetectionEvent].
 * Generates sensible defaults for display-only fields not present in the event.
 */
fun DetectionEvent.toPIIEntityDisplayData(
    rawText: String = "",
    maskedText: String = "",
    tokenSpans: List<TokenSpan> = emptyList(),
    startCharIndex: Int = 0,
    endCharIndex: Int = 0,
    validationDetails: List<ValidationDetail> = emptyList(),
    isWhitelisted: Boolean = false,
    isFalsePositive: Boolean = false,
    contextWindow: String = ""
): PIIEntityDisplayData {
    return PIIEntityDisplayData(
        id = this.id,
        entityType = this.entityType,
        severity = this.severity,
        confidence = this.confidence,
        rawText = rawText,
        maskedText = maskedText,
        sourceAppPackage = this.sourceApp,
        sourceAppName = this.sourceAppName,
        timestamp = this.timestamp,
        actionTaken = this.actionTaken,
        inferenceTimeMs = this.inferenceTimeMs,
        tokenSpans = tokenSpans,
        startCharIndex = startCharIndex,
        endCharIndex = endCharIndex,
        validationDetails = validationDetails,
        isWhitelisted = isWhitelisted,
        isFalsePositive = isFalsePositive,
        contextWindow = contextWindow
    )
}

// ---------------------------------------------------------------------------
// Time formatting utilities
// ---------------------------------------------------------------------------

/**
 * Formats a timestamp into a relative human-readable string.
 */
private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "${minutes}m ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "${hours}h ago"
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "${days}d ago"
        }
        else -> {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

/**
 * Formats a timestamp into an absolute date-time string.
 */
private fun formatAbsoluteTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ---------------------------------------------------------------------------
// Preview parameter provider
// ---------------------------------------------------------------------------

private class PIIEntityDisplayDataProvider : PreviewParameterProvider<PIIEntityDisplayData> {
    override val values: Sequence<PIIEntityDisplayData> = sequenceOf(
        PIIEntityDisplayData(
            entityType = EntityType.CREDIT_CARD,
            severity = Severity.CRITICAL,
            confidence = 0.97f,
            maskedText = "****-****-****-4242",
            sourceAppPackage = "com.example.shopping",
            sourceAppName = "Shopping App",
            timestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5),
            inferenceTimeMs = 38,
            startCharIndex = 14,
            endCharIndex = 33,
            tokenSpans = listOf(
                TokenSpan("4242", 0, 4, "B-CREDIT_CARD", 0.98f),
                TokenSpan("4242", 5, 9, "I-CREDIT_CARD", 0.96f),
                TokenSpan("4242", 10, 14, "I-CREDIT_CARD", 0.97f),
                TokenSpan("4242", 15, 19, "I-CREDIT_CARD", 0.95f)
            ),
            validationDetails = listOf(
                ValidationDetail("Luhn Check", true, "Card number passes Luhn algorithm"),
                ValidationDetail("Length Check", true, "16 digits is valid for Visa"),
                ValidationDetail("BIN Lookup", true, "BIN 424242 matches known Visa range")
            )
        ),
        PIIEntityDisplayData(
            entityType = EntityType.SSN,
            severity = Severity.CRITICAL,
            confidence = 0.92f,
            maskedText = "***-**-6789",
            sourceAppPackage = "com.example.forms",
            sourceAppName = "Forms App",
            timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(2),
            inferenceTimeMs = 45
        ),
        PIIEntityDisplayData(
            entityType = EntityType.EMAIL,
            severity = Severity.HIGH,
            confidence = 0.88f,
            maskedText = "j***@***.com",
            sourceAppPackage = "com.example.email",
            sourceAppName = "Email Client",
            timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(12),
            inferenceTimeMs = 22
        ),
        PIIEntityDisplayData(
            entityType = EntityType.PERSON_NAME,
            severity = Severity.MEDIUM,
            confidence = 0.65f,
            maskedText = "J*** D**",
            sourceAppPackage = "com.example.contacts",
            sourceAppName = "Contacts",
            timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1),
            inferenceTimeMs = 18
        )
    )
}

// ---------------------------------------------------------------------------
// Preview composables
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "PII Entity Card - Credit Card (Critical)")
@Composable
private fun PIIEntityCardCreditCardPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                confidence = 0.97f,
                maskedText = "****-****-****-4242",
                sourceAppName = "Shopping App",
                sourceAppPackage = "com.example.shop",
                timestamp = System.currentTimeMillis() - 300_000L,
                inferenceTimeMs = 38,
                tokenSpans = listOf(
                    TokenSpan("4242", 0, 4, "B-CC", 0.98f),
                    TokenSpan("4242", 5, 9, "I-CC", 0.96f)
                ),
                validationDetails = listOf(
                    ValidationDetail("Luhn Check", true, "Passes Luhn algorithm"),
                    ValidationDetail("Length", true, "Valid 16-digit card number")
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - SSN (Critical)")
@Composable
private fun PIIEntityCardSSNPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.SSN,
                severity = Severity.CRITICAL,
                confidence = 0.92f,
                maskedText = "***-**-6789",
                sourceAppName = "Tax App",
                timestamp = System.currentTimeMillis() - 7_200_000L,
                inferenceTimeMs = 45
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Password (Critical)")
@Composable
private fun PIIEntityCardPasswordPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.PASSWORD,
                severity = Severity.CRITICAL,
                confidence = 0.85f,
                maskedText = "**********",
                sourceAppName = "Browser",
                timestamp = System.currentTimeMillis() - 60_000L,
                inferenceTimeMs = 30
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Email (High)")
@Composable
private fun PIIEntityCardEmailPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                confidence = 0.88f,
                maskedText = "j***@***.com",
                sourceAppName = "Email Client",
                timestamp = System.currentTimeMillis() - 43_200_000L,
                inferenceTimeMs = 22
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Phone (High)")
@Composable
private fun PIIEntityCardPhonePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.PHONE,
                severity = Severity.HIGH,
                confidence = 0.78f,
                maskedText = "(***) ***-4567",
                sourceAppName = "Messaging",
                timestamp = System.currentTimeMillis() - 3_600_000L,
                inferenceTimeMs = 25
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Name (Medium)")
@Composable
private fun PIIEntityCardNamePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.PERSON_NAME,
                severity = Severity.MEDIUM,
                confidence = 0.65f,
                maskedText = "J*** D**",
                sourceAppName = "Contacts",
                timestamp = System.currentTimeMillis() - 86_400_000L,
                inferenceTimeMs = 18
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Expanded")
@Composable
private fun PIIEntityCardExpandedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                confidence = 0.97f,
                maskedText = "****-****-****-4242",
                sourceAppName = "Shopping App",
                timestamp = System.currentTimeMillis() - 300_000L,
                inferenceTimeMs = 38,
                startCharIndex = 14,
                endCharIndex = 33,
                tokenSpans = listOf(
                    TokenSpan("4242", 0, 4, "B-CREDIT_CARD", 0.98f),
                    TokenSpan("4242", 5, 9, "I-CREDIT_CARD", 0.96f),
                    TokenSpan("4242", 10, 14, "I-CREDIT_CARD", 0.97f),
                    TokenSpan("4242", 15, 19, "I-CREDIT_CARD", 0.95f)
                ),
                validationDetails = listOf(
                    ValidationDetail("Luhn Check", true, "Passes Luhn algorithm"),
                    ValidationDetail("Length Check", true, "16 digits is valid for Visa"),
                    ValidationDetail("BIN Lookup", true, "BIN 424242 matches known range")
                ),
                contextWindow = "...please enter your card number: ****-****-****-4242 and..."
            ),
            initiallyExpanded = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Whitelisted")
@Composable
private fun PIIEntityCardWhitelistedPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.API_KEY,
                severity = Severity.CRITICAL,
                confidence = 0.91f,
                maskedText = "sk-****...****xY9z",
                sourceAppName = "Code Editor",
                timestamp = System.currentTimeMillis() - 600_000L,
                inferenceTimeMs = 35,
                isWhitelisted = true
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - False Positive Reported")
@Composable
private fun PIIEntityCardFalsePositivePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.PERSON_NAME,
                severity = Severity.MEDIUM,
                confidence = 0.52f,
                maskedText = "A*** S***",
                sourceAppName = "Notes",
                timestamp = System.currentTimeMillis() - 1_800_000L,
                inferenceTimeMs = 15,
                isFalsePositive = true
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Shimmer Loading")
@Composable
private fun PIIEntityCardShimmerPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(),
            isLoading = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Compact Variant")
@Composable
private fun PIIEntityCardCompactPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PIIEntityCardCompact(
                data = PIIEntityDisplayData(
                    entityType = EntityType.CREDIT_CARD,
                    severity = Severity.CRITICAL,
                    confidence = 0.97f,
                    sourceAppName = "Shopping App",
                    timestamp = System.currentTimeMillis() - 300_000L
                )
            )
            PIIEntityCardCompact(
                data = PIIEntityDisplayData(
                    entityType = EntityType.EMAIL,
                    severity = Severity.HIGH,
                    confidence = 0.88f,
                    sourceAppName = "Email",
                    timestamp = System.currentTimeMillis() - 3_600_000L
                )
            )
            PIIEntityCardCompact(
                data = PIIEntityDisplayData(
                    entityType = EntityType.PERSON_NAME,
                    severity = Severity.MEDIUM,
                    confidence = 0.65f,
                    sourceAppName = "Contacts",
                    timestamp = System.currentTimeMillis() - 86_400_000L
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Selectable")
@Composable
private fun PIIEntityCardSelectablePreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        var selected1 by remember { mutableStateOf(true) }
        var selected2 by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PIIEntityCardSelectable(
                data = PIIEntityDisplayData(
                    entityType = EntityType.SSN,
                    severity = Severity.CRITICAL,
                    confidence = 0.92f,
                    sourceAppName = "Tax Forms",
                    timestamp = System.currentTimeMillis() - 7_200_000L
                ),
                isSelected = selected1,
                onSelectionChanged = { selected1 = it }
            )
            PIIEntityCardSelectable(
                data = PIIEntityDisplayData(
                    entityType = EntityType.PHONE,
                    severity = Severity.HIGH,
                    confidence = 0.78f,
                    sourceAppName = "Messaging",
                    timestamp = System.currentTimeMillis() - 3_600_000L
                ),
                isSelected = selected2,
                onSelectionChanged = { selected2 = it }
            )
        }
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Medical ID")
@Composable
private fun PIIEntityCardMedicalPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.MEDICAL_ID,
                severity = Severity.HIGH,
                confidence = 0.82f,
                maskedText = "MED-****-****",
                sourceAppName = "Health Portal",
                timestamp = System.currentTimeMillis() - 1_200_000L,
                inferenceTimeMs = 28
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Date of Birth")
@Composable
private fun PIIEntityCardDOBPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.DATE_OF_BIRTH,
                severity = Severity.MEDIUM,
                confidence = 0.72f,
                maskedText = "**//**/19**",
                sourceAppName = "Social App",
                timestamp = System.currentTimeMillis() - 5_400_000L,
                inferenceTimeMs = 20
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "PII Entity Card - Address")
@Composable
private fun PIIEntityCardAddressPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        PIIEntityCard(
            data = PIIEntityDisplayData(
                entityType = EntityType.ADDRESS,
                severity = Severity.MEDIUM,
                confidence = 0.70f,
                maskedText = "*** M*** St, S** F***",
                sourceAppName = "Maps",
                timestamp = System.currentTimeMillis() - 172_800_000L,
                inferenceTimeMs = 32
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Confidence Bar Standalone")
@Composable
private fun ConfidenceBarPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConfidenceBar(confidence = 0.97f, severityColor = SeverityCritical)
            ConfidenceBar(confidence = 0.78f, severityColor = SeverityHigh)
            ConfidenceBar(confidence = 0.55f, severityColor = SeverityMedium)
            ConfidenceBar(confidence = 0.30f, severityColor = SuccessGreen)
        }
    }
}

@Preview(showBackground = true, name = "Severity Badges")
@Composable
private fun SeverityBadgesPreview() {
    PrivacyGuardTheme(dynamicColor = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SeverityBadge(Severity.CRITICAL)
            SeverityBadge(Severity.HIGH)
            SeverityBadge(Severity.MEDIUM)
        }
    }
}
