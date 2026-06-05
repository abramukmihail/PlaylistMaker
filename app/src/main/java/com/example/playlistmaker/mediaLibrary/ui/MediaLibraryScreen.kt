package com.example.playlistmaker.mediaLibrary.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.mediaLibrary.ui.fragments.FavoritesFragment
import com.example.playlistmaker.mediaLibrary.ui.fragments.PlaylistsListFragment
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.components.TopBar
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun MediaLibraryScreen(
    fragment: Fragment,
    settingsViewModel: SettingsViewModel,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val darkTheme by settingsViewModel.themeState.observeAsState(initial = false)
    val context = LocalContext.current
    val activity = context as? FragmentActivity ?: return

    val viewPager = remember { ViewPager2(activity) }
    val adapter = remember { MediaLibraryPagerAdapter(fragment, activity) }
    val tabTitles = listOf(
        stringResource(R.string.favourites),
        stringResource(R.string.playlists)
    )

    LaunchedEffect(Unit) {
        viewPager.adapter = adapter
    }

    LaunchedEffect(selectedTabIndex) {
        if (viewPager.currentItem != selectedTabIndex) {
            viewPager.setCurrentItem(selectedTabIndex, false)
        }
    }

    LaunchedEffect(viewPager) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                onTabSelected(position)
            }
        })
    }

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopBar(title = stringResource(R.string.media_library))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                indicator = { tabPositions ->
                    val currentTabPosition = tabPositions.getOrNull(selectedTabIndex)
                    if (currentTabPosition != null) {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(currentTabPosition)
                                .padding(horizontal = 16.dp)
                                .requiredHeight(2.dp)
                                .background(MaterialTheme.colorScheme.onBackground)
                        )
                    }
                },
                divider = {}
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            AndroidView(
                factory = { viewPager },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

class MediaLibraryPagerAdapter(
    private val fragment: Fragment,
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment()
            else -> PlaylistsListFragment()
        }
    }
}