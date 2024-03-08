package com.github.k409.fitflow.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.ui.common.TextWithLabel
import com.github.k409.fitflow.ui.components.activity.CircularProgressBar
import com.github.k409.fitflow.ui.components.activity.DistanceAndCalories
import com.github.k409.fitflow.ui.components.calendar.CalendarView
import java.time.LocalDate

@Composable
fun ActivityScreen() {
    val activityViewModel: ActivityViewModel = hiltViewModel()
    val todaySteps by activityViewModel.todaySteps.observeAsState()
    val todayGoal: Long = 6000 // TODO goal setter

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var selectedDateRecord by remember { mutableStateOf(todaySteps) }

    LaunchedEffect(key1 = Unit) {
        activityViewModel.updateTodayStepsManually()
        if (selectedDate.value == LocalDate.now()) {
            selectedDateRecord = todaySteps
        }
    }

    LaunchedEffect(key1 = selectedDate.value) {
        val record = activityViewModel.getStepRecord(selectedDate.value)
        selectedDateRecord = record
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            todaySteps?.let {
                CircularProgressBar(
                    taken = it.totalSteps,
                    goal = todayGoal,
                    radius = 80.dp,
                )
            }
            todaySteps?.let {
                DistanceAndCalories(
                    calories = it.caloriesBurned,
                    distance = it.totalDistance,
                )
            }
        }

        OutlinedCard(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 8.dp)
            ) {
                CalendarView(
                    selectedDate = selectedDate,
                    weeksCount = 52,
                )

                (selectedDateRecord ?: DailyStepRecord()).let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val options = listOf(
                            "Steps" to it.totalSteps,
                            "Calories" to "${it.caloriesBurned} kcal",
                            "Distance" to "${it.totalDistance} km",
                        )

                        options.forEachIndexed { _, option ->
                            TextWithLabel(
                                label = option.first,
                                text = option.second.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}
