package com.github.k409.fitflow.ui.components.aquarium

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R

@Composable
fun Aquarium(
    modifier: Modifier = Modifier,
) {
    val sandPainter = painterResource(id = R.drawable.sand)
    val plantPainter = painterResource(id = R.drawable.plant)

    val aquariumBackground = Brush.linearGradient(
        colors = listOf(Color(0xFFA7B9D3), Color(0xFF9CED96), Color(0xffd0e7cf)),
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = aquariumBackground
                )
        ) {
//            val constraintsWidth = maxWidth
//            val constraintsHeight = maxHeight
//            val density = LocalDensity.current

            AnimatedWaves(
                modifier = Modifier.alpha(0.5f),
                waveCount = 3,
                verticalWavesStart = 0.15f,
                waveAmplitude = 50f,
                waveFrequency = 0.04f / 2
            )
            AnimatedWaves(verticalWavesStart = 0.15f)


            Image(
                painter = sandPainter, contentDescription = "Sand", modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = plantPainter,
                contentDescription = "Plant",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(150.dp)
            )

//            AnimatedPrimaryFish(modifier = Modifier.align(Alignment.TopCenter))
            CircularPrimaryFish(modifier = Modifier.align(Alignment.Center))
        }
    }
}






