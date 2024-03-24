package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object AquariumTokens {
    val AquariumBackground = Brush.linearGradient(
        colors = listOf(Color(0xFFA7B9D3), Color(0xFF9CED96), Color(0xffd0e7cf)),
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