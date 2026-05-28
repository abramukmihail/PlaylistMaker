package com.example.playlistmaker.mediaLibrary.data.repository

import com.example.playlistmaker.mediaLibrary.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.example.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.example.playlistmaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackDao: TrackDao,
    private val favoriteTrackDao: FavoriteTrackDao
) : PlaylistRepository {

    private val gson = Gson()
    private val trackIdsType = object : TypeToken<List<Int>>() {}.type

    override suspend fun createPlaylist(playlist: Playlist, coverPath: String?): Long {
        val entity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            imagePath = coverPath,
            trackIds = "[]",
            tracksCount = 0,
            createdAt = playlist.createdAt
        )
        return playlistDao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist, coverPath: String?): Boolean {
        val existingPlaylist = playlistDao.getPlaylistById(playlist.id) ?: return false
        val updatedEntity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = coverPath ?: existingPlaylist.imagePath,
            trackIds = existingPlaylist.trackIds,
            tracksCount = existingPlaylist.tracksCount,
            createdAt = existingPlaylist.createdAt
        )
        playlistDao.updatePlaylist(updatedEntity)
        return true
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        val existingTrack = playlistTrackDao.getTrackByPlaylistAndTrack(playlist.id, track.trackId)
        if (existingTrack != null) {
            return false
        }

        val existingTrackInDb = trackDao.getTrackById(track.trackId)
        if (existingTrackInDb == null) {
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

        playlistTrackDao.insertTrack(
            PlaylistTrackEntity(
                playlistId = playlist.id,
                trackId = track.trackId
            )
        )

        val entity = playlistDao.getPlaylistById(playlist.id) ?: return false
        val currentTrackIds = gson.fromJson<List<Int>>(entity.trackIds, trackIdsType) ?: emptyList()
        val updatedTrackIds = currentTrackIds.toMutableList().apply {
            add(track.trackId)
        }
        val updatedEntity = entity.copy(
            trackIds = gson.toJson(updatedTrackIds, trackIdsType),
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(updatedEntity)

        return true
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int): Boolean {
        playlistTrackDao.deleteTrack(playlistId, trackId)
        
        val entity = playlistDao.getPlaylistById(playlistId) ?: return false
        val currentTrackIds = gson.fromJson<List<Int>>(entity.trackIds, trackIdsType) ?: emptyList()
        val updatedTrackIds = currentTrackIds.filter { it != trackId }
        
        val updatedEntity = entity.copy(
            trackIds = gson.toJson(updatedTrackIds, trackIdsType),
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(updatedEntity)
        
        val otherPlaylistEntries = playlistTrackDao.getPlaylistEntriesByTrackId(trackId)
        val isFavorite = favoriteTrackDao.isFavorite(trackId)
        
        if (otherPlaylistEntries.isEmpty() && !isFavorite) {
            val trackEntity = trackDao.getTrackById(trackId)
            trackEntity?.let {
                trackDao.deleteTrack(it)
            }
        }
        return true
    }

    override suspend fun deletePlaylist(playlist: Playlist): Boolean {
        val trackEntries = playlistTrackDao.getAllTracksByPlaylistId(playlist.id)
        
        playlistDao.deletePlaylist(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                imagePath = playlist.coverPath,
                trackIds = "",
                tracksCount = playlist.trackCount,
                createdAt = playlist.createdAt
            )
        )
        
        trackEntries.forEach { entry ->
            val otherPlaylistEntries = playlistTrackDao.getPlaylistEntriesByTrackId(entry.trackId)
            val isFavorite = favoriteTrackDao.isFavorite(entry.trackId)
            
            if (otherPlaylistEntries.isEmpty() && !isFavorite) {
                val trackEntity = trackDao.getTrackById(entry.trackId)
                trackEntity?.let {
                    trackDao.deleteTrack(it)
                }
            }
        }
        return true
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .distinctUntilChanged()
            .map { entities ->
                entities.map { entity ->
                    Playlist(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description,
                        coverPath = entity.imagePath,
                        trackCount = entity.tracksCount,
                        createdAt = entity.createdAt
                    )
                }
            }
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        val entity = playlistDao.getPlaylistById(id) ?: return null
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.imagePath,
            trackCount = entity.tracksCount,
            createdAt = entity.createdAt
        )
    }

    override suspend fun getPlaylistTracks(playlistId: Int): List<Track> {
        val trackLinks = playlistTrackDao
            .getTracksByPlaylistId(playlistId)
            .first()

        if (trackLinks.isEmpty()) {
            return emptyList()
        }

        val trackIds = trackLinks.map { it.trackId }
        val trackEntities = trackDao.getTracksByIds(trackIds)

        val tracksMap = trackEntities.associateBy { it.trackId }
        return trackLinks.mapNotNull { link ->
            tracksMap[link.trackId]?.let { entity ->
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
                    country = entity.country
                )
            }
        }
    }

    override suspend fun getTrackCount(playlistId: Int): Int {
        return playlistTrackDao.getTrackCount(playlistId)
    }

    override suspend fun getAllPlaylistsSync(): List<Playlist> {
        return playlistDao.getAllPlaylists().first().map { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                coverPath = entity.imagePath,
                trackCount = entity.tracksCount,
                createdAt = entity.createdAt
            )
        }
    }
}
