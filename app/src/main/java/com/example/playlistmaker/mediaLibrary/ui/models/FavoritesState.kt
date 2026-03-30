package com.example.playlistmaker.mediaLibrary.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
}
