package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.AquariumFishType
import com.github.k409.fitflow.ui.common.noRippleClickable
import kotlin.math.roundToInt

@Composable
fun Aquarium(
    modifier: Modifier = Modifier,
) {
    val aquariumBackground = Brush.linearGradient(
        colors = listOf(Color(0xFFA7B9D3), Color(0xFF9CED96), Color(0xffd0e7cf)),
    )

    val currentFish = remember { mutableIntStateOf(0) }
    val waterLevel = remember { mutableFloatStateOf(0.85f) }
    val healthLevel = remember { mutableFloatStateOf(0.85f) }

    fun onDoubleClick() {
        currentFish.intValue = (currentFish.intValue + 1) % AquariumFishType.entries.size

        val newLevel = waterLevel.floatValue.minus(0.1f)

        waterLevel.floatValue = if (newLevel < 0.1f) 0.85f else newLevel
    }

    fun onLongClick() {
        waterLevel.floatValue = 0.85f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = aquariumBackground,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val height = constraints.maxHeight
            val width = constraints.maxWidth

            AnimatedWaves(waterLevel = waterLevel.floatValue)

            WaterBubbles(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(waterLevel.floatValue)
                    .alpha(0.20f)
                    .align(Alignment.BottomCenter),
                colors = listOf(Color.Blue, Color.Green),
                bubbleCount = 6,
                offsetX = width.toFloat(),
                offsetY = height.toFloat(),
            )

            Sand()
            Plant()

            AquariumMetrics(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                waterLevel = waterLevel.floatValue,
                healthLevel = healthLevel.floatValue
            )

            Crossfade(
                targetState = AquariumFishType.entries[currentFish.intValue],
                label = "",
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    DraggableFish(
                        modifier = Modifier
                            .fillMaxHeight(waterLevel.floatValue)
                            .noRippleClickable(
                                onDoubleClick = ::onDoubleClick,
                                onLongClick = ::onLongClick
                            ),
                        /* .border(
                             width = 1.dp,
                             color = Color.Red,
                         )*/
                        fishSize = 100.dp,
                        fishDrawableId = it.imageRes,
                    )
                }

            }
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
                    color = textColor
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
                    color = textColor
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
