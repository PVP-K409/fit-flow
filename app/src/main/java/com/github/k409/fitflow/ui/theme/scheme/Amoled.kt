package com.github.k409.fitflow.ui.theme.scheme

import androidx.compose.ui.graphics.Color

object Amoled {
    private val md_theme_amoled_background = Color(0xFF000000)
    private val md_theme_amoled_surface = Color(0xFF000000)

    val AmoledColorScheme =
        Green.DarkColorScheme.copy(
            background = md_theme_amoled_background,
            surface = md_theme_amoled_surface,
        )
}
