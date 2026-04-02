package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediaLibrary.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default)
    val playerState: LiveData<PlayerState> = _playerState

    private val _playbackProgress = MutableLiveData<PlaybackProgress?>()
    val playbackProgress: LiveData<PlaybackProgress?> = _playbackProgress

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _addToPlaylistStatus = MutableLiveData<Pair<Boolean, String>?>()
    val addToPlaylistStatus: LiveData<Pair<Boolean, String>?> = _addToPlaylistStatus

    private var currentTrack: Track? = null

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
        currentTrack = track
        viewModelScope.launch {
            val isFavorite = favoriteInteractor.isFavorite(track.trackId)
            track.isFavorite = isFavorite
            _isFavorite.value = isFavorite
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

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                _playlists.value = playlists
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val success = playlistInteractor.addTrackToPlaylist(playlist, track)
            _addToPlaylistStatus.value = Pair(success, playlist.name)
        }
    }

    fun resetAddToPlaylistStatus() {
        _addToPlaylistStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }
}
