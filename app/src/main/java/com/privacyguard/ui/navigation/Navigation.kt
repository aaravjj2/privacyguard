package com.privacyguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")
    data object History : Screen("history")
    data object Whitelist : Screen("whitelist")
    data object Settings : Screen("settings")
}

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
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingFlow(
                onComplete = {
                    onOnboardingComplete()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onRequestAccessibility = onRequestAccessibility,
                onRequestOverlayPermission = onRequestOverlayPermission,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onSkip = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel()
            logRepository?.let { viewModel.setRepository(it) }

            DashboardScreen(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToWhitelist = { navController.navigate(Screen.Whitelist.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onToggleMonitoring = onToggleMonitoring
            )
        }

        composable(Screen.History.route) {
            val viewModel: HistoryViewModel = viewModel()
            logRepository?.let { viewModel.setRepository(it) }

            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Whitelist.route) {
            val viewModel: WhitelistViewModel = viewModel()

            WhitelistScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
