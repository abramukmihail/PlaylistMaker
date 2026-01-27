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
import kotlin.collections.any
import kotlin.collections.lastIndex
import kotlin.collections.map
import kotlin.collections.toMutableList

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) : TrackRepository {

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

    override fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_STORAGE, null)
        return HistoryMapper.fromJson(json, gson)
    }

    override fun addToSearchHistory(track: Track) {
        val historyList = getSearchHistory().toMutableList()

        if (historyList.size == HISTORY_LIMIT) {
            historyList.removeAt(historyList.lastIndex)
        }

        if (historyList.any { it.trackId == track.trackId }) {
            historyList.removeIf { it.trackId == track.trackId }
        }

        historyList.add(0, track)

        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_STORAGE, HistoryMapper.toJson(historyList, gson))
            .apply()
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_STORAGE)
            .apply()
    }

    companion object {
        private const val SEARCH_HISTORY_STORAGE = "search_history"
        private const val HISTORY_LIMIT = 10
    }
}