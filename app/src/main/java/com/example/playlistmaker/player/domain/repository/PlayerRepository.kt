package com.example.playlistmaker.player.domain.repository

import kotlinx.coroutines.flow.StateFlow
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.models.PlaybackProgress

interface PlayerRepository {
    val playerState: StateFlow<PlayerState>
    val playbackProgress: StateFlow<PlaybackProgress?>

    suspend fun prepare(url: String?)
    fun play()
    fun pause()
    fun updateProgress()
    fun release()
}