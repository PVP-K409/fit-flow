package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.noRippleClickable
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CircularPrimaryFish(
    modifier: Modifier = Modifier,
    initialFishSize: Dp = 100.dp,
    maxFishSize: Dp = 300.dp,
    movementRadius: Dp = 100.dp,
) {
    val coroutineScope = rememberCoroutineScope()

    val primaryFishPainter = painterResource(id = R.drawable.primary_fish)

    val currentFishSize = remember { Animatable(initialFishSize.value) }
    val currentRotationAngle = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteTransition")
    val animationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000),
            repeatMode = RepeatMode.Restart,
        ),
        label = "RotationAnimation",
    )

    val xPosition = remember(animationValue) {
        (cos(animationValue.toDouble()) * movementRadius.value).toFloat()
    }

    val yPosition = remember(animationValue) {
        (sin(animationValue.toDouble()) * movementRadius.value).toFloat()
    }

    Image(
        painter = primaryFishPainter,
        contentDescription = "Fish",
        modifier = modifier
            .size(currentFishSize.value.dp)
            .offset {
                IntOffset(
                    xPosition.roundToInt(),
                    yPosition.roundToInt(),
                )
            }
            .graphicsLayer(rotationZ = currentRotationAngle.value)
            .noRippleClickable(onClick = {
                coroutineScope.launch {
                    currentRotationAngle.animateTo(
                        currentRotationAngle.value + 360f,
                        animationSpec = tween(durationMillis = 2000),
                    )
                }
            }, onLongClick = {
                coroutineScope.launch {
                    val newFishSize = Random.nextLong(
                        initialFishSize.value.toLong(),
                        maxFishSize.value.toLong(),
                    )

                    currentFishSize.animateTo(newFishSize.toFloat())
                }
            }),
    )
}

@Composable
fun AnimatedPrimaryFish(
    modifier: Modifier = Modifier,
    initialFishSize: Dp = 100.dp,
    maxFishSize: Dp = 300.dp,
) {
    val coroutineScope = rememberCoroutineScope()

    val primaryFishPainter = painterResource(id = R.drawable.primary_fish)
    val currentFishSize = remember { Animatable(initialFishSize.value) }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val xPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "FishXPositionAnimation",
    )

    Image(
        painter = primaryFishPainter,
        contentDescription = "Fish",
        modifier = modifier
            .size(currentFishSize.value.dp)
            .graphicsLayer(
                translationX = xPosition,
            )
            .noRippleClickable(onClick = {
                coroutineScope.launch {
                    if (currentFishSize.value < maxFishSize.value) {
                        currentFishSize.animateTo(currentFishSize.value + 30)
                    } else {
                        currentFishSize.animateTo(initialFishSize.value)
                    }
                }
            }, onLongClick = {
                coroutineScope.launch {
                    if (currentFishSize.value == maxFishSize.value) {
                        currentFishSize.animateTo(initialFishSize.value)
                    } else {
                        currentFishSize.animateTo(maxFishSize.value)
                    }
                }
            }),
    )
}
