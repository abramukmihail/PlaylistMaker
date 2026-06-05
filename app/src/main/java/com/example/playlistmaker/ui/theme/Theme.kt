package com.example.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            background = DarkBackground,
            onBackground = DarkText,
            primary = Primary,
            onSurfaceVariant = DarkIcon,
            primaryContainer = SearchDarkBg,
            secondaryContainer = ContainerDarkText
        )
    } else {
        lightColorScheme(
            background = LightBackground,
            onBackground = LightText,
            primary = Primary,
            onSurfaceVariant = LightIcon,
            primaryContainer = SearchLightBg,
            secondaryContainer = ContainerLightText
        )
    }
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}