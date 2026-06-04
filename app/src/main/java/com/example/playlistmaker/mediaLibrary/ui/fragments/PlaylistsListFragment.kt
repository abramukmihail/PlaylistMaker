package com.example.playlistmaker.mediaLibrary.ui.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.example.playlistmaker.mediaLibrary.ui.PlaylistsListScreen
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.inject
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsListFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {return ComposeView(requireContext()).apply {
        setContent {
            PlaylistsListScreen(
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                onNewPlaylistClick = { navigateToNewPlaylist() },
                onPlaylistClick = { playlist -> navigateToPlaylist(playlist) }
            )
    }
        }
    }
    private fun navigateToNewPlaylist() {
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
    }

    private fun navigateToPlaylist(playlist: com.example.playlistmaker.mediaLibrary.domain.models.Playlist) {
        val bundle = Bundle().apply {
            putInt("playlist_id", playlist.id)
        }
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistFragment, bundle)
    }
}
