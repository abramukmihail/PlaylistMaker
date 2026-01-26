package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors
import java.util.concurrent.Executor

class SearchInteractorImpl(private val repository: TrackRepository,
                           private val executor: Executor) : SearchInteractor {

    override fun searchTracks(term: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
                val result = repository.searchTracks(term)
                consumer.consume(result.tracks, result.resultCode)
        }
    }
}