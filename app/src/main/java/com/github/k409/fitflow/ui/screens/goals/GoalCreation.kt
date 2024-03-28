package com.github.k409.fitflow.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.DropdownMenu
import com.github.k409.fitflow.ui.navigation.NavRoutes


@Composable
fun GoalCreation(
    goalsViewModel: GoalsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val goalOptions = listOf(stringResource(id = R.string.daily), stringResource(id = R.string.weekly))
    val distanceOptionsDay = List(50) { i -> "${0.5 * (i + 1)} km" }
    val distanceOptionsWeek = List(50) { i -> "${2 * (i + 1)} km" }

    var selectedGoal by remember { mutableStateOf("") }
    var selectedExercise by remember { mutableStateOf("") }
    var selectedDistance by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showInlineError by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Create Goal", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenu(
                options = goalOptions,
                selectedOption = selectedGoal,
                label = "Select Goal Period",
                onOptionSelected = {
                    selectedGoal = it
                    selectedDistance = ""
                    selectedExercise = ""
                }
            )

            InlineError(selectedGoal.isEmpty() && showInlineError)

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedGoal.isNotEmpty()) {

                val exerciseOptions = goalsViewModel.getValidExerciseTypes(selectedGoal)

                if (exerciseOptions.isNotEmpty()) {
                    DropdownMenu(
                        options = exerciseOptions,
                        selectedOption = selectedExercise,
                        label = "Select Exercise",
                        onOptionSelected = { selectedExercise = it }
                    )

                    InlineError(selectedExercise.isEmpty() && showInlineError)

                    Spacer(modifier = Modifier.height(24.dp))

                    val distanceOptions = if (selectedGoal == stringResource(id = R.string.daily)) {
                        distanceOptionsDay
                    } else {
                        distanceOptionsWeek
                    }

                    DropdownMenu(
                        options = distanceOptions,
                        selectedOption = selectedDistance,
                        label = "Select Distance",
                        onOptionSelected = { selectedDistance = it }
                    )

                    InlineError(selectedDistance.isEmpty() && showInlineError)

                    Spacer(modifier = Modifier.height(24.dp))
                }
                else {
                    NoValidGoalsMessage()
                }


            }

            Button(onClick = {
                if (selectedGoal.isNotEmpty() && selectedExercise.isNotEmpty() && selectedDistance.isNotEmpty()) {
                    showConfirmationDialog = true
                } else {
                    showInlineError = true
                }
            }) {
                Text("Create")
            }

            if (showConfirmationDialog) {
                ConfirmationDialog(
                    selectedGoal,
                    selectedExercise,
                    selectedDistance,
                    onDismissRequest = { showConfirmationDialog = false },
                    onConfirm = {
                        goalsViewModel.submitGoalAsync(selectedGoal, selectedExercise, selectedDistance.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0)
                        showConfirmationDialog = false
                        navController.navigate(NavRoutes.Goals.route)
                    }
                )
            }
        }
    }
}

@Composable
fun InlineError(show: Boolean) {
    if (show) {
        Text(
            text = "Please select this field",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
        )
    }
}

@Composable
fun NoValidGoalsMessage() {
    Text(
        text = "No valid exercises are available for the selected goal period.",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(16.dp)
    )
}


@Composable
fun ConfirmationDialog(
    selectedGoal: String,
    selectedExercise: String,
    selectedDistance: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Are you sure you want to create this goal?") },
        text = { Text("$selectedGoal\n$selectedExercise\n$selectedDistance") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}











