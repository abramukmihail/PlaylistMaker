package com.example.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_track_table ORDER BY createdAt DESC")
    fun getFavoriteTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getFavoriteTrackIds(): List<Int>
}
