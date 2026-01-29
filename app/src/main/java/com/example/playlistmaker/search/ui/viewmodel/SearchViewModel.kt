package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactor.TrackInteractor
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.isEmpty

class SearchViewModel(
    private val trackInteractor: TrackInteractor
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Empty)
    val searchState: LiveData<SearchState> = _searchState

    private val _historyState = MutableLiveData<List<Track>>(emptyList())
    val historyState: LiveData<List<Track>> = _historyState

    private var searchJob: Job? = null
    private var debounceJob: Job? = null
    private val searchDebounceDelay = 2000L

    init {
        loadHistory()
    }

    fun searchDebounced(query: String) {
        debounceJob?.cancel()

        if (query.isEmpty()) {
            _searchState.value = SearchState.Empty
            loadHistory()
            return
        }

        _searchState.value = SearchState.Loading
        debounceJob = viewModelScope.launch {
            delay(searchDebounceDelay)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val result = trackInteractor.searchTracks(query)

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
                        _searchState.value = SearchState.EmptyResult
                    }
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error.NetworkError(
                    e.message ?: "Unknown error"
                )
            }
        }
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
        searchJob?.cancel()
        debounceJob?.cancel()
        _searchState.value = SearchState.Empty
    }
}