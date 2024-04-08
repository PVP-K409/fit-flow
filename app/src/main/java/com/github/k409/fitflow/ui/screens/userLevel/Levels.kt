package com.github.k409.fitflow.ui.screens.userLevel

internal val levels = listOf(
    Level(
        "Beginner",
        0,
        499),
    Level(
        "Novice",
        500,
        2499),
    Level(
        "Intermediate",
        2500,
        4999),
    Level(
        "Expert",
        5000,
        7499),
    Level(
        "Master",
        7500,
        Int.MAX_VALUE
    )
)
internal data class Level(
    val name: String = "",
    val minXP: Int = -1,
    val maxXP: Int = -1,
)
