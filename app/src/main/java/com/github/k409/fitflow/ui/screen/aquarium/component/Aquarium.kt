package com.github.k409.fitflow.ui.screen.aquarium.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.FishPhase.Companion.getPhase
import com.github.k409.fitflow.ui.navigation.NavRoutes
import com.github.k409.fitflow.ui.screen.aquarium.AquariumUiState
import com.github.k409.fitflow.ui.screen.inventory.InventoryViewModel
import kotlin.math.roundToInt

@Composable
fun AquariumContent(
    modifier: Modifier = Modifier,
    uiState: AquariumUiState.Success,
    aquariumBackground: Brush = AquariumTokens.AquariumBackground,
    navController: NavController,
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
) {
    val waterLevel = uiState.aquariumStats.waterLevel
    val healthLevel = uiState.aquariumStats.healthLevel

    val backgroundAlpha by animateFloatAsState(
        targetValue = calculateAquariumBackgroundAlpha(waterLevel),
        animationSpec = tween(
            durationMillis = AquariumTokens.AquariumBackgroundAlphaAnimationDuration,
            easing = AquariumTokens.AquariumBackgroundAlphaAnimationEasing,
        ),
        label = "Aquarium Background Alpha Animation",
    )

    val waterLevelAnimation by animateFloatAsState(
        targetValue = waterLevel,
        animationSpec = tween(
            durationMillis = AquariumTokens.WaterLevelAnimationDuration,
            easing = AquariumTokens.WaterLevelAnimationEasing,
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
        navController = navController,
        inventoryViewModel = inventoryViewModel,
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
    navController: NavController,
    inventoryViewModel: InventoryViewModel,
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

            InventoryButton(
                modifier = Modifier
                    .align(Alignment.TopStart),
                navController = navController,
            )

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
                    val phase = getPhase(healthLevel)

                    Box(
                        modifier = Modifier
                            .size(
                                width = width.dp,
                                height = (height * 0.075).dp,
                            )
                            .align(Alignment.BottomCenter),
                    ) {
                        for (item in uiState.aquariumItems) {
                            if (item.item.type == "decoration") {
                                BouncingDraggableFish(
                                    initialFishSize = 85.dp,
                                    imageDownloadUrl = item.item.image,
                                    bounceEnabled = false,
                                    initialPosition = Offset(item.offsetX, item.offsetY),
                                    onDragEnd = { offset ->
                                        inventoryViewModel.updateInventoryItem(
                                            item.copy(offsetX = offset.x, offsetY = offset.y),
                                        )
                                    },
                                    uniformSize = true,
                                )
                            }
                        }
                    }
                    for (item in uiState.aquariumItems) {
                        if (item.item.type == "fish") {
                            BouncingDraggableFish(
                                initialFishSize = fishSize,
                                fishDrawableId = R.drawable.gold_fish_dead,
                                imageDownloadUrl = item.item.phases?.get(phase.name)
                                    ?: item.item.image,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AquariumMetrics(
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

@Composable
fun InventoryButton(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 50.dp)
            .clickable {
                navController.navigate(NavRoutes.Inventory.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(100),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                tint = Color(0xFF434B48),
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = "Inventory",
            )
        }
    }
}

private fun calculateFishSize(waterLevel: Float): Dp {
    val minSize = AquariumTokens.MinFishSize
    val maxSize = AquariumTokens.MaxFishSize

    return minSize + (maxSize - minSize) * waterLevel
}

private fun calculateAquariumBackgroundAlpha(waterLevel: Float): Float {
    val minAlpha = AquariumTokens.MinAquariumBackgroundAlpha
    val maxAlpha = AquariumTokens.MaxAquariumBackgroundAlpha

    return minAlpha + (maxAlpha - minAlpha) * waterLevel
}
