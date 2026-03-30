package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediaLibrary.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val _playbackProgress = MutableLiveData<PlaybackProgress?>()
    val playbackProgress: LiveData<PlaybackProgress?> = _playbackProgress

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    init {
        viewModelScope.launch {
            playerInteractor.playerState.collect { state ->
                _playerState.value = state
            }
        }

        viewModelScope.launch {
            playerInteractor.playbackProgress.collect { progress ->
                _playbackProgress.value = progress
            }
        }
    }

    fun setupTrack(track: Track) {
        _isFavorite.value = track.isFavorite
        viewModelScope.launch {
            playerInteractor.prepare(track.previewUrl)
        }
    }

    fun togglePlayback() {
        playerInteractor.togglePlayback()
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteInteractor.removeTrackFromFavorites(track)
                track.isFavorite = false
                _isFavorite.value = false
            } else {
                favoriteInteractor.addTrackToFavorites(track)
                track.isFavorite = true
                _isFavorite.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }
}
