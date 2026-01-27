package com.example.playlistmaker.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.interactor.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.interactor.TrackInteractor
import com.example.playlistmaker.search.domain.interactor.impl.TrackInteractorImpl
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.interactor.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.provider.ResourceSharingConfigProvider
import com.example.playlistmaker.sharing.data.provider.SharingConfigProvider
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.interactor.impl.SharingInteractorImpl
import com.google.gson.Gson

object Creator {

    private fun provideTrackRepository(context: Context): TrackRepositoryImpl {
        return TrackRepositoryImpl(
            networkClient = RetrofitNetworkClient(),
            sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE),
            gson = Gson()
        )
    }

    private fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(provideTrackRepository(context))
    }

    fun provideSearchViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(provideTrackInteractor(context)) as T
            }
        }
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val externalNavigator = ExternalNavigatorImpl(context)
        val configProvider: SharingConfigProvider = ResourceSharingConfigProvider(context)
        val emailData = configProvider.getSharingConfig()

        return SharingInteractorImpl(externalNavigator, emailData)
    }

    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val settingsInteractor = provideSettingsInteractor(context)
                val sharingInteractor = provideSharingInteractor(context)
                return SettingsViewModel(settingsInteractor, sharingInteractor) as T
            }
        }
    }
    private fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl()
    }
    fun providePlayerViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(providePlayerInteractor()) as T
            }
        }
    }
}