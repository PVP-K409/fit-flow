package com.github.k409.fitflow.model.theme

data class ThemePreferences(
    val themeType: ThemeType = ThemeType.AUTOMATIC,
    val theme: Theme = Theme.GREEN
)