package com.example.playlistmaker.mediaLibrary.data.repository

import com.example.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistmaker.mediaLibrary.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.mediaLibrary.domain.repository.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.mediaLibrary.data.db.dao.FavoriteTrackDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import com.example.playlistmaker.mediaLibrary.data.mapper.FavoriteTrackMapper
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao
) : FavoriteRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDao.getFavoriteTracks()
            .distinctUntilChanged()
            .map { entities ->
            entities.map { FavoriteTrackMapper.mapToTrack(it) }
        }
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackDao.insertTrack(FavoriteTrackMapper.mapToEntity(track))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTrackDao.deleteTrack(FavoriteTrackMapper.mapToEntity(track))
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return favoriteTrackDao.getFavoriteTrackIds()
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoriteTrackDao.isFavorite(trackId)
    }
}
