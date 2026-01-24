package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.api.repository.SearchResult

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(term: String): SearchResult {
        val response = networkClient.doRequest(SearchRequest(term))

        return if (response.resultCode == 200) {
            val tracks = (response as TrackResponse).results.map { TrackMapper.mapToDomain(it) }
            SearchResult(tracks, 200)
        } else if (response.resultCode == -1) {

            SearchResult(emptyList(), -1)
        } else {

            SearchResult(emptyList(), 0)
        }
    }
}