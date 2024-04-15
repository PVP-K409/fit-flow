package com.github.k409.fitflow.ui.screen.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.getIconByType
import com.github.k409.fitflow.ui.common.ConfirmDialog
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

    val expandedDropdown by remember { mutableStateOf(ExpandedDropdown.NONE) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.create_goal),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Start),
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            )

            GoalDropdownMenu(
                options = goalOptions,
                selectedOption = selectedGoal,
                label = stringResource(R.string.period),
                onOptionSelected = {
                    selectedGoal = it
                    selectedDistance = ""
                    selectedExercise = ""
                },
                expandedState = expandedDropdown == ExpandedDropdown.GOAL,
            )

            InlineError(selectedGoal.isEmpty() && showInlineError)

            if (selectedGoal.isNotEmpty()) {
                val exerciseOptions = goalsViewModel.getValidExerciseTypes(selectedGoal)

                if (exerciseOptions.isNotEmpty()) {
                    ExerciseDropdownMenu(
                        options = exerciseOptions,
                        selectedOption = selectedExercise,
                        label = stringResource(R.string.exercise),
                        onOptionSelected = { selectedExercise = it },
                        expandedState = expandedDropdown == ExpandedDropdown.EXERCISE,
                    )

                    InlineError(selectedExercise.isEmpty() && showInlineError)

                    val distanceOptions = if (selectedGoal == stringResource(id = R.string.daily)) {
                        distanceOptionsDay
                    } else {
                        distanceOptionsWeek
                    }

                    GoalDropdownMenu(
                        options = distanceOptions,
                        selectedOption = selectedDistance,
                        label = stringResource(id = R.string.distance),
                        onOptionSelected = { selectedDistance = it },
                        expandedState = expandedDropdown == ExpandedDropdown.DISTANCE,
                    )

                    InlineError(selectedDistance.isEmpty() && showInlineError)
                } else {
                    NoValidGoalsMessage()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (selectedGoal.isNotEmpty() && selectedExercise.isNotEmpty() && selectedDistance.isNotEmpty()) {
                    showConfirmationDialog = true
                } else {
                    showInlineError = true
                }
            }) {
                Text(stringResource(R.string.create))
            }

            if (showConfirmationDialog) {
                ConfirmDialog(
                    dialogTitle = "Are you sure you want to create this goal?",
                    dialogText = "$selectedGoal\n$selectedExercise\n$selectedDistance",
                    onDismiss = { showConfirmationDialog = false },
                    onConfirm = {
                        goalsViewModel.submitGoalAsync(selectedGoal, selectedExercise, selectedDistance.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0)
                        showConfirmationDialog = false
                        navController.navigate(NavRoutes.Goals.route)
                    },
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
            modifier = Modifier.padding(start = 16.dp, top = 2.dp),
        )
    }
}

@Composable
fun NoValidGoalsMessage() {
    Text(
        text = "No valid exercises are available for the selected goal period.",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(16.dp),
    )
}

@Composable
fun GoalDropdownMenu(
    options: List<String>,
    selectedOption: String,
    label: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    expandedState: Boolean,
) {
    var isExpanded by remember { mutableStateOf(expandedState) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = true },
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = selectedOption.ifEmpty { stringResource(R.string.select_an_option) },
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }

    if (isExpanded) {
        Dialog(
            onDismissRequest = { isExpanded = false },
        ) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .wrapContentSize(Alignment.Center)
                    .sizeIn(maxHeight = 300.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .shadow(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                        ),
                ) {
                    items(options) { option ->
                        DropdownOption(
                            option = option,
                            onOptionSelected = {
                                onOptionSelected(option)
                                isExpanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseDropdownMenu(
    options: List<String>,
    selectedOption: String,
    label: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    expandedState: Boolean,
) {
    var isExpanded by remember { mutableStateOf(expandedState) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = true },
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = selectedOption.ifEmpty { stringResource(R.string.select_an_option) },
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }

    if (isExpanded) {
        Dialog(onDismissRequest = { isExpanded = false }) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .wrapContentSize(Alignment.Center)
                    .sizeIn(maxHeight = 300.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .shadow(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                        ),
                ) {
                    options.forEach { option ->
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(option)
                                        isExpanded = false
                                    }
                                    .padding(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(id = getIconByType(option)),
                                    contentDescription = "Exercise icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownOption(
    option: String,
    onOptionSelected: () -> Unit,
) {
    Text(
        text = option,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOptionSelected() }
            .padding(16.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}

enum class ExpandedDropdown {
    NONE, GOAL, EXERCISE, DISTANCE
}
