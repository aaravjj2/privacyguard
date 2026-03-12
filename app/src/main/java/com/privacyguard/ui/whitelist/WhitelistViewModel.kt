package com.privacyguard.ui.whitelist

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.WhitelistManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Data class representing a single installed application
 * with its whitelist and suggestion status.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val isWhitelisted: Boolean,
    val isSuggested: Boolean = false
)

/**
 * UI state for the whitelist management screen.
 * Supports search, loading, error, and empty states.
 */
data class WhitelistUiState(
    // App lists
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),

    // Search
    val searchQuery: String = "",

    // Stats
    val whitelistCount: Int = 0,
    val suggestedCount: Int = 0,

    // UI flags
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /** Whether any apps are currently whitelisted. */
    val hasWhitelistedApps: Boolean get() = whitelistCount > 0

    /** Whether search is actively being used. */
    val isSearching: Boolean get() = searchQuery.isNotEmpty()

    /** Count of whitelisted apps that are also suggested. */
    val suggestedWhitelistedCount: Int
        get() = apps.count { it.isSuggested && it.isWhitelisted }

    /** Count of user-added (non-suggested) whitelisted apps. */
    val userWhitelistedCount: Int
        get() = apps.count { it.isWhitelisted && !it.isSuggested }
}

/**
 * ViewModel for the whitelist management screen.
 *
 * Responsibilities:
 * - Loading installed applications from the PackageManager
 * - Search filtering with debounce
 * - Toggle (add/remove) app whitelist status
 * - Managing suggested trusted apps (password managers)
 * - Clear all whitelisted apps
 * - Observing whitelist changes from the WhitelistManager
 */
class WhitelistViewModel : ViewModel() {

    companion object {
        /** Debounce delay for search input (milliseconds). */
        private const val SEARCH_DEBOUNCE_MS = 250L
    }

    private var whitelistManager: WhitelistManager? = null
    private var packageManager: PackageManager? = null

    private val _uiState = MutableStateFlow(WhitelistUiState())
    val uiState: StateFlow<WhitelistUiState> = _uiState.asStateFlow()

    // Search debounce job
    private var searchDebounceJob: Job? = null

    // ---------------------------------------------------------------------------
    // Public API - Initialization
    // ---------------------------------------------------------------------------

    /**
     * Initialize the view model with the required dependencies.
     * Triggers initial loading of installed apps and whitelist state.
     */
    fun initialize(manager: WhitelistManager, pm: PackageManager) {
        if (whitelistManager == manager && packageManager == pm) return
        whitelistManager = manager
        packageManager = pm
        loadApps()
        observeWhitelist()
    }

    // ---------------------------------------------------------------------------
    // Public API - App Toggle
    // ---------------------------------------------------------------------------

    /**
     * Toggle an app's whitelist status.
     * Uses optimistic update for instant UI feedback, then persists.
     */
    fun toggleApp(packageName: String) {
        val wm = whitelistManager ?: return

        // Optimistic update: toggle in local state immediately
        _uiState.update { state ->
            val updatedApps = state.apps.map { app ->
                if (app.packageName == packageName) {
                    app.copy(isWhitelisted = !app.isWhitelisted)
                } else app
            }
            val sortedApps = sortAppList(updatedApps)
            val newWhitelistCount = sortedApps.count { it.isWhitelisted }

            state.copy(
                apps = sortedApps,
                whitelistCount = newWhitelistCount
            )
        }
        applyFilter()

        // Persist the change
        viewModelScope.launch {
            try {
                wm.toggleWhitelist(packageName)
            } catch (e: Exception) {
                // Revert optimistic update on failure
                refreshAppList()
                _uiState.update {
                    it.copy(errorMessage = "Failed to update whitelist: ${e.message}")
                }
            }
        }
    }

    /**
     * Add multiple apps to the whitelist at once (e.g., all suggested apps).
     */
    fun whitelistAllSuggested() {
        val wm = whitelistManager ?: return
        viewModelScope.launch {
            val suggested = _uiState.value.apps.filter { it.isSuggested && !it.isWhitelisted }
            suggested.forEach { app ->
                try {
                    wm.addToWhitelist(app.packageName)
                } catch (_: Exception) { }
            }
            refreshAppList()
        }
    }

    // ---------------------------------------------------------------------------
    // Public API - Search
    // ---------------------------------------------------------------------------

