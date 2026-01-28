package com.example.playlistmaker.sharing.domain.interactor

import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingInteractor {
    fun shareApp()
    fun contactSupport()
    fun transitionAgreement()
}