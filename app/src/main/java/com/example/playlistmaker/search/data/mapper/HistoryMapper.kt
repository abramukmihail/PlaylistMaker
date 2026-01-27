package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.text.isNullOrEmpty

object HistoryMapper {
    private val typeToken = object : TypeToken<List<Track>>() {}.type

    fun toJson(tracks: List<Track>, gson: Gson): String {
        return gson.toJson(tracks)
    }

    fun fromJson(json: String?, gson: Gson): List<Track> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                gson.fromJson(json, typeToken) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}