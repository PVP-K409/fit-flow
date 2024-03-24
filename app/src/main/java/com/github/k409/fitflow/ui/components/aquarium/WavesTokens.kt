package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

internal object WavesTokens {
    val WaveBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x1A3C726A),
            Color(0xFF7FB1C0),
            Color(0x80838BC0),
            Color(0x6383FD6F),
        ),
    )
    val WavesAnimationDurationMillis = 6000
    val WavesAnimationEasing = LinearEasing

    val WaveAmplitude = 50f
    val WaveFrequency = 0.03f
}