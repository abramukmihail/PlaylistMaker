package com.example.playlistmaker.player.domain.interactor.impl


import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var progressUpdateJob: Job? = null

    override val playerState: StateFlow<PlayerState> = playerRepository.playerState
    override val playbackProgress: StateFlow<PlaybackProgress?> = playerRepository.playbackProgress

    override suspend fun prepare(url: String?) {
        playerRepository.prepare(url)
    }

    override fun play() {
        playerRepository.play()
        startProgressUpdates()
    }

    override fun pause() {
        playerRepository.pause()
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
                playerRepository.updateProgress()
                delay(PROGRESS_UPDATE_DELAY_MS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }


    override fun release() {
        stopProgressUpdates()
        playerRepository.release()
    }

    companion object {
        private const val PROGRESS_UPDATE_DELAY_MS = 300L
    }
}