package com.github.k409.fitflow.ui.screen.aquarium.component

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object AquariumTokens {
    val MorningBackground = Brush.linearGradient(
        colors = listOf(
            Color(255, 252, 0),
            Color(255, 255, 255),
        ),
    )
    val AfternoonBackground = Brush.linearGradient(
        colors = listOf(
            Color(67, 198, 172),
            Color(248, 255, 174),
        ),
    )
    val EveningBackground = Brush.linearGradient(
        colors = listOf(
            Color(0, 90, 167),
            Color(255, 253, 228),
        ),
    )
    val NightBackground = Brush.linearGradient(
        colors = listOf(
            Color(15, 32, 39),
            Color(32, 58, 67),
            Color(44, 83, 100),
        ),
    )
    val MinFishSize = 100.dp
    val MaxFishSize = 150.dp
    const val WaterLevelAnimationDuration = 1500
    val WaterLevelAnimationEasing = FastOutLinearInEasing
    const val MinAquariumBackgroundAlpha = 0.5f
    const val MaxAquariumBackgroundAlpha = 1.0f
    const val AquariumBackgroundAlphaAnimationDuration = 500
    val AquariumBackgroundAlphaAnimationEasing = FastOutLinearInEasing
}
