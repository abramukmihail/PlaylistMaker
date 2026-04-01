package com.example.playlistmaker.mediaLibrary.domain.interactor.impl

import com.example.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun createPlaylist(playlist: Playlist, imagePath: String?): Long {
        return playlistRepository.createPlaylist(playlist, imagePath)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        return playlistRepository.addTrackToPlaylist(playlist, track)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun getPlaylistTracks(playlistId: Int): List<Track> {
        return playlistRepository.getPlaylistTracks(playlistId)
    }
}
