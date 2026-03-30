package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.mediaLibrary.domain.repository.FavoriteRepository
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
    private val gson: Gson,
    private val favoriteRepository: FavoriteRepository
) : TrackRepository {

    override fun searchTracks(searchQuery: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(SearchRequest(searchQuery))
        when (response.resultCode) {
            -1 -> {
                emit(SearchResult(emptyList(), -1))
            }
            200 -> {
                val tracks = (response as TrackResponse).results.map { TrackMapper.mapToDomain(it) }
                val favoriteTrackIds = favoriteRepository.getFavoriteTrackIds()
                tracks.forEach { track ->
                    if (favoriteTrackIds.contains(track.trackId)) {
                        track.isFavorite = true
                    }
                }
                emit(SearchResult(tracks, 200))
            }
            else -> {
                emit(SearchResult(emptyList(), response.resultCode))
            }
        }
    }

    override fun getSearchHistory(): Flow<List<Track>> = flow {
        val json = sharedPreferences.getString(SEARCH_HISTORY_STORAGE, null)
        val history = HistoryMapper.fromJson(json, gson)
        val favoriteTrackIds = favoriteRepository.getFavoriteTrackIds()
        history.forEach { track ->
            if (favoriteTrackIds.contains(track.trackId)) {
                track.isFavorite = true
            }
        }
        emit(history)
    }

    override suspend fun addToSearchHistory(track: Track) {
        withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(SEARCH_HISTORY_STORAGE, null)
            val historyList = HistoryMapper.fromJson(json, gson).toMutableList()

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
