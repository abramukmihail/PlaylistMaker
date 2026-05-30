package com.example.playlistmaker.player.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.service.PlayerServiceConnection
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerService : Service(), PlayerServiceConnection {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    override val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _progress = MutableSharedFlow<PlaybackProgress>(replay = 1)
    override val progress = _progress.asSharedFlow()

    private var progressJob: Job? = null
    private var isBackground = false
    private var currentTrack: Track? = null

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlayerServiceConnection = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener { mp ->
                scope.launch {
                    _state.value = PlayerState.Ready(mp.duration)
                    startProgressUpdates()
                    updateForegroundNotification()
                }
            }
            setOnCompletionListener {
                scope.launch {
                    _state.value = PlayerState.Completed
                    stopProgressUpdates()
                    _progress.emit(PlaybackProgress.create(0, mediaPlayer?.duration ?: 0))
                    updateForegroundNotification()
                }
            }
            setOnErrorListener { _, what, extra ->
                scope.launch { _state.value = PlayerState.Error("Error: $what, $extra") }
                true
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows current track"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        scope.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun setCurrentTrack(track: Track) {
        currentTrack = track
    }

    override fun prepare(track: Track) {
        release()
        currentTrack = track
        _state.value = PlayerState.Preparing
        val url = track.previewUrl.orEmpty()
        if (url.isEmpty()) {
            _state.value = PlayerState.Error("No preview URL")
            return
        }
        try {
            mediaPlayer?.apply {
                reset()
                setDataSource(url)
                prepareAsync()
            }
        } catch (e: Exception) {
            _state.value = PlayerState.Error(e.message ?: "Unknown error")
        }
    }

    override fun playPause() {
        when (_state.value) {
            is PlayerState.Ready -> play()
            is PlayerState.Paused -> play()
            PlayerState.Completed -> play()
            is PlayerState.Playing -> pause()
            else -> {}
        }
    }

    private fun play() {
        mediaPlayer?.start()
        _state.update { PlayerState.Playing }
        startProgressUpdates()
        updateForegroundNotification()
    }

    private fun pause() {
        mediaPlayer?.pause()
        _state.update { PlayerState.Paused }
        stopProgressUpdates()
        emitCurrentProgress()
        updateForegroundNotification()
    }

    override fun release() {
        stopProgressUpdates()
        mediaPlayer?.reset()
        _state.update { PlayerState.Idle }
        stopForegroundNotification()
    }

    override fun setAppInBackground(background: Boolean) {
        isBackground = background
        updateForegroundNotification()
    }

    override fun isPlaying(): Boolean {
        return _state.value is PlayerState.Playing
    }

    private fun updateForegroundNotification() {
        val playing = _state.value is PlayerState.Playing
        if (isBackground && playing && _state.value != PlayerState.Completed) {
            showForegroundNotification()
        } else {
            stopForegroundNotification()
        }
    }

    private fun showForegroundNotification() {
        val artist = currentTrack?.artistName.orEmpty()
        val trackName = currentTrack?.trackName.orEmpty()

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artist - $trackName")
            .setSmallIcon(R.drawable.ic_play_100)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            else 0
        )
    }

    private fun stopForegroundNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                emitCurrentProgress()
                delay(PROGRESS_UPDATE_INTERVAL_MILLIS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun emitCurrentProgress() {
        val mp = mediaPlayer ?: return
        val pos = mp.currentPosition
        val dur = mp.duration
        if (dur > 0) {
            _progress.tryEmit(PlaybackProgress.create(pos, dur))
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "playback_channel"
        const val NOTIFICATION_ID = 101
        private const val PROGRESS_UPDATE_INTERVAL_MILLIS = 300L
    }
}