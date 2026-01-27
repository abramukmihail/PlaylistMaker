package com.example.playlistmaker.search.domain.interactor.impl

import com.example.playlistmaker.search.domain.interactor.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.SearchResult
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackInteractorImpl(
    private val trackRepository: TrackRepository
) : TrackInteractor {

    override suspend fun searchTracks(query: String): SearchResult {
        return withContext(Dispatchers.IO) {
            trackRepository.searchTracks(query)
        }
    }

    override fun getSearchHistory(): List<Track> {
        return trackRepository.getSearchHistory()
    }

    override fun addToSearchHistory(track: Track) {
        trackRepository.addToSearchHistory(track)
    }

    override fun clearSearchHistory() {
        trackRepository.clearSearchHistory()
    }
}