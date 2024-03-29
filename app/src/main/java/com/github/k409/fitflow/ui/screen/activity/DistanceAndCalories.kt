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

@Composable
fun DistanceAndCalories(
    calories: Long?,
    distance: Double?,
    fontSize: TextUnit = 26.sp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.mode_heat_24px),
                contentDescription = stringResource(R.string.heat_icon),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp),
            )
            Text(
                text = "$calories",
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Cal",
                color = color.copy(alpha = 0.6f),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.distance_24px),
                contentDescription = stringResource(R.string.distance_icon),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp),
            )
            Text(
                text = "$distance",
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "km",
                color = color.copy(alpha = 0.6f),
            )
        }
    }
}
