package com.example.playlistmaker.medialibrary.ui.activiti.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.medialibrary.ui.activiti.fragments.FavoritesFragment
import com.example.playlistmaker.medialibrary.ui.activiti.fragments.PlaylistsFragment

class MediaLibraryViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}