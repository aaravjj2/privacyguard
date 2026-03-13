@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.privacyguard.ui.whitelist

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.privacyguard.ui.theme.*

/**
 * Screen for managing the whitelist of trusted applications that are
 * excluded from PII monitoring. Includes search, suggested apps,
 * loading and empty states, and toggle controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhitelistScreen(
    viewModel: WhitelistViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Clear all whitelist dialog
    if (showClearAllDialog) {
        ClearWhitelistDialog(
            onConfirm = {
                viewModel.clearAllWhitelisted()
                showClearAllDialog = false
            },
            onDismiss = { showClearAllDialog = false }
        )
    }

    // Info dialog explaining whitelisting
    if (showInfoDialog) {
        WhitelistInfoDialog(onDismiss = { showInfoDialog = false })
    }

    Scaffold(
        topBar = {
            WhitelistTopBar(
                whitelistCount = uiState.whitelistCount,
                totalApps = uiState.apps.size,
                onNavigateBack = onNavigateBack,
                onShowInfo = { showInfoDialog = true },
                onClearAll = { showClearAllDialog = true },
                hasWhitelistedApps = uiState.whitelistCount > 0
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            WhitelistSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                resultCount = uiState.filteredApps.size,
                isActive = uiState.searchQuery.isNotEmpty()
            )

            // Info banner
            WhitelistInfoBanner()

            // Whitelist count summary
            AnimatedVisibility(
                visible = uiState.whitelistCount > 0 && !uiState.isLoading,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                WhitelistCountBanner(
                    whitelistCount = uiState.whitelistCount,
                    totalApps = uiState.apps.size
                )
            }

            // Main content
            when {
                uiState.isLoading -> {
                    WhitelistLoadingState()
                }
                uiState.errorMessage != null -> {
                    WhitelistErrorState(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.retryLoading() }
                    )
                }
                uiState.filteredApps.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                    WhitelistNoSearchResults(query = uiState.searchQuery)
                }
                uiState.apps.isEmpty() -> {
                    WhitelistEmptyState()
                }
                else -> {
                    WhitelistAppList(
                        filteredApps = uiState.filteredApps,
                        onToggle = { packageName -> viewModel.toggleApp(packageName) }
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
private fun WhitelistTopBar(
    whitelistCount: Int,
    totalApps: Int,
    onNavigateBack: () -> Unit,
    onShowInfo: () -> Unit,
    onClearAll: () -> Unit,
    hasWhitelistedApps: Boolean
) {
    TopAppBar(
        title = {
            Column {
                Text("Trusted Apps")
                AnimatedContent(
                    targetState = whitelistCount,
                    transitionSpec = {
                        fadeIn(tween(200)) + slideInVertically { -it / 2 } togetherWith
                                fadeOut(tween(200)) + slideOutVertically { it / 2 }
                    },
                    label = "whitelist_count"
                ) { count ->
                    Text(
                        "$count of $totalApps apps whitelisted",
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
            IconButton(
                onClick = onShowInfo,
                modifier = Modifier.semantics {
                    contentDescription = "About whitelisting"
                }
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = "Help")
            }
            if (hasWhitelistedApps) {
                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier.semantics {
                        contentDescription = "Remove all apps from whitelist"
                    }
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear Whitelist")
                }
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Search Bar
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistSearchBar(
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
                contentDescription = if (isActive) "Search apps, $resultCount results" else "Search installed apps"
            },
        placeholder = { Text("Search apps by name or package...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
// Info Banner
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistInfoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = TrustBlue.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Trusted apps will not trigger PII alerts. Password managers and secure apps are suggested by default. You can toggle any app on or off.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Whitelist Count Banner
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistCountBanner(whitelistCount: Int, totalApps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = ProtectionActive,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "$whitelistCount trusted",
                style = MaterialTheme.typography.labelMedium,
                color = ProtectionActive,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            "$totalApps apps total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// App List
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistAppList(
    filteredApps: List<AppInfo>,
    onToggle: (String) -> Unit
) {
    val suggested = filteredApps.filter { it.isSuggested }
    val whitelisted = filteredApps.filter { it.isWhitelisted && !it.isSuggested }
    val others = filteredApps.filter { !it.isSuggested && !it.isWhitelisted }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Suggested Trusted Apps Section
        if (suggested.isNotEmpty()) {
            item(key = "header_suggested") {
                SectionHeader(
                    title = "Suggested Trusted Apps",
                    subtitle = "Password managers and secure apps",
                    icon = Icons.Default.Star,
                    iconTint = AlertYellow
                )
            }
            items(suggested, key = { "s_${it.packageName}" }) { app ->
                AppListItem(
                    app = app,
                    onToggle = { onToggle(app.packageName) },
                    isSuggested = true
                )
            }
            item(key = "divider_suggested") {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }

        // Currently Whitelisted Apps Section
        if (whitelisted.isNotEmpty()) {
            item(key = "header_whitelisted") {
                SectionHeader(
                    title = "Whitelisted Apps",
                    subtitle = "${whitelisted.size} apps trusted",
                    icon = Icons.Default.VerifiedUser,
                    iconTint = ProtectionActive
                )
            }
            items(whitelisted, key = { "w_${it.packageName}" }) { app ->
                AppListItem(
                    app = app,
                    onToggle = { onToggle(app.packageName) },
                    isSuggested = false
                )
            }
            item(key = "divider_whitelisted") {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }

        // All Other Apps
        if (others.isNotEmpty()) {
            item(key = "header_all") {
                SectionHeader(
                    title = "All Apps",
                    subtitle = "${others.size} apps installed",
                    icon = Icons.Default.Apps,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(others, key = { "a_${it.packageName}" }) { app ->
                AppListItem(
                    app = app,
                    onToggle = { onToggle(app.packageName) },
                    isSuggested = false
                )
            }
        }

        // Bottom padding
        item(key = "bottom_spacer") {
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Section Header
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// App List Item
// ---------------------------------------------------------------------------

@Composable
fun AppListItem(
    app: AppInfo,
    onToggle: () -> Unit,
    isSuggested: Boolean = false
) {
    val iconTint by animateColorAsState(
        targetValue = if (app.isWhitelisted) ProtectionActive
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "app_icon_tint"
    )

    val cardColor by animateColorAsState(
        targetValue = if (app.isWhitelisted)
            ProtectionActive.copy(alpha = 0.04f)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "app_card_bg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${app.appName}, ${if (app.isWhitelisted) "trusted" else "not trusted"}"
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (app.isWhitelisted) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            }
            Spacer(Modifier.width(12.dp))

            // App name and package
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        app.appName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSuggested) {
                        Spacer(Modifier.width(6.dp))
                        Badge(
                            containerColor = AlertYellow.copy(alpha = 0.2f),
                            contentColor = AlertYellow
                        ) {
                            Text(
                                "Suggested",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // Trust toggle
            Switch(
                checked = app.isWhitelisted,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = ProtectionActive,
                    checkedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.semantics {
                    contentDescription = if (app.isWhitelisted) "Remove ${app.appName} from trusted apps"
                    else "Add ${app.appName} to trusted apps"
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Loading State
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading installed apps" },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Loading installed apps...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Scanning for installed applications",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Error State
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.size(48.dp),
                    tint = AlertRed
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Failed to load apps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
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
// Empty State
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "No installed apps found" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.PhoneAndroid,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "No Apps Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "No user-installed apps were found on this device. System apps are excluded from the whitelist.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// No Search Results
// ---------------------------------------------------------------------------

@Composable
private fun WhitelistNoSearchResults(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "No apps match search query $query" },
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
            Spacer(Modifier.height(12.dp))
            Text(
                "No apps match \"$query\"",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Try a different search term or check the app name.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Dialogs
// ---------------------------------------------------------------------------

@Composable
private fun ClearWhitelistDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.ClearAll,
                contentDescription = null,
                tint = AlertRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Remove All Trusted Apps") },
        text = {
            Text(
                "This will remove all apps from the whitelist, including password managers. All apps will be monitored for PII.\n\nYou can re-add apps at any time."
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
private fun WhitelistInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("About Trusted Apps") },
        text = {
            Column {
                Text(
                    "Trusted (whitelisted) apps are excluded from PrivacyGuard's PII monitoring.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                InfoBulletPoint("Password managers are suggested by default since they handle sensitive data intentionally.")
                InfoBulletPoint("Toggling an app ON adds it to the whitelist and stops monitoring that app.")
                InfoBulletPoint("Toggling an app OFF resumes monitoring for that app.")
                InfoBulletPoint("System apps are not shown since they are excluded from monitoring.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        }
    )
}

@Composable
private fun InfoBulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Preview Composables
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "App List Item - Whitelisted")
@Composable
private fun AppListItemWhitelistedPreview() {
    PrivacyGuardTheme {
        AppListItem(
            app = AppInfo(
                packageName = "com.bitwarden.mobile",
                appName = "Bitwarden",
                isWhitelisted = true,
                isSuggested = true
            ),
            onToggle = {},
            isSuggested = true
        )
    }
}

@Preview(showBackground = true, name = "App List Item - Not Whitelisted")
@Composable
private fun AppListItemNotWhitelistedPreview() {
    PrivacyGuardTheme {
        AppListItem(
            app = AppInfo(
                packageName = "com.example.browser",
                appName = "Example Browser",
                isWhitelisted = false,
                isSuggested = false
            ),
            onToggle = {},
            isSuggested = false
        )
    }
}

@Preview(showBackground = true, name = "Whitelist Empty State")
@Composable
private fun WhitelistEmptyStatePreview() {
    PrivacyGuardTheme {
        WhitelistEmptyState()
    }
}

@Preview(showBackground = true, name = "Whitelist Loading State")
@Composable
private fun WhitelistLoadingStatePreview() {
    PrivacyGuardTheme {
        WhitelistLoadingState()
    }
}

@Preview(showBackground = true, name = "Whitelist Error State")
@Composable
private fun WhitelistErrorStatePreview() {
    PrivacyGuardTheme {
        WhitelistErrorState(
            message = "Could not retrieve installed apps. Please check permissions.",
            onRetry = {}
        )
    }
}
