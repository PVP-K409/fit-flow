@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.k409.fitflow.ui

import android.icu.text.CompactDecimalFormat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.items.dropletbutton.DropletButton
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.navigation.FitFlowNavGraph
import com.github.k409.fitflow.ui.navigation.NavRoutes
import java.util.Locale

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
    } else if (user.gender.isEmpty() || user.dateOfBirth.isEmpty()) {
        NavRoutes.ProfileCreation.route
    } else {
        NavRoutes.Aquarium.route
    }

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val topBarState = rememberSaveable { (mutableStateOf(false)) }

    UpdateTopAndBottomBarVisibility(
        currentScreen = currentScreen,
        bottomBarState = bottomBarState,
        topBarState = topBarState,
    )

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
                user = user,
            )
        },
        bottomBar = {
            FitFlowBottomBar(
                navController = navController,
                currentScreen = currentScreen,
                visible = !(
                    navController.previousBackStackEntry != null && !NavRoutes.bottomNavBarItems.contains(
                        currentScreen,
                    )
                    ) && bottomBarState.value,
                containerColor = if (currentScreen == NavRoutes.Aquarium) Color(0xFFE4C68B) else MaterialTheme.colorScheme.surface,
            )
        },
    ) { innerPadding ->
        val topPadding =
            if (currentScreen == NavRoutes.Aquarium) 0.dp else innerPadding.calculateTopPadding()
        val bottomPadding = innerPadding.calculateBottomPadding().minus(10.dp).coerceAtLeast(0.dp)

        FitFlowNavGraph(
            modifier = Modifier.padding(
                bottom = bottomPadding,
                top = topPadding,
            ),
            navController = navController,
            startDestination = startDestination,
        )
    }
}

@Composable
private fun UpdateTopAndBottomBarVisibility(
    currentScreen: NavRoutes,
    bottomBarState: MutableState<Boolean>,
    topBarState: MutableState<Boolean>,
) {
    when (currentScreen) {
        NavRoutes.Login, NavRoutes.Default -> {
            bottomBarState.value = false
            topBarState.value = false
        }

        NavRoutes.Settings, NavRoutes.ProfileCreation -> {
            bottomBarState.value = false
            topBarState.value = true
        }

        NavRoutes.Aquarium -> {
            bottomBarState.value = true
            topBarState.value = false
        }

        else -> {
            bottomBarState.value = true
            topBarState.value = true
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
    user: User,
) {
    if (topBarState) {
        Surface {
            TopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = Color.Transparent),
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
                    PointsAndXPIndicatorRow(
                        modifier = Modifier.padding(end = 16.dp),
                        points = user.points,
                        xp = user.xp,
                    )

                    IconButton(
                        onClick = {
                            navController.navigate(NavRoutes.Settings.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            error = {
                                Icon(
                                    modifier = Modifier.padding(3.dp),
                                    imageVector = Icons.Outlined.PersonOutline,
                                    contentDescription = null,
                                )
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape,
                                )
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    shape = CircleShape,
                                ),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun PointsAndXPIndicatorRow(
    modifier: Modifier = Modifier,
    points: Int,
    xp: Int,
) {
    fun formatShortNumber(number: Number): String {
        return CompactDecimalFormat.getInstance(
            Locale.getDefault(),
            CompactDecimalFormat.CompactStyle.SHORT,
        ).format(number)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ElevatedAssistChip(
            onClick = { },
            border = AssistChipDefaults.assistChipBorder(enabled = false),
            shape = RoundedCornerShape(100),
            label = {
                Text(
                    text = formatShortNumber(points),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            },
        )

        ElevatedAssistChip(
            onClick = { },
            border = AssistChipDefaults.assistChipBorder(enabled = false),
            shape = RoundedCornerShape(100),
            label = {
                Text(
                    text = formatShortNumber(xp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.xp),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            },
        )
    }
}

@Composable
fun FitFlowBottomBar(
    navController: NavController,
    currentScreen: NavRoutes,
    visible: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    style: BottomBarStyle = BottomBarStyle.Animated,
) {
    val onNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (style == BottomBarStyle.Animated) {
        AnimatedBottomBar(
            visible = visible,
            currentScreen = currentScreen,
            onNavigate = onNavigate,
        )
    } else if (style == BottomBarStyle.Material) {
        MaterialNavigationBar(
            visible = visible,
            containerColor = containerColor,
            currentScreen = currentScreen,
            onNavigate = onNavigate,
        )
    }
}

@Composable
private fun MaterialNavigationBar(
    visible: Boolean,
    containerColor: Color,
    currentScreen: NavRoutes,
    onNavigate: (String) -> Unit,
) {
    if (visible) {
        NavigationBar(
            modifier = Modifier
                .background(containerColor)
                .windowInsetsPadding(BottomAppBarDefaults.windowInsets)
                .height(60.dp),
        ) {
            NavRoutes.bottomNavBarItems.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = null,
                        )
                    },
                    selected = currentScreen.route == screen.route,
                    onClick = {
                        onNavigate(screen.route)
                    },
                )
            }
        }
    }
}

@Composable
private fun AnimatedBottomBar(
    visible: Boolean,
    currentScreen: NavRoutes,
    onNavigate: (String) -> Unit,
) {
    if (visible) {
        AnimatedNavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(BottomAppBarDefaults.windowInsets)
                .height(64.dp),
            selectedIndex = NavRoutes.bottomNavBarItems.indexOf(currentScreen),
            barColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            ballColor = MaterialTheme.colorScheme.primary,
            ballAnimation = Teleport(tween(300)),
        ) {
            NavRoutes.bottomNavBarItems.forEach { route ->
                DropletButton(
                    modifier = Modifier.fillMaxSize(),
                    size = 26.dp,
                    dropletColor = MaterialTheme.colorScheme.primary,
                    isSelected = currentScreen.route == route.route,
                    onClick = { onNavigate(route.route) },
                    icon = route.iconRes,
                )
            }
        }
    }
}

enum class BottomBarStyle {
    Animated, Material
}
