package com.example.playlistmaker.presentation.creator

import android.content.Context
import com.example.playlistmaker.data.impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.data.impl.SettingsRepositoryImpl

object Creator {

    private fun getTrackRepository(): TrackRepositoryImpl {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getTrackRepository())
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl(context))
    }

    fun provideHistoryInteractor(context: Context): HistoryInteractor {
        val repository = HistoryRepositoryImpl(
            context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        )
        return HistoryInteractorImpl(repository)
    }
}