package com.github.k409.fitflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.k409.fitflow.ui.screens.activity.ActivityScreen
import com.github.k409.fitflow.ui.screens.aquarium.AquariumScreen
import com.github.k409.fitflow.ui.screens.goals.GoalCreation
import com.github.k409.fitflow.ui.screens.goals.GoalsScreen
import com.github.k409.fitflow.ui.screens.hydration.WaterLoggingScreen
import com.github.k409.fitflow.ui.screens.inventory.InventoryScreen
import com.github.k409.fitflow.ui.screens.login.LoginScreen
import com.github.k409.fitflow.ui.screens.market.MarketScreen
import com.github.k409.fitflow.ui.screens.profile.ProfileCreationScreen
import com.github.k409.fitflow.ui.screens.settings.SettingsScreen
import com.github.k409.fitflow.ui.screens.level.LevelScreen

@Composable
fun FitFlowNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.Aquarium.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavRoutes.Aquarium.route) {
            AquariumScreen(navController = navController)
        }
        composable(NavRoutes.Activity.route) {
            ActivityScreen()
        }
        composable(NavRoutes.Goals.route) {
            GoalsScreen(navController = navController)
        }
        composable(NavRoutes.Marketplace.route) {
            MarketScreen()
        }
        composable(NavRoutes.GoalCreation.route) {
            GoalCreation(navController = navController)
        }
        composable(NavRoutes.Hydration.route) {
            WaterLoggingScreen()
        }

        composable(NavRoutes.Inventory.route) {
            InventoryScreen()
        }

        composable(NavRoutes.Levels.route) {
            LevelScreen()
        }

        composable(NavRoutes.Marketplace.route) {
            MarketScreen()
        }

        composable(NavRoutes.ProfileCreation.route) {
            ProfileCreationScreen(navController)
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onSuccessfulSignIn = {
                    navController.navigate(NavRoutes.Aquarium.route) {
                        popUpTo(NavRoutes.Aquarium.route) {
                            inclusive = false
                        }
                    }
                },
            )
        }
    }
}
