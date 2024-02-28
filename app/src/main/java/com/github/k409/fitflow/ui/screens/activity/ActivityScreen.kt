package com.github.k409.fitflow.ui.screens.activity

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R

@Composable
fun ActivityScreen() {
    val activityViewModel: ActivityViewModel = hiltViewModel()
    val todaySteps by activityViewModel.todaySteps.observeAsState()
    val startAnimate = remember { mutableStateOf(false) }
    val animatedSteps by animateIntAsState(
        targetValue = if (startAnimate.value) todaySteps?.current?.toInt() ?: 0 else 0, label = "0"
    )
    val animatedCalories by animateIntAsState(
        targetValue = if (startAnimate.value) todaySteps?.calories?.toInt() ?: 0 else 0, label = "0"
    )

    LaunchedEffect(key1 = Unit) {
        activityViewModel.updateTodayStepsManually()
        startAnimate.value = true // Correctly updating the state
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.steps, animatedSteps))
        Text(text = stringResource(R.string.calories, animatedCalories))
        Text(text = stringResource(R.string.distance, todaySteps?.distance.toString()))
    }
}