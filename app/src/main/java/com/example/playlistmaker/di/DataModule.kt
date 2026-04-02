package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import android.content.SharedPreferences
import com.example.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistmaker.mediaLibrary.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.PlaylistDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.example.playlistmaker.mediaLibrary.data.repository.FavoriteRepositoryImpl
import com.example.playlistmaker.mediaLibrary.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.mediaLibrary.domain.repository.FavoriteRepository
import com.example.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.core.qualifier.named

private const val ITUNES_URL = "https://itunes.apple.com"
val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<RetrofitNetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single<SharedPreferences>(named("settings_prefs")) {
        androidContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single<FavoriteTrackDao> {
        get<AppDatabase>().favoriteTrackDao()
    }

    single<PlaylistDao> {
        get<AppDatabase>().playlistDao()
    }

    single<PlaylistTrackDao> {
        get<AppDatabase>().playlistTrackDao()
    }

    single<TrackDao> {
        get<AppDatabase>().trackDao()
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(
        favoriteTrackDao = get(),
        trackDao = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    single<TrackRepositoryImpl> {
        TrackRepositoryImpl(
            networkClient = get(),
            sharedPreferences = get(),
            gson = get(),
            favoriteRepository = get()
        )
    }

    single<SettingsRepositoryImpl> {
        SettingsRepositoryImpl(get(named("settings_prefs")))
    }
    factory { { MediaPlayer() } }

    single<PlayerRepository> {
        PlayerRepositoryImpl(
            mediaPlayerProvider = get()
        )
    }
}
