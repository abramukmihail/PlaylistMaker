package com.example.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = arguments?.getParcelable<Track>(TRACK_EXTRA)

        setupClickListeners(track)
        setupObservers()
        showTrack(track)
    }

    private fun setupClickListeners(track: Track?) {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.startStop.setOnClickListener {
            viewModel.togglePlayback()
        }
        binding.toFavourites.setOnClickListener {
            track?.let { viewModel.onFavoriteClicked(it) }
        }
    }

    private fun setupObservers() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            updatePlayerState(state)
        }

        viewModel.playbackProgress.observe(viewLifecycleOwner) { progress ->
            updatePlaybackProgress(progress)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            val iconRes = if (isFavorite) R.drawable.ic_add_to_favourites_51 else R.drawable.ic_favourite_filled
            binding.toFavourites.setImageResource(iconRes)
        }
    }

    private fun showTrack(track: Track?) {
        if (track != null) {
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.trackTime2.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(track.trackTimeMillis.toLongOrNull() ?: 0L)

            setupOptionalField(
                binding.collectionNameGroup,
                binding.collectionName2,
                track.collectionName
            )
            setupOptionalField(
                binding.releaseDateGroup,
                binding.releaseDate2,
                track.releaseDate?.take(4)
            )
            setupOptionalField(
                binding.primaryGenreNameGroup,
                binding.primaryGenreName2,
                track.primaryGenreName
            )
            setupOptionalField(binding.countryGroup, binding.country2, track.country)

            loadArtwork(track)
            viewModel.setupTrack(track)
        }
    }

    private fun updatePlayerState(state: PlayerState) {
        when (state) {
            is PlayerState.Default, is PlayerState.Idle, is PlayerState.Preparing -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = false
            }

            is PlayerState.Prepared, is PlayerState.Paused, is PlayerState.Completed -> {
                binding.startStop.setImageResource(R.drawable.ic_play_100)
                binding.startStop.isEnabled = true
            }

            is PlayerState.Playing -> {
                binding.startStop.setImageResource(R.drawable.ic_pause_100)
                binding.startStop.isEnabled = true
            }
        }
    }

    private fun updatePlaybackProgress(progress: PlaybackProgress?) {
        binding.remainingTime.text = progress?.formattedCurrent ?: TIME_0
    }

    private fun setupOptionalField(
        group: androidx.constraintlayout.widget.Group,
        textView: android.widget.TextView,
        value: String?
    ) {
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
        if (viewModel.playerState.value is PlayerState.Playing) {
            viewModel.togglePlayback()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TIME_0 = "00:00"
        const val TRACK_EXTRA = "track_extra"
    }
}
