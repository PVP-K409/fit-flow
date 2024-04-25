package com.github.k409.fitflow.ui.screen.aquarium.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.InventoryItem
import com.github.k409.fitflow.ui.common.noRippleClickable
import com.github.k409.fitflow.ui.common.thenIf
import com.github.k409.fitflow.ui.screen.inventory.InventoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FishImage(
    modifier: Modifier = Modifier,
    @DrawableRes fishDrawableId: Int = R.drawable.primary_fish,
    fishSize: Dp = 100.dp,
) {
    val fishVector = ImageVector.vectorResource(id = fishDrawableId)

    Image(
        imageVector = fishVector,
        contentDescription = "Fish",
        modifier = modifier.width(fishSize),
    )
}

@Composable
fun DraggableFishBox(
    modifier: Modifier = Modifier,
    fishModifier: Modifier = Modifier,
    @DrawableRes fishDrawableId: Int = R.drawable.primary_fish,
    imageDownloadUrl: String = "",
    fishSize: Dp = 100.dp,
    initialOffset: Offset = Offset(0f, 0f),
    onPositionChanged: (x: Float, y: Float) -> Unit = { _, _ -> },
    ) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize(),
    ) {
        val parentWidth = constraints.maxWidth
        val parentHeight = constraints.maxHeight

        var offsetX by remember { mutableFloatStateOf(initialOffset.x) }
        var offsetY by remember { mutableFloatStateOf(initialOffset.y) }

        var fishHeight by remember(fishSize) { mutableFloatStateOf(fishSize.value) }
        var fishWidth by remember(fishSize) { mutableFloatStateOf(fishSize.value) }

        val transition = rememberInfiniteTransition(label = "")
        val translationY by transition.animateFloat(
            initialValue = 0f,
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "",
        )

        if (imageDownloadUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageDownloadUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                error = painterResource(id = fishDrawableId),
                contentDescription = "",
                modifier = fishModifier
                    .offset {
                        IntOffset(
                            offsetX.roundToInt(),
                            offsetY.roundToInt() + translationY.roundToInt(),
                        )
                    }
                    .align(Alignment.TopStart)
                    .width(fishSize)
                    .pointerInput(fishSize) {
                        val boxSize = this.size

                        detectDragGestures { _, dragAmount ->
                            offsetX = (offsetX + dragAmount.x).coerceIn(
                                0f,
                                parentWidth - boxSize.width.toFloat(),
                            )
                            offsetY = (offsetY + dragAmount.y).coerceIn(
                                0f,
                                parentHeight - boxSize.height.toFloat(),
                            )

                            onPositionChanged(offsetX, offsetY)
                        }
                    }
                    .onSizeChanged { size ->
                        fishHeight = size.height.toFloat()
                        fishWidth = size.width.toFloat()

                        // center the fish
                        offsetX = (parentWidth - size.width) / 2f
                        offsetY = (parentHeight - size.height) / 2f

                        onPositionChanged(offsetX, offsetY)
                    },
            )
        }
    }
}

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

@Composable
fun BouncingDraggableFish(
    modifier: Modifier = Modifier,
    fishDrawableId: Int = R.drawable.primary_fish,
    bounceEnabled: Boolean = true,
    dragEnabled: Boolean = true,
    savePosition: Boolean = false,
    initialFishSize: Dp = 100.dp,
    initialVelocity: Offset = Offset(2f, 2f),
    fish: InventoryItem,
    phaseName: String = "",
    initialPosition: Offset = Offset(-1f, -1f),
    inventoryViewModel: InventoryViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        var fishSize by remember { mutableStateOf(IntSize(0, 0)) }

        // TODO possible bug when water level changes
        val containerWidth by remember { derivedStateOf { constraints.maxWidth.toFloat() - fishSize.width } }
        val containerHeight by remember { derivedStateOf { constraints.maxHeight.toFloat() - fishSize.height } }

        // TODO: Fix the random position
        val randomX = if (initialPosition.x < 0f) {
            Random.nextInt(0, containerWidth.toInt().coerceAtLeast(1)).toFloat()
        } else initialPosition.x

        val randomY = if (initialPosition.y < 0f) {
            Random.nextInt(0, containerHeight.toInt().coerceAtLeast(1)).toFloat()
        } else initialPosition.y

        var isDragging by remember { mutableStateOf(false) }
        var position by remember { mutableStateOf( Offset(randomX, randomY)) }
        val flipFish = remember { mutableStateOf(false) }

        val animatedRotation by animateFloatAsState(
            targetValue = if (flipFish.value) 180f else 0f,
            animationSpec = tween(durationMillis = 200),
            label = "Fish Rotation Animation",
        )

        if (bounceEnabled) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    var velocity = initialVelocity

                    while (true) {
                        if (!isDragging) {
                            position += velocity

                            position = Offset(
                                position.x.coerceIn(0f, containerWidth),
                                position.y.coerceIn(0f, containerHeight),
                            )

                            if (position.x >= containerWidth || position.x <= 0) {
                                velocity = Offset(-velocity.x, velocity.y)
                            }

                            if (position.y >= containerHeight || position.y <= 0) {
                                velocity = Offset(velocity.x, -velocity.y)
                            }

                            // moving to the right, don't flip the fish
                            if (velocity.x > 0) {
                                flipFish.value = false
                            }

                            // if moving to the left, flip the fish
                            if (velocity.x < 0) {
                                flipFish.value = true
                            }
                        }

                        delay(16) // (60fps) => 1000ms / 60 = 16ms
                    }
                }
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fish.item.phases?.get(phaseName)
                    ?: fish.item.image)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            error = painterResource(id = fishDrawableId),
            contentDescription = "",
            modifier = Modifier
                .width(initialFishSize)
                .offset {
                    IntOffset(
                        position.x
                            .roundToInt()
                            .coerceIn(0, containerWidth.roundToInt()),
                        position.y
                            .roundToInt()
                            .coerceIn(0, containerHeight.roundToInt()),
                    )
                }
                .thenIf(dragEnabled) {
                    Modifier.pointerInput(initialFishSize) {
                        detectDragGestures(
                            onDragStart = { _ -> isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                if(savePosition) {
                                    inventoryViewModel.updateInventoryItem(
                                        InventoryItem(
                                            fish.item,
                                            true,
                                            position.x,
                                            position.y,
                                        ))
                                    } },
                            onDrag = { _, dragAmount ->
                                val newOffsetX = (position.x + dragAmount.x).coerceIn(
                                    0f,
                                    containerWidth,
                                )

                                val newOffsetY = (position.y + dragAmount.y).coerceIn(
                                    0f,
                                    containerHeight,
                                )

                                position = Offset(newOffsetX, newOffsetY)
                            },
                        )
                    }
                }
                .graphicsLayer {
                    rotationY = animatedRotation
                }
                .onSizeChanged { size ->
                    fishSize = size
                },
        )
    }
}