    /**
     * Set the search query with debounced filtering.
     */
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            applyFilter()
        }
    }

    // ---------------------------------------------------------------------------
    // Public API - Bulk Operations
    // ---------------------------------------------------------------------------

    /**
     * Remove all apps from the whitelist.
     */
    fun clearAllWhitelisted() {
        viewModelScope.launch {
            try {
                whitelistManager?.clearAll()

                // Update local state
                _uiState.update { state ->
                    val updatedApps = state.apps.map { it.copy(isWhitelisted = false) }
                    state.copy(
                        apps = sortAppList(updatedApps),
                        whitelistCount = 0
                    )
                }
                applyFilter()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to clear whitelist: ${e.message}")
                }
            }
        }
    }

    /**
     * Re-populate with default trusted apps (password managers).
     */
    fun restoreDefaults() {
        viewModelScope.launch {
            try {
                whitelistManager?.prePopulateDefaults()
                refreshAppList()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to restore defaults: ${e.message}")
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Public API - Error Handling
    // ---------------------------------------------------------------------------

    /**
     * Dismiss the current error message.
     */
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Retry loading apps after an error.
     */
    fun retryLoading() {
        _uiState.update { it.copy(errorMessage = null) }
        loadApps()
    }

    // ---------------------------------------------------------------------------
    // Internal - Loading Apps
    // ---------------------------------------------------------------------------

    /**
     * Load all installed (non-system) applications from the PackageManager.
     * Runs on the IO dispatcher to avoid blocking the main thread.
     */
    private fun loadApps() {
        val pm = packageManager ?: return
        val wm = whitelistManager ?: return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val appInfoList = withContext(Dispatchers.IO) {
                    loadInstalledApps(pm, wm)
                }

                val suggestedCount = appInfoList.count { it.isSuggested }

                _uiState.update {
                    it.copy(
                        apps = appInfoList,
                        filteredApps = appInfoList,
                        whitelistCount = wm.getWhitelistCount(),
                        suggestedCount = suggestedCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load apps: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Query the PackageManager for installed apps and build the AppInfo list.
     * Filters out system apps and sorts by whitelist status then name.
     */
    private fun loadInstalledApps(
        pm: PackageManager,
        wm: WhitelistManager
    ): List<AppInfo> {
        val installedApps = try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            emptyList()
        }

        val appInfoList = installedApps
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { appInfo ->
                val name = try {
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    appInfo.packageName
                }
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = name,
                    isWhitelisted = wm.isWhitelisted(appInfo.packageName),
                    isSuggested = WhitelistManager.SUGGESTED_APP_NAMES
                        .containsKey(appInfo.packageName)
                )
            }

        // Add suggested apps that are not installed (for visibility)
        val installedPackages = appInfoList.map { it.packageName }.toSet()
        val missingDefaults = WhitelistManager.SUGGESTED_APP_NAMES
            .filter { it.key !in installedPackages }
            .map { (pkg, name) ->
                AppInfo(
                    packageName = pkg,
                    appName = name,
                    isWhitelisted = wm.isWhitelisted(pkg),
                    isSuggested = true
                )
            }

        return sortAppList(appInfoList + missingDefaults)
    }

    // ---------------------------------------------------------------------------
    // Internal - Refresh and Observation
    // ---------------------------------------------------------------------------

    /**
     * Refresh the local app list from the current whitelist state.
     */
    private fun refreshAppList() {
        val wm = whitelistManager ?: return
        val updated = _uiState.value.apps.map { app ->
            app.copy(isWhitelisted = wm.isWhitelisted(app.packageName))
        }
        val sorted = sortAppList(updated)

        _uiState.update {
            it.copy(
                apps = sorted,
                whitelistCount = wm.getWhitelistCount()
            )
        }
        applyFilter()
    }

    /**
     * Observe changes to the whitelist set from the WhitelistManager.
     */
    private fun observeWhitelist() {
        viewModelScope.launch {
            whitelistManager?.getWhitelistedAppsFlow()?.collect {
                refreshAppList()
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Internal - Filtering
    // ---------------------------------------------------------------------------

    /**
     * Apply the current search query to the full app list.
     * Matches against app name and package name (case-insensitive).
     */
    private fun applyFilter() {
        val state = _uiState.value
        val filtered = if (state.searchQuery.isBlank()) {
            state.apps
        } else {
            val query = state.searchQuery.lowercase().trim()
            state.apps.filter { app ->
                app.appName.lowercase().contains(query) ||
                app.packageName.lowercase().contains(query)
            }
        }
        _uiState.update { it.copy(filteredApps = filtered) }
    }

    // ---------------------------------------------------------------------------
    // Internal - Sorting
    // ---------------------------------------------------------------------------

    /**
     * Sort the app list with the following priority:
     * 1. Suggested apps first
     * 2. Whitelisted apps next
     * 3. Alphabetically by app name within each group
     */
    private fun sortAppList(apps: List<AppInfo>): List<AppInfo> {
        return apps.sortedWith(
            compareByDescending<AppInfo> { it.isSuggested }
                .thenByDescending { it.isWhitelisted }
                .thenBy { it.appName.lowercase() }
        )
    }

    // ---------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        searchDebounceJob?.cancel()
    }
}
