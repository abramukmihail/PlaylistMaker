package com.example.playlistmaker.settings.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()
        viewModel.loadThemeState()
    }

    private fun setupClickListeners() {
        with(binding) {
            shareAppContainer.setOnClickListener {
                viewModel.shareApp()
            }
            supportContainer.setOnClickListener {
                viewModel.contactSupport()
            }
            userAgreement.setOnClickListener {
                viewModel.transitionAgreement()
            }

            themeSwitcher.setOnCheckedChangeListener { _, checked ->
                viewModel.toggleDarkTheme(checked)
            }
        }
    }

    private fun setupObservers() {
        viewModel.themeState.observe(viewLifecycleOwner) { isDarkTheme ->
            binding.themeSwitcher.isChecked = isDarkTheme
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}