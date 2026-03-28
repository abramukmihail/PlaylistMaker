package com.example.playlistmaker.search.domain.interactor.impl

import com.example.playlistmaker.search.domain.interactor.TrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.SearchResult
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(
    private val trackRepository: TrackRepository
) : TrackInteractor {

    override fun searchTracks(query: String): Flow<SearchResult> {
        return trackRepository.searchTracks(query)
    }

    override fun getSearchHistory(): List<Track> {
        return trackRepository.getSearchHistory()
    }

    override suspend fun addToSearchHistory(track: Track) {
        trackRepository.addToSearchHistory(track)
    }

    override suspend fun clearSearchHistory() {
        trackRepository.clearSearchHistory()
    }
}