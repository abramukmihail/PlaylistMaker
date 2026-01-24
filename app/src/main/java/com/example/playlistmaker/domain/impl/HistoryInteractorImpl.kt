package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.repository.HistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    override fun addTrackToHistory(track: Track): List<Track> {
        repository.addTrackToHistory(track)
        return repository.getSearchHistory()
    }

    override fun getSearchHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}