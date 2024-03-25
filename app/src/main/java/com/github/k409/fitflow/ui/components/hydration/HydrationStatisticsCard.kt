package com.github.k409.fitflow.ui.components.hydration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LineAxis
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.HydrationStats
import com.github.k409.fitflow.ui.common.TextLabelWithDivider

@Composable
fun HydrationStatisticsCard(
    modifier: Modifier = Modifier,
    stats: HydrationStats,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        OutlinedCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.your_hydration_statistics),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Light,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextLabelWithDivider(
                    dividerVisible = false,
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.Start,
                    ),
                    data = listOf(
                        stringResource(R.string.Yesterday) to "${stats.yesterdayTotalAmount} ml",
                        stringResource(R.string.Week) to "${
                            stats.thisWeekTotalAmount.toFloat().div(1000)
                        } l",
                        stringResource(R.string.Month) to "${
                            stats.thisMonthTotalAmount.toFloat().div(1000)
                        } l",
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LineAxis,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.average_consumption),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Light,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextLabelWithDivider(
                    dividerVisible = false,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.Start,
                    ),
                    data = listOf(
                        stringResource(R.string.this_week) to "${
                            String.format(
                                "%.2f",
                                stats.thisWeekTotalAmount.toFloat().div(7).div(1000),
                            )
                        } l",
                        stringResource(R.string.this_month) to "${
                            String.format(
                                "%.2f",
                                stats.thisMonthTotalAmount.toFloat().div(30).div(1000),
                            )
                        } l",
                    ),
                )
            }
        }
    }
}
