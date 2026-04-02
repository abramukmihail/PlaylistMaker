package com.example.playlistmaker.mediaLibrary.ui.models

import com.example.playlistmaker.mediaLibrary.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}