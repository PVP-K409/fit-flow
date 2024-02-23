package com.github.k409.fitflow.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.ViewTimeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.k409.fitflow.R

sealed class NavRoutes(
    val route: String,
    @StringRes val stringRes: Int,
    val icon: ImageVector
) {
    companion object {
        val navRoutes = listOf(Home, Activity, Goals, Marketplace)
        val bottomNavBarItems = listOf(Home, Activity, Goals, Marketplace)
    }

    data object Home : NavRoutes("home", R.string.home, Icons.Outlined.Home)

    data object Activity : NavRoutes("activity", R.string.activity, Icons.Outlined.ViewTimeline)

    data object Goals : NavRoutes("goals", R.string.goals, Icons.Outlined.Adjust)

    data object Marketplace : NavRoutes("marketplace", R.string.marketplace, Icons.Outlined.Store)

    data object ProfileCreation : NavRoutes("profileCreation", R.string.profileCreation, Icons.Outlined.PersonOutline)

    data object ProfileSettings : NavRoutes("profileSettings", R.string.profileSettings, Icons.Outlined.Settings)
}