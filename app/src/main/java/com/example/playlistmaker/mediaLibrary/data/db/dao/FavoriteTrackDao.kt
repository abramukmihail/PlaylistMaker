package com.example.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.mediaLibrary.data.db.entity.TrackWithFavoriteInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrackEntity)

    @Query("""
        SELECT t.*, ft.addedAt 
        FROM favorite_tracks ft 
        INNER JOIN tracks t ON ft.trackId = t.trackId 
        ORDER BY ft.addedAt DESC
    """)
    fun getFavoriteTracks(): Flow<List<TrackWithFavoriteInfo>>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getFavoriteTrackIds(): List<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Int): Boolean
}
