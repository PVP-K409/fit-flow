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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.AfternoonBackground
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.EveningBackground
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.MorningBackground
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.NightBackground
import com.github.k409.fitflow.ui.screen.inventory.InventoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun AquariumContent(
    modifier: Modifier = Modifier,
    uiState: AquariumUiState.Success,
    navController: NavController,
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
) {
    val waterLevel = uiState.aquariumStats.waterLevel
    val healthLevel = uiState.aquariumStats.healthLevel

    var aquariumBackground by remember { mutableStateOf(AfternoonBackground) }

    LaunchedEffect(Unit) {
        while (isActive) {
            when (LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))) {
                in "00:00".."06:59" -> aquariumBackground = NightBackground
                in "07:00".."11:59" -> aquariumBackground = MorningBackground
                in "12:00".."16:59" -> aquariumBackground = AfternoonBackground
                in "17:00".."21:59" -> aquariumBackground = EveningBackground
                in "22:00".."23:59" -> aquariumBackground = NightBackground
            }
            delay((60000 - LocalTime.now().second * 1000).toLong())
        }
    }

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

                if (waterLevelAnimation <= 0f) {
                    val fishesCount = uiState.fishes.size
                    val bones = listOf(R.drawable.fish_bone_1, R.drawable.fish_bone_2)
                    val rotation = remember { Random.nextInt(360) }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        for (i in 0 until fishesCount) {
                            FishImage(
                                modifier = Modifier
                                    .padding(bottom = 20.dp)
                                    .rotate(rotation.toFloat()),
                                fishSize = 90.dp,
                                fishDrawableId = bones[i % 2],
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight(waterLevelAnimation),
                ) {
                    val phase = getPhase(healthLevel)

                    DecorationsBox(
                        modifier = Modifier
                            .height(150.dp)
                            .align(Alignment.BottomCenter),
                        uiState = uiState,
                        inventoryViewModel = inventoryViewModel
                    )

                    for ((index, item) in uiState.fishes.withIndex()) {
                        val xOffset = (width / uiState.fishes.size) * (index).toFloat()

                        if (waterLevel > 0f && healthLevel > 0f) {
                            BouncingDraggableFish(
                                initialFishSize = fishSize,
                                fishDrawableId = R.drawable.primary_fish,
                                initialPosition = Offset(xOffset, 0f),
                                imageDownloadUrl = item.item.phases?.get(phase.name)
                                    ?: item.item.image,
                            )
                        } else {
                            BouncingDraggableFish(
                                modifier = Modifier
                                    .padding(top = 20.dp),
                                initialFishSize = fishSize,
                                fishDrawableId = R.drawable.primary_fish,
                                bounceEnabled = false,
                                initialPosition = Offset(xOffset, 0f),
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
private fun DecorationsBox(
    modifier: Modifier = Modifier,
    uiState: AquariumUiState.Success,
    inventoryViewModel: InventoryViewModel
) {
    Box(
        modifier = modifier,
    ) {

        for (item in uiState.decorations) {
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
