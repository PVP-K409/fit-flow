package com.github.k409.fitflow.ui.screen.level

import com.github.k409.fitflow.R

internal val levels = listOf(
    Level(
        1,
        R.drawable.beginner,
        "Beginner",
        0,
        499,
    ),
    Level(
        2,
        R.drawable.novice,
        "Novice",
        500,
        2499,
    ),
    Level(
        3,
        R.drawable.intermediate,
        "Intermediate",
        2500,
        4999,
    ),
    Level(
        4,
        R.drawable.professional,
        "Professional",
        5000,
        7499,
    ),
    Level(
        5,
        R.drawable.master,
        "Master",
        7500,
        Int.MAX_VALUE,
    ),
)

internal data class Level(
    val id: Int = -1,
    val icon: Int = -1,
    val name: String = "",
    val minXP: Int = -1,
    val maxXP: Int = -1,
)
