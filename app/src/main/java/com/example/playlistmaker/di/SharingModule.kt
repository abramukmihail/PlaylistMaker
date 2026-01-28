package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.provider.ResourceSharingConfigProvider
import com.example.playlistmaker.sharing.data.provider.SharingConfigProvider
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.interactor.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import com.example.playlistmaker.sharing.domain.model.EmailData
import org.koin.dsl.module

val sharingModule = module {

    single<SharingConfigProvider> {
        ResourceSharingConfigProvider(androidContext())
    }
    single<EmailData> {
        get<SharingConfigProvider>().getSharingConfig()
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get(),
            emailData = get()
        )
    }

}