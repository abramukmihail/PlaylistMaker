package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is SearchRequest) {
                val resp = itunesService.findTrack(dto.term).execute()
                val body = resp.body() ?: TrackResponse(0, emptyList())

                body.apply { resultCode = resp.code() }
            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: Exception) {
            Response().apply { resultCode = -1 }
        }
    }
    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }

}