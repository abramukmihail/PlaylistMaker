package com.example.playlistmaker.mediaLibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel(): ViewModel() {
    private val favoritesLiveData = MutableLiveData<String>()
    fun observeLiveData(): LiveData<String> = favoritesLiveData
}