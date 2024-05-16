package com.github.k409.fitflow.ui.screen.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.screen.activity.exerciseSession.Parametric

@Composable
fun DistanceAndCalories(
    calories: Long?,
    distance: Double?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Parametric(
            modifier = Modifier.size(36.dp),
            title = "Cal",
            value = calories.toString(),
            icon = R.drawable.mode_heat_24px,
        )
        Parametric(
            modifier = Modifier.size(36.dp),
            title = "km",
            value = distance.toString(),
            icon = R.drawable.distance_24px,
        )
    }
}
