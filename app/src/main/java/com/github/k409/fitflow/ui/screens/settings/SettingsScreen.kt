package com.github.k409.fitflow.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.components.settings.SettingsEntry
import com.github.k409.fitflow.ui.components.settings.SettingsEntryGroupText
import com.github.k409.fitflow.ui.components.settings.SettingsGroupSpacer
import com.github.k409.fitflow.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by settingsViewModel.currentUser.collectAsState(initial = User())

    Column(
        modifier = Modifier.fillMaxSize(),
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
    }
}
