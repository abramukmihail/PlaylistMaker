package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default)
    override val playerState: StateFlow<PlayerState> = _playerState

    private val _playbackProgress = MutableStateFlow<PlaybackProgress?>(null)
    override val playbackProgress: StateFlow<PlaybackProgress?> = _playbackProgress

    override suspend fun prepare(url: String?) {
        _playerState.update { PlayerState.Preparing }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                if (url.isNullOrEmpty()) {
                    _playerState.update { PlayerState.Default }
                    return@apply
                }

                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    val duration = it.duration
                    _playerState.update { PlayerState.Prepared(duration) }
                    _playbackProgress.update { PlaybackProgress.create(0, duration) }
                }

                setOnCompletionListener {
                    _playerState.update { PlayerState.Completed }
                    mediaPlayer?.let { mp ->
                        _playbackProgress.update { PlaybackProgress.create(0, mp.duration) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _playerState.update { PlayerState.Default }
            }
        }
    }

    override fun play() {
        when (val currentState = playerState.value) {
            is PlayerState.Prepared, is PlayerState.Paused -> {
                mediaPlayer?.start()
                _playerState.update { PlayerState.Playing }
            }
            is PlayerState.Completed -> {
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
                _playerState.update { PlayerState.Playing }
            }
            else -> {}
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _playerState.update { PlayerState.Paused }
    }

    override fun updateProgress() {
        val currentPosition = mediaPlayer?.currentPosition ?: 0
        val duration = mediaPlayer?.duration ?: 0

        if (duration > 0) {
            _playbackProgress.update { PlaybackProgress.create(currentPosition, duration) }
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.update { PlayerState.Default }
        _playbackProgress.update { null }
    }
}