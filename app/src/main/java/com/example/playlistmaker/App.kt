package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.creator.Creator

class App : Application() {
    private lateinit var settingsInteractor: SettingsInteractor
    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.provideSettingsInteractor(this)
        settingsInteractor.applySavedTheme()
    }
}