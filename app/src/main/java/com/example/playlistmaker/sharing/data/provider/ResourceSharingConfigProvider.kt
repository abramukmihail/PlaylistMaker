package com.example.playlistmaker.sharing.data.provider

import android.content.Context
import com.example.playlistmaker.sharing.domain.model.EmailData

class ResourceSharingConfigProvider(
    private val context: Context
) : SharingConfigProvider {

    override fun getSharingConfig(): EmailData {
        return EmailData(
            playStoreUrl = "https://play.google.com/store/apps/details?id=com.example.playlistmaker",
            userAgreementUrl = "https://example.com/user-agreement",
            supportEmail = "support@example.com",
            message = "Message to developer",
            messageTitle = "App Feedback"
        )
    }
}