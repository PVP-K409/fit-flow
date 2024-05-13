package com.github.k409.fitflow.ui.screen.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
