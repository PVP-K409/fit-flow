package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.noRippleClickable
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun Aquarium(
    modifier: Modifier = Modifier,
    waveCount: Int = 3,
) {
    val sandPainter = painterResource(id = R.drawable.sand)
    val plantPainter = painterResource(id = R.drawable.plant)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            WavesBackground(waveCount = waveCount)

            Image(
                painter = sandPainter, contentDescription = "Sand", modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = plantPainter,
                contentDescription = "Plant",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(150.dp)
            )

            AnimatedPrimaryFish(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun AnimatedPrimaryFish(
    modifier: Modifier = Modifier,
    maxFishSize: Dp = 500.dp,
    initialFishSize: Dp = 100.dp
) {
    val coroutineScope = rememberCoroutineScope()

    val primaryFishPainter = painterResource(id = R.drawable.primary_fish)
    val currentFishSize = remember { Animatable(initialFishSize.value) }

    /* Animation for Fish
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val xPosition by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 200f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000), repeatMode = RepeatMode.Reverse
        ), label = ""
    )*/

    Image(
        painter = primaryFishPainter,
        contentDescription = "Fish",
        modifier = modifier
            .size(currentFishSize.value.dp)
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
            })
    )
}


@Composable
fun WavesBackground(waveCount: Int = 3, verticalWavesStart: Float = 0.3f) {
    val waveAmplitude = remember { mutableFloatStateOf(50f) }
    val waveFrequency = remember { mutableFloatStateOf(0.03f) }

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val amplitudeAnimation by infiniteTransition.animateFloat(
        initialValue = waveAmplitude.floatValue,
        targetValue = waveAmplitude.floatValue * 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val frequencyAnimation by infiniteTransition.animateFloat(
        initialValue = waveFrequency.floatValue,
        targetValue = waveFrequency.floatValue * 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val wavePath = Path()

        val startY = size.height * verticalWavesStart
        wavePath.moveTo(0f, startY)

        for (i in 0 until waveCount) {
            val x1 = (i * size.width) / waveCount
            val y1 =
                startY + amplitudeAnimation * sin((i * size.width / waveCount) * frequencyAnimation)

            val x2 = ((i + 1) * size.width) / waveCount
            val y2 =
                startY + amplitudeAnimation * sin(((i + 1) * size.width / waveCount) * frequencyAnimation)


            wavePath.cubicTo(
                x1 + size.width / (2 * waveCount), y1, x1 + size.width / (2 * waveCount), y2, x2, y2
            )
        }

        wavePath.lineTo(size.width, size.height)
        wavePath.lineTo(0f, size.height)
        wavePath.close()

        drawRect(
            color = Color(0xffb5c8e8),
            size = Size(size.width, size.height),
        )

        drawPath(
            path = wavePath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xffd0e7cf), Color(0xffb5c8e8)),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
        )
    }
}