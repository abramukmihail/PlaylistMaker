package com.example.playlistmaker.mediaLibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.mediaLibrary.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : NewPlaylistViewModel(playlistInteractor) {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun getPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlistData = playlistInteractor.getPlaylistById(playlistId)
            playlistData?.let {
                _playlist.postValue(it)
            }
        }
    }

    fun updatePlaylist(id: Int, name: String, description: String?, coverPath: String?) {
        viewModelScope.launch {
            val updatedPlaylist = Playlist(
                id = id,
                name = name,
                description = description,
                coverPath = coverPath
            )
            playlistInteractor.updatePlaylist(updatedPlaylist, coverPath)
        }
    }
}
