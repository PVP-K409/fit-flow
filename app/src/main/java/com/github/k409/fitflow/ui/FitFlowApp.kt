@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.k409.fitflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

        when (currentScreen) {
            NavRoutes.Home -> {
                bottomBarState.value = true
                topBarState.value = true
            }

            else -> {
                bottomBarState.value = true
                topBarState.value = true
            }
        }


        Scaffold(
            topBar = {
                FitFlowTopBar(
                    topBarState = topBarState.value,
                    currentRoute = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null &&
                            !NavRoutes.bottomNavBarItems.contains(currentScreen),
                    navigateUp = { navController.navigateUp() },
                    containerColor = if (currentScreen == NavRoutes.Home) Color(0xffb5c8e8) else MaterialTheme.colorScheme.surface
                )
            },
            bottomBar = {
                FitFlowBottomBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    visible = bottomBarState.value,
                    containerColor = if (currentScreen == NavRoutes.Home) Color(0xFFE4C68B) else MaterialTheme.colorScheme.surface
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
    containerColor: Color
) {
    if (topBarState) {
        Surface {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = containerColor
                ),
                title = {
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

                })

        }
    }


}

@Composable
fun FitFlowBottomBar(
    navController: NavController,
    currentDestination: NavDestination?,
    visible: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    if (visible) {
        NavigationBar(
            modifier = Modifier
                .background(containerColor)
                .padding(12.dp)
                .clip(RoundedCornerShape(30.dp))
                .height(62.dp),
            windowInsets = NavigationBarDefaults.windowInsets.exclude(WindowInsets(bottom = 12.dp))
        ) {
            NavRoutes.bottomNavBarItems.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = null
                        )
                    },
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
