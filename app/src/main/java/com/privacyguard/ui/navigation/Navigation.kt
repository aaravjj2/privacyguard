package com.privacyguard.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.WhitelistManager
import com.privacyguard.ui.dashboard.DashboardScreen
import com.privacyguard.ui.dashboard.DashboardViewModel
import com.privacyguard.ui.history.HistoryScreen
import com.privacyguard.ui.history.HistoryViewModel
import com.privacyguard.ui.onboarding.OnboardingFlow
import com.privacyguard.ui.settings.SettingsScreen
import com.privacyguard.ui.whitelist.WhitelistScreen
import com.privacyguard.ui.whitelist.WhitelistViewModel

// ---------------------------------------------------------------------------
// Route definitions
// ---------------------------------------------------------------------------

/**
 * Sealed class representing all navigable screens in PrivacyGuard.
 * Supports arguments and deep link URIs.
 */
sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")
    data object History : Screen("history?filter={filter}") {
        fun createRoute(filter: String? = null): String {
            return if (filter != null) "history?filter=$filter" else "history"
        }
        const val BASE_ROUTE = "history"
    }
    data object Whitelist : Screen("whitelist")
    data object Settings : Screen("settings")
    data object EventDetail : Screen("event/{eventId}") {
        fun createRoute(eventId: String): String = "event/$eventId"
        const val ARG_EVENT_ID = "eventId"
    }
}

/**
 * Items for the bottom navigation bar.
 */
enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val description: String
) {
    DASHBOARD(
        screen = Screen.Dashboard,
        label = "Dashboard",
        icon = Icons.Default.Dashboard,
        selectedIcon = Icons.Default.Dashboard,
        description = "View protection dashboard"
    ),
    HISTORY(
        screen = Screen.History,
        label = "History",
        icon = Icons.Default.History,
        selectedIcon = Icons.Default.History,
        description = "View detection history"
    ),
    WHITELIST(
        screen = Screen.Whitelist,
        label = "Trusted",
        icon = Icons.Default.VerifiedUser,
        selectedIcon = Icons.Default.VerifiedUser,
        description = "Manage trusted apps"
    ),
    SETTINGS(
        screen = Screen.Settings,
        label = "Settings",
        icon = Icons.Default.Settings,
        selectedIcon = Icons.Default.Settings,
        description = "App settings"
    )
}

// ---------------------------------------------------------------------------
// Transition animation specs
// ---------------------------------------------------------------------------

private const val TRANSITION_DURATION = 350

private fun enterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth / 4 },
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    )
}

private fun exitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    )
}

private fun popEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    )
}

private fun popExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 4 },
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    )
}

// ---------------------------------------------------------------------------
// Main Navigation Host
// ---------------------------------------------------------------------------

private const val DEEP_LINK_BASE = "privacyguard://app"

/**
 * Root navigation host for PrivacyGuard.
 * Provides animated transitions between screens, deep link support,
 * and argument passing for filtered views.
 */
