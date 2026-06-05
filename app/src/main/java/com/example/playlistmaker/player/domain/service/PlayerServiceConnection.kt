package com.example.playlistmaker.player.domain.service

import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceConnection {
    val state: StateFlow<PlayerState>
    val progress: Flow<PlaybackProgress>
    fun prepare(track: Track)
    fun playPause()
    fun release()
    fun startForegroundMode()
    fun stopForegroundMode()
    fun isPlaying(): Boolean
}