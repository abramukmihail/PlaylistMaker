package com.example.playlistmaker.settings.domain.interactor.impl

import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSetting(): Boolean =
        repository.isDarkThemeEnabled()

    override fun updateThemeSetting(darkTheme: Boolean) {
        repository.setDarkThemeEnabled(darkTheme)
    }

    override fun applySavedTheme() {
        repository.applySavedTheme()
    }
}