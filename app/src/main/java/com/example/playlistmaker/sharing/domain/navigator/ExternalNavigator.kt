package com.example.playlistmaker.sharing.domain.navigator

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(emailData: EmailData)
    fun openLink(emailData: EmailData)
    fun openEmail(emailData: EmailData)
}