package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.repository.SearchResult
import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    suspend fun searchTracks(query: String): SearchResult
    fun getSearchHistory(): List<Track>
    fun addToSearchHistory(track: Track)
    fun clearSearchHistory()
}
