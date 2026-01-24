package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()

}