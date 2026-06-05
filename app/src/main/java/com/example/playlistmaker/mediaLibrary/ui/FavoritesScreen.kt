package com.example.playlistmaker.mediaLibrary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.mediaLibrary.ui.models.FavoritesState
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.components.EmptyState
import com.example.playlistmaker.ui.components.TrackItem
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel,
    onTrackClick: (Track) -> Unit
) {
    val darkTheme by settingsViewModel.themeState.observeAsState(initial = false)
    val state by viewModel.state.observeAsState(FavoritesState.Empty)

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state) {
                is FavoritesState.Content -> {
                    val tracks = (state as FavoritesState.Content).tracks
                    if (tracks.isEmpty()) {
                        EmptyState(
                            iconRes = R.drawable.ic_not_found_120,
                            modifier = Modifier
                                .padding(top = 60.dp),
                            message = stringResource(R.string.no_media)
                        )
                    } else {
                        LazyColumn {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            items(tracks, key = { it.trackId }) { track ->
                                TrackItem(track = track, onClick = onTrackClick)
                            }
                        }
                    }
                }
                FavoritesState.Empty -> {
                    EmptyState(
                        iconRes = R.drawable.ic_not_found_120,
                        modifier = Modifier
                            .padding(top = 60.dp),
                        message = stringResource(R.string.no_media)
                    )
                }
            }
        }
    }
}