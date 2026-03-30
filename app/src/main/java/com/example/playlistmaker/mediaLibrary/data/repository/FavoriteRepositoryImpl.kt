package com.example.playlistmaker.mediaLibrary.data.repository

import com.example.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.mediaLibrary.domain.repository.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getFavoriteTracks().map { entities ->
            entities.map { mapToTrack(it) }
        }
    }

    override suspend fun addTrackToFavorites(track: Track) {
        appDatabase.favoriteTrackDao().insertTrack(mapToEntity(track))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        appDatabase.favoriteTrackDao().deleteTrack(mapToEntity(track))
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return appDatabase.favoriteTrackDao().getFavoriteTrackIds()
    }

    private fun mapToEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    private fun mapToTrack(entity: FavoriteTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl,
            isFavorite = true
        )
    }
}
