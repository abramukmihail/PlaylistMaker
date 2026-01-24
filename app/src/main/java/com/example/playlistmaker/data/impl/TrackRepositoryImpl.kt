package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.dto.TrackResponse

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))

        return if (response.resultCode == 200) {
            (response as TrackResponse).results.map { TrackMapper.mapToDomain(it) }
        } else {
            emptyList()
        }
    }
}