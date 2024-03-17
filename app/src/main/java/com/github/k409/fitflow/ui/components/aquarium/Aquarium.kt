package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.model.AquariumFishType
import com.github.k409.fitflow.ui.common.noRippleClickable

@Composable
fun Aquarium(
    modifier: Modifier = Modifier,
) {
    val aquariumBackground = Brush.linearGradient(
        colors = listOf(Color(0xFFA7B9D3), Color(0xFF9CED96), Color(0xffd0e7cf)),
    )

    val currentFish = remember { mutableIntStateOf(0) }
    val waterLevel = remember { mutableFloatStateOf(0.85f) }

    fun onDoubleClick() {
        currentFish.intValue = (currentFish.intValue + 1) % AquariumFishType.entries.size

        val newLevel = waterLevel.floatValue.minus(0.1f)

        waterLevel.floatValue = if (newLevel < 0.1f) 0.85f else newLevel
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

            Crossfade(
                targetState = AquariumFishType.entries[currentFish.intValue],
                label = "",
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    DraggableFish(
                        modifier = Modifier
                            .fillMaxHeight(waterLevel.floatValue)
                            .noRippleClickable(onDoubleClick = ::onDoubleClick)
                            .border(
                                width = 1.dp,
                                color = Color.Red,
                            ),
                        fishSize = 100.dp,
                        fishDrawableId = it.imageRes,
                    )
                }

            }
        }
    }
}
