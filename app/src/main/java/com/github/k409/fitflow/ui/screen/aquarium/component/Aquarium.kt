package com.github.k409.fitflow.ui.screen.aquarium.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.FishPhase
import com.github.k409.fitflow.model.FishPhase.Companion.getPhase
import com.github.k409.fitflow.model.InventoryItem
import com.github.k409.fitflow.ui.navigation.NavRoutes
import com.github.k409.fitflow.ui.screen.aquarium.AquariumUiState
import com.github.k409.fitflow.ui.screen.aquarium.AquariumViewModel
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.AfternoonBackground
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.EveningBackground
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.MorningBackground
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumTokens.NightBackground
import com.github.k409.fitflow.ui.screen.inventory.InventoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
    aquariumViewModel: AquariumViewModel = hiltViewModel(),
) {
    var waterLevel by remember { mutableFloatStateOf(uiState.localAquariumStats.waterLevel) }
    var healthLevel by remember { mutableFloatStateOf(uiState.localAquariumStats.healthLevel) }

    var aquariumBackground by remember { mutableStateOf(AfternoonBackground) }

    LaunchedEffect(Unit) {
        if (waterLevel != uiState.aquariumStats.waterLevel || healthLevel != uiState.aquariumStats.healthLevel) {
            // Log.d("AquariumContent", "aquarium stats changed: ${uiState.aquariumStats}")
            waterLevel = uiState.aquariumStats.waterLevel
            healthLevel = uiState.aquariumStats.healthLevel
            aquariumViewModel.updateLocalAquariumStats(uiState.aquariumStats)
        }
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
        onInventoryClick = {
            navController.navigate(NavRoutes.Inventory.route) {
                launchSingleTop = true
                restoreState = true
            }
        },
        onDecorationDragEnd = { item, offset ->
            inventoryViewModel.updateInventoryItem(
                item.copy(offsetX = offset.x, offsetY = offset.y),
            )
        },
    )
}

@Composable
private fun AquariumLayout(
    modifier: Modifier,
    uiState: AquariumUiState.Success,
    aquariumBackground: Brush,
    backgroundAlpha: Float,
    waterLevelAnimation: Float,
    waterLevel: Float,
    healthLevel: Float,
    fishSize: Dp,
    onInventoryClick: () -> Unit,
    onDecorationDragEnd: (InventoryItem, Offset) -> Unit,
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
                modifier = Modifier.align(Alignment.TopStart),
                iconColor = Color.White,
                containerColor = if (aquariumBackground == MorningBackground) Color.Black.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.3f),
                onInventoryClick = onInventoryClick,
            )

            AquariumMetrics(
                modifier = Modifier.align(Alignment.TopEnd),
                waterLevel = waterLevel,
                healthLevel = healthLevel,
                waterTextColor = when {
                    waterLevel == 1f -> Color(0xff4cff00)
                    waterLevel >= 0.75f -> Color(0xffb0ff8f)
                    waterLevel >= 0.5f -> Color(0xfffffe00)
                    waterLevel >= 0.25f -> Color(0xffffac00)
                    else -> Color(0xFFFF3333)
                },
                healthTextColor = when {
                    healthLevel == 1f -> Color(0xff4cff00)
                    healthLevel >= 0.75f -> Color(0xffb0ff8f)
                    healthLevel >= 0.5f -> Color(0xfffffe00)
                    healthLevel >= 0.25f -> Color(0xffffac00)
                    else -> Color(0xFFFF3333)
                },
                dividerColor = Color.White,
                containerColor = if (aquariumBackground == MorningBackground) Color.Black.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.3f),
            )

            DecorationsBox(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(150.dp)
                    .align(Alignment.BottomCenter),
                uiState = uiState,
                onDecorationDragEnd = onDecorationDragEnd,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                FishBonesContainer(waterLevelAnimation = waterLevelAnimation, uiState = uiState)

                FishContainer(
                    uiState = uiState,
                    width = width,
                    waterLevelAnimation = waterLevelAnimation,
                    healthLevel = healthLevel,
                    fishSize = fishSize,
                    phase = getPhase(healthLevel),
                )
            }
        }
    }
}

