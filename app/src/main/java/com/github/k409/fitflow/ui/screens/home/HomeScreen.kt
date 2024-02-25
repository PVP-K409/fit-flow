package com.github.k409.fitflow.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun HomeScreen(
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            WavesBackground(waveCount = 3)
            AnimatedPrimaryFish(modifier = Modifier.align(Alignment.CenterStart))
        }
    }
}

@Composable
fun AnimatedPrimaryFish(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val maxFishSize = 400.dp
    val primaryFishPainter = painterResource(id = R.drawable.primary_fish)
    val currentFishSize = remember { Animatable(200.dp.value) }

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val xPosition by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 400f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000), repeatMode = RepeatMode.Reverse
        ), label = ""
    )

//    val yPosition by infiniteTransition.animateFloat(
//        initialValue = 0f, targetValue = 300f, animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 2000), repeatMode = RepeatMode.Reverse
//        ), label = ""
//    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 20f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000), repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Image(
        painter = primaryFishPainter,
        contentDescription = "Fish",
        modifier = modifier
            .size(currentFishSize.value.dp)
            .graphicsLayer(
                translationX = xPosition,
//                translationY = yPosition,
                rotationZ = rotationAngle
            )
            .noRippleClickable(onClick = {
                coroutineScope.launch {
                    if (currentFishSize.value < maxFishSize.value) {
                        currentFishSize.animateTo(currentFishSize.value + 10)
                    } else {
                        currentFishSize.animateTo(200f)
                    }
                }
            })
    )
}


@Composable
fun WavesBackground(waveCount: Int = 3, verticalStart: Float = 0.3f) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val waveAmplitude = remember { mutableFloatStateOf(40f) }
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
            tween(durationMillis = 1500, easing = LinearEasing), RepeatMode.Reverse
        ),
        label = ""
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
    ) {
        val wavePath = Path()

        val startY = size.height * verticalStart
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

        drawPath(
            path = wavePath,
            brush = Brush.linearGradient(
                colors = listOf(primaryColor, Color.LightGray),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
        )
    }
}

@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.then(
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
)


