package com.github.k409.fitflow.ui.screen.activity.exerciseLog

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.ExerciseRecord
import com.github.k409.fitflow.ui.common.Dialog
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.navigation.NavRoutes
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExercisesLogPage(
    exerciseLogViewModel: ExercisesLogViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val exerciseRecords by exerciseLogViewModel.exerciseRecords.collectAsState()
    val loading by exerciseLogViewModel.loading.collectAsState()
    val isDialogOpen = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val permissionContract = PermissionController.createRequestPermissionResultContract()
    val launcher = rememberLauncherForActivityResult(contract = permissionContract) {
        coroutineScope.launch {
            if (!exerciseLogViewModel.permissionsGranted()) {
                navController.navigate(NavRoutes.Aquarium.route)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (!exerciseLogViewModel.permissionsGranted()) {
            launcher.launch(exerciseLogViewModel.permissions)
        } else {
            exerciseLogViewModel.loadExerciseRecords()
        }
    }

    if (loading) {
        FitFlowCircularProgressIndicator()
    } else {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val maxDistance = if (exerciseRecords.maxOfOrNull { it.distance } != null) exerciseRecords.maxOf { it.distance.toFloat() } else 0f
        val maxDuration = if (exerciseRecords.maxOfOrNull { Duration.between(it.startTime, it.endTime).toMinutes() } != null) exerciseRecords.maxOf { Duration.between(it.startTime, it.endTime).toMinutes() } else 0f
        val exerciseTypes = exerciseRecords.mapNotNull { it.exerciseType }.distinct()
        var startDate by remember { mutableStateOf(if (exerciseRecords.isNotEmpty()) exerciseRecords.minOf { it.startTime }.atZone(ZoneId.systemDefault()) else ZonedDateTime.now() - Duration.ofDays(1)) }
        var endDate by remember { mutableStateOf(if (exerciseRecords.isNotEmpty()) exerciseRecords.maxOf { it.endTime }.atZone(ZoneId.systemDefault()) else ZonedDateTime.now()) }

        var distanceSliderPosition by remember { mutableStateOf(0f..maxDistance) }
        var durationSliderPosition by remember { mutableStateOf(0f..maxDuration.toFloat()) }
        val selectedExercise = remember { mutableStateListOf<Boolean>() }
        selectedExercise.addAll(exerciseTypes.map { true })

        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate.toInstant().toEpochMilli(),
            initialSelectedEndDateMillis = endDate.toInstant().toEpochMilli(),
            initialDisplayedMonthMillis = endDate.toInstant().toEpochMilli(),
            yearRange = startDate.year..endDate.year,
            initialDisplayMode = DisplayMode.Input,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            val filteredRecords = exerciseRecords.filter {
                it.distance.toFloat() in distanceSliderPosition.start..distanceSliderPosition.endInclusive + 0.001f &&
                    Duration.between(it.startTime, it.endTime).toMinutes() in durationSliderPosition.start.toInt()..durationSliderPosition.endInclusive.toInt() &&
                    it.startTime.truncatedTo(ChronoUnit.DAYS) >= startDate.toInstant().truncatedTo(ChronoUnit.DAYS) && it.endTime.truncatedTo(ChronoUnit.DAYS) <= endDate.toInstant().truncatedTo(ChronoUnit.DAYS) &&
                    selectedExercise[exerciseTypes.indexOf(it.exerciseType)]
            }
            if (filteredRecords.isEmpty()) {
                NoExerciseLogsFound()
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn {
                        items(
                            filteredRecords,
                        ) { record ->
                            ExerciseRecordCard(record)
                        }
                    }
                }
            }
            if (exerciseRecords.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = { Text(text = "Filter") },
                    onClick = {
                        isDialogOpen.value = true
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.FilterList,
                            contentDescription = "Filter exercise logs",
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 32.dp, start = 24.dp),
                )
            }
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.ExerciseSession.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 24.dp),
            ) {
                Icon(
                    NavRoutes.ExerciseSession.icon,
                    contentDescription = stringResource(R.string.create_exercise_session),
                )
            }
        }
        // Filter dialog
        if (isDialogOpen.value) {
            var showDatePicker by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Dialog(
                    title = stringResource(R.string.filter_exercise_log),
                    dismissButtonTitle = "Reset filters",
                    saveButtonTitle = "Close",
                    onDismiss = {
                        distanceSliderPosition = 0f..maxDistance
                        durationSliderPosition = 0f..maxDuration.toFloat()
                        startDate = exerciseRecords.minOf { it.startTime }
                            .atZone(ZoneId.systemDefault())
                        endDate = exerciseRecords.maxOf { it.endTime }
                            .atZone(ZoneId.systemDefault())
                        selectedExercise.clear()
                        selectedExercise.addAll(exerciseTypes.map { true })
                    },
                    onSaveClick = { isDialogOpen.value = false },
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(weight = 1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(5.dp),

                        ) {
                            Text(
                                text = stringResource(R.string.distance_km),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 12.dp),
                            )
                            RangeSlider(
                                value = distanceSliderPosition,
                                onValueChange = { range -> distanceSliderPosition = range },
                                valueRange = 0f..maxDistance,
                                // add steps for more precise values
                                steps = maxDistance.times(10).toInt(),
                            )
                            Text(
                                text = String.format(
                                    Locale.ENGLISH,
                                    "%.2f ",
                                    distanceSliderPosition.start,
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = String.format(
                                    Locale.ENGLISH,
                                    "%.2f ",
                                    distanceSliderPosition.endInclusive,
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(5.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.duration_minutes),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 12.dp),
                            )
                            RangeSlider(
                                value = durationSliderPosition,
                                onValueChange = { range -> durationSliderPosition = range },
                                steps = maxDuration.toInt() - 1,
                                valueRange = 0f..maxDuration.toFloat(),
                            )
                            Text(
                                text = String.format(
                                    Locale.ENGLISH,
                                    "%.0f ",
                                    durationSliderPosition.start,
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = String.format(
                                    Locale.ENGLISH,
                                    "%.0f ",
                                    durationSliderPosition.endInclusive,
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            if (startDate != null) {
                                TextField(
                                    value = startDate.format(formatter) + " - " + endDate.format(formatter),
                                    onValueChange = {},
                                    label = {
                                        Text(
                                            text = stringResource(R.string.date_range),
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.padding(bottom = 5.dp),
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    ),
                                    readOnly = true,
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .padding(top = 5.dp, bottom = 5.dp),
                                    interactionSource = remember { MutableInteractionSource() }
                                        .also { interactionSource ->
                                            LaunchedEffect(interactionSource) {
                                                interactionSource.interactions.collect {
                                                    if (it is PressInteraction.Release) {
                                                        showDatePicker = true
                                                    }
                                                }
                                            }
                                        },
                                )
                            }
                        }
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            startDate = ZonedDateTime.ofInstant(
                                                datePickerState.selectedStartDateMillis?.let {
                                                    Instant.ofEpochMilli(
                                                        it,
                                                    )
                                                },
                                                ZoneId.systemDefault(),
                                            )
                                            endDate = ZonedDateTime.ofInstant(
                                                datePickerState.selectedEndDateMillis?.let {
                                                    Instant.ofEpochMilli(
                                                        it,
                                                    )
                                                },
                                                ZoneId.systemDefault(),
                                            )
                                            showDatePicker = false
                                        },
                                    ) {
                                        Text(stringResource(R.string.confirm))
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDatePicker = false },
                                    ) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                },
                            ) {
                                DateRangePicker(
                                    state = datePickerState,
                                    title = {
                                        DateRangePickerDefaults.DateRangePickerTitle(
                                            displayMode = datePickerState.displayMode,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 24.dp, top = 16.dp),
                                        )
                                    },
                                    headline = {
                                        DateRangePickerDefaults.DateRangePickerHeadline(
                                            selectedStartDateMillis = datePickerState.selectedStartDateMillis,
                                            selectedEndDateMillis = datePickerState.selectedEndDateMillis,
                                            displayMode = datePickerState.displayMode,
                                            dateFormatter = remember { DatePickerDefaults.dateFormatter() },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 24.dp),
                                        )
                                    }
                                )
                            }
                        }

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            maxItemsInEachRow = 2,
                        ) {
                            Text(
                                text = stringResource(R.string.exercise_type),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .fillMaxWidth(),
                            )
                            for (type in exerciseTypes) {
                                FilterChip(
                                    label = {
                                        Text(
                                            text = type,
                                            color = MaterialTheme.colorScheme.secondary,
                                        )
                                    },
                                    selected = selectedExercise[exerciseTypes.indexOf(type)],
                                    onClick = {
                                        selectedExercise[exerciseTypes.indexOf(type)] =
                                            !selectedExercise[exerciseTypes.indexOf(type)]
                                    },
                                    modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                                    leadingIcon = if (selectedExercise[exerciseTypes.indexOf(type)]) {
                                        {
                                            Icon(
                                                imageVector = Icons.Outlined.Done,
                                                contentDescription = "Done icon",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            )
                                        }
                                    } else {
                                        null
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.onSecondary,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                    ),
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
fun ExerciseRecordCard(record: ExerciseRecord) {
    OutlinedCard(
        modifier = Modifier
            .padding(16.dp),
    ) {
        val title = record.exerciseType ?: "Exercise"
        ExerciseCardHeader(title = title, record.startTime)
        ExerciseRecordView(record)
    }
}

@Composable
fun ExerciseCardHeader(title: String, endDate: Instant) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startLocalDateTime = formatter.format(endDate.atZone(ZoneId.systemDefault()).toLocalDateTime()).toString()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 16.dp, bottom = 12.dp, top = 12.dp, end = 12.dp),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = startLocalDateTime,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun ExerciseRecordView(record: ExerciseRecord) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startLocalDateTime = record.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val endLocalDateTime = record.endTime.atZone(ZoneId.systemDefault()).toLocalDateTime()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5F)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = record.icon),
                contentDescription = "${record.exerciseType} icon",
                modifier = Modifier
                    .size(68.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "${timeFormatter.format(startLocalDateTime)} - ${timeFormatter.format(endLocalDateTime)}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85F),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                val duration = Duration.between(record.startTime, record.endTime)
                val hours = duration.toHours().toString()
                val minutes = (duration.toMinutes() % 60).toString()
                val calories = "${record.calories}"
                val distance = "${record.distance}"

                MetricColumn(icon = R.drawable.mode_heat_24px, text = "$calories Cal")
                Spacer(modifier = Modifier.width(12.dp))
                MetricColumn(icon = R.drawable.distance_24px, text = "$distance km")
                Spacer(modifier = Modifier.width(12.dp))
                MetricColumn(icon = R.drawable.clock, text = "$hours h $minutes min")
            }
        }
    }
}

@Composable
fun MetricColumn(
    icon: Int,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = "metric",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(36.dp),
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = color.copy(alpha = 0.85F),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun NoExerciseLogsFound() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "🕵️", fontSize = 48.sp)
        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = stringResource(R.string.it_seems_that_you_have_not_been_active_recently),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
        )
        Text(
            text = stringResource(R.string.try_manually_logging_the_exercises),
            fontSize = 10.sp,
        )
    }
}
