package com.example.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_tracks",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["trackId"], unique = true)]
)
data class FavoriteTrackEntity(
    @PrimaryKey
    val trackId: Int,
    val addedAt: Long = System.currentTimeMillis()
)
