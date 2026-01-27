package com.example.playlistmaker.search.domain.models

sealed interface SearchState {
    object Empty : SearchState
    object Loading : SearchState
    object EmptyResult : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    sealed interface Error : SearchState {
        object NoConnection : Error
        data class NetworkError(val message: String) : Error
    }
}