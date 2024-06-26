package com.github.k409.fitflow.ui.screen.activity

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.ui.common.CalendarView
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.TextWithLabel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
internal fun ActivityPage(activityViewModel: ActivityViewModel) {
    val todaySteps by activityViewModel.todaySteps.collectAsState()
    val todayGoal: Long = todaySteps?.stepGoal ?: 0L
    val loading by activityViewModel.loading.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val permissionContract = PermissionController.createRequestPermissionResultContract()
    val launcher = rememberLauncherForActivityResult(permissionContract) {
        coroutineScope.launch {
            activityViewModel.updateTodayStepsManually()
        }
    }

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var selectedDateRecord by remember { mutableStateOf(todaySteps) }

    LaunchedEffect(key1 = Unit) {
        activityViewModel.checkForNewDay()
        if (!activityViewModel.permissionsGranted()) {
            launcher.launch(activityViewModel.permissions)
        } else {
            activityViewModel.updateTodayStepsManually()
        }

        if (selectedDate.value == LocalDate.now()) {
            selectedDateRecord = todaySteps
        }
    }

    LaunchedEffect(
        key1 = selectedDate.value,
        key2 = todaySteps,
    ) {
        val record = activityViewModel.getStepRecord(selectedDate.value)
        selectedDateRecord = record
    }

    if (loading) {
        FitFlowCircularProgressIndicator()
    } else {
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
                    .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 8.dp),
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
                                stringResource(R.string.steps) to it.totalSteps,
                                stringResource(R.string.calories) to "${it.caloriesBurned} cal",
                                stringResource(R.string.distance) to "${it.totalDistance} km",
                            )

                            options.forEachIndexed { _, option ->
                                TextWithLabel(
                                    label = option.first,
                                    text = option.second.toString(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
