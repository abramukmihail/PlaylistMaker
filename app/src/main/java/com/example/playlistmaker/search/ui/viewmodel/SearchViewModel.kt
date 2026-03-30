package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactor.TrackInteractor
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class SearchViewModel(
    private val trackInteractor: TrackInteractor
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Empty)
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>(emptyList())
    val historyState: LiveData<List<Track>> = _historyState

    private var latestSearchText: String? = null
    private var currentSearchJob: Job? = null

    private val trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
        performSearch(changedText)
    }

    private var isClickAllowed = true

    init {
        loadHistory()
    }

    fun searchDebounced(query: String) {
        if (query.isEmpty()) {
            _searchState.value = SearchState.Empty
            loadHistory()
            return
        }
        if (latestSearchText == query) return

        latestSearchText = query
        trackSearchDebounce(query)
    }

    private fun performSearch(query: String) {
        currentSearchJob?.cancel()
        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            trackInteractor
                .searchTracks(query)
                .collect { result ->
                    if (latestSearchText == query) {
                        when (result.resultCode) {
                            200 -> {
                                if (result.tracks.isEmpty()) {
                                    _searchState.value = SearchState.EmptyResult
                                } else {
                                    _searchState.value = SearchState.Content(result.tracks)
                                }
                            }

                            -1 -> {
                                _searchState.value = SearchState.Error.NoConnection
                            }

                            else -> {
                                _searchState.value =
                                    SearchState.Error.NetworkError("Error ${result.resultCode}")
                            }
                        }
                    }
                }
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun addToSearchHistory(track: Track) {
        viewModelScope.launch {
            trackInteractor.addToSearchHistory(track)
            loadHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            trackInteractor.clearSearchHistory()
            _historyState.value = emptyList()
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = trackInteractor.getSearchHistory()
        }
    }

    fun cancelSearch() {
        currentSearchJob?.cancel()
        currentSearchJob = null
        latestSearchText = null
        _searchState.value = SearchState.Empty
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}