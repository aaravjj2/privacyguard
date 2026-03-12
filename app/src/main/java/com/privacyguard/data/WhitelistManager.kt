package com.privacyguard.data

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the whitelist of trusted apps that are excluded from PII monitoring.
 * Uses an in-memory HashSet for O(1) lookup performance.
 */
class WhitelistManager(private val prefs: SharedPreferences) {

    companion object {
        private const val WHITELIST_KEY = "whitelisted_apps"
        private const val DEFAULTS_POPULATED_KEY = "defaults_populated"

        val DEFAULT_TRUSTED_APPS = setOf(
            "com.agilebits.onepassword",      // 1Password
            "com.bitwarden.mobile",           // Bitwarden
            "org.keepassdroid",               // KeePassDroid
            "com.lastpass.lpandroid",         // LastPass
            "com.x8bit.bitwarden",           // Bitwarden (alt)
            "keepass2android.keepass2android"  // Keepass2Android
        )

        val SUGGESTED_APP_NAMES = mapOf(
            "com.agilebits.onepassword" to "1Password",
            "com.bitwarden.mobile" to "Bitwarden",
            "org.keepassdroid" to "KeePassDroid",
            "com.lastpass.lpandroid" to "LastPass",
            "com.x8bit.bitwarden" to "Bitwarden",
            "keepass2android.keepass2android" to "Keepass2Android"
        )
    }

    private val whitelistedAppsSet = mutableSetOf<String>()
    private val _whitelistedApps = MutableStateFlow<Set<String>>(emptySet())

    init {
        loadFromPrefs()
        if (!prefs.getBoolean(DEFAULTS_POPULATED_KEY, false)) {
            prePopulateDefaults()
        }
    }

    /**
     * Reactive stream of whitelisted app package names.
     */
    fun getWhitelistedAppsFlow(): Flow<Set<String>> = _whitelistedApps.asStateFlow()

    /**
     * Check if an app is whitelisted. O(1) lookup.
     */
    fun isWhitelisted(packageName: String): Boolean {
        return whitelistedAppsSet.contains(packageName)
    }

    /**
     * Add an app to the whitelist.
     */
    fun addToWhitelist(packageName: String) {
        whitelistedAppsSet.add(packageName)
        saveToPrefs()
        _whitelistedApps.value = whitelistedAppsSet.toSet()
    }

    /**
     * Remove an app from the whitelist.
     */
    fun removeFromWhitelist(packageName: String) {
        whitelistedAppsSet.remove(packageName)
        saveToPrefs()
        _whitelistedApps.value = whitelistedAppsSet.toSet()
    }

    /**
     * Toggle an app's whitelist status.
     */
    fun toggleWhitelist(packageName: String): Boolean {
        return if (isWhitelisted(packageName)) {
            removeFromWhitelist(packageName)
            false
        } else {
            addToWhitelist(packageName)
            true
        }
    }

    /**
     * Get all whitelisted apps as a set.
     */
    fun getAllWhitelistedApps(): Set<String> = whitelistedAppsSet.toSet()

    /**
     * Get count of whitelisted apps.
     */
    fun getWhitelistCount(): Int = whitelistedAppsSet.size

    /**
     * Pre-populate with known password manager apps.
     */
    fun prePopulateDefaults() {
        DEFAULT_TRUSTED_APPS.forEach { whitelistedAppsSet.add(it) }
        saveToPrefs()
        _whitelistedApps.value = whitelistedAppsSet.toSet()
        prefs.edit().putBoolean(DEFAULTS_POPULATED_KEY, true).apply()
    }

    /**
     * Clear all whitelisted apps.
     */
    fun clearAll() {
        whitelistedAppsSet.clear()
        saveToPrefs()
        _whitelistedApps.value = emptySet()
    }

    private fun loadFromPrefs() {
        val stored = prefs.getStringSet(WHITELIST_KEY, emptySet()) ?: emptySet()
        whitelistedAppsSet.clear()
        whitelistedAppsSet.addAll(stored)
        _whitelistedApps.value = whitelistedAppsSet.toSet()
    }

    private fun saveToPrefs() {
        prefs.edit()
            .putStringSet(WHITELIST_KEY, whitelistedAppsSet.toSet())
            .apply()
    }
}
