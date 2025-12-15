package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        const val TRACK_EXTRA = "track_extra"
        private const val TIME_0 = "00:00"   }

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var artwork: ImageView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var collectionNameGroup: Group
    private lateinit var releaseDateGroup: Group
    private lateinit var primaryGenreNameGroup: Group
    private lateinit var countryGroup: Group
    private lateinit var backButton: Button
    private lateinit var playButton: ImageView
    private lateinit var remainingTime: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private val updateTimeRunnable = Runnable { updateCurrentTime() }
    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        initViews()
        setupClickListeners()
        initMediaPlayer()
        showTrack()
    }

    private fun initViews() {
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime2)
        artwork = findViewById(R.id.cover)
        collectionName = findViewById(R.id.collectionName2)
        releaseDate = findViewById(R.id.releaseDate2)
        primaryGenreName = findViewById(R.id.primaryGenreName2)
        country = findViewById(R.id.country2)
        collectionNameGroup = findViewById(R.id.collectionNameGroup)
        releaseDateGroup = findViewById(R.id.releaseDateGroup)
        primaryGenreNameGroup = findViewById(R.id.primaryGenreNameGroup)
        countryGroup = findViewById(R.id.countryGroup)
        backButton = findViewById(R.id.back)
        playButton = findViewById(R.id.start_stop)
        remainingTime = findViewById(R.id.remainingTime)
        handler = Handler(Looper.getMainLooper())
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
        playButton.setOnClickListener {
            playbackControl()
        }
    }
    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
    }

    private fun showTrack() {
        val track = intent.getSerializableExtra(TRACK_EXTRA) as? Track

        if (track != null) {

            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())

            setupOptionalField(collectionNameGroup, collectionName, track.collectionName)
            setupOptionalField(releaseDateGroup, releaseDate, track.releaseDate?.substring(0, 4))
            setupOptionalField(primaryGenreNameGroup, primaryGenreName, track.primaryGenreName)
            setupOptionalField(countryGroup, country, track.country)

            loadArtwork(track)

            preparePlayer(track.previewUrl)
        }
    }
    private fun preparePlayer(url: String) {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
                playButton.setImageResource(R.drawable.ic_play_100)
                remainingTime.text = TIME_0
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                playButton.setImageResource(R.drawable.ic_play_100)
                remainingTime.text = TIME_0
                handler.removeCallbacks(updateTimeRunnable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause_100)
        playerState = STATE_PLAYING
        startUpdatingTime()
    }
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play_100)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable)
    }
    private fun updateCurrentTime() {
        if (mediaPlayer.isPlaying) {
            val currentPosition = mediaPlayer.currentPosition
            remainingTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
            handler.postDelayed(updateTimeRunnable, 300)
        }
    }
    private fun startUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable)
        handler.post(updateTimeRunnable)
    }
    private fun setupOptionalField(group: Group, textView: TextView, value: String?) {
        if (value.isNullOrEmpty()) {
            group.visibility = View.GONE
        } else {
            textView.text = value
            group.visibility = View.VISIBLE
        }
    }


    private fun loadArtwork(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_312)
            .error(R.drawable.ic_placeholder_312)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_radius)))
            .into(artwork)
    }
    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer.release()
    }

}