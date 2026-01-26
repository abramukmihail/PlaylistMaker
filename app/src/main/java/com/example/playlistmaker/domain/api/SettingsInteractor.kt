package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun getThemeSetting(): Boolean
    fun updateThemeSetting(darkTheme: Boolean)
    fun applySavedTheme()

}