package com.example.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.Embedded

data class TrackWithFavoriteInfo(
    @Embedded val track: TrackEntity,
    val addedAt: Long
)