@Composable
private fun FishBonesContainer(
    waterLevelAnimation: Float,
    uiState: AquariumUiState.Success,
) {
    if (waterLevelAnimation <= 0f) {
        val fishesCount = uiState.fishes.size
        val bones = listOf(R.drawable.fish_bone_1, R.drawable.fish_bone_2)

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            for (i in 0 until fishesCount) {
                val rotation = remember { Random.nextInt(45) }

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
}

@Composable
private fun FishContainer(
    uiState: AquariumUiState.Success,
    width: Int,
    waterLevelAnimation: Float,
    healthLevel: Float,
    fishSize: Dp,
    phase: FishPhase,
) {
    Box(
        modifier = Modifier.fillMaxHeight(waterLevelAnimation),
    ) {
        for ((index, item) in uiState.fishes.withIndex()) {
            val xOffset = (width / uiState.fishes.size) * (index).toFloat()

            if (waterLevelAnimation > 0f && healthLevel >= 0f) {
                BouncingDraggableFish(
                    initialFishSize = fishSize,
                    fishDrawableId = R.drawable.primary_fish,
                    initialPosition = Offset(xOffset, 0f),
                    bounceEnabled = healthLevel > 0f,
                    flipped = healthLevel <= 0f,
                    imageDownloadUrl = item.item.phases?.get(phase.name) ?: item.item.image,
                )
            }
        }
    }
}

@Composable
private fun DecorationsBox(
    modifier: Modifier = Modifier,
    uiState: AquariumUiState.Success,
    onDecorationDragEnd: (InventoryItem, Offset) -> Unit,
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
                onDragEnd = { onDecorationDragEnd(item, it) },
                uniformSize = true,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AquariumMetrics(
    modifier: Modifier = Modifier,
    waterLevel: Float,
    healthLevel: Float,
    waterTextColor: Color = Color(0xffffffff),
    healthTextColor: Color = Color(0xffffffff),
    waterLevelIconTint: Color = Color(0xFF03A9F4),
    healthLevelIconTint: Color = Color(0xFFF44336),
    dividerColor: Color = Color(0xFF434B48),
    containerColor: Color = Color.White.copy(alpha = 0.3f),
) {
    val state = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(100),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 43.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = {
            scope.launch {
                state.show(MutatePriority.UserInput)
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TooltipBox(positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(10.dp), tooltip = {
                RichTooltip(modifier = Modifier.padding(end = 7.dp), colors = TooltipDefaults.richTooltipColors(containerColor = containerColor), shape = RoundedCornerShape(50)) {
                    Column {
                        Row {
                            Text(
                                "${stringResource(R.string.water_level)}: ${(waterLevel * 100).roundToInt()}%",
                                modifier = Modifier.padding(start = 25.dp, end = 25.dp),
                                style = MaterialTheme.typography.titleSmall,
                                color = waterTextColor,
                            )
                        }
                        Text(
                            text = when {
                                waterLevel == 1f -> stringResource(R.string.excellent_your_hydration_level_is_perfect_and_the_aquarium_is_full_of_water)
                                waterLevel >= 0.75f -> stringResource(R.string.nicely_done_your_fishes_are_well_hydrated)
                                waterLevel >= 0.5f -> stringResource(R.string.your_fishes_are_hydrated_keep_up_the_good_work)
                                waterLevel >= 0.25f -> stringResource(R.string.your_fishes_are_getting_thirsty_increase_aquarium_water_level_by_drinking_more_water)
                                else -> stringResource(R.string.your_aquarium_has_dried_out_you_should_really_consider_drinking_more_water)
                            },
                            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = dividerColor,
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = dividerColor, thickness = 1.dp)

                        Text("${stringResource(R.string.health_level)}: ${(healthLevel * 100).roundToInt()}%", modifier = Modifier.padding(start = 25.dp, end = 25.dp), style = MaterialTheme.typography.titleSmall, color = healthTextColor)
                        Text(
                            text = when {
                                healthLevel == 1f -> stringResource(R.string.excellent_your_health_level_is_perfect_and_the_fishes_are_as_strong_as_ever)
                                healthLevel >= 0.75f -> stringResource(R.string.well_done_your_fishes_are_strong_and_in_good_health)
                                healthLevel >= 0.5f -> stringResource(R.string.your_fishes_are_healthy_keep_up_the_good_work)
                                healthLevel >= 0.25f -> stringResource(R.string.your_fishes_are_getting_sick_exercise_more_to_keep_them_healthy)
                                else -> stringResource(R.string.your_fishes_are_dying_you_should_start_working_out_to_help_them_and_yourself) },
                            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = dividerColor,
                        )
                    }
                }
            }, state = state) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = "${(waterLevel * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = waterTextColor,
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
                        color = healthTextColor,
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
}

@Composable
fun InventoryButton(
    modifier: Modifier = Modifier,
    iconColor: Color = Color(0xFF434B48),
    containerColor: Color = Color.White.copy(alpha = 0.3f),
    onInventoryClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 50.dp)
            .clickable(onClick = onInventoryClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(100),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                tint = iconColor,
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
