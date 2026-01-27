package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _themeState
    private val _uiState = MutableLiveData<SettingsUiState>()
    val uiState: LiveData<SettingsUiState> = _uiState

    fun getCurrentThemeSetting(): Boolean = settingsInteractor.getThemeSetting()

    fun loadThemeState() {
        viewModelScope.launch {
            val isDarkTheme = settingsInteractor.getThemeSetting()
            _themeState.postValue(isDarkTheme)
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsInteractor.updateThemeSetting(enabled)
            _themeState.postValue(enabled)
            _uiState.postValue(SettingsUiState.ThemeChanged(enabled))
        }
    }

    fun shareApp() {
        viewModelScope.launch {
            sharingInteractor.shareApp()
        }
    }

    fun contactSupport() {
        viewModelScope.launch {
            sharingInteractor.contactSupport()
        }
    }

    fun transitionAgreement() {
        viewModelScope.launch {
            sharingInteractor.transitionAgreement()
        }
    }
    sealed class SettingsUiState {
        data class ThemeChanged(val isDarkTheme: Boolean) : SettingsUiState()
    }
}