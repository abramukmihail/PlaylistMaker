package com.example.playlistmaker.mediaLibrary.data.mapper

import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.search.domain.models.Track

object FavoriteTrackMapper {

    fun mapToEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.trackId
        )
    }
}