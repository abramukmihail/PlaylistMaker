package com.example.playlistmaker.utils

import android.content.Context
import com.example.playlistmaker.R

object TrackInfoFormatter {
    fun getTrackCountText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count трек"
            count % 10 in 2..4 && (count % 100 < 10 || count % 100 > 20) -> "$count трека"
            else -> "$count треков"
        }
    }

    fun formatMinutes(minutes: Long): String {
        return when {
            minutes % 10 == 1L && minutes % 100 != 11L -> "$minutes минута"
            minutes % 10 in 2L..4L && (minutes % 100 < 10 || minutes % 100 > 20) -> "$minutes минуты"
            else -> "$minutes минут"
        }
    }
}
