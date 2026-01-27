package com.example.playlistmaker.sharing.domain.model

data class EmailData(
    val playStoreUrl: String,
    val userAgreementUrl: String,
    val supportEmail: String,
    val message: String,
    val messageTitle: String
)