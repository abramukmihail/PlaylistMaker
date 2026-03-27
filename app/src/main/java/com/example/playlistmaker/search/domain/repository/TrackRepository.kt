package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(searchQuery: String): Flow<SearchResult>
    fun getSearchHistory(): List<Track>
    suspend fun addToSearchHistory(track: Track)
    suspend fun clearSearchHistory()
}

data class SearchResult(
    val tracks: List<Track>,
    val resultCode: Int
)