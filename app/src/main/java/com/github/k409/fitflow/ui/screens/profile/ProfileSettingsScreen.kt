package com.github.k409.fitflow.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.k409.fitflow.model.SettingsItem
import com.github.k409.fitflow.ui.navigation.NavRoutes

val moreOptionsList = listOf(
    SettingsItem("Edit Profile", Icons.Outlined.Create, NavRoutes.ProfileCreation.route),
)

fun navigateToProfileSettingsScreen(navController: NavController) {
    navController.navigate(NavRoutes.ProfileSettings.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun navigateToFeatureScreen(
    settingsItem: SettingsItem,
    navController: NavController,
) {
    navController.navigate(settingsItem.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun ProfileSettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(text = "")
    }
    moreOptionsList.forEach {
        MoreOptionsComp(it, navController)
    }
}

@Composable
fun MoreOptionsComp(
    settingsItem: SettingsItem,
    navController: NavController,
) {
    Row(
        modifier = Modifier.padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = settingsItem.listIcon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(6.dp),
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f),
        ) {
            Text(
                text = settingsItem.name,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        IconButton(onClick = { navigateToFeatureScreen(settingsItem, navController) }) {
            Image(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}
