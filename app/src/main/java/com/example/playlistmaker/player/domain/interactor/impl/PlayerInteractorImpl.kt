package com.example.playlistmaker.player.domain.interactor.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.apply
import kotlin.let
import kotlin.text.isNullOrEmpty

class PlayerInteractorImpl : PlayerInteractor {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var mediaPlayer: MediaPlayer? = null
    private var progressUpdateJob: Job? = null

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default)
    override val playerState: StateFlow<PlayerState> = _playerState

    private val _playbackProgress = MutableStateFlow<PlaybackProgress?>(null)
    override val playbackProgress: StateFlow<PlaybackProgress?> = _playbackProgress

    override suspend fun prepare(url: String?) {
        _playerState.value = PlayerState.Preparing

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                if (url.isNullOrEmpty()) {
                    _playerState.value = PlayerState.Default
                    return@apply
                }

                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    val duration = it.duration
                    _playerState.value = PlayerState.Prepared(duration)
                    _playbackProgress.value = PlaybackProgress.create(0, duration)
                }

                setOnCompletionListener {
                    _playerState.value = PlayerState.Completed
                    stopProgressUpdates()
                    mediaPlayer?.let { mp ->
                        _playbackProgress.value = PlaybackProgress.create(0, mp.duration)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _playerState.value = PlayerState.Default
            }
        }
    }

    override fun play() {
        when (val currentState = playerState.value) {
            is PlayerState.Prepared, is PlayerState.Paused -> {
                mediaPlayer?.start()
                _playerState.value = PlayerState.Playing
                startProgressUpdates()
            }
            is PlayerState.Completed -> {
                mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
                _playerState.value = PlayerState.Playing
                startProgressUpdates()
            }
            else -> {}
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _playerState.value = PlayerState.Paused
        stopProgressUpdates()
    }

    override fun togglePlayback() {
        when (playerState.value) {
            is PlayerState.Playing -> pause()
            else -> play()
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()

        progressUpdateJob = coroutineScope.launch {
            while (isActive) {
                updateProgress()
                delay(300L)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun updateProgress() {
        val currentPosition = mediaPlayer?.currentPosition ?: 0
        val duration = mediaPlayer?.duration ?: 0

        if (duration > 0) {
            _playbackProgress.value = PlaybackProgress.create(currentPosition, duration)
        }
    }

    override fun release() {
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = PlayerState.Default
        _playbackProgress.value = null
    }
}