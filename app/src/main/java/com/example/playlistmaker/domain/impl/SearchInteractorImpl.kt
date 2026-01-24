package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: TrackRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
            try {
                val tracks = repository.searchTracks(term)
                consumer.consume(tracks)
            } catch (e: Exception) {
                consumer.consume(emptyList())
            }
        }
    }
}