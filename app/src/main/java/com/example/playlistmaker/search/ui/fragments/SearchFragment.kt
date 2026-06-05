package com.example.playlistmaker.search.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.playlistmaker.search.ui.SearchScreen
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.player.ui.fragments.AudioPlayerFragment
import androidx.fragment.app.Fragment
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.inject
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R


class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
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
        findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.restoreQuery()
    }
}