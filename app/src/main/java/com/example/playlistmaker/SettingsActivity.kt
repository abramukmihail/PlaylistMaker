package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<Button>(R.id.back)
        val shareAppContainer = findViewById<FrameLayout>(R.id.share_app_container)
        val supportContainer = findViewById<FrameLayout>(R.id.support_container)
        val agreementContainer = findViewById<FrameLayout>(R.id.user_agreement)
        backButton.setOnClickListener { finish() }
        shareAppContainer.setOnClickListener { shareApp() }
        supportContainer.setOnClickListener { contactSupport() }
        agreementContainer.setOnClickListener { transitionAgreement() }
    }

    private fun shareApp() {
        val message = resources.getString(R.string.share_message)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/url"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(shareIntent)
    }

    private fun contactSupport() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = "mailto:".toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
        supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
        startActivity(supportIntent)
    }

    private fun transitionAgreement() {
        val agreementIntent = Intent(
            Intent.ACTION_VIEW,
            getString(R.string.practicum_offer).toUri()
        )
        startActivity(agreementIntent)

    }
}
