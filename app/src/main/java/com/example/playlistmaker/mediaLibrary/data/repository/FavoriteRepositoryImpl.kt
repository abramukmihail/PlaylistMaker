package com.example.playlistmaker.mediaLibrary.data.repository

import com.example.playlistmaker.mediaLibrary.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.example.playlistmaker.mediaLibrary.domain.repository.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao,
    private val trackDao: TrackDao
) : FavoriteRepository {

    override suspend fun addTrackToFavorites(track: Track) {

        val existingTrack = trackDao.getTrackById(track.trackId)
        if (existingTrack == null) {

            trackDao.insertTrack(
                TrackEntity(
                    trackId = track.trackId,
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTimeMillis = track.trackTimeMillis,
                    artworkUrl100 = track.artworkUrl100,
                    previewUrl = track.previewUrl,
                    collectionName = track.collectionName,
                    releaseDate = track.releaseDate,
                    primaryGenreName = track.primaryGenreName,
                    country = track.country
                )
            )
        }

        favoriteTrackDao.insertTrack(
            FavoriteTrackEntity(trackId = track.trackId)
        )
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTrackDao.deleteTrack(
            FavoriteTrackEntity(trackId = track.trackId)
        )
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDao.getFavoriteTracks()
            .distinctUntilChanged()
            .map { trackWithInfoList ->
                trackWithInfoList.map { trackWithInfo ->
                    val entity = trackWithInfo.track
                    Track(
                        trackId = entity.trackId,
                        trackName = entity.trackName,
                        artistName = entity.artistName,
                        trackTimeMillis = entity.trackTimeMillis,
                        artworkUrl100 = entity.artworkUrl100,
                        previewUrl = entity.previewUrl,
                        collectionName = entity.collectionName,
                        releaseDate = entity.releaseDate,
                        primaryGenreName = entity.primaryGenreName,
                        country = entity.country,
                        isFavorite = true
                    )
                }
            }
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return favoriteTrackDao.getFavoriteTrackIds()
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoriteTrackDao.isFavorite(trackId)
    }
}