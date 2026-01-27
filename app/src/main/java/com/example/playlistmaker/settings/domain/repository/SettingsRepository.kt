package com.example.playlistmaker.settings.domain.repository

interface SettingsRepository {
    fun setDarkThemeEnabled(enabled: Boolean)
    fun isDarkThemeEnabled(): Boolean
    fun applySavedTheme()
}