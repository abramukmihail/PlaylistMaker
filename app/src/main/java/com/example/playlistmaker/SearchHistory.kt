package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit
import kotlin.collections.any
import kotlin.collections.lastIndex
import kotlin.collections.toList
import kotlin.collections.toMutableList
import kotlin.jvm.java

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun getSavedTracks(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_STORAGE, null)
        return gson.fromJson(json, Array<Track>::class.java)?.toList() ?: emptyList()
    }

    fun addTrackToHistory(track: Track) {
        val historyList = getSavedTracks().toMutableList()
        if (historyList.size == HISTORY_LIMIT) {
            historyList.removeAt(historyList.lastIndex)
        }
        if (historyList.any { it.trackId == track.trackId }) {
            historyList.removeIf { it.trackId == track.trackId }
        }
        historyList.add(0, track)
        sharedPreferences.edit {
            putString(SEARCH_HISTORY_STORAGE, gson.toJson(historyList))
        }
    }

    fun clearSearchHistory() {
        sharedPreferences.edit {
            remove(SEARCH_HISTORY_STORAGE)
        }
    }

    fun isHistoryEmpty(): Boolean {
        return getSavedTracks().isEmpty()
    }

    companion object {
        private const val SEARCH_HISTORY_STORAGE = "search_history_storage"
        private const val HISTORY_LIMIT = 10
    }
}