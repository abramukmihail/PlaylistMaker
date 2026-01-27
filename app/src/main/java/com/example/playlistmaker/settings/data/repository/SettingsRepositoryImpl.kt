package com.example.playlistmaker.settings.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    context: Context
) : SettingsRepository {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_DARK_THEME, enabled)
            apply()
        }
        applyTheme(enabled)
    }

    override fun isDarkThemeEnabled(): Boolean =
        sharedPreferences.getBoolean(KEY_DARK_THEME, false)

    override fun applySavedTheme() {
        applyTheme(isDarkThemeEnabled())
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        private const val SHARED_PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
    }
}