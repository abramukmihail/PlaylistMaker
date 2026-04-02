package com.example.playlistmaker.mediaLibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _createPlaylistResult = MutableLiveData<Boolean>()
    val createPlaylistResult: LiveData<Boolean> = _createPlaylistResult

    fun createPlaylist(name: String, description: String?, coverPath: String?) {
        viewModelScope.launch {
            val playlist = Playlist(
                name = name,
                description = description,
                coverPath = coverPath
            )
            playlistInteractor.createPlaylist(playlist, coverPath)
            _createPlaylistResult.postValue(true)
        }
    }
}
