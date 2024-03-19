package com.github.k409.fitflow.ui.screens.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.ProgressGraph

@Composable
fun GoalsScreen() {

    val data = listOf(0.5f, 0.3f, 0.8f, 0.6f, 0.9f, 0.0f, 0.7f)
    val selected = remember {
        mutableStateOf<Int?>(null)
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Selected value: ${selected.value?.let { data.getOrNull(it) } ?: "None"}")
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
}
