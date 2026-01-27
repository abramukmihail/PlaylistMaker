package com.example.playlistmaker.player.domain.interactor

import kotlinx.coroutines.flow.StateFlow
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.models.PlaybackProgress

interface PlayerInteractor {
    val playerState: StateFlow<PlayerState>
    val playbackProgress: StateFlow<PlaybackProgress?>

    suspend fun prepare(url: String?)
    fun play()
    fun pause()
    fun togglePlayback()
    fun release()
}