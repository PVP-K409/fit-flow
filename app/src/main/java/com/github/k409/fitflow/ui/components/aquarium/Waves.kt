package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

@Composable
fun AnimatedWaves(
    modifier: Modifier = Modifier,
    waveCount: Int = 1,
    verticalWavesStart: Float = 0.3f,
    waveAmplitude: Float = 50f,
    waveFrequency: Float = 0.03f,
    brush: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF7FABC0), Color(0x1A3C726A)),
    ),
) {
    val waveAmplitudeState = remember { mutableFloatStateOf(waveAmplitude) }
    val waveFrequencyState = remember { mutableFloatStateOf(waveFrequency) }

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val amplitudeAnimation by infiniteTransition.animateFloat(
        initialValue = waveAmplitudeState.floatValue,
        targetValue = waveAmplitudeState.floatValue * 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500 * 4, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
    )

    val frequencyAnimation by infiniteTransition.animateFloat(
        initialValue = waveFrequencyState.floatValue,
        targetValue = waveFrequencyState.floatValue * 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500 * 4, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
    )

    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        drawWave(
            waveCount = waveCount,
            verticalStart = verticalWavesStart,
            amplitude = amplitudeAnimation,
            frequency = frequencyAnimation,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0x1A3C726A),
                    Color(0xFF7FB1C0),
                    Color(0x80838BC0),
                    Color(0x6383FD6F),
                ),
            ),
        )
    }
}

fun DrawScope.drawWave(
    waveCount: Int,
    verticalStart: Float,
    amplitude: Float,
    frequency: Float,
    brush: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF7FABC0), Color(0x1A3C726A)),
    ),
) {
    val wavePath = Path()

    val startY = size.height * verticalStart
    wavePath.moveTo(0f, startY)

    for (i in 0 until waveCount) {
        val x1 = (i * size.width) / waveCount
        val y1 =
            startY + amplitude * sin((i * size.width / waveCount) * frequency)

        val x2 = ((i + 1) * size.width) / waveCount
        val y2 =
            startY + amplitude * sin(((i + 1) * size.width / waveCount) * frequency)

        wavePath.cubicTo(
            x1 + size.width / (2 * waveCount),
            y1,
            x1 + size.width / (2 * waveCount),
            y2,
            x2,
            y2,
        )
    }

    wavePath.lineTo(size.width, size.height)
    wavePath.lineTo(0f, size.height)
    wavePath.close()

    drawPath(
        path = wavePath,
        brush = brush,
    )
}
