package com.github.k409.fitflow.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.BrokenImage
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
    val icon: ImageVector = Icons.Outlined.BrokenImage,
) {
    companion object {
        val navRoutes = listOf(
            Home,
            Activity,
            Goals,
            Marketplace,
            Login,
            ProfileCreation,
            Settings,
        )
        val bottomNavBarItems = listOf(Home, Activity, Goals, Marketplace)
    }

    data object Default : NavRoutes("default", R.string.app_name)

    data object Home : NavRoutes("home", R.string.home, Icons.Outlined.Home)

    data object Activity : NavRoutes("activity", R.string.activity, Icons.Outlined.ViewTimeline)

    data object Goals : NavRoutes("goals", R.string.goals, Icons.Outlined.Adjust)

    data object Marketplace : NavRoutes("marketplace", R.string.marketplace, Icons.Outlined.Store)

    data object ProfileCreation :
        NavRoutes("profileCreation", R.string.profile_creation, Icons.Outlined.PersonOutline)

    data object Login : NavRoutes("login", R.string.user)

    data object Settings : NavRoutes("settings", R.string.settings, Icons.Outlined.Settings)
}
