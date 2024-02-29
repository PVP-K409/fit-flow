package com.github.k409.fitflow.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.noRippleClickable(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
): Modifier = this.then(
    combinedClickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onLongClick = onLongClick,
        onClick = onClick,
        onDoubleClick = onDoubleClick
    )
)