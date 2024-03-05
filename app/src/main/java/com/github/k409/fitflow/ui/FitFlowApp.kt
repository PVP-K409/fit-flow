@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.k409.fitflow.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
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

@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FitFlowApp(
    sharedUiState: SharedUiState.Success,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val user = sharedUiState.user

    val currentScreen =
        NavRoutes.navRoutes.find { it.route == currentDestination?.route } ?: NavRoutes.Default

    val startDestination = if (user.uid.isEmpty()) {
        NavRoutes.Login.route
    } else {
        NavRoutes.Home.route
    }

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val topBarState = rememberSaveable { (mutableStateOf(false)) }

    when (currentScreen) {
        NavRoutes.Login -> {
            bottomBarState.value = false
            topBarState.value = false
        }

        NavRoutes.Settings -> {
            bottomBarState.value = false
            topBarState.value = true
        }

        NavRoutes.Home -> {
            bottomBarState.value = true
            topBarState.value = false
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
                canNavigateBack = navController.previousBackStackEntry != null && !NavRoutes.bottomNavBarItems.contains(
                    currentScreen,
                ),
                navigateUp = { navController.navigateUp() },
                navController = navController,
                containerColor = if (currentScreen == NavRoutes.Home) Color(0xffb5c8e8) else MaterialTheme.colorScheme.surface,
            )
        },
        bottomBar = {
            FitFlowBottomBar(
                navController = navController,
                currentDestination = currentDestination,
                visible = !(
                        navController.previousBackStackEntry != null && !NavRoutes.bottomNavBarItems.contains(
                            currentScreen,
                        )
                        ) && bottomBarState.value,
                containerColor = if (currentScreen == NavRoutes.Home) Color(0xFFE4C68B) else MaterialTheme.colorScheme.surface,
            )
        },
    ) { innerPadding ->
        val topPadding =
            if (currentScreen == NavRoutes.Home) 0.dp else innerPadding.calculateTopPadding()
        val bottomPadding = innerPadding.calculateBottomPadding()

        FitFlowNavGraph(
            modifier = Modifier.padding(
                bottom = bottomPadding,
                top = topPadding,
//                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
//                end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
            ),
            navController = navController,
            startDestination = startDestination,
        )
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
    containerColor: Color,
    navController: NavController,
) {
    if (topBarState) {
        Surface {
            TopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.topAppBarColors().copy(
//                    containerColor = containerColor
                ),
                title = {
                    Text(
                        text = stringResource(id = currentRoute.stringRes),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(NavRoutes.Settings.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        Image(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Profile settings",
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape,
                                )
                                .padding(3.dp),
                        )
                    }
                },
            )
        }
    }
}

@Composable
fun FitFlowBottomBar(
    navController: NavController,
    currentDestination: NavDestination?,
    visible: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    if (visible) {
        NavigationBar(
            modifier = Modifier
                .background(containerColor)
//                .padding(8.dp)
//                .clip(RoundedCornerShape(50))
                .height(70.dp),
            windowInsets = NavigationBarDefaults.windowInsets.exclude(WindowInsets(bottom = 12.dp)),
        ) {
            NavRoutes.bottomNavBarItems.forEach { screen ->
                NavigationBarItem(
                    modifier = Modifier,
//                        .padding(top = 10.dp),
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = null,
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
                    },
                )
            }
        }
    }
}
