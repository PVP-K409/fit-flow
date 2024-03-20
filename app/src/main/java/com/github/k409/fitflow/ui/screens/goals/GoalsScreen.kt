package com.github.k409.fitflow.ui.screens.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.ProgressGraph
import com.github.k409.fitflow.ui.common.TextWithLabel

@Composable
fun GoalsScreen() {
    ProgressGraphUsageSample()
}

@Composable
fun ProgressGraphUsageSample() {
    val weeklyData = listOf(4846, 5548, 8900, 9009, 18558, 1059, 757)
    val monthlyData = listOf(
        4846,
        5548,
        8900,
        9009,
        18558,
        1059,
        757,
        4846,
        5548,
        8900,
        9009,
        18558,
    ).map { it * 7 }

    Column {
        ProgressGraphContainer(
            data = weeklyData,
            title = "Current Week Progress",
            xAxisLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        )

        ProgressGraphContainer(
            data = monthlyData,
            title = "Weekly Progress",
            xAxisLabels = listOf(
                "Week 1",
                "Week 6",
                "Week 12",
            ),
        )
    }
}

@Composable
private fun ProgressGraphContainer(
    data: List<Int>,
    title: String,
    xAxisLabels: List<String>,
) {
    val selected = remember {
        mutableStateOf<Int?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    )
    {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            TextWithLabel(
                label = "Steps",
                text = selected.value?.let { data.getOrNull(it) }?.toString() ?: "0",
            )

            VerticalDivider(
                modifier = Modifier
                    .height(18.dp)
                    .padding(horizontal = 12.dp),
                thickness = 1.dp
            )

            TextWithLabel(
                label = "Calories",
                text = selected.value?.let { data.getOrNull(it) }?.div(5)?.toString()
                    ?: "0",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            ProgressGraph(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                data = data,
                xAxisLabels = xAxisLabels,
                onSelectedIndexChange = { index ->
                    selected.value = index
                },
            )
        }

    }
}