@Composable
fun PrivacyGuardNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route,
    logRepository: EncryptedLogRepository? = null,
    whitelistManager: WhitelistManager? = null,
    onToggleMonitoring: (Boolean) -> Unit = {},
    onRequestAccessibility: () -> Unit = {},
    onRequestOverlayPermission: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    onOnboardingComplete: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        // ---- Onboarding ----
        composable(
            route = Screen.Onboarding.route,
            deepLinks = listOf(navDeepLink { uriPattern = "$DEEP_LINK_BASE/onboarding" }),
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            OnboardingFlow(
                onComplete = {
                    onOnboardingComplete()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRequestAccessibility = onRequestAccessibility,
                onRequestOverlayPermission = onRequestOverlayPermission,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onSkip = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---- Dashboard ----
        composable(
            route = Screen.Dashboard.route,
            deepLinks = listOf(navDeepLink { uriPattern = "$DEEP_LINK_BASE/dashboard" })
        ) {
            val viewModel: DashboardViewModel = viewModel()
            logRepository?.let { viewModel.setRepository(it) }

            DashboardScreen(
                viewModel = viewModel,
                onNavigateToHistory = {
                    navController.navigate(Screen.History.BASE_ROUTE) {
                        launchSingleTop = true
                    }
                },
                onNavigateToWhitelist = {
                    navController.navigate(Screen.Whitelist.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                },
                onToggleMonitoring = onToggleMonitoring
            )
        }

        // ---- History (with optional filter argument) ----
        composable(
            route = Screen.History.route,
            arguments = listOf(
                navArgument("filter") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "$DEEP_LINK_BASE/history" },
                navDeepLink { uriPattern = "$DEEP_LINK_BASE/history?filter={filter}" }
            )
        ) { backStackEntry ->
            val filterArg = backStackEntry.arguments?.getString("filter")
            val viewModel: HistoryViewModel = viewModel()
            logRepository?.let { viewModel.setRepository(it) }

            // Apply initial filter from navigation argument
            LaunchedEffect(filterArg) {
                filterArg?.let { filter ->
                    viewModel.setFilterFromString(filter)
                }
            }

            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---- Whitelist ----
        composable(
            route = Screen.Whitelist.route,
            deepLinks = listOf(navDeepLink { uriPattern = "$DEEP_LINK_BASE/whitelist" })
        ) {
            val viewModel: WhitelistViewModel = viewModel()

            WhitelistScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---- Settings ----
        composable(
            route = Screen.Settings.route,
            deepLinks = listOf(navDeepLink { uriPattern = "$DEEP_LINK_BASE/settings" })
        ) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---- Event Detail (with eventId argument) ----
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(
                navArgument(Screen.EventDetail.ARG_EVENT_ID) {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "$DEEP_LINK_BASE/event/{eventId}" }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString(Screen.EventDetail.ARG_EVENT_ID) ?: ""
            // Event detail screen placeholder - uses history view model to find the event
            val viewModel: HistoryViewModel = viewModel()
            logRepository?.let { viewModel.setRepository(it) }

            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom Navigation Bar
// ---------------------------------------------------------------------------

/**
 * Bottom navigation bar composable for PrivacyGuard.
 * Shows four primary destinations with animated selection state.
 */
@Composable
fun PrivacyGuardBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom bar on main screens
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.History.route,
        Screen.History.BASE_ROUTE,
        Screen.Whitelist.route,
        Screen.Settings.route
    )

    AnimatedVisibility(
        visible = showBottomBar,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(modifier = modifier) {
            BottomNavItem.entries.forEach { item ->
                val isSelected = isRouteSelected(currentRoute, item)

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        val targetRoute = when (item.screen) {
                            is Screen.History -> Screen.History.BASE_ROUTE
                            else -> item.screen.route
                        }
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                            contentDescription = null
                        )
                    },
                    label = { Text(item.label) },
                    modifier = Modifier.semantics {
                        contentDescription = "${item.description}${if (isSelected) ", selected" else ""}"
                    }
                )
            }
        }
    }
}

/**
 * Check if a given route corresponds to a bottom navigation item.
 */
private fun isRouteSelected(currentRoute: String?, item: BottomNavItem): Boolean {
    return when (item) {
        BottomNavItem.HISTORY -> currentRoute?.startsWith("history") == true
        else -> currentRoute == item.screen.route
    }
}

// ---------------------------------------------------------------------------
// Navigation helper extensions
// ---------------------------------------------------------------------------

/**
 * Navigate to the history screen with an optional severity filter.
 */
fun NavHostController.navigateToHistory(filter: String? = null) {
    val route = Screen.History.createRoute(filter)
    navigate(route) {
        launchSingleTop = true
    }
}

/**
 * Navigate to the event detail screen for a specific event.
 */
fun NavHostController.navigateToEventDetail(eventId: String) {
    navigate(Screen.EventDetail.createRoute(eventId)) {
        launchSingleTop = true
    }
}

/**
 * Navigate to the dashboard, clearing the back stack.
 */
fun NavHostController.navigateToDashboard() {
    navigate(Screen.Dashboard.route) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
