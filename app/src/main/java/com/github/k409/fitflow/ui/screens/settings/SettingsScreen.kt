package com.github.k409.fitflow.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.exyte.animatednavbar.utils.noRippleClickable
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.theme.Theme
import com.github.k409.fitflow.model.theme.ThemePreferences
import com.github.k409.fitflow.model.theme.ThemeType
import com.github.k409.fitflow.ui.common.FancyIndicatorTabs
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.components.settings.SettingsEntry
import com.github.k409.fitflow.ui.components.settings.SettingsEntryGroupText
import com.github.k409.fitflow.ui.components.settings.SettingsGroupSpacer
import com.github.k409.fitflow.ui.navigation.NavRoutes
import com.github.k409.fitflow.ui.theme.getColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by settingsViewModel.currentUser.collectAsState(initial = User())
    val settingsUiState by settingsViewModel.settingsUiState.collectAsStateWithLifecycle()

    if (settingsUiState is SettingsUiState.Loading) {
        FitFlowCircularProgressIndicator()
        return
    }

    val themePreferences = (settingsUiState as SettingsUiState.Success).themePreferences

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AppearanceSettingsGroup(
            themePreferences = themePreferences,
            onUpdateThemePreferences = settingsViewModel::updateThemePreferences
        )

        ProfileSettingsGroup(navController = navController)

        AccountSettingsGroup(
            currentUser = currentUser,
            coroutineScope = coroutineScope,
            settingsViewModel = settingsViewModel
        )
    }
}

@Composable
private fun AccountSettingsGroup(
    currentUser: User,
    coroutineScope: CoroutineScope,
    settingsViewModel: SettingsViewModel
) {
    SettingsEntryGroupText(title = "Account")

    SettingsEntry(
        title = "Email",
        text = currentUser.email.ifEmpty { "Not logged in" },
    )

    SettingsEntry(
        title = "Log out",
        text = "You are logged in as ${currentUser.name}",
        onClick = {
            coroutineScope.launch {
                settingsViewModel.signOut()
            }
        },
    )

    SettingsGroupSpacer()
}

@Composable
private fun ProfileSettingsGroup(
    navController: NavHostController,
) {
    SettingsEntryGroupText(title = "Profile")

    SettingsEntry(
        title = "Edit profile",
        text = "Edit your profile",
        onClick = {
            navController.navigate(NavRoutes.ProfileCreation.route)
        },
    )

    SettingsGroupSpacer()
}

@Composable
private fun ColumnScope.AppearanceSettingsGroup(
    themePreferences: ThemePreferences,
    onUpdateThemePreferences: (ThemePreferences) -> Unit,
) {
    SettingsEntryGroupText(title = "Appearance")

    Column(
        modifier = Modifier.padding(
            horizontal = 32.dp,
            vertical = 16.dp
        )
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Colors",
            style = typography.titleMedium,
        )
        val values = ThemeType.entries
            .map { it.title }

        FancyIndicatorTabs(
            values = values,
            selectedIndex = values.indexOf(themePreferences.themeType.title),
            onValueChange = {
                val themeType = ThemeType.entries[it]

                onUpdateThemePreferences(
                    themePreferences.copy(
                        themeType = themeType
                    )
                )
            }
        )
    }

    AnimatedVisibility(
        visible = themePreferences.themeType != ThemeType.AUTOMATIC,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        ) {
            Text(
                text = "Theme",
                style = typography.titleMedium,
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    bottom = 8.dp
                ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val themes = when (themePreferences.themeType) {
                    ThemeType.DARK -> Theme.darkColorThemes
                    ThemeType.LIGHT -> Theme.lightColorThemes
                    else -> emptyList()
                }

                items(
                    themes
                ) { theme ->
                    val colorScheme = getColorScheme(
                        ThemePreferences(
                            themeType = themePreferences.themeType,
                            theme = theme,
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .noRippleClickable {
                                onUpdateThemePreferences(
                                    themePreferences.copy(
                                        theme = theme
                                    )
                                )
                            })
                    {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(color = colorScheme.primaryContainer)
                                .border(
                                    width = 2.dp,
                                    color = if (themePreferences.theme == theme) colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = theme.title,
                            style = typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    SettingsGroupSpacer()
}
