package com.example.playlistmaker.mediaLibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.mediaLibrary.ui.models.FavoritesState
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.player.ui.fragments.AudioPlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()

    private var favoritesAdapter: TrackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesAdapter = TrackAdapter(
            tracks = emptyList(),
            onTrackClick = { track -> navigateToPlayer(track) },
            onTrackLongClick = null
        )

        binding.favoritesRecyclerView.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> {
                binding.emptyStateLayout.isVisible = true
                binding.favoritesRecyclerView.isVisible = false
            }
            is FavoritesState.Content -> {
                binding.emptyStateLayout.isVisible = false
                binding.favoritesRecyclerView.isVisible = true
                favoritesAdapter?.updateTracks(state.tracks)
            }
        }
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = Bundle().apply {
            putParcelable(AudioPlayerFragment.TRACK_EXTRA, track)
        }
        findNavController().navigate(R.id.audioPlayerFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        favoritesAdapter = null
    }

}
