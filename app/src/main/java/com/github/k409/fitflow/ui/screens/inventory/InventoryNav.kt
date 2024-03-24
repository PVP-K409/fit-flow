package com.github.k409.fitflow.ui.screens.inventory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.Water
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val items = listOf(
    BottomNavigationItem(
        title = "Fishes",
        selectedIcon = Icons.Filled.Water,
        unselectedIcon = Icons.Outlined.Water,
    ),
    BottomNavigationItem(
        title = "Decorations",
        selectedIcon = Icons.Filled.Anchor,
        unselectedIcon = Icons.Outlined.Anchor,
    ),
)
