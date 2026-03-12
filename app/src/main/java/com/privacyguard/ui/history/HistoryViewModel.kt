package com.privacyguard.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privacyguard.data.DetectionEvent
import com.privacyguard.data.EncryptedLogRepository
import com.privacyguard.ml.Severity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state representing the history screen.
 * Supports pagination, filtering, and search.
 */
data class HistoryUiState(
    // Full event list from repository
    val events: List<DetectionEvent> = emptyList(),

    // Filtered events (after applying severity + search)
    val filteredEvents: List<DetectionEvent> = emptyList(),

    // Filter and search state
    val selectedFilter: SeverityFilter = SeverityFilter.ALL,
    val searchQuery: String = "",

    // Pagination state
    val currentPage: Int = 0,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    val hasMorePages: Boolean = false,

    // Per-filter event counts for badge display
    val filterCounts: Map<SeverityFilter, Int> = emptyMap(),

    // UI flags
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    /** Total number of events matching the current filter. */
    val filteredTotal: Int get() = filteredEvents.size

    /** Number of pages available for the current filter. */
    val totalPages: Int
        get() = if (filteredEvents.isEmpty()) 0
        else (filteredEvents.size + pageSize - 1) / pageSize

    /** Whether any filter or search is actively applied. */
    val isFiltering: Boolean
        get() = selectedFilter != SeverityFilter.ALL || searchQuery.isNotEmpty()
}

/**
 * Severity filter options for the history screen.
 * Each entry has a user-facing label.
 */
enum class SeverityFilter(val label: String) {
    ALL("All"),
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium")
}

/**
 * ViewModel for the history screen. Manages:
 * - Loading and observing detection events from the repository
 * - Severity-based filtering with count badges
 * - Text search with debounce
 * - Pagination (load-more pattern)
 * - Single-event and bulk deletion
 */
class HistoryViewModel : ViewModel() {

