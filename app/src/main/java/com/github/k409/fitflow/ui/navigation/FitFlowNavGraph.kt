package com.github.k409.fitflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.k409.fitflow.ui.screen.activity.ActivityScreen
import com.github.k409.fitflow.ui.screen.activity.exerciseSession.ExerciseSessionScreen
import com.github.k409.fitflow.ui.screen.aquarium.AquariumScreen
import com.github.k409.fitflow.ui.screen.friends.FriendsScreen
import com.github.k409.fitflow.ui.screen.goals.GoalCreation
import com.github.k409.fitflow.ui.screen.goals.GoalsScreen
import com.github.k409.fitflow.ui.screen.hydration.HydrationScreen
import com.github.k409.fitflow.ui.screen.info.InfoScreen
import com.github.k409.fitflow.ui.screen.inventory.InventoryScreen
import com.github.k409.fitflow.ui.screen.issue.ReportIssueScreen
import com.github.k409.fitflow.ui.screen.leaderboard.FriendsLeaderboard
import com.github.k409.fitflow.ui.screen.leaderboard.GlobalLeaderboardScreen
import com.github.k409.fitflow.ui.screen.level.LevelScreen
import com.github.k409.fitflow.ui.screen.level.LevelUpScreen
import com.github.k409.fitflow.ui.screen.login.LoginScreen
import com.github.k409.fitflow.ui.screen.market.MarketScreen
import com.github.k409.fitflow.ui.screen.profile.ProfileCreationScreen
import com.github.k409.fitflow.ui.screen.profile.ProfileScreen
import com.github.k409.fitflow.ui.screen.settings.SettingsScreen
import com.github.k409.fitflow.ui.screen.you.YouScreen

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
            ActivityScreen(navController = navController)
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

        composable(NavRoutes.ExerciseSession.route) {
            ExerciseSessionScreen()
        }

        composable(NavRoutes.Hydration.route) {
            HydrationScreen()
        }

        composable(NavRoutes.Inventory.route) {
            InventoryScreen()
        }

        composable(NavRoutes.Levels.route) {
            LevelScreen()
        }

        composable(NavRoutes.GlobalLeaderboard.route) {
            GlobalLeaderboardScreen()
        }

        composable(NavRoutes.FriendsLeaderboard.route) {
            FriendsLeaderboard()
        }

        composable(NavRoutes.Friends.route) {
            FriendsScreen()
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
            LoginScreen()
        }

        composable(NavRoutes.You.route) {
            YouScreen()
        }

        composable(NavRoutes.LevelUp.route) {
            LevelUpScreen()
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onNavigate = { route ->
                    navController.navigate(route.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        composable(NavRoutes.ReportIssue.route) {
            ReportIssueScreen(navigateBack = { navController.popBackStack() })
        }

        composable(NavRoutes.Info.route) {
            InfoScreen()
        }
    }
}
