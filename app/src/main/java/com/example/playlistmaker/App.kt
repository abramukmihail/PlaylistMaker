package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.sharingModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.android.ext.android.getKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()


            //stopKoin()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                sharingModule,
                viewModelModule
            )
        }

        applySavedThemeWithKoin()
    }

    private fun applySavedThemeWithKoin() {
        val settingsInteractor = getKoin().get<SettingsInteractor>()
        settingsInteractor.applySavedTheme()
    }
}