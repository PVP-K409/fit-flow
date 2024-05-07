package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

val levels = listOf(
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

data class Level(
    val id: Int = -1,
    val icon: Int = -1,
    val name: String = "",
    val minXP: Int = -1,
    val maxXP: Int = -1,
)

fun getUserLevel(userXp: Int): Level {
    return levels.firstOrNull { userXp in it.minXP..it.maxXP } ?: levels.first()
}

fun Level.getProgress(userXp: Int): Float {
    val progress = if (this.maxXP == Int.MAX_VALUE) {
        if (userXp >= this.minXP) 1f else 0f
    } else {
        (userXp - this.minXP) / (this.maxXP - this.minXP).toFloat()
    }

    return progress.coerceIn(0f, 1f)
}

fun Level.getProgressText(userXp: Int): String {
    val progress = getProgress(userXp)

    val levelProgressText = if (maxXP == Int.MAX_VALUE) {
        "$minXP +"
    } else if (progress >= 1) {
        "$maxXP / $maxXP"
    } else {
        "$userXp / $maxXP"
    }

    return levelProgressText
}