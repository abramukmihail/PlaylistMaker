package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.dsl.module
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl

val repositoryModule = module {

    single<TrackRepository> { get<TrackRepositoryImpl>() }

    single<SettingsRepository> { get<SettingsRepositoryImpl>() }

}