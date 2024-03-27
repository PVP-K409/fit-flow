package com.github.k409.fitflow.ui.screens.goals

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.GoalRecord
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.navigation.NavRoutes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun GoalsScreen(
    goalsViewModel: GoalsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val todayGoals by goalsViewModel.todayGoals.collectAsState()
    val weeklyGoals by goalsViewModel.weeklyGoals.collectAsState()

    val weekly = "Weekly"
    val daily = "Daily"
    var showProgress by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val permissionContract = PermissionController.createRequestPermissionResultContract()
    val launcher = rememberLauncherForActivityResult(permissionContract) {
        coroutineScope.launch {
            goalsViewModel.loadGoals(daily)
            goalsViewModel.loadGoals(weekly)
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (!goalsViewModel.permissionsGranted()) {
            launcher.launch(goalsViewModel.permissions)
        }

        val updateDaily = async { goalsViewModel.loadGoals(daily) }
        val updateWeekly = async { goalsViewModel.loadGoals(weekly) }

        awaitAll(updateDaily, updateWeekly)

        showProgress = false
    }

    if (showProgress) {
        FitFlowCircularProgressIndicator()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item { Text("Today's Goals:", style = MaterialTheme.typography.bodyLarge) }
                item { GoalsList(goalsSelected = todayGoals) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { Text("Weekly Goals:", style = MaterialTheme.typography.bodyLarge) }
                item { GoalsList(goalsSelected = weeklyGoals) }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.GoalCreation.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp)
                    .padding(end = 24.dp)
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
private fun GoalsList(
    goalsSelected: Map<String, GoalRecord>?,
) {
    goalsSelected.let { goals ->
        if (goals.isNullOrEmpty()) {
            Text("No goals.")
        } else {
            goals.values.forEach { goal ->
                OutlinedCard(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Goal: ${goal.description}", style = MaterialTheme.typography.bodyLarge)
                        Text("Target: ${goal.target}", style = MaterialTheme.typography.bodySmall)
                        Text("Progress: ${goal.currentProgress}", style = MaterialTheme.typography.bodySmall)
                        Text("Start Date: ${goal.startDate}", style = MaterialTheme.typography.bodySmall)
                        Text("End Date: ${goal.endDate}", style = MaterialTheme.typography.bodySmall)
                        Text("Type: ${goal.type}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
