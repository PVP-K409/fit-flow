package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.screens.aquarium.AquariumUiState
import kotlin.math.roundToInt


@Composable
fun AquariumContent(
    modifier: Modifier = Modifier,
    uiState: AquariumUiState.Success,
    onWaterLevelChanged: (Float) -> Unit,
    onHealthLevelChanged: (Float) -> Unit,
    aquariumBackground: Brush = AquariumTokens.AquariumBackground,
) {
    val waterLevel = uiState.aquariumStats.waterLevel
    val healthLevel = uiState.aquariumStats.healthLevel

    val backgroundAlpha by animateFloatAsState(
        targetValue = calculateAquariumBackgroundAlpha(waterLevel),
        animationSpec = tween(
            durationMillis = AquariumTokens.AquariumBackgroundAlphaAnimationDuration,
            easing = AquariumTokens.AquariumBackgroundAlphaAnimationEasing
        ),
        label = "Aquarium Background Alpha Animation",
    )

    val waterLevelAnimation by animateFloatAsState(
        targetValue = waterLevel,
        animationSpec = tween(
            durationMillis = AquariumTokens.WaterLevelAnimationDuration,
            easing = AquariumTokens.WaterLevelAnimationEasing
        ),
        label = "Water Level Animation",
    )

    val fishSize = remember(waterLevelAnimation) { calculateFishSize(waterLevelAnimation) }

    AquariumLayout(
        modifier = modifier,
        aquariumBackground = aquariumBackground,
        backgroundAlpha = backgroundAlpha,
        waterLevelAnimation = waterLevelAnimation,
        waterLevel = waterLevel,
        healthLevel = healthLevel,
        fishSize = fishSize,
        uiState = uiState,
        onWaterLevelChanged = onWaterLevelChanged,
        onHealthLevelChanged = onHealthLevelChanged
    )
}

@Composable
private fun AquariumLayout(
    modifier: Modifier,
    aquariumBackground: Brush,
    backgroundAlpha: Float,
    waterLevelAnimation: Float,
    waterLevel: Float,
    healthLevel: Float,
    fishSize: Dp,
    uiState: AquariumUiState.Success,
    onWaterLevelChanged: (Float) -> Unit,
    onHealthLevelChanged: (Float) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = aquariumBackground,
                alpha = backgroundAlpha,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val height = constraints.maxHeight
            val width = constraints.maxWidth

            AnimatedWaves(waterLevel = waterLevelAnimation)

            WaterBubbles(
                modifier = Modifier
                    .fillMaxHeight(waterLevelAnimation)
                    .align(Alignment.BottomCenter),
                bubbleCount = 6,
                waterLevel = waterLevel,
                offsetX = width.toFloat(),
                offsetY = height.toFloat(),
            )

            Sand()
            Plant()

            AquariumMetrics(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                waterLevel = waterLevel,
                healthLevel = healthLevel,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(waterLevelAnimation),
                ) {
                    DraggableFishBox(
                        fishSize = fishSize,
                        fishDrawableId = uiState.aquariumStats.fish.getPhaseImage(healthLevel),
                    )
                }
            }

            // TODO FOR TESTING
            ButtonToModifyMetrics(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                waterLevel = waterLevel,
                onWaterLevelChanged = onWaterLevelChanged,
                healthLevel = healthLevel,
                onHealthLevelChanged = onHealthLevelChanged,
            )
        }
    }
}

@Composable
private fun ButtonToModifyMetrics(
    modifier: Modifier = Modifier,
    waterLevel: Float,
    onWaterLevelChanged: (Float) -> Unit,
    healthLevel: Float,
    onHealthLevelChanged: (Float) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(vertical = 70.dp),
    ) {
        IconButton(onClick = {
            val newWaterLevel = (waterLevel + 0.25f).coerceIn(0.0f, 1.0f)
            onWaterLevelChanged(newWaterLevel)
        }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increase Water Level",
            )
        }

        IconButton(
            onClick =
            {
                val newWaterLevel = (waterLevel - 0.25f).coerceIn(0.0f, 1.0f)
                onWaterLevelChanged(newWaterLevel)
            },
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease Water Level",
            )
        }

        IconButton(onClick = {
            val newHealthLevel = (healthLevel + 0.25f).coerceIn(0.0f, 1.0f)
            onHealthLevelChanged(newHealthLevel)
        }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increase Health Level",
            )
        }

        IconButton(onClick = {
            val newHealthLevel = (healthLevel - 0.25f).coerceIn(0.0f, 1.0f)
            onHealthLevelChanged(newHealthLevel)
        }) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease Health Level",
            )
        }
    }
}

@Composable
fun AquariumMetrics(
    modifier: Modifier = Modifier,
    waterLevel: Float,
    healthLevel: Float,
    textColor: Color = Color(0xffffffff),
    waterLevelIconTint: Color = Color(0xFF03A9F4),
    healthLevelIconTint: Color = Color(0xFFF44336),
    dividerColor: Color = Color(0xFF434B48),
) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 50.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(100),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "${(waterLevel * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = "Water Level",
                    tint = waterLevelIconTint,
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(12.dp)
                        .padding(horizontal = 12.dp),
                    thickness = 1.dp,
                    color = dividerColor,
                )

                Text(
                    text = "${(healthLevel * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ecg_heart_24px),
                    contentDescription = "Health",
                    tint = healthLevelIconTint,
                )
            }
        }
    }
}

internal fun calculateFishSize(waterLevel: Float): Dp {
    val minSize = AquariumTokens.MinFishSize
    val maxSize = AquariumTokens.MaxFishSize

    return minSize + (maxSize - minSize) * waterLevel
}

internal fun calculateAquariumBackgroundAlpha(waterLevel: Float): Float {
    val minAlpha = AquariumTokens.MinAquariumBackgroundAlpha
    val maxAlpha = AquariumTokens.MaxAquariumBackgroundAlpha

    return minAlpha + (maxAlpha - minAlpha) * waterLevel
}
