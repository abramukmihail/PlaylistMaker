package com.example.playlistmaker.mediaLibrary.domain.interactor

import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist, coverPath: String?): Long
    suspend fun updatePlaylist(playlist: Playlist, coverPath: String?): Boolean
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int): Boolean
    suspend fun deletePlaylist(playlist: Playlist): Boolean
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Int): Playlist?
    suspend fun getPlaylistTracks(playlistId: Int): List<Track>
    suspend fun getTotalDuration(playlistId: Int): Long
}
