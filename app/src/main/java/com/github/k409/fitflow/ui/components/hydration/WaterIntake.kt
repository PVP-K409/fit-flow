package com.github.k409.fitflow.ui.components.hydration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.k409.fitflow.R

@Composable
fun WaterIntake(
    milliliters: Long?,
    thisWeek: Double?,
    thisMonth: Double?,
    fontSize: TextUnit = 20.sp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.Yesterday), fontWeight = FontWeight.Bold)
            Text(
                text = "$milliliters ml",
                textAlign = TextAlign.Center,
                fontSize = fontSize,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.Week), fontWeight = FontWeight.Bold)
            Text(
                text = "${thisWeek?.div(1000)} l",
                textAlign = TextAlign.Center,
                fontSize = fontSize,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.Month), fontWeight = FontWeight.Bold)
            Text(
                text = "${thisMonth?.div(1000)} l",
                textAlign = TextAlign.Center,
                fontSize = fontSize,
            )
        }
    }
}