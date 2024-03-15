package com.github.k409.fitflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.k409.fitflow.ui.screens.activity.ActivityScreen
import com.github.k409.fitflow.ui.screens.goals.GoalsScreen
import com.github.k409.fitflow.ui.screens.home.HomeScreen
import com.github.k409.fitflow.ui.screens.hydration.WaterLoggingScreen
import com.github.k409.fitflow.ui.screens.login.LoginScreen
import com.github.k409.fitflow.ui.screens.market.MarketScreen
import com.github.k409.fitflow.ui.screens.profile.ProfileCreationScreen
import com.github.k409.fitflow.ui.screens.settings.SettingsScreen

@Composable
fun FitFlowNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.Home.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(NavRoutes.Home.route) {
            HomeScreen()
        }
        composable(NavRoutes.Activity.route) {
            ActivityScreen()
        }
        composable(NavRoutes.Goals.route) {
            GoalsScreen()
        }
        composable(NavRoutes.Marketplace.route) {
            MarketScreen()
        }

        composable(NavRoutes.Hydration.route) {
            WaterLoggingScreen()
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
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) {
                            inclusive = false
                        }
                    }
                },
            )
        }
    }
}
