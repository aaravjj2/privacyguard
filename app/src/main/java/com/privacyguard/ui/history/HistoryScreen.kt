package com.privacyguard.ui.history

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.UserAction
import com.privacyguard.ml.EntityType
import com.privacyguard.ml.Severity
import com.privacyguard.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * History screen displaying all PII detection events with filtering,
 * searching, swipe-to-delete, and pagination support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf<String?>(null) }

    // Clear-all confirmation dialog
    if (showClearDialog) {
        ClearAllConfirmationDialog(
            onConfirm = {
                viewModel.deleteAllEvents()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }

    // Single event delete confirmation
    showDeleteConfirmation?.let { eventId ->
        DeleteSingleEventDialog(
            onConfirm = {
                viewModel.deleteEvent(eventId)
                showDeleteConfirmation = null
            },
            onDismiss = { showDeleteConfirmation = null }
        )
    }

    Scaffold(
        topBar = {
            HistoryTopBar(
                eventCount = uiState.filteredEvents.size,
                totalCount = uiState.events.size,
                isEmpty = uiState.isEmpty,
                onNavigateBack = onNavigateBack,
                onClearAll = { showClearDialog = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            HistorySearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                resultCount = uiState.filteredEvents.size,
                isActive = uiState.searchQuery.isNotEmpty()
            )

            // Filter chips row
            FilterChipsRow(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) },
                eventCounts = uiState.filterCounts
            )

            // Summary banner when filtering
            AnimatedVisibility(
                visible = uiState.selectedFilter != SeverityFilter.ALL || uiState.searchQuery.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                FilterActiveBanner(
                    resultCount = uiState.filteredEvents.size,
                    totalCount = uiState.events.size,
                    filterLabel = uiState.selectedFilter.label,
                    searchQuery = uiState.searchQuery,
                    onClearFilters = {
                        viewModel.setFilter(SeverityFilter.ALL)
                        viewModel.setSearchQuery("")
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main content area
            when {
                uiState.isLoading -> {
                    HistoryLoadingState()
                }
                uiState.isEmpty -> {
                    EmptyHistoryContent()
                }
                uiState.filteredEvents.isEmpty() -> {
                    NoResultsContent(
                        searchQuery = uiState.searchQuery,
                        filterLabel = uiState.selectedFilter.label
                    )
                }
                else -> {
                    HistoryEventList(
                        events = uiState.filteredEvents,
                        pageSize = uiState.pageSize,
                        currentPage = uiState.currentPage,
                        hasMorePages = uiState.hasMorePages,
                        onLoadMore = { viewModel.loadNextPage() },
                        onDeleteEvent = { eventId -> viewModel.deleteEvent(eventId) }
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Top Bar
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    eventCount: Int,
    totalCount: Int,
    isEmpty: Boolean,
    onNavigateBack: () -> Unit,
    onClearAll: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text("Detection History")
                if (!isEmpty) {
                    Text(
                        "$totalCount total events",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.semantics {
                    contentDescription = "Navigate back"
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (!isEmpty) {
                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier.semantics {
                        contentDescription = "Clear all detection history"
                    }
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
                }
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Search Bar
// ---------------------------------------------------------------------------

@Composable
private fun HistorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    resultCount: Int,
    isActive: Boolean
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = if (isActive) "Search field, $resultCount results" else "Search by type or app"
            },
        placeholder = { Text("Search by type, app, or action...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

// ---------------------------------------------------------------------------
// Filter Chips Row
// ---------------------------------------------------------------------------

@Composable
private fun FilterChipsRow(
    selectedFilter: SeverityFilter,
    onFilterSelected: (SeverityFilter) -> Unit,
    eventCounts: Map<SeverityFilter, Int>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SeverityFilter.entries.toList()) { filter ->
            val count = eventCounts[filter] ?: 0
            val isSelected = selectedFilter == filter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(filter.label)
                        if (count > 0 && filter != SeverityFilter.ALL) {
                            Spacer(Modifier.width(4.dp))
                            Badge(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    when (filter) {
                        SeverityFilter.CRITICAL -> {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SeverityCritical)
                                )
                            }
                        }
                        SeverityFilter.HIGH -> {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SeverityHigh)
                                )
                            }
                        }
                        SeverityFilter.MEDIUM -> {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SeverityMedium)
                                )
                            }
                        }
                        SeverityFilter.ALL -> null
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = "${filter.label} filter${if (isSelected) ", selected" else ""}"
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Filter Active Banner
// ---------------------------------------------------------------------------

@Composable
private fun FilterActiveBanner(
    resultCount: Int,
    totalCount: Int,
    filterLabel: String,
    searchQuery: String,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = TrustBlue.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                buildString {
                    append("Showing $resultCount of $totalCount")
                    if (filterLabel != "All") append(" ($filterLabel)")
                    if (searchQuery.isNotEmpty()) append(" matching \"$searchQuery\"")
                },
                style = MaterialTheme.typography.bodySmall,
                color = TrustBlue,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onClearFilters,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text("Clear", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Event List with Pagination and Swipe-to-Delete
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryEventList(
    events: List<DetectionEvent>,
    pageSize: Int,
    currentPage: Int,
    hasMorePages: Boolean,
    onLoadMore: () -> Unit,
    onDeleteEvent: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val displayedEvents = events.take((currentPage + 1) * pageSize)

    // Detect when user scrolls near the end for pagination
    val reachedEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= displayedEvents.size - 3
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd && hasMorePages) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            displayedEvents,
            key = { it.id }
        ) { event ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteEvent(event.id)
                        true
                    } else false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    SwipeDeleteBackground(dismissState)
                },
                enableDismissFromStartToEnd = false,
                content = {
                    HistoryEventCard(event)
                }
            )
        }

        // Loading indicator for pagination
        if (hasMorePages) {
            item(key = "loading_more") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // End of list indicator
        if (!hasMorePages && displayedEvents.isNotEmpty()) {
            item(key = "end_indicator") {
                EndOfListIndicator(totalCount = events.size)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Swipe Delete Background
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeDeleteBackground(dismissState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
            AlertRed else Color.Transparent,
        label = "swipe_bg_color"
    )
    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f,
        label = "swipe_icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete event",
                tint = Color.White,
                modifier = Modifier.size((24 * scale).dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Delete",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

// ---------------------------------------------------------------------------
// History Event Card
// ---------------------------------------------------------------------------

@Composable
fun HistoryEventCard(event: DetectionEvent) {
    val severityColor = when (event.severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${event.severity.displayName} ${event.entityType.displayName} detected from ${event.sourceAppName ?: event.sourceApp ?: "unknown"}"
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Severity indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(severityColor)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    event.severity.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = severityColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Spacer(Modifier.width(12.dp))

            // Event details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.entityType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
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
                if (event.inferenceTimeMs > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Analyzed in ${event.inferenceTimeMs}ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Timestamp and confidence
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatRelativeTimestamp(event.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(severityColor.copy(alpha = 0.5f))
                    )
                    Spacer(Modifier.width(4.dp))
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
}

// ---------------------------------------------------------------------------
// Empty / No Results / Loading States
// ---------------------------------------------------------------------------

@Composable
fun EmptyHistoryContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "No detection history" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = ProtectionActive
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "No Detection History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "PrivacyGuard hasn't detected any PII yet.\nYour data is safe and protected.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Icon(
                Icons.Default.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = ProtectionActive.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun NoResultsContent(searchQuery: String = "", filterLabel: String = "All") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "No matching results" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "No matching results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                buildString {
                    if (searchQuery.isNotEmpty()) {
                        append("No events match \"$searchQuery\"")
                        if (filterLabel != "All") append(" with $filterLabel severity")
                    } else {
                        append("No $filterLabel severity events found")
                    }
                    append(". Try adjusting your filters.")
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Loading detection history...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// End-of-list Indicator
// ---------------------------------------------------------------------------

@Composable
private fun EndOfListIndicator(totalCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(modifier = Modifier.fillMaxWidth(0.4f))
        Spacer(Modifier.height(8.dp))
        Text(
            "$totalCount events total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Dialogs
// ---------------------------------------------------------------------------

@Composable
private fun ClearAllConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.DeleteForever,
                contentDescription = null,
                tint = AlertRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Clear All History") },
        text = {
            Text(
                "This will permanently delete all detection history. This action cannot be undone.\n\nDetection statistics will be reset to zero."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Clear All", color = AlertRed, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun DeleteSingleEventDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Event") },
        text = { Text("Are you sure you want to delete this detection event?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = AlertRed)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ---------------------------------------------------------------------------
// Utility functions
// ---------------------------------------------------------------------------

private fun formatRelativeTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
            "${mins}m ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "${hours}h ago"
        }
        diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "${days}d ago"
        }
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

// ---------------------------------------------------------------------------
// Preview Composables
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "History Event Card - Critical")
@Composable
private fun HistoryEventCardCriticalPreview() {
    PrivacyGuardTheme {
        HistoryEventCard(
            event = DetectionEvent(
                entityType = EntityType.CREDIT_CARD,
                severity = Severity.CRITICAL,
                sourceApp = "com.example.browser",
                sourceAppName = "Chrome Browser",
                actionTaken = UserAction.CLIPBOARD_CLEARED,
                confidence = 0.97f,
                inferenceTimeMs = 38L
            )
        )
    }
}

@Preview(showBackground = true, name = "History Event Card - High")
@Composable
private fun HistoryEventCardHighPreview() {
    PrivacyGuardTheme {
        HistoryEventCard(
            event = DetectionEvent(
                entityType = EntityType.EMAIL,
                severity = Severity.HIGH,
                sourceApp = "com.example.mail",
                sourceAppName = "Mail App",
                actionTaken = UserAction.DISMISSED,
                confidence = 0.82f,
                inferenceTimeMs = 52L
            )
        )
    }
}

@Preview(showBackground = true, name = "History Event Card - Medium")
@Composable
private fun HistoryEventCardMediumPreview() {
    PrivacyGuardTheme {
        HistoryEventCard(
            event = DetectionEvent(
                entityType = EntityType.PERSON_NAME,
                severity = Severity.MEDIUM,
                sourceApp = "com.example.chat",
                sourceAppName = "Chat App",
                actionTaken = UserAction.AUTO_DISMISSED,
                confidence = 0.65f,
                inferenceTimeMs = 45L
            )
        )
    }
}

@Preview(showBackground = true, name = "Empty History")
@Composable
private fun EmptyHistoryContentPreview() {
    PrivacyGuardTheme {
        EmptyHistoryContent()
    }
}

@Preview(showBackground = true, name = "No Results")
@Composable
private fun NoResultsContentPreview() {
    PrivacyGuardTheme {
        NoResultsContent(searchQuery = "passport", filterLabel = "Critical")
    }
}
