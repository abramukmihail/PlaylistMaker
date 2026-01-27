package com.example.playlistmaker.player.domain.models

import java.util.Locale
import kotlin.text.format

data class PlaybackProgress(
    val currentPosition: Int,
    val duration: Int,
    val formattedCurrent: String,
    val formattedRemaining: String
) {
    companion object {
        fun create(currentPosition: Int, duration: Int): PlaybackProgress {
            return PlaybackProgress(
                currentPosition = currentPosition,
                duration = duration,
                formattedCurrent = formatTime(currentPosition),
                formattedRemaining = formatTime(duration - currentPosition)
            )
        }

        private fun formatTime(milliseconds: Int): String {
            val totalSeconds = milliseconds / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }
}