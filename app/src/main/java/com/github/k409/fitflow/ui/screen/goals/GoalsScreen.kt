package com.github.k409.fitflow.ui.screen.goals

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.GoalRecord
import com.github.k409.fitflow.model.HealthConnectExercises
import com.github.k409.fitflow.model.getIconByType
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.navigation.NavRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun GoalsScreen(
    goalsViewModel: GoalsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val todayGoals by goalsViewModel.todayGoals.collectAsState()
    val weeklyGoals by goalsViewModel.weeklyGoals.collectAsState()

    val weekly = "Weekly"
    val daily = "Daily"
    val loading by goalsViewModel.loading.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val permissionContract = PermissionController.createRequestPermissionResultContract()
    val launcher = rememberLauncherForActivityResult(permissionContract) {
        coroutineScope.launch {
            if (goalsViewModel.permissionsGranted()) {
                goalsViewModel.loadGoals(daily)
                goalsViewModel.loadGoals(weekly)
            } else {
                navController.navigate(NavRoutes.Aquarium.route)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (!goalsViewModel.permissionsGranted()) {
            launcher.launch(goalsViewModel.permissions)
        }

        goalsViewModel.loadGoals(daily)
        goalsViewModel.loadGoals(weekly)
    }

    if (loading) {
        FitFlowCircularProgressIndicator()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .matchParentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item { GoalsHeader(title = stringResource(R.string.todays_goals)) }
                item { GoalsList(goalsSelected = todayGoals) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { GoalsHeader(title = stringResource(R.string.weekly_goals)) }
                item { GoalsList(goalsSelected = weeklyGoals) }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.GoalCreation.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp)
                    .padding(end = 24.dp),
            ) {
                Icon(
                    NavRoutes.GoalCreation.icon,
                    contentDescription = stringResource(R.string.add_goal),
                )
            }
        }
    }
}

@Composable
fun GoalsHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun GoalsList(
    goalsSelected: Map<String, GoalRecord>?,
) {
    goalsSelected?.values?.forEachIndexed { index, goal ->
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = goal) {
            delay(index * 200L)
            visible = true
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        ) {
            GoalCard(goal = goal)
        }
    } ?: Text("")
}

@Composable
private fun GoalCard(
    goal: GoalRecord,
) {
    val endDate = LocalDate.parse(goal.endDate, DateTimeFormatter.ISO_LOCAL_DATE).minusDays(1)
    val adjustedEndDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

    val values = goal.description.split(" ")
    val duration = values.getOrNull(0) ?: ""
    val exercise = values.getOrNull(1) ?: ""

    // Sketchy way to get the translated exercise and duration
    val translatedExercise =
        HealthConnectExercises.entries.find { it.type == exercise }?.title?.let { stringResource(id = it) }
    val translatedDuration =
        when (duration) {
            "Daily" -> stringResource(R.string.daily)
            "Weekly" -> stringResource(R.string.weekly)
            else -> ""
        }

    val description = "$translatedDuration $translatedExercise"

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(6.dp),
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            GoalHeader(description, getIconByType(goal.type), adjustedEndDate)
            Spacer(Modifier.height(18.dp))
            Progress(
                goal = goal,
                color = if (goal.completed) {
                    MaterialTheme.colorScheme.primary
                } else MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.5f,
                ),
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun GoalHeader(
    title: String,
    icon: Int,
    endDate: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp),
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Goal",
            tint = color,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = endDate,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun Progress(
    goal: GoalRecord,
    color: Color,
) {
    val unit =
        if (goal.type == "Walking") stringResource(R.string.steps) else "km"
    var displayProgress = if (unit == stringResource(R.string.steps)) {
        goal.currentProgress.toLong()
            .toString()
    } else goal.currentProgress.toString()
    val displayTarget = if (unit == stringResource(R.string.steps)) {
        goal.target.toLong()
            .toString()
    } else goal.target.toString()

    if (goal.completed) displayProgress = displayTarget

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = "$displayProgress / $displayTarget $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(end = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "${goal.points}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = "Goal",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "${goal.xp}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    painter = painterResource(id = R.drawable.xp),
                    contentDescription = "Goal",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        LinearProgressIndicator(
            progress = { (goal.currentProgress / goal.target).toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(end = 8.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
        )
    }
}
