package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}