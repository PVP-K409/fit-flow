package com.github.k409.fitflow.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun ActivityScreen() {
    val activityViewModel: ActivityViewModel = hiltViewModel()
    val todaySteps = activityViewModel.todaySteps.observeAsState().value
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Steps: ${todaySteps?.current ?: "Loading"}")

        // Adding padding for better spacing
        Button(
            onClick = {
                coroutineScope.launch {
                    activityViewModel.updateTodayStepsManually()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Refresh")
        }
    }
}