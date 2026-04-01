package com.example.playlistmaker.mediaLibrary.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackCount: Int = 0
) : Parcelable
