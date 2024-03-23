package com.github.k409.fitflow.ui.components.hydration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.TextLabelWithDivider

@Composable
fun WaterIntakeLog(
    modifier: Modifier = Modifier,
    milliliters: Long,
    thisWeek: Double,
    thisMonth: Double,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = stringResource(R.string.your_hydration_statistics),
                style = MaterialTheme.typography.labelLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextLabelWithDivider(
                horizontalArrangement = Arrangement.Start,
                data = listOf(
                    stringResource(R.string.Yesterday) to "$milliliters ml",
                    stringResource(R.string.Week) to "${thisWeek.div(1000)} l",
                    stringResource(R.string.Month) to "${thisMonth.div(1000)} l",
                ),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.average_consumption),
                style = MaterialTheme.typography.labelLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextLabelWithDivider(
                horizontalArrangement = Arrangement.Start,
                data = listOf(
                    stringResource(R.string.this_week) to "${
                        String.format(
                            "%.2f",
                            thisWeek.div(7).div(1000)
                        )
                    } l",
                    stringResource(R.string.this_month) to "${
                        String.format(
                            "%.2f",
                            thisMonth.div(30).div(1000)
                        )
                    } l",
                ),
            )
        }
    }
}
