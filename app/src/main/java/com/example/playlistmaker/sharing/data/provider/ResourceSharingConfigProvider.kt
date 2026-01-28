package com.example.playlistmaker.sharing.data.provider

import com.example.playlistmaker.R
import android.content.Context
import com.example.playlistmaker.sharing.domain.model.EmailData

class ResourceSharingConfigProvider(
    private val context: Context
) : SharingConfigProvider {

    override fun getSharingConfig(): EmailData {
        return EmailData(
            playStoreUrl = context.getString(R.string.share_message),
            userAgreementUrl = context.getString(R.string.practicum_offer),
            supportEmail = context.getString(R.string.support_email),
            message = context.getString(R.string.support_message),
            messageTitle = context.getString(R.string.support_subject),
        )
    }
}