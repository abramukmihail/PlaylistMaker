package com.example.playlistmaker.mediaLibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.TrackInfoFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

sealed class OperationResult {
    object Success : OperationResult()
    data class Error(val message: String? = null) : OperationResult()
    object Loading : OperationResult()
}

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _totalDuration = MutableLiveData<Long>()
    val totalDuration: LiveData<Long> = _totalDuration

    private val _trackCount = MutableLiveData<Int>()
    val trackCount: LiveData<Int> = _trackCount

    private val _deleteTrackResult = MutableLiveData<OperationResult>()
    val deleteTrackResult: LiveData<OperationResult> = _deleteTrackResult

    private val _shareText = MutableLiveData<String>()
    val shareText: LiveData<String> = _shareText

    private val _deletePlaylistResult = MutableLiveData<OperationResult>()
    val deletePlaylistResult: LiveData<OperationResult> = _deletePlaylistResult

    fun loadPlaylistInfo(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val trackCount = playlistInteractor.getPlaylistTracks(playlistId).size
            withContext(Dispatchers.Main) {
                _playlist.value = playlist
                _trackCount.value = trackCount
            }
        }
    }

    fun loadPlaylistTracks(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val tracks = playlistInteractor.getPlaylistTracks(playlistId)
            val totalDuration = playlistInteractor.getTotalDuration(playlistId)
            withContext(Dispatchers.Main) {
                _tracks.value = tracks
                _totalDuration.value = totalDuration
            }
        }
    }

    fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch {
            _deleteTrackResult.postValue(OperationResult.Loading)
            val success = try {
                playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
            } catch (e: Exception) {
                false
            }
            if (success) {
                _deleteTrackResult.value = OperationResult.Success
                loadPlaylistTracks(playlistId)
                loadPlaylistInfo(playlistId)
            } else {
                _deleteTrackResult.value = OperationResult.Error()
            }
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            _deletePlaylistResult.postValue(OperationResult.Loading)
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val success = playlist?.let {
                try {
                    playlistInteractor.deletePlaylist(it)
                } catch (e: Exception) {
                    false
                }
            } ?: false
            _deletePlaylistResult.value = if (success) {
                OperationResult.Success
            } else {
                OperationResult.Error()
            }
        }
    }

    fun prepareShareText() {
        viewModelScope.launch {
            val playlist = _playlist.value ?: return@launch
            val tracks = _tracks.value ?: emptyList()
            val shareTextBuilder = StringBuilder()
            shareTextBuilder.appendLine(playlist.name)
            playlist.description?.let {
                if (it.isNotEmpty()) {
                    shareTextBuilder.appendLine(it)
                }
            }
            shareTextBuilder.appendLine(TrackInfoFormatter.getTrackCountText(tracks.size))
            shareTextBuilder.appendLine()
            tracks.forEachIndexed { index, track ->
                shareTextBuilder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${formatTrackDuration(track.trackTimeMillis)})")
            }
            _shareText.value = shareTextBuilder.toString()
        }

    }
    fun clearShareText() {
        _shareText.value = ""
    }

    private fun formatTrackDuration(timeMillis: String): String {
        val millis = timeMillis.toLongOrNull() ?: 0L
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}
