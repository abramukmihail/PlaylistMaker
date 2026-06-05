package com.example.playlistmaker.player.ui.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.mediaLibrary.ui.adapter.PlaylistSmallAdapter
import com.example.playlistmaker.player.domain.models.PlaybackProgress
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CustomSnackbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import com.example.playlistmaker.player.ui.custom.PlaybackButtonView
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import com.example.playlistmaker.player.data.PlayerService
import com.example.playlistmaker.player.domain.service.PlayerServiceConnection

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: PlaylistSmallAdapter
    private var serviceConnection: ServiceConnection? = null
    private var boundService: PlayerServiceConnection? = null
    private var currentTrack: Track? = null

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

         currentTrack = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(TRACK_EXTRA)
        }

        if (currentTrack != null) {
            displayTrackInfo(currentTrack!!)
            setupClickListeners(currentTrack!!)
            setupBottomSheet()
            observeViewModel()
            bindPlayerService()
            checkNotificationPermission()
        } else {
            findNavController().popBackStack()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }
    }
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }
    }


    private fun displayTrackInfo(track: Track) {
        bindPlayerService()
        checkNotificationPermission()
        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName

            val durationMillis = try {
                track.trackTimeMillis.toIntOrNull() ?: 0
            } catch (e: NumberFormatException) {
                0
            }
            trackTime2.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationMillis)

            Glide.with(requireContext())
                .load(track.getCoverArtwork())
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_312)
                .error(R.drawable.ic_placeholder_312)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_radius)))
                .into(cover)

            collectionNameGroup.isVisible = !track.collectionName.isNullOrEmpty()
            if (collectionNameGroup.isVisible) {
                collectionName2.text = track.collectionName
            }

            releaseDateGroup.isVisible = !track.releaseDate.isNullOrEmpty()
            if (releaseDateGroup.isVisible) {
                releaseDate2.text = track.releaseDate?.take(4)
            }

            primaryGenreNameGroup.isVisible = !track.primaryGenreName.isNullOrEmpty()
            if (primaryGenreNameGroup.isVisible) {
                primaryGenreName2.text = track.primaryGenreName
            }

            countryGroup.isVisible = !track.country.isNullOrEmpty()
            if (countryGroup.isVisible) {
                country2.text = track.country
            }
        }
    }
    private fun bindPlayerService() {
        val track = currentTrack ?: return
        val intent = Intent(requireContext(), PlayerService::class.java).apply {
            putExtra(PlayerService.EXTRA_TRACK, track)
        }
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val localBinder = binder as? PlayerService.LocalBinder
                boundService = localBinder?.getService()
                boundService?.let {
                    viewModel.onServiceConnected(it)
                    viewModel.setupTrack(currentTrack!!)
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                boundService = null
            }
        }
        requireContext().bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let { requireContext().unbindService(it) }
        _binding = null
    }

    private fun setupClickListeners(track: Track) {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.playbackButton.setOnPlaybackClickListener(object : PlaybackButtonView.OnPlaybackClickListener {
            override fun onPlaybackClick() {
                viewModel.togglePlayback()
            }
        })

        binding.toFavourites.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.toPlaylist.setOnClickListener {
            showPlaylistBottomSheet()
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.playlistsBottomSheet.root

        val density = resources.displayMetrics.density
        val peekHeightInPx = (505 * density).toInt()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            isDraggable = true
            peekHeight = peekHeightInPx
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!isAdded || _binding == null) return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.playlistsBottomSheet.root.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.playlistsBottomSheet.root.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        playlistAdapter = PlaylistSmallAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }

        binding.playlistsBottomSheet.playlistsRecyclerView.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.playlistsBottomSheet.btnNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistsBottomSheet.root.visibility = View.GONE
        binding.overlay.visibility = View.GONE
    }

    private fun showPlaylistBottomSheet() {
        viewModel.loadPlaylists()
        binding.playlistsBottomSheet.root.visibility = View.VISIBLE
        binding.overlay.visibility = View.VISIBLE
        binding.overlay.alpha = 1f
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun observeViewModel() {
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

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
        }

        viewModel.addToPlaylistStatus.observe(viewLifecycleOwner) { status ->
            status?.let { (success, playlistName) ->
                val message = if (success) {
                    getString(R.string.track_added_to_playlist, playlistName)
                } else {
                    getString(R.string.track_already_in_playlist, playlistName)
                }
                CustomSnackbar.show(binding.root, message)
                viewModel.resetAddToPlaylistStatus()

                if (success) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.loadPlaylists()
                }
            }
        }
    }

    private fun updatePlayerState(state: PlayerState) {
        when (state) {
            is PlayerState.Idle, is PlayerState.Preparing -> {
                binding.playbackButton.setPlaybackState(false)
                binding.playbackButton.isEnabled = false
            }
            is PlayerState.Ready, is PlayerState.Paused, is PlayerState.Completed -> {
                binding.playbackButton.setPlaybackState(false)
                binding.playbackButton.isEnabled = true
            }
            is PlayerState.Playing -> {
                binding.playbackButton.setPlaybackState(true)
                binding.playbackButton.isEnabled = true
            }
            is PlayerState.Error -> {
                binding.playbackButton.setPlaybackState(false)
                binding.playbackButton.isEnabled = false
            }
        }
    }

    private fun updatePlaybackProgress(progress: PlaybackProgress?) {
        binding.remainingTime.text = progress?.formattedCurrent ?: "00:00"
    }
    override fun onResume() {
        super.onResume()
        viewModel.onAppForegrounded()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.playerState.value is PlayerState.Playing) {
            viewModel.onAppBackgrounded()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TRACK_EXTRA = "track_extra"
        private const val REQUEST_CODE_NOTIFICATIONS = 1001
    }
}