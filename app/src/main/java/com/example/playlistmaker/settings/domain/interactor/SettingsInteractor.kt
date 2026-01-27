package com.example.playlistmaker.settings.domain.interactor

interface SettingsInteractor {
    fun getThemeSetting(): Boolean
    fun updateThemeSetting(darkTheme: Boolean)
    fun applySavedTheme()

}