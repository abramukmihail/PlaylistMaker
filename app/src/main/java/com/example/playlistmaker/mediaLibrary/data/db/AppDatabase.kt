package com.example.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.mediaLibrary.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity

@Database(version = 2, entities = [FavoriteTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
}
