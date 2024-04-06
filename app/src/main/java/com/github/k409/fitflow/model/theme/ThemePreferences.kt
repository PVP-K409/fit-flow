package com.github.k409.fitflow.model.theme

data class ThemePreferences(
    val themeMode: ThemeMode = ThemeMode.AUTOMATIC,
    val themeColour: ThemeColour = ThemeColour.GREEN,
)
