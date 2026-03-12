package com.privacyguard.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.ml.Severity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val events: List<DetectionEvent> = emptyList(),
    val filteredEvents: List<DetectionEvent> = emptyList(),
    val selectedFilter: SeverityFilter = SeverityFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true
)

enum class SeverityFilter(val label: String) {
    ALL("All"),
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium")
}

class HistoryViewModel : ViewModel() {

    private var logRepository: EncryptedLogRepository? = null

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun setRepository(repo: EncryptedLogRepository) {
        logRepository = repo
        observeEvents()
    }

    fun setFilter(filter: SeverityFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun deleteEvent(id: String) {
        logRepository?.deleteEvent(id)
    }

    fun deleteAllEvents() {
        logRepository?.deleteAllEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            logRepository?.getAllEvents()?.collect { events ->
                _uiState.update { it.copy(events = events, isEmpty = events.isEmpty()) }
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.events

        // Apply severity filter
        when (state.selectedFilter) {
            SeverityFilter.CRITICAL -> filtered = filtered.filter { it.severity == Severity.CRITICAL }
            SeverityFilter.HIGH -> filtered = filtered.filter { it.severity == Severity.HIGH }
            SeverityFilter.MEDIUM -> filtered = filtered.filter { it.severity == Severity.MEDIUM }
            SeverityFilter.ALL -> { /* no filter */ }
        }

        // Apply search
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase()
            filtered = filtered.filter {
                it.entityType.displayName.lowercase().contains(query) ||
                (it.sourceApp?.lowercase()?.contains(query) == true) ||
                (it.sourceAppName?.lowercase()?.contains(query) == true)
            }
        }

        _uiState.update { it.copy(filteredEvents = filtered) }
    }
}
