package com.github.k409.fitflow.ui.screens.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.ProgressGraph
import com.github.k409.fitflow.ui.common.TextWithLabel

@Composable
fun GoalsScreen() {

    val data = listOf(4846, 5548, 8900, 9009, 18558, 1059, 757)
    val selected = remember {
        mutableStateOf<Int?>(null)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
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

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            ProgressGraph(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                data = data,
                xAxisLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                onSelectedIndexChange = { index ->
                    selected.value = index
                },
            )
        }

    }
}
