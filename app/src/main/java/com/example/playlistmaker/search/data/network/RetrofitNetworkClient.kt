package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val itunesService: ItunesApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.findTrack(dto.term)
                response.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply { resultCode = -1 }
            }
        }
    }
}