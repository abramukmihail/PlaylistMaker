package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator
import kotlin.apply

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(emailData: EmailData) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/url"
            putExtra(Intent.EXTRA_TEXT, emailData.playStoreUrl)
        }
        context.startActivity(Intent.createChooser(shareIntent, null))
    }

    override fun openEmail(emailData: EmailData) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, emailData.messageTitle)
            putExtra(Intent.EXTRA_TEXT, emailData.message)
        }
        context.startActivity(emailIntent)
    }

    override fun openLink(emailData: EmailData) {
        val browserIntent = Intent(Intent.ACTION_VIEW, emailData.userAgreementUrl.toUri())
        context.startActivity(browserIntent)
    }
}