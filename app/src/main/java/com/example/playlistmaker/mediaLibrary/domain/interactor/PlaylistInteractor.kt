package com.example.playlistmaker.mediaLibrary.domain.interactor

import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist, imagePath: String?): Long
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Int): Playlist?
    suspend fun getPlaylistTracks(playlistId: Int): List<Track>
}
