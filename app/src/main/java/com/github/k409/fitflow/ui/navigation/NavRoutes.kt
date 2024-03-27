package com.github.k409.fitflow.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.ViewTimeline
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.k409.fitflow.R

sealed class NavRoutes(
    val route: String,
    @StringRes val stringRes: Int,
    val icon: ImageVector = Icons.Outlined.ErrorOutline,
    @DrawableRes val iconRes: Int = R.drawable.error_24px,
) {
    companion object {
        val navRoutes = listOf(
            Aquarium,
            Activity,
            Goals,
            Marketplace,
            Hydration,
            Login,
            ProfileCreation,
            Settings,
            Inventory,
            GoalCreation,
        )
        val bottomNavBarItems = listOf(Aquarium, Activity, Hydration, Goals, Marketplace)
    }

    data object Default : NavRoutes("default", R.string.app_name)

    data object Aquarium : NavRoutes("aquarium", R.string.home, Icons.Outlined.Home, R.drawable.home_24px)

    data object Activity : NavRoutes(
        "activity",
        R.string.activity,
        Icons.Outlined.ViewTimeline,
        R.drawable.view_timeline_24px,
    )

    data object Goals :
        NavRoutes("goals", R.string.goals, Icons.Outlined.Adjust, R.drawable.adjust_24px)

    data object Marketplace :
        NavRoutes("marketplace", R.string.marketplace, Icons.Outlined.Store, R.drawable.store_24px)

    data object Hydration :
        NavRoutes("hydration", R.string.waterLogging, Icons.Outlined.WaterDrop, R.drawable.water_drop_24px)

    data object ProfileCreation :
        NavRoutes("profileCreation", R.string.profile_creation, Icons.Outlined.PersonOutline)

    data object Login : NavRoutes("login", R.string.user)

    data object Settings : NavRoutes("settings", R.string.settings, Icons.Outlined.Settings)

    data object Inventory : NavRoutes("inventory", R.string.inventory, Icons.Outlined.Inventory2)

    data object GoalCreation: NavRoutes("goalCreation",R.string.goal_creation, Icons.Outlined.Add )
}
