package com.example.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(): ViewModel() {
    private val playlistsLiveData = MutableLiveData<String>()
    fun observeLiveData(): LiveData<String> = playlistsLiveData
}