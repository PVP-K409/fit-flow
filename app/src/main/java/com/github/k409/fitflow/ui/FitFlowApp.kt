@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.k409.fitflow.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.k409.fitflow.ui.navigation.FitFlowNavGraph
import com.github.k409.fitflow.ui.navigation.NavRoutes
import com.github.k409.fitflow.ui.screens.profile.navigateToProfileSettingsScreen
import com.github.k409.fitflow.ui.theme.FitFlowTheme

@Composable
fun FitFlowApp() {
    FitFlowTheme(dynamicColor = false) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val currentScreen = NavRoutes.navRoutes.find { it.route == currentDestination?.route }
            ?: NavRoutes.Home

        val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
        val topBarState = rememberSaveable { (mutableStateOf(true)) }


        Scaffold(
            topBar = {
                FitFlowTopBar(
                    topBarState = topBarState.value,
                    currentRoute = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null &&
                            !NavRoutes.bottomNavBarItems.contains(currentScreen),
                    navController = navController,
                navigateUp =  { navController.navigateUp() } )
            },
            bottomBar = {
                FitFlowBottomBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    visible = !(navController.previousBackStackEntry != null &&
                            !NavRoutes.bottomNavBarItems.contains(currentScreen)) // bottomBarState.value
                )
            }
        ) { innerPadding ->
            FitFlowNavGraph(
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitFlowTopBar(
    modifier: Modifier = Modifier,
    topBarState: Boolean,
    currentRoute: NavRoutes,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    navController: NavController,
) {
    Surface {
        AnimatedVisibility(
            visible = topBarState,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = currentRoute.stringRes),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
                modifier = modifier,
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                },
                actions = {
                    var isClicked by remember { mutableStateOf(false) }
                    IconButton(onClick = { isClicked = true }) {
                        Image(
                            // replace with proper user image later
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Profile settings",
                            modifier = Modifier
                                .size(160.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                )
                        )
                    }
                    if (isClicked) {
                        isClicked = false
                        navigateToProfileSettingsScreen(navController = navController)
                    }
                })
        }
    }
}

@Composable
fun FitFlowBottomBar(
    navController: NavController,
    currentDestination: NavDestination?,
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = ExitTransition.None,
    ) {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets.exclude(WindowInsets(bottom = 12.dp))
        ) {
            NavRoutes.bottomNavBarItems.forEach { screen ->
                NavigationBarItem(icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = null
                    )
                },
                    label = { Text(stringResource(screen.stringRes)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }
    }
}
