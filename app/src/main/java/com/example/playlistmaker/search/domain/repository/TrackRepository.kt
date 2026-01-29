package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.repository.SearchResult
import com.example.playlistmaker.search.domain.models.Track

interface TrackRepository {
    fun searchTracks(searchQuery: String): SearchResult
    fun getSearchHistory(): List<Track>
    fun addToSearchHistory(track: Track)
    fun clearSearchHistory()
}

data class SearchResult(
    val tracks: List<Track>,
    val resultCode: Int
)