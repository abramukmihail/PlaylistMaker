package com.example.playlistmaker.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.repository.HistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class HistoryRepositoryImpl(private val sharedPreferences: SharedPreferences,
                            private val gson: Gson,
                            private val executor: Executor) : HistoryRepository {


    override fun addTrackToHistory(track: Track) {
        executor.execute {
            val historyList = getSearchHistory().toMutableList()

            if (historyList.size == HISTORY_LIMIT) {
                historyList.removeAt(historyList.lastIndex)
            }

            if (historyList.any { it.trackId == track.trackId }) {
                historyList.removeIf { it.trackId == track.trackId }
            }

            historyList.add(0, track)

            sharedPreferences.edit()
                .putString(SEARCH_HISTORY_STORAGE, gson.toJson(historyList))
                .apply()
        }
    }

    override fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_STORAGE, null)
        return gson.fromJson(json, Array<Track>::class.java)?.toList() ?: emptyList()
    }

    override fun clearSearchHistory() {
        executor.execute {
            sharedPreferences.edit()
                .remove(SEARCH_HISTORY_STORAGE)
                .apply()
        }
    }
        companion object {
            private const val SEARCH_HISTORY_STORAGE = "search_history"
            private const val HISTORY_LIMIT = 10
        }

}