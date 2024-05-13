package com.github.k409.fitflow.ui.screen.settings

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.exyte.animatednavbar.utils.noRippleClickable
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.theme.ThemeColour
import com.github.k409.fitflow.model.theme.ThemeMode
import com.github.k409.fitflow.model.theme.ThemePreferences
import com.github.k409.fitflow.ui.common.FancyIndicatorTabs
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.navigation.NavRoutes
import com.github.k409.fitflow.ui.theme.getColorScheme
import kotlinx.coroutines.CoroutineScope

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
            onUpdateThemePreferences = settingsViewModel::updateThemePreferences,
        )

        ProfileSettingsGroup(navController = navController)

        AccountSettingsGroup(
            currentUser = currentUser,
            coroutineScope = coroutineScope,
            settingsViewModel = settingsViewModel,
        )

        LanguageSettingsGroup()
    }
}

@Composable
private fun AccountSettingsGroup(
    currentUser: User,
    coroutineScope: CoroutineScope,
    settingsViewModel: SettingsViewModel,
) {
    SettingsEntryGroupText(title = stringResource(R.string.account_settings_group_title))

    SettingsEntry(
        title = stringResource(R.string.email),
        text = currentUser.email.ifEmpty { stringResource(R.string.not_logged_in) },
    )

    SettingsEntry(
        title = stringResource(R.string.log_out),
        text = stringResource(R.string.you_are_logged_in_as, currentUser.name),
        onClick = settingsViewModel::signOut,
    )

    SettingsGroupSpacer()
}

@Composable
private fun ProfileSettingsGroup(
    navController: NavHostController,
) {
    SettingsEntryGroupText(title = stringResource(R.string.profile_settings_group_title))

    SettingsEntry(
        title = stringResource(R.string.edit_profile),
        text = stringResource(R.string.edit_your_profile),
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
    SettingsEntryGroupText(title = stringResource(R.string.appearance_settings_group_title))

    ThemeModeSelector(
        themePreferences = themePreferences,
        onUpdateThemePreferences = onUpdateThemePreferences,
    )

    ThemeColourSelector(
        themePreferences = themePreferences,
        onUpdateThemePreferences = onUpdateThemePreferences,
    )

    SettingsGroupSpacer()
}

@Composable
private fun ThemeModeSelector(
    themePreferences: ThemePreferences,
    onUpdateThemePreferences: (ThemePreferences) -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            horizontal = 32.dp,
            vertical = 16.dp,
        ),
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(R.string.mode),
            style = typography.titleMedium,
        )

        val values = ThemeMode.entries.map { stringResource(it.title) }

        FancyIndicatorTabs(
            values = values,
            selectedIndex = values.indexOf(stringResource(themePreferences.themeMode.title)),
            onValueChange = {
                val themeMode = ThemeMode.entries[it]

                onUpdateThemePreferences(
                    themePreferences.copy(
                        themeMode = themeMode,
                    ),
                )
            },
        )
    }
}

@Composable
private fun ColumnScope.ThemeColourSelector(
    themePreferences: ThemePreferences,
    onUpdateThemePreferences: (ThemePreferences) -> Unit,
) {
    AnimatedVisibility(
        visible = themePreferences.themeMode != ThemeMode.AUTOMATIC,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.colour),
                style = typography.titleMedium,
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    bottom = 8.dp,
                ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val dynamicColorsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

                val themeColours = when (themePreferences.themeMode) {
                    ThemeMode.DARK -> ThemeColour.darkColours
                    ThemeMode.LIGHT -> ThemeColour.lightColours
                    else -> emptyList()
                }.toMutableList().apply { if (!dynamicColorsAvailable) remove(ThemeColour.DYNAMIC) }

                items(
                    themeColours,
                ) { theme ->
                    val colorScheme = getColorScheme(
                        ThemePreferences(
                            themeMode = themePreferences.themeMode,
                            themeColour = theme,
                        ),
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .noRippleClickable {
                                onUpdateThemePreferences(
                                    themePreferences.copy(
                                        themeColour = theme,
                                    ),
                                )
                            },
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(color = colorScheme.primaryContainer)
                                .border(
                                    width = 2.dp,
                                    color = if (themePreferences.themeColour == theme) colorScheme.primary else Color.Transparent,
                                    shape = CircleShape,
                                ),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(theme.title),
                            style = typography.labelMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSettingsGroup() {
    val localeOptions = mapOf(
        R.string.system_default to "",
        R.string.en to "en",
        R.string.lt to "lt",
    ).mapKeys { stringResource(it.key) }

    var currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()

    if (currentLocale == LocaleListCompat.getEmptyLocaleList().toLanguageTags()) {
        currentLocale = localeOptions.keys.first()
    }

    SettingsEntryGroupText(title = stringResource(R.string.localization))

    LanguageSelector(
        selectedLocaleValue = currentLocale,
        localeOptions = localeOptions,
        onLocaleSelect = { selectionLocale ->
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(
                    localeOptions[selectionLocale],
                )
            )
        },
    )

    SettingsGroupSpacer()
}

@Composable
private fun LanguageSelector(
    selectedLocaleValue: String,
    localeOptions: Map<String, String>,
    onLocaleSelect: (String) -> Unit,
) {
    val selectedIndex = remember {
        mutableIntStateOf(localeOptions.values.indexOf(selectedLocaleValue).let {
            if (it == -1) 0 else it
        })
    }

    Column(
        modifier = Modifier.padding(
            horizontal = 32.dp,
            vertical = 16.dp,
        ),
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(R.string.language),
            style = typography.titleMedium,
        )

        FancyIndicatorTabs(
            values = localeOptions.keys.toList(),
            selectedIndex = selectedIndex.intValue,
            onValueChange = {
                selectedIndex.intValue = it
                onLocaleSelect(localeOptions.keys.toList()[it])
            },
        )
    }
}
