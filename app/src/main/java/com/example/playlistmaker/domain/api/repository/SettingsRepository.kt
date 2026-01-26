package com.example.playlistmaker.domain.api.repository

interface SettingsRepository {
    fun setDarkThemeEnabled(enabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
    fun applySavedTheme()
}