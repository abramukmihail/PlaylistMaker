package com.example.playlistmaker.mediaLibrary.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.mediaLibrary.ui.fragments.FavoritesFragment
import com.example.playlistmaker.mediaLibrary.ui.fragments.PlaylistsFragment

class MediaLibraryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FavoritesFragment()
            }

            else -> {
                PlaylistsFragment()
            }
        }
    }
}