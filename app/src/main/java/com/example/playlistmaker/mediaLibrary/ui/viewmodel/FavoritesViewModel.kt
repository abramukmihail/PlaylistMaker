package com.example.playlistmaker.mediaLibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediaLibrary.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.mediaLibrary.ui.models.FavoritesState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    private var dbJob: Job? = null

    init {
        fillData()
    }

    fun fillData() {
        dbJob?.cancel()
        
        dbJob = viewModelScope.launch {
            favoriteInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _state.postValue(FavoritesState.Empty)
        } else {
            _state.postValue(FavoritesState.Content(tracks))
        }
    }
}
