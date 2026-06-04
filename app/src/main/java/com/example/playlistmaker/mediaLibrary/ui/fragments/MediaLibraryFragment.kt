package com.example.playlistmaker.mediaLibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.mediaLibrary.ui.MediaLibraryScreen
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.MediaLibraryViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by inject()
    private val mediaLibraryViewModel: MediaLibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val selectedTabIndex by mediaLibraryViewModel.selectedTabIndex.collectAsStateWithLifecycle()
                MediaLibraryScreen(
                    fragment = this@MediaLibraryFragment,
                    settingsViewModel = settingsViewModel,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { mediaLibraryViewModel.setSelectedTabIndex(it) }
                )
            }
        }
    }
}
