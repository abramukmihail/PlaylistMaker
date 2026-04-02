package com.example.playlistmaker.mediaLibrary.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {

    private val editViewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getInt("playlist_id") ?: 0
        editViewModel.getPlaylist(playlistId)

        binding.btnCreate.text = getString(R.string.save_playlist)
        binding.headerTitle.text = getString(R.string.edit_playlist_title)

        editViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.tilName.setText(playlist.name)
            binding.tilDescription.setText(playlist.description)
            if (!playlist.coverPath.isNullOrEmpty()) {
                val file = File(playlist.coverPath)
                Glide.with(this)
                    .load(file)
                    .into(binding.cover)
                binding.coverIcon.visibility = View.GONE
            }
        }

        binding.btnCreate.setOnClickListener {
            val name = binding.tilName.text.toString()
            val description = binding.tilDescription.text.toString()
            val imagePath = imageUri?.let { saveImageToInternalStorage(it, name) }

            editViewModel.updatePlaylist(playlistId, name, description, imagePath)
            findNavController().popBackStack()
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun handleExit() {
        findNavController().popBackStack()
    }
}
