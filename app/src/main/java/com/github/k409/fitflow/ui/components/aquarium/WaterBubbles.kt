package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Bubble(
    val start: Offset,
    val end: Offset,
    val radius: Float,
    val duration: Long,
    val easing: Easing,
)

private const val minAnimationDuration = 3000L
private const val maxAnimationDuration = 10000L

private const val minRadius = 10f
private const val maxRadius = 26f

@Composable
fun WaterBubbles(
    colors: List<Color>,
    bubbleCount: Int,
    modifier: Modifier = Modifier,
    offsetX: Float,
    offsetY: Float,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    Box(
        modifier = modifier,
    ) {
        val bubbles = remember {
            List(bubbleCount) {
                val radius = Random.nextLong(minRadius.toLong(), maxRadius.toLong()).toFloat()

                Bubble(
                    start = Offset(
                        x = Random.nextFloat() * offsetX,
                        y = offsetX,
                    ),
                    end = Offset(
                        x = Random.nextFloat() * offsetX,
                        y = 0f + radius + (Random.nextFloat() * offsetY / 2f),
                    ),
                    duration = Random.nextLong(minAnimationDuration, maxAnimationDuration),
                    easing = when (Random.nextInt(3)) {
                        0 -> LinearEasing
                        1 -> FastOutSlowInEasing
                        else -> CubicBezierEasing(0.23f, 0.12f, 0.25f, 1.0f)
                    },
                    radius = radius,
                )
            }
        }

        bubbles.forEach { bubble ->
            val xValue by infiniteTransition.animateFloat(
                initialValue = bubble.start.x,
                targetValue = bubble.end.x,
                animationSpec = infiniteRepeatable(
                    animation = tween(bubble.duration.toInt(), easing = bubble.easing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "",
            )

            val yValue by infiniteTransition.animateFloat(
                initialValue = bubble.start.y,
                targetValue = bubble.end.y,
                animationSpec = infiniteRepeatable(
                    animation = tween(bubble.duration.toInt(), easing = bubble.easing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "",
            )

            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = bubble.duration.toInt()
                        0.5f at 0 using LinearOutSlowInEasing
                        1f at bubble.duration.toInt() using LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart,
                ),
                label = "",
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
            ) {
                drawCircle(
                    color = Color.Blue,
                    radius = bubble.radius * 1.2f,
                    center = Offset(xValue, yValue),
                )

                drawCircle(
                    brush = Brush.linearGradient(
                        colors = colors,
                        start = Offset(xValue - 90, yValue),
                        end = Offset(xValue + 90, yValue),
                    ),
                    radius = bubble.radius,
                    center = Offset(xValue, yValue),
                )
            }
        }
    }
}
