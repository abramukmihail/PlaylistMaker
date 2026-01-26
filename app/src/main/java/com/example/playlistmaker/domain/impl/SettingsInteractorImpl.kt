package com.example.playlistmaker.domain.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.repository.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun getThemeSetting(): Boolean =
        repository.isDarkThemeEnabled()

    override fun updateThemeSetting(darkTheme: Boolean) {
        repository.setDarkThemeEnabled(darkTheme)
    }
    override fun applySavedTheme() {
        repository.applySavedTheme()
    }
}

