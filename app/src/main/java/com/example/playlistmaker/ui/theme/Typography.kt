package com.example.playlistmaker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium))
val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular))

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)