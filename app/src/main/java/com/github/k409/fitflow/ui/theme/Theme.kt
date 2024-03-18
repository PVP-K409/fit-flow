package com.github.k409.fitflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.github.k409.fitflow.model.theme.ThemeColour
import com.github.k409.fitflow.model.theme.ThemeMode
import com.github.k409.fitflow.model.theme.ThemePreferences
import com.github.k409.fitflow.ui.theme.scheme.Amoled
import com.github.k409.fitflow.ui.theme.scheme.Green
import com.github.k409.fitflow.ui.theme.scheme.Pink

@Composable
fun FitFlowTheme(
    themePreferences: ThemePreferences,
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(
        themePreferences = themePreferences
    )
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = colorScheme.surfaceColorAtElevation(3.dp).toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                themePreferences.themeMode == ThemeMode.LIGHT || (themePreferences.themeMode == ThemeMode.AUTOMATIC && !isSystemInDarkTheme)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

@Composable
fun getColorScheme(
    themePreferences: ThemePreferences
): ColorScheme {
    return getColorScheme(
        themeMode = themePreferences.themeMode,
        themeColour = themePreferences.themeColour,
    )
}

@Composable
fun getColorScheme(
    themeMode: ThemeMode,
    themeColour: ThemeColour
): ColorScheme {

    val dynamicColorsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    return when (themeMode) {
        ThemeMode.DARK -> {
            when {
                themeColour == ThemeColour.GREEN -> Green.DarkColorScheme
                themeColour == ThemeColour.PINK -> Pink.PinkDarkColorScheme
                themeColour == ThemeColour.AMOLED -> Amoled.AmoledColorScheme
                themeColour == ThemeColour.DYNAMIC && dynamicColorsAvailable -> dynamicDarkColorScheme(
                    LocalContext.current
                )

                else -> Green.DarkColorScheme
            }
        }

        ThemeMode.LIGHT -> {
            when {
                themeColour == ThemeColour.GREEN -> Green.LightColorScheme
                themeColour == ThemeColour.PINK -> Pink.PinkLightColorScheme
                themeColour == ThemeColour.DYNAMIC && dynamicColorsAvailable -> dynamicLightColorScheme(
                    LocalContext.current
                )

                else -> Green.LightColorScheme
            }
        }

        ThemeMode.AUTOMATIC -> {
            when {
                isSystemInDarkTheme() && dynamicColorsAvailable -> dynamicDarkColorScheme(
                    LocalContext.current
                )
                dynamicColorsAvailable -> dynamicLightColorScheme(
                    LocalContext.current
                )

                else -> Green.LightColorScheme
            }
        }
    }
}
