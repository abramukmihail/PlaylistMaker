package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.mapper.HistoryMapper
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.domain.repository.SearchResult
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : TrackRepository {

    override fun searchTracks(searchQuery: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(SearchRequest(searchQuery))
        when (response.resultCode) {
            -1 -> {
                emit(SearchResult(emptyList(), -1))
            }
            200 -> {
                val tracks = (response as TrackResponse).results.map { TrackMapper.mapToDomain(it) }
                emit(SearchResult(tracks, 200))
            }
            else -> {
                emit(SearchResult(emptyList(), response.resultCode))
            }
        }
    }

    override fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_STORAGE, null)
        return HistoryMapper.fromJson(json, gson)
    }

    override suspend fun addToSearchHistory(track: Track) {
        withContext(Dispatchers.IO) {
            val historyList = getSearchHistory().toMutableList()

            historyList.removeIf { it.trackId == track.trackId }
            historyList.add(0, track)

            if (historyList.size > HISTORY_LIMIT) {
                historyList.removeAt(historyList.lastIndex)
            }

            sharedPreferences.edit()
                .putString(SEARCH_HISTORY_STORAGE, HistoryMapper.toJson(historyList, gson))
                .apply()
        }
    }

    override suspend fun clearSearchHistory() {

        sharedPreferences.edit()
                .remove(SEARCH_HISTORY_STORAGE)
                .apply()

    }

    companion object {
        private const val SEARCH_HISTORY_STORAGE = "search_history"
        private const val HISTORY_LIMIT = 10
    }
}