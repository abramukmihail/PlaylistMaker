package com.example.playlistmaker.mediaLibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.player.ui.fragments.AudioPlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.ui.platform.ComposeView
import com.example.playlistmaker.mediaLibrary.ui.FavoritesScreen
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.inject

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FavoritesScreen(
                    viewModel = viewModel,
                    settingsViewModel = settingsViewModel,
                    onTrackClick = { track -> navigateToPlayer(track) }
                )
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

}
