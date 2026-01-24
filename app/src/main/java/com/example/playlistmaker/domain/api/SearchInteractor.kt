package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchInteractor {
    fun searchTracks(term: String, consumer: SearchConsumer)

    interface SearchConsumer {
        fun consume(foundTracks: List<Track>, resultCode: Int)
    }
}