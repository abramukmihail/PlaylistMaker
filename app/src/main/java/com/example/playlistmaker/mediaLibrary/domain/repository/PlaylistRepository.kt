package com.example.playlistmaker.mediaLibrary.domain.repository

import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist, coverPath: String?): Long
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Int): Playlist?
    suspend fun getPlaylistTracks(playlistId: Int): List<Track>
    suspend fun getTrackCount(playlistId: Int): Int
}
