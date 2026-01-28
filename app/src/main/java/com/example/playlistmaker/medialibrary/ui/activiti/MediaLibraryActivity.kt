package com.example.playlistmaker.medialibrary.ui.activiti

import android.os.Bundle
import com.example.playlistmaker.R
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMedialibraryBinding
import com.example.playlistmaker.medialibrary.ui.activiti.adapter.MediaLibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedialibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMedialibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(this)

        tabMediator = TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favourites)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.back.setOnClickListener {
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}