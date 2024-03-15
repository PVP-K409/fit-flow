package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R

@Composable
fun BoxScope.Sand(
    modifier: Modifier = Modifier,
) {
    val sandPainter = painterResource(id = R.drawable.sand)

    Image(
        painter = sandPainter,
        contentDescription = "Sand",
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .fillMaxHeight(0.2f),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
fun BoxScope.Plant(
    modifier: Modifier = Modifier,
) {
    val plantPainter = painterResource(id = R.drawable.plant)

    Image(
        painter = plantPainter,
        contentDescription = "Plant",
        modifier = modifier
            .align(Alignment.BottomEnd)
            .height(150.dp),
    )
}
