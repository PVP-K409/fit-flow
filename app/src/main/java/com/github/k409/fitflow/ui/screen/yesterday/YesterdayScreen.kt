package com.github.k409.fitflow.ui.screen.yesterday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.goals.GoalsHeader
import com.github.k409.fitflow.ui.screen.goals.GoalsList
import com.github.k409.fitflow.ui.screen.you.EffortRow
import com.github.k409.fitflow.ui.screen.you.OutlineCardContainer
import kotlinx.coroutines.launch
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

@Composable
private fun YesterdayScreenContent(viewModel: YesterdayViewModel, uiState: YesterdayUiState.Success) {
    val hydration = (uiState.hydration.collectAsState(initial = null).value?.waterIntake ?: 0) / 1000f
    val coroutineScope = rememberCoroutineScope()

    val colors = MaterialTheme.colorScheme
    val background = Brush.linearGradient(
        colors = listOf(
            colors.primaryContainer,
            colors.onPrimaryContainer,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(top = 16.dp, bottom = 26.dp),
    ) {
        OutlineCardContainer(
            title = stringResource(R.string.welcome_back),
            subtitleText = stringResource(R.string.here_s_a_summary_of_your_activity_from_yesterday),
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    EffortRow(
                        leftLabel = stringResource(R.string.steps_taken),
                        leftValue = uiState.stepRecord?.recordDate ?: LocalDate.now()
                            .minusDays(1).toString(),
                        rightLabel = uiState.stepRecord?.totalSteps.toString(),
                        rightValue = stringResource(R.string.steps).lowercase(),
                        iconImageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                    )
                }
                item { HorizontalDivider() }
                item {
                    EffortRow(
                        leftLabel = stringResource(R.string.waterLogging),
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
                }
                item { HorizontalDivider() }
                item {
                    EffortRow(
                        leftLabel = stringResource(R.string.calories_burned),
                        leftValue = uiState.stepRecord?.recordDate ?: LocalDate.now()
                            .minusDays(1).toString(),
                        rightLabel = uiState.stepRecord?.caloriesBurned.toString(),
                        rightValue = stringResource(R.string.kcal),
                        iconImageVector = Icons.Default.LocalFireDepartment,
                    )
                }
                item { HorizontalDivider() }
                if (uiState.dailyGoals?.any { it.value.completed } == true) {
                    item { GoalsHeader(title = stringResource(R.string.completed_daily_goals)) }
                    item { GoalsList(goalsSelected = uiState.dailyGoals.filter { it.value.completed }) }
                }
                if (uiState.weeklyGoals?.any { it.value.completed } == true) {
                    item { GoalsHeader(title = stringResource(R.string.completed_weekly_goals)) }
                    item { GoalsList(goalsSelected = uiState.weeklyGoals.filter { it.value.completed }) }
                }
                item {
                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(0.5f),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.updateYesterdayPreference()
                            }
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            color = colors.onPrimary,
                        )
                    }
                }
            }
        }
    }
}