    companion object {
        /** Debounce delay for search input (milliseconds). */
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    private var logRepository: EncryptedLogRepository? = null

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // Debounce job for search input
    private var searchDebounceJob: Job? = null

    // ---------------------------------------------------------------------------
    // Public API - Repository
    // ---------------------------------------------------------------------------

    /**
     * Connect the log repository and begin observing events.
     */
    fun setRepository(repo: EncryptedLogRepository) {
        if (logRepository == repo) return
        logRepository = repo
        _uiState.update { it.copy(isLoading = true) }
        observeEvents()
    }

    // ---------------------------------------------------------------------------
    // Public API - Filtering
    // ---------------------------------------------------------------------------

    /**
     * Set the severity filter and reapply to the event list.
     */
    fun setFilter(filter: SeverityFilter) {
        _uiState.update { it.copy(selectedFilter = filter, currentPage = 0) }
        applyFilters()
    }

    /**
     * Set the filter from a string argument (e.g., from navigation deep links).
     */
    fun setFilterFromString(filterStr: String) {
        val filter = SeverityFilter.entries.find {
            it.name.equals(filterStr, ignoreCase = true)
        } ?: SeverityFilter.ALL
        setFilter(filter)
    }

    // ---------------------------------------------------------------------------
    // Public API - Search
    // ---------------------------------------------------------------------------

    /**
     * Update the search query with debounce.
     * Filters are applied after the debounce delay to avoid excessive
     * recomposition while the user is still typing.
     */
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _uiState.update { it.copy(currentPage = 0) }
            applyFilters()
        }
    }

    // ---------------------------------------------------------------------------
    // Public API - Pagination
    // ---------------------------------------------------------------------------

    /**
     * Load the next page of results.
     */
    fun loadNextPage() {
        val state = _uiState.value
        if (!state.hasMorePages) return
        _uiState.update { it.copy(currentPage = state.currentPage + 1) }
        updatePaginationState()
    }

    /**
     * Reset pagination back to the first page.
     */
    fun resetPagination() {
        _uiState.update { it.copy(currentPage = 0) }
        updatePaginationState()
    }

    // ---------------------------------------------------------------------------
    // Public API - Deletion
    // ---------------------------------------------------------------------------

    /**
     * Delete a single detection event by ID.
     */
    fun deleteEvent(id: String) {
        viewModelScope.launch {
            try {
                logRepository?.deleteEvent(id)
                // Optimistically remove from local state for instant feedback
                _uiState.update { state ->
                    val updatedEvents = state.events.filter { it.id != id }
                    state.copy(
                        events = updatedEvents,
                        isEmpty = updatedEvents.isEmpty()
                    )
                }
                applyFilters()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete event: ${e.message}")
                }
            }
        }
    }

    /**
     * Delete all detection events from the repository.
     */
    fun deleteAllEvents() {
        viewModelScope.launch {
            try {
                logRepository?.deleteAllEvents()
                _uiState.update {
                    it.copy(
                        events = emptyList(),
                        filteredEvents = emptyList(),
                        isEmpty = true,
                        currentPage = 0,
                        hasMorePages = false,
                        filterCounts = emptyMap()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to clear history: ${e.message}")
                }
            }
        }
    }

    /**
     * Dismiss an error message.
     */
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ---------------------------------------------------------------------------
    // Internal - Observation
    // ---------------------------------------------------------------------------

    private fun observeEvents() {
        viewModelScope.launch {
            logRepository?.getAllEvents()?.collect { events ->
                _uiState.update {
                    it.copy(
                        events = events,
                        isEmpty = events.isEmpty(),
                        isLoading = false
                    )
                }
                computeFilterCounts(events)
                applyFilters()
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Internal - Filtering and Search
    // ---------------------------------------------------------------------------

    /**
     * Apply severity filter and search query to produce the filtered event list.
     */
    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.events

        // Apply severity filter
        filtered = when (state.selectedFilter) {
            SeverityFilter.CRITICAL -> filtered.filter { it.severity == Severity.CRITICAL }
            SeverityFilter.HIGH -> filtered.filter { it.severity == Severity.HIGH }
            SeverityFilter.MEDIUM -> filtered.filter { it.severity == Severity.MEDIUM }
            SeverityFilter.ALL -> filtered
        }

        // Apply text search across multiple fields
        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase().trim()
            filtered = filtered.filter { event ->
                event.entityType.displayName.lowercase().contains(query) ||
                (event.sourceApp?.lowercase()?.contains(query) == true) ||
                (event.sourceAppName?.lowercase()?.contains(query) == true) ||
                event.actionTaken.displayName.lowercase().contains(query) ||
                event.severity.displayName.lowercase().contains(query)
            }
        }

        _uiState.update { it.copy(filteredEvents = filtered) }
        updatePaginationState()
    }

    /**
     * Compute the number of events for each severity filter.
     * Used to show count badges on filter chips.
     */
    private fun computeFilterCounts(events: List<DetectionEvent>) {
        val counts = mutableMapOf<SeverityFilter, Int>()
        counts[SeverityFilter.ALL] = events.size
        counts[SeverityFilter.CRITICAL] = events.count { it.severity == Severity.CRITICAL }
        counts[SeverityFilter.HIGH] = events.count { it.severity == Severity.HIGH }
        counts[SeverityFilter.MEDIUM] = events.count { it.severity == Severity.MEDIUM }
        _uiState.update { it.copy(filterCounts = counts) }
    }

    // ---------------------------------------------------------------------------
    // Internal - Pagination
    // ---------------------------------------------------------------------------

    /**
     * Update pagination-related flags based on the current filtered list.
     */
    private fun updatePaginationState() {
        val state = _uiState.value
        val displayedCount = (state.currentPage + 1) * state.pageSize
        val hasMore = displayedCount < state.filteredEvents.size

        _uiState.update { it.copy(hasMorePages = hasMore) }
    }

    // ---------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        searchDebounceJob?.cancel()
    }
}
