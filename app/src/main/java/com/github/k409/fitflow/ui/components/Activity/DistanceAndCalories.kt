package com.github.k409.fitflow.ui.components.Activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DistanceAndCalories(
    calories: Long?,
    distance: Double?,
    fontSize: TextUnit = 20.sp
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Distance", fontWeight = FontWeight.Bold)
            Text(
                "$distance m",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = fontSize
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Calories", fontWeight = FontWeight.Bold)
            Text(
                "$calories kcal",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = fontSize
            )
        }
    }
}