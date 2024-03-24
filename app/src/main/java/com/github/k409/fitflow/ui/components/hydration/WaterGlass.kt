package com.github.k409.fitflow.ui.components.hydration

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.AnimatedCounter

@Composable
fun WaterIndicator(
    modifier: Modifier = Modifier,
    totalWaterAmount: Int,
    usedWaterAmount: Int,
    unit: String = "ml",
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WaterGlass(
            modifier = modifier,
            totalWaterAmount = totalWaterAmount,
            usedWaterAmount = usedWaterAmount,
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedCounter(
            count = usedWaterAmount,
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
        )

        Text(
            text = "/$totalWaterAmount $unit",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun WaterGlass(
    modifier: Modifier = Modifier,
    totalWaterAmount: Int,
    usedWaterAmount: Int,
    indicatorColor: Color = Color.White,
    waterWavesColor: Color = Color(0xFF52A1FF),
    bottleColor: Color = Color(0xFF2D2C30),
) {
    val targetWaterPercentage = if (totalWaterAmount != 0) {
        (usedWaterAmount.toFloat() / totalWaterAmount.toFloat()).coerceIn(0.05f, 1f)
    } else {
        0.05f
    }

    val waterPercentage = animateFloatAsState(
        targetValue = targetWaterPercentage,
        label = "Water level animation",
        animationSpec = tween(durationMillis = 1000),
    ).value

    Box(
        modifier = modifier
            .width(150.dp)
            .height(150.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val bodyPath = Path().apply {
                // bottom left corner
                moveTo(width * 0.15f, height * 0.9f)

                lineTo(width * 0.1f, 0.1f * height)
                lineTo(0f, 0f)

                lineTo(width * 0.9f, 0f)

                // bottom right corner
                lineTo(width * 0.85f, height * 0.9f)

                // bottom right corner arc
                arcTo(
                    rect = Rect(
                        left = width * 0.6f,
                        top = height * 0.8f,
                        right = width * 0.85f,
                        bottom = height,
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                // bottom left corner arc
                arcTo(
                    rect = Rect(
                        left = width * 0.15f,
                        top = height * 0.8f,
                        right = width * 0.4f,
                        bottom = height,
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                close()
            }

            clipPath(
                path = bodyPath,
            ) {
                drawRect(
                    color = bottleColor,
                    size = size,
                    topLeft = Offset(0f, 0f),
                )

                val waterWavesYPosition = (1 - waterPercentage) * (size.height)

                val wavesPath = Path().apply {
                    moveTo(
                        x = 0f,
                        y = waterWavesYPosition,
                    )
                    lineTo(
                        x = size.width,
                        y = waterWavesYPosition,
                    )
                    lineTo(
                        x = size.width,
                        y = size.height,
                    )
                    lineTo(
                        x = 0f,
                        y = size.height,
                    )
                    close()
                }
                drawPath(
                    path = wavesPath,
                    color = waterWavesColor,
                )
            }
        }
    }
}

@Preview
@Composable
fun WaterBottlePreview() {
    var usedWaterAmount by remember { mutableIntStateOf(600) }
    val totalWaterAmount = 2500

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            WaterIndicator(
                totalWaterAmount = totalWaterAmount,
                usedWaterAmount = usedWaterAmount,
            )

            Button(modifier = Modifier.padding(top = 32.dp), onClick = {
                usedWaterAmount += 100
            }) {
                Text(text = "Add 100ml")
            }
        }
    }
}
