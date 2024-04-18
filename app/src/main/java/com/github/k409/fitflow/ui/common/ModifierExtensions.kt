package com.github.k409.fitflow.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.noRippleClickable(
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
): Modifier = this.then(
    combinedClickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onLongClick = onLongClick,
        onClick = onClick,
        onDoubleClick = onDoubleClick,
    ),
)

@Composable
fun Modifier.timedClick(
    timeInMillis: Long,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: (Boolean) -> Unit,
) = composed {
    var timeOfTouch = -1L

    LaunchedEffect(key1 = timeInMillis, key2 = interactionSource) {
        interactionSource.interactions
            .onEach { interaction: Interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        timeOfTouch = System.currentTimeMillis()
                    }

                    is PressInteraction.Release -> {
                        val currentTime = System.currentTimeMillis()
                        onClick(currentTime - timeOfTouch > timeInMillis)
                    }

                    is PressInteraction.Cancel -> {
                        onClick(false)
                    }
                }
            }
            .launchIn(this)
    }

    Modifier.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = {},
    )
}

inline fun Modifier.thenIf(
    condition: Boolean,
    crossinline other: Modifier.() -> Modifier,
) = if (condition) then(other()) else this

inline fun Modifier.thenIfNot(
    condition: Boolean,
    crossinline other: Modifier.() -> Modifier,
) = if (!condition) then(other()) else this

inline fun <T> Modifier.thenIfNotNull(
    value: T?,
    crossinline other: Modifier.(T) -> Modifier,
) = if (value != null) then(other(value)) else this
