package com.example.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: String, // JSON list of track IDs
    val tracksCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)
