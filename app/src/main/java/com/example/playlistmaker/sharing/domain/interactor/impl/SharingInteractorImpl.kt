package com.example.playlistmaker.sharing.domain.interactor.impl

import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    override val emailData: EmailData
) : SharingInteractor {

    override fun shareApp() = externalNavigator.shareLink(emailData)

    override fun contactSupport() = externalNavigator.openEmail(emailData)

    override fun transitionAgreement() = externalNavigator.openLink(emailData)
}