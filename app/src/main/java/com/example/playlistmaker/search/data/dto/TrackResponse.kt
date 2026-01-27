package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.search.data.dto.Response

data class TrackResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()