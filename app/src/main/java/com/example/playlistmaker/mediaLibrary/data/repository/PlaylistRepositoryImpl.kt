package com.example.playlistmaker.mediaLibrary.data.repository

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
    private val trackDao: TrackDao
) : PlaylistRepository {

    private val gson = Gson()
    private val trackIdsType = object : TypeToken<List<Int>>() {}.type

    override suspend fun createPlaylist(playlist: Playlist, imagePath: String?): Long {
        val entity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            imagePath = imagePath,
            trackIds = "[]",
            tracksCount = 0
        )
        return playlistDao.insertPlaylist(entity)
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
                        trackCount = entity.tracksCount
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
            trackCount = entity.tracksCount
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

        return trackEntities.map { entity ->
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

    override suspend fun getTrackCount(playlistId: Int): Int {
        return playlistTrackDao.getTrackCount(playlistId)
    }
}
