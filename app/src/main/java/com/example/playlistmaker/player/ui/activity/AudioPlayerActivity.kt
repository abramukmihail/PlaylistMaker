package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Group

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()
        showTrack()
    }

    private fun setupClickListeners() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.startStop.setOnClickListener {
            viewModel.togglePlayback()
        }
    }

    private fun setupObservers() {
        viewModel.playerState.observe(this) { state ->
            updatePlayerState(state)
        }

        viewModel.playbackProgress.observe(this) { progress ->
            updatePlaybackProgress(progress)
        }
    }

    private fun showTrack() {
        val track = intent.getSerializableExtra(TRACK_EXTRA) as? Track

        if (track != null) {
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime2.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())

            setupOptionalField(binding.collectionNameGroup, binding.collectionName2, track.collectionName)
            setupOptionalField(binding.releaseDateGroup, binding.releaseDate2, track.releaseDate?.substring(0, 4))
            setupOptionalField(binding.primaryGenreNameGroup, binding.primaryGenreName2, track.primaryGenreName)
            setupOptionalField(binding.countryGroup, binding.country2, track.country)

            loadArtwork(track)
            viewModel.setupTrack(track)
        }
    }

    private fun updatePlayerState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = false
            }
            is PlayerState.Preparing -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = false
            }
            is PlayerState.Prepared -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = true
            }
            is PlayerState.Playing -> {
                binding.startStop.setImageResource(R.drawable.ic_pause_100)
                binding.startStop.isEnabled = true
            }
            is PlayerState.Paused -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = true
            }
            is PlayerState.Completed -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = true
            }
        }
    }

    private fun updatePlaybackProgress(progress: PlaybackProgress?) {
        progress?.let {
            binding.remainingTime.text = it.formattedCurrent
        } ?: run {
            binding.remainingTime.text = TIME_0
        }
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
            .into(binding.cover)
    }

    override fun onPause() {
        super.onPause()
        if (isChangingConfigurations) return

        if (viewModel.playerState.value is PlayerState.Playing) {
            viewModel.togglePlayback()
        }
    }
    companion object {
        private const val TIME_0 = "00:00"
        const val TRACK_EXTRA = "track_extra"
    }
}