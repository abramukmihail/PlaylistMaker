package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.NewPlaylistViewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { SearchViewModel(get()) }

    viewModel { SettingsViewModel(get(), get()) }

    viewModel {
        PlayerViewModel(
            playerInteractor = get(),
            favoriteInteractor = get(),
            playlistInteractor = get()
        )
    }

    viewModel { PlaylistsViewModel(get()) }

    viewModel { NewPlaylistViewModel(get()) }

    viewModel { FavoritesViewModel(get()) }
}