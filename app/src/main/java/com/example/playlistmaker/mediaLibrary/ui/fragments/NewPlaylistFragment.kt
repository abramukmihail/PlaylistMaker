package com.example.playlistmaker.mediaLibrary.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Button
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlayListBinding
import com.example.playlistmaker.mediaLibrary.ui.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.utils.CustomSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlayListBinding? = null
    protected val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var imageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.cover.setImageURI(uri)
            binding.coverIcon.visibility = View.GONE
            imageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupTextWatcher()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleExit()
            }
        })
    }

    private fun setupListeners() {
        binding.back.setOnClickListener {
            handleExit()
        }

        binding.cover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCreate.setOnClickListener {
            val name = binding.tilName.text.toString()
            val description = binding.tilDescription.text.toString()
            val imagePath = imageUri?.let { saveImageToInternalStorage(it, name) }

            viewModel.createPlaylist(name, description, imagePath)
            
            CustomSnackbar.show(requireView(), getString(R.string.playlist_created, name))
            findNavController().popBackStack()
        }
    }

    private fun setupTextWatcher() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = s.toString()
                binding.btnCreate.isEnabled = name.isNotBlank()
                
                val colorRes = if (name.isNotBlank()) R.color.blue else R.color.gray
                binding.btnCreate.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
                
                updateTextInputLayoutColors(name.isNotBlank())
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.tilName.addTextChangedListener(textWatcher)
    }

    private fun updateTextInputLayoutColors(isNotEmpty: Boolean) {
        val colorRes = if (isNotEmpty) R.color.blue else R.color.icon_color
        val colorStateList = ContextCompat.getColorStateList(requireContext(), colorRes)
        binding.tilNameContainer.setBoxStrokeColorStateList(colorStateList!!)
        binding.tilNameContainer.defaultHintTextColor = colorStateList
    }

    private fun handleExit() {
        if (imageUri != null || !binding.tilName.text.isNullOrBlank() || !binding.tilDescription.text.isNullOrBlank()) {
            showConfirmDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showConfirmDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_exit_confirmation, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .show()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnFinish).setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri, playlistName: String): String {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${playlistName}_${System.currentTimeMillis()}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
