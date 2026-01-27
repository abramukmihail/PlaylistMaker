package com.example.playlistmaker.sharing.data.provider

import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingConfigProvider {
    fun getSharingConfig(): EmailData
}