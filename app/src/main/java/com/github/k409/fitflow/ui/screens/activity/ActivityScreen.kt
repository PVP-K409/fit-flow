package com.github.k409.fitflow.ui.screens.activity

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.ui.common.HorizontalPagerIndicator
import com.github.k409.fitflow.ui.common.ProgressGraph
import com.github.k409.fitflow.ui.common.TextWithLabel
import com.github.k409.fitflow.ui.components.activity.CircularProgressBar
import com.github.k409.fitflow.ui.components.activity.DistanceAndCalories
import com.github.k409.fitflow.ui.components.calendar.CalendarView
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ActivityScreen(activityViewModel: ActivityViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = {
        2
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) { page ->
            when (page) {
                0 -> {
                    ActivityContent(activityViewModel = activityViewModel)
                }

                1 -> {
                    ProgressGraphUsageSample()
                }
            }
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            pagerState = pagerState,
        )
    }
}

@Composable
private fun ActivityContent(activityViewModel: ActivityViewModel) {
    val todaySteps by activityViewModel.todaySteps.observeAsState()
    val todayGoal: Long = 6000 // TODO goal setter
    val permissionContract = PermissionController.createRequestPermissionResultContract()
    val launcher = rememberLauncherForActivityResult(permissionContract) {
    }

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var selectedDateRecord by remember { mutableStateOf(todaySteps) }

    LaunchedEffect(key1 = Unit) {
        if (!activityViewModel.permissionsGranted()) {
            launcher.launch(activityViewModel.permissions)
        }

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
