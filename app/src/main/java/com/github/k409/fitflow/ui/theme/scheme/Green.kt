package com.github.k409.fitflow.ui.theme.scheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object Green {
    private val md_theme_light_primary = Color(0xFF006B55)
    private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
    private val md_theme_light_primaryContainer = Color(0xFF7EF8D4)
    private val md_theme_light_onPrimaryContainer = Color(0xFF002018)
    private val md_theme_light_secondary = Color(0xFF4B635B)
    private val md_theme_light_onSecondary = Color(0xFFFFFFFF)
    private val md_theme_light_secondaryContainer = Color(0xFFCEE9DD)
    private val md_theme_light_onSecondaryContainer = Color(0xFF072019)
    private val md_theme_light_tertiary = Color(0xFF406376)
    private val md_theme_light_onTertiary = Color(0xFFFFFFFF)
    private val md_theme_light_tertiaryContainer = Color(0xFFC4E7FF)
    private val md_theme_light_onTertiaryContainer = Color(0xFF001E2C)
    private val md_theme_light_error = Color(0xFFBA1A1A)
    private val md_theme_light_errorContainer = Color(0xFFFFDAD6)
    private val md_theme_light_onError = Color(0xFFFFFFFF)
    private val md_theme_light_onErrorContainer = Color(0xFF410002)
    private val md_theme_light_background = Color(0xFFFBFDFA)
    private val md_theme_light_onBackground = Color(0xFF191C1B)
    private val md_theme_light_surface = Color(0xFFFBFDFA)
    private val md_theme_light_onSurface = Color(0xFF191C1B)
    private val md_theme_light_surfaceVariant = Color(0xFFDBE5DF)
    private val md_theme_light_onSurfaceVariant = Color(0xFF3F4945)
    private val md_theme_light_outline = Color(0xFF6F7975)
    private val md_theme_light_inverseOnSurface = Color(0xFFEFF1EE)
    private val md_theme_light_inverseSurface = Color(0xFF2E312F)
    private val md_theme_light_inversePrimary = Color(0xFF60DBB8)

    /*val md_theme_light_shadow = Color(0xFF000000)*/
    private val md_theme_light_surfaceTint = Color(0xFF006B55)
    private val md_theme_light_outlineVariant = Color(0xFFBFC9C3)
    private val md_theme_light_scrim = Color(0xFF000000)

    private val md_theme_dark_primary = Color(0xFF60DBB8)
    private val md_theme_dark_onPrimary = Color(0xFF00382B)
    private val md_theme_dark_primaryContainer = Color(0xFF005140)
    private val md_theme_dark_onPrimaryContainer = Color(0xFF7EF8D4)
    private val md_theme_dark_secondary = Color(0xFFB2CCC1)
    private val md_theme_dark_onSecondary = Color(0xFF1D352D)
    private val md_theme_dark_secondaryContainer = Color(0xFF344C43)
    private val md_theme_dark_onSecondaryContainer = Color(0xFFCEE9DD)
    private val md_theme_dark_tertiary = Color(0xFFA8CBE2)
    private val md_theme_dark_onTertiary = Color(0xFF0D3446)
    private val md_theme_dark_tertiaryContainer = Color(0xFF284B5E)
    private val md_theme_dark_onTertiaryContainer = Color(0xFFC4E7FF)
    private val md_theme_dark_error = Color(0xFFFFB4AB)
    private val md_theme_dark_errorContainer = Color(0xFF93000A)
    private val md_theme_dark_onError = Color(0xFF690005)
    private val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
    private val md_theme_dark_background = Color(0xFF191C1B)
    private val md_theme_dark_onBackground = Color(0xFFE1E3E0)
    private val md_theme_dark_surface = Color(0xFF191C1B)
    private val md_theme_dark_onSurface = Color(0xFFE1E3E0)
    private val md_theme_dark_surfaceVariant = Color(0xFF3F4945)
    private val md_theme_dark_onSurfaceVariant = Color(0xFFBFC9C3)
    private val md_theme_dark_outline = Color(0xFF89938E)
    private val md_theme_dark_inverseOnSurface = Color(0xFF191C1B)
    private val md_theme_dark_inverseSurface = Color(0xFFE1E3E0)
    private val md_theme_dark_inversePrimary = Color(0xFF006B55)

    /*val md_theme_dark_shadow = Color(0xFF000000)*/
    private val md_theme_dark_surfaceTint = Color(0xFF60DBB8)
    private val md_theme_dark_outlineVariant = Color(0xFF3F4945)
    private val md_theme_dark_scrim = Color(0xFF000000)
    /*val seed = Color(0xFF006C56)*/

    val LightColorScheme = lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )

    val DarkColorScheme = darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )
}
