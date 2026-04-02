package com.example.playlistmaker.mediaLibrary.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.DialogConfirmationBinding
import com.example.playlistmaker.databinding.FragmentPlaylistMainBinding
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.OperationResult
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.adapter.TrackAdapter
import com.example.playlistmaker.utils.CustomSnackbar
import com.example.playlistmaker.utils.TrackInfoFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var isPreparingShareText = false
    private var playlistId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = arguments?.getInt(PLAYLIST_ID_ARG, 0) ?: 0
        if (playlistId == 0) {
            findNavController().popBackStack()
            return
        }

        setupRecyclerView()
        setupBottomSheet()
        setupMenuBottomSheet()
        setupClickListeners()
        observeViewModel()

        viewModel.loadPlaylistInfo(playlistId)
        viewModel.loadPlaylistTracks(playlistId)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(
            tracks = emptyList(),
            onTrackClick = { track ->
                navigateToPlayer(track)
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )
        binding.tracksBottomSheet.tracksRecyclerView.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.tracksBottomSheet.root
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            isHideable = false
            isDraggable = true
            skipCollapsed = false
        }
        bottomSheet.isClickable = false
        bottomSheet.setOnClickListener(null)
        binding.overlay.isVisible = false
        updateBottomSheetPeekHeight()
    }

    private fun updateBottomSheetPeekHeight() {
        if (!isAdded || _binding == null) return
        binding.shareButton.post {
            if (!isAdded || _binding == null) return@post
            val location = IntArray(2)
            binding.shareButton.getLocationInWindow(location)
            val buttonsY = location[1]
            val windowHeight = binding.root.height
            val peekHeightValue = windowHeight - buttonsY + binding.shareButton.height - 24
            bottomSheetBehavior.peekHeight = peekHeightValue
            binding.tracksBottomSheet.root.alpha = 1f
        }
    }

    private fun setupMenuBottomSheet() {
        val bottomSheet = binding.menuBottomSheet.root
        menuBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            isDraggable = true
            peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height_menu)
            
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (!isAdded || _binding == null) return
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                            binding.menuBottomSheet.root.visibility = View.GONE
                        }
                        BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.overlay.isVisible = true
                            binding.menuBottomSheet.root.visibility = View.VISIBLE
                        }
                        else -> {}
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (!isAdded || _binding == null) return
                    binding.overlay.alpha = slideOffset + 0.5f
                }
            })
        }
        binding.menuBottomSheet.root.visibility = View.GONE
        binding.overlay.isVisible = false
        binding.overlay.setOnClickListener {
            hideMenuBottomSheet()
        }
        
        binding.playlistMenu.setOnClickListener {
            showMenuBottomSheet()
        }
        
        binding.menuBottomSheet.btnShare.setOnClickListener {
            hideMenuBottomSheet()
            onShareClicked()
        }
        
        binding.menuBottomSheet.btnEdit.setOnClickListener {
            hideMenuBottomSheet()
            navigateToEditPlaylist()
        }
        
        binding.menuBottomSheet.btnDeletePlaylist.setOnClickListener {
            hideMenuBottomSheet()
            showDeletePlaylistDialog()
        }
    }

    private fun setupClickListeners() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.shareButton.setOnClickListener {
            onShareClicked()
        }
    }

    private fun showMenuBottomSheet() {
        if (!isAdded) return
        binding.menuBottomSheet.root.visibility = View.VISIBLE
        binding.overlay.isVisible = true
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideMenuBottomSheet() {
        if (!isAdded) return
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun onShareClicked() {
        if (isPreparingShareText) return
        val trackCount = viewModel.trackCount.value ?: 0
        if (trackCount == 0) {
            CustomSnackbar.show(
                binding.root,
                getString(R.string.nothing_to_share)
            )
        } else {
            isPreparingShareText = true
            viewModel.prepareShareText()
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        val dialogBinding = DialogConfirmationBinding.inflate(layoutInflater)
        dialogBinding.tvTitle.text = getString(R.string.delete_track_confirm)
        dialogBinding.tvTitle.visibility = View.VISIBLE
        
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnNo.visibility = View.VISIBLE
        dialogBinding.btnYes.visibility = View.VISIBLE
        
        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            viewModel.removeTrackFromPlaylist(playlistId, track.trackId)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDeletePlaylistDialog() {
        val dialogBinding = DialogConfirmationBinding.inflate(layoutInflater)
        val playlistName = viewModel.playlist.value?.name ?: ""
        dialogBinding.tvTitle.text = getString(R.string.delete_playlist_confirm, playlistName)
        dialogBinding.tvTitle.visibility = View.VISIBLE
        
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnNo.visibility = View.VISIBLE
        dialogBinding.btnYes.visibility = View.VISIBLE
        
        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            viewModel.deletePlaylist(playlistId)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = Bundle().apply { putParcelable("track_extra", track) }
        findNavController().navigate(R.id.action_playlistFragment_to_audioPlayerFragment, bundle)
    }

    private fun navigateToEditPlaylist() {
        val bundle = Bundle().apply {
            putInt(PLAYLIST_ID_ARG, playlistId)
        }
        findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, bundle)
    }

    private fun observeViewModel() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                displayPlaylistInfo(it)
                displayMenuPlaylistInfo(it)
                updateBottomSheetPeekHeight()
            }
        }
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.updateTracks(tracks)
            updateTracksVisibility(tracks)
        }
        viewModel.totalDuration.observe(viewLifecycleOwner) { duration ->
            val minutes = duration / 60000
            binding.totalTime.text = resources.getQuantityString(R.plurals.minutes, minutes.toInt(), minutes.toInt())
        }
        viewModel.trackCount.observe(viewLifecycleOwner) { count ->
            binding.playlistTrackCount.text = resources.getQuantityString(R.plurals.tracks, count, count)
        }
        
        viewModel.deletePlaylistResult.observe(viewLifecycleOwner) { result ->
            if (result is OperationResult.Success) {
                CustomSnackbar.show(binding.root, getString(R.string.playlist_deleted))
                findNavController().popBackStack()
            }
        }
        
        viewModel.shareText.observe(viewLifecycleOwner) { shareText ->
            isPreparingShareText = false
            if (shareText.isNotEmpty()) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
                viewModel.clearShareText()
            }
        }
    }

    private fun updateTracksVisibility(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            binding.tracksBottomSheet.tracksRecyclerView.visibility = View.GONE
            binding.tracksBottomSheet.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.tracksBottomSheet.tracksRecyclerView.visibility = View.VISIBLE
            binding.tracksBottomSheet.tvEmptyState.visibility = View.GONE
        }
    }

    private fun displayPlaylistInfo(playlist: com.example.playlistmaker.mediaLibrary.domain.models.Playlist) {
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description ?: ""
        binding.playlistDescription.isVisible = !playlist.description.isNullOrEmpty()
        
        if (!playlist.coverPath.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(playlist.coverPath)
                .placeholder(R.drawable.ic_placeholder_312)
                .centerCrop()
                .into(binding.cover)
        } else {
            binding.cover.setImageResource(R.drawable.ic_placeholder_312)
        }
    }

    private fun displayMenuPlaylistInfo(playlist: com.example.playlistmaker.mediaLibrary.domain.models.Playlist) {
        binding.menuBottomSheet.menuPlaylistName.text = playlist.name
        binding.menuBottomSheet.menuPlaylistTrackCount.text = resources.getQuantityString(R.plurals.tracks, playlist.trackCount, playlist.trackCount)
        
        if (!playlist.coverPath.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(playlist.coverPath)
                .placeholder(R.drawable.ic_placeholder_45)
                .centerCrop()
                .into(binding.menuBottomSheet.menuCover)
        } else {
            binding.menuBottomSheet.menuCover.setImageResource(R.drawable.ic_placeholder_45)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PLAYLIST_ID_ARG = "playlist_id"
    }
}
