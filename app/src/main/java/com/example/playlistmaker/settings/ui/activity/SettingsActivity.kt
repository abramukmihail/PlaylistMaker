package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        with(binding) {
            back.setOnClickListener { finish() }
            shareAppContainer.setOnClickListener {
                viewModel.shareApp()
            }
            supportContainer.setOnClickListener {
                viewModel.contactSupport()
            }
            userAgreement.setOnClickListener {
                viewModel.transitionAgreement()
            }

            themeSwitcher.isChecked = viewModel.getCurrentThemeSetting()

            themeSwitcher.setOnCheckedChangeListener { _, checked ->
                viewModel.toggleDarkTheme(checked)
            }
        }
    }

    private fun setupObservers() {
        viewModel.themeState.observe(this) { isDarkTheme ->
            binding.themeSwitcher.isChecked = isDarkTheme
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadThemeState()
    }
}