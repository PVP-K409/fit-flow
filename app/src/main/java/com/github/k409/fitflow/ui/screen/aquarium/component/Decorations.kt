package com.github.k409.fitflow.ui.screen.aquarium.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R

@Composable
fun BoxScope.Sand(
    modifier: Modifier = Modifier,
) {
    val sandVector = ImageVector.vectorResource(id = R.drawable.sand)

    Image(
        imageVector = sandVector,
        contentDescription = "Sand",
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .fillMaxHeight(0.2f),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
fun BoxScope.CoralCuspPlant(
    modifier: Modifier = Modifier,
) {
    Plant(
        modifier = modifier,
        id = R.drawable.plant,
        alignment = Alignment.BottomEnd,
    )
}

@Composable
fun BoxScope.Crab(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomStart,
    height: Dp = 150.dp,
) {
    val crabVector = ImageVector.vectorResource(id = R.drawable.crab)

    Image(
        imageVector = crabVector,
        contentDescription = "Crab",
        modifier = modifier
            .align(alignment)
            .height(height),
    )
}

@Composable
fun BoxScope.Plant(
    modifier: Modifier = Modifier,
    id: Int = R.drawable.plant,
    alignment: Alignment = Alignment.BottomEnd,
    height: Dp = 150.dp,
) {
    val plantVector = ImageVector.vectorResource(id = id)

    Image(
        imageVector = plantVector,
        contentDescription = "Plant",
        modifier = modifier
            .align(alignment)
            .height(height),
    )
}
