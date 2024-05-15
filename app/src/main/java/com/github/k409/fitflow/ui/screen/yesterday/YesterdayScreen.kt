package com.github.k409.fitflow.ui.screen.yesterday

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.goals.GoalsHeader
import com.github.k409.fitflow.ui.screen.goals.GoalsList
import com.github.k409.fitflow.ui.screen.you.EffortRow
import com.github.k409.fitflow.ui.screen.you.OutlineCardContainer
import java.time.LocalDate
import java.util.Locale

@Composable
fun YesterdayScreen(
    viewModel: YesterdayViewModel = hiltViewModel(),
) {
    val uiState by viewModel.yesterdayUiState.collectAsState()

    when (uiState) {
        is YesterdayUiState.Loading, YesterdayUiState.NotStarted -> {
            FitFlowCircularProgressIndicator()
        }

        is YesterdayUiState.Success -> {
            YesterdayScreenContent(viewModel = viewModel, uiState = uiState as YesterdayUiState.Success)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun YesterdayScreenContent(viewModel: YesterdayViewModel, uiState: YesterdayUiState.Success) {
    val colors = MaterialTheme.colorScheme

    val hydration = (uiState.hydration.collectAsState(initial = null).value?.waterIntake ?: 0) / 1000f
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        OutlineCardContainer(
            title = "Statistics from yesterday",
            subtitleText = "Here's a summary of your activity metrics from yesterday",
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                // modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    EffortRow(
                        leftLabel = "Steps taken",
                        leftValue = uiState.stepRecord?.recordDate ?: LocalDate.now()
                            .minusDays(1).toString(),
                        rightLabel = uiState.stepRecord?.totalSteps.toString(),
                        rightValue = stringResource(R.string.steps).lowercase(),
                        iconImageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                    )
                    HorizontalDivider()
                }

                item {
                    EffortRow(
                        leftLabel = "Water intake",
                        leftValue = uiState.hydration.collectAsState(initial = null).value?.date
                            ?: LocalDate.now().minusDays(1).toString(),
                        rightLabel = String.format(
                            Locale.getDefault(),
                            "%.2f",
                            hydration,
                        ),
                        rightValue = stringResource(R.string.litres),
                        iconImageVector = Icons.Default.WaterDrop,
                    )
                    HorizontalDivider()
                }
                item {
                    EffortRow(
                        leftLabel = "Calories burned",
                        leftValue = uiState.stepRecord?.recordDate ?: LocalDate.now()
                            .minusDays(1).toString(),
                        rightLabel = uiState.stepRecord?.caloriesBurned.toString(),
                        rightValue = stringResource(R.string.kcal),
                        iconImageVector = Icons.Default.LocalFireDepartment,
                    )
                    HorizontalDivider()
                }
                if (uiState.dailyGoals?.any { it.value.completed } == true) {
                    item { GoalsHeader(title = "Completed daily goals") }
                    item { GoalsList(goalsSelected = uiState.dailyGoals.filter { it.value.completed }) }
                    // item { Spacer(modifier = Modifier.height(16.dp)) }
                }
                if (uiState.weeklyGoals?.any { it.value.completed } == true) {
                    item { GoalsHeader(title = "Completed weekly goals") }
                    item { GoalsList(goalsSelected = uiState.weeklyGoals.filter { it.value.completed }) }
                    // item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
