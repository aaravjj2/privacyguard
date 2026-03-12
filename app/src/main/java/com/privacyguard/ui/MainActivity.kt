package com.privacyguard.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.privacyguard.BuildConfig
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.data.KeystoreManager
import com.privacyguard.data.WhitelistManager
import com.privacyguard.service.ClipboardMonitorService
import com.privacyguard.service.ModelLifecycleManager
import com.privacyguard.ui.navigation.PrivacyGuardNavHost
import com.privacyguard.ui.navigation.Screen
import com.privacyguard.ui.theme.PrivacyGuardTheme

class MainActivity : ComponentActivity() {

    private lateinit var keystoreManager: KeystoreManager
    private lateinit var logRepository: EncryptedLogRepository
    private lateinit var whitelistManager: WhitelistManager
    private lateinit var modelLifecycleManager: ModelLifecycleManager

    companion object {
        private const val PREFS_KEY = "privacyguard_prefs"
        private const val LOG_PREFS_KEY = "privacyguard_log"
        private const val WHITELIST_PREFS_KEY = "privacyguard_whitelist"
        private const val ONBOARDING_COMPLETE_KEY = "onboarding_complete"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeComponents()

        val isOnboardingComplete = getSharedPreferences(PREFS_KEY, MODE_PRIVATE)
            .getBoolean(ONBOARDING_COMPLETE_KEY, false)

        setContent {
            PrivacyGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    PrivacyGuardNavHost(
                        navController = navController,
                        startDestination = if (isOnboardingComplete) Screen.Dashboard.route
                                          else Screen.Onboarding.route,
                        logRepository = logRepository,
                        whitelistManager = whitelistManager,
                        onToggleMonitoring = { active ->
                            if (active) {
                                ClipboardMonitorService.start(this)
                            } else {
                                ClipboardMonitorService.stop(this)
                            }
                        },
                        onRequestAccessibility = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onRequestOverlayPermission = {
                            startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                            )
                        },
                        onRequestNotificationPermission = {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                requestPermissions(
                                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                    1001
                                )
                            }
                        },
                        onOnboardingComplete = {
                            getSharedPreferences(PREFS_KEY, MODE_PRIVATE)
                                .edit()
                                .putBoolean(ONBOARDING_COMPLETE_KEY, true)
                                .apply()
                        }
                    )
                }
            }
        }
    }

    private fun initializeComponents() {
        keystoreManager = KeystoreManager(this)

        val logPrefs = try {
            keystoreManager.createEncryptedPreferences(LOG_PREFS_KEY)
        } catch (e: Exception) {
            getSharedPreferences(LOG_PREFS_KEY, MODE_PRIVATE)
        }

        val whitelistPrefs = try {
            keystoreManager.createEncryptedPreferences(WHITELIST_PREFS_KEY)
        } catch (e: Exception) {
            getSharedPreferences(WHITELIST_PREFS_KEY, MODE_PRIVATE)
        }

        logRepository = EncryptedLogRepository(logPrefs)
        whitelistManager = WhitelistManager(whitelistPrefs)

        modelLifecycleManager = ModelLifecycleManager(
            context = this,
            personalKey = BuildConfig.MELANGE_PERSONAL_KEY
        )
        lifecycle.addObserver(modelLifecycleManager)
    }
}
