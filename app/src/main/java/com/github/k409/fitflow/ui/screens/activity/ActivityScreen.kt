package com.github.k409.fitflow.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.components.activity.CircularProgressBar
import com.github.k409.fitflow.ui.components.activity.DistanceAndCalories

@Composable
fun ActivityScreen() {
    val activityViewModel: ActivityViewModel = hiltViewModel()
    val todaySteps by activityViewModel.todaySteps.observeAsState()
    val todayGoal: Long = 6000 // TODO goal setter

    LaunchedEffect(key1 = Unit) {
        activityViewModel.updateTodayStepsManually()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        todaySteps?.let {
            CircularProgressBar(
                taken = it.totalSteps,
                goal = todayGoal,
            )
        }
        todaySteps?.let {
            DistanceAndCalories(
                calories = it.caloriesBurned,
                distance = it.totalDistance,
            )
        }
    }
}
