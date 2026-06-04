package com.example.playlistmaker.mediaLibrary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaLibraryViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _selectedTabIndex = MutableStateFlow(savedStateHandle.get<Int>(SELECTED_TAB_KEY) ?: 0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
        savedStateHandle[SELECTED_TAB_KEY] = index
    }

    companion object {
        private const val SELECTED_TAB_KEY = "selected_tab_index"
    }
}