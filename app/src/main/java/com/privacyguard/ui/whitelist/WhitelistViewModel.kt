package com.privacyguard.ui.whitelist

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.WhitelistManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AppInfo(
    val packageName: String,
    val appName: String,
    val isWhitelisted: Boolean,
    val isSuggested: Boolean = false
)

data class WhitelistUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val whitelistCount: Int = 0,
    val isLoading: Boolean = false
)

class WhitelistViewModel : ViewModel() {

    private var whitelistManager: WhitelistManager? = null
    private var packageManager: PackageManager? = null

    private val _uiState = MutableStateFlow(WhitelistUiState())
    val uiState: StateFlow<WhitelistUiState> = _uiState.asStateFlow()

    fun initialize(manager: WhitelistManager, pm: PackageManager) {
        whitelistManager = manager
        packageManager = pm
        loadApps()
        observeWhitelist()
    }

    fun toggleApp(packageName: String) {
        whitelistManager?.toggleWhitelist(packageName)
        refreshAppList()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    private fun loadApps() {
        _uiState.update { it.copy(isLoading = true) }

        val pm = packageManager ?: return
        val wm = whitelistManager ?: return

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
                    isSuggested = WhitelistManager.SUGGESTED_APP_NAMES.containsKey(appInfo.packageName)
                )
            }
            .sortedWith(compareByDescending<AppInfo> { it.isWhitelisted }.thenBy { it.appName })

        _uiState.update {
            it.copy(
                apps = appInfoList,
                filteredApps = appInfoList,
                whitelistCount = wm.getWhitelistCount(),
                isLoading = false
            )
        }
    }

    private fun refreshAppList() {
        val wm = whitelistManager ?: return
        val updated = _uiState.value.apps.map { app ->
            app.copy(isWhitelisted = wm.isWhitelisted(app.packageName))
        }.sortedWith(compareByDescending<AppInfo> { it.isWhitelisted }.thenBy { it.appName })

        _uiState.update {
            it.copy(
                apps = updated,
                whitelistCount = wm.getWhitelistCount()
            )
        }
        applyFilter()
    }

    private fun observeWhitelist() {
        viewModelScope.launch {
            whitelistManager?.getWhitelistedAppsFlow()?.collect {
                refreshAppList()
            }
        }
    }

    private fun applyFilter() {
        val state = _uiState.value
        val filtered = if (state.searchQuery.isBlank()) {
            state.apps
        } else {
            val query = state.searchQuery.lowercase()
            state.apps.filter {
                it.appName.lowercase().contains(query) ||
                it.packageName.lowercase().contains(query)
            }
        }
        _uiState.update { it.copy(filteredApps = filtered) }
    }
}
