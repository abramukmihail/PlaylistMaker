package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(term: String): SearchResult
}

data class SearchResult(
    val tracks: List<Track>,
    val resultCode: Int
)