package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.service.RouteTrackingService
import com.github.k409.fitflow.ui.common.ConfirmDialog
import com.github.k409.fitflow.ui.screen.goals.ExerciseDropdownMenu
import com.github.k409.fitflow.ui.screen.goals.ExpandedDropdown
import com.github.k409.fitflow.ui.screen.goals.InlineError
import com.github.k409.fitflow.util.formatTimeFromSeconds
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.MapView
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExerciseSessionScreen(
    exerciseSessionViewModel: ExerciseSessionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val fineLocationPermissionState = rememberMultiplePermissionsState(
        RouteTrackingService.fineLocationPermissions,
    )
    val sessionActive = RouteTrackingService.isTracking.collectAsState()
    val exercise = RouteTrackingService.selectedExercise.collectAsState()

    val timeInSecond = RouteTrackingService.timeRunInSecond.collectAsState()
    val distance = RouteTrackingService.distanceInKm.collectAsState()
    val avgSpeed = RouteTrackingService.avgSpeed.collectAsState()
    val calories = RouteTrackingService.calories.collectAsState()

    var selectedExercise by remember { mutableStateOf("") }
    val expandedDropdown by remember { mutableStateOf(ExpandedDropdown.NONE) }
    var showInlineError by remember { mutableStateOf(false) }
    var showConfirmationDialogStart by remember { mutableStateOf(false) }
    var showConfirmationDialogStop by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        if (!fineLocationPermissionState.allPermissionsGranted) {
            fineLocationPermissionState.launchMultiplePermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (fineLocationPermissionState.allPermissionsGranted) {
            if (sessionActive.value) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = exercise.value,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp),
                    )
                    TimeDisplay(
                        modifier = Modifier.padding(16.dp),
                        timeInSeconds = timeInSecond.value,
                    )
                    Text("${String.format(Locale.US, "%.2f", distance.value)} km")
                    Text("${String.format(Locale.US, "%.2f", avgSpeed.value)} km/h")
                    Text("${calories.value} cal")
                }
                Box(modifier = Modifier.fillMaxHeight(0.8f)) {
                    AndroidView({ MapView(it).apply { onCreate(null) } }) { mapView ->
                        mapView.getMapAsync { googleMap ->
                            RouteTrackingService.setGoogleMap(googleMap)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else { // show dropdown
                val exerciseTypes = exerciseSessionViewModel.getValidExerciseSessionActivitiesTypes()
                ExerciseDropdownMenu(
                    options = exerciseTypes,
                    selectedOption = selectedExercise,
                    label = stringResource(R.string.exercise),
                    onOptionSelected = { selectedExercise = it },
                    expandedState = expandedDropdown == ExpandedDropdown.EXERCISE,
                )

                InlineError(selectedExercise.isEmpty() && showInlineError)
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ControlButtons(
                    sessionActive = sessionActive.value,
                    onStart = {
                        if (selectedExercise.isNotEmpty()) {
                            showConfirmationDialogStart = true
                        } else {
                            showInlineError = true
                        }
                    },
                    onStop = {
                        showConfirmationDialogStop = true
                    },
                )
            }
        }
        if (!fineLocationPermissionState.allPermissionsGranted) {
            Spacer(modifier = Modifier.weight(1f))
            ToSettings()
        }
        if (showConfirmationDialogStart) {
            ConfirmDialog(
                dialogTitle = stringResource(R.string.are_you_sure_you_want_start_this_exercise_session),
                dialogText = stringResource(R.string.selected_exercise, selectedExercise),
                onDismiss = { showConfirmationDialogStart = false },
                onConfirm = {
                    showConfirmationDialogStart = false
                    RouteTrackingService.selectedExercise.value = selectedExercise
                    Intent(context, RouteTrackingService::class.java).also {
                        it.action = RouteTrackingService.Actions.START.toString()
                        context.startService(it)
                    }
                },
            )
        }
        if (showConfirmationDialogStop) {
            ConfirmDialog(
                dialogTitle = stringResource(R.string.are_you_sure_you_want_stop_this_exercise_session),
                dialogText = stringResource(
                    R.string.new_exercise_dialog_text,
                    String.format(Locale.US, "%.2f", distance.value),
                    formatTimeFromSeconds(timeInSecond.value),
                    calories.value,
                    String.format(Locale.US, "%.2f", avgSpeed.value)
                ).trimIndent(),
                onDismiss = { showConfirmationDialogStop = false },
                onConfirm = {
                    showConfirmationDialogStop = false
                    Intent(context, RouteTrackingService::class.java).also {
                        it.action = RouteTrackingService.Actions.STOP.toString()
                        context.startService(it)
                    }
                },
            )
        }
    }
}

@Composable
fun TimeDisplay(
    modifier: Modifier,
    timeInSeconds: Long,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val formattedTime = formatTimeFromSeconds(timeInSeconds)

    Text(
        text = formattedTime,
        style = textStyle,
        modifier = modifier,
    )
}

@Composable
fun ControlButtons(
    sessionActive: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (!sessionActive) {
            ActionButton(
                text = stringResource(R.string.start),
                onClick = onStart,
                modifier = Modifier.weight(1f),
            )
        }
        if (sessionActive) {
            ActionButton(
                text = stringResource(R.string.stop),
                onClick = onStop,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Text(text)
    }
}

@Composable
fun ToSettings() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.location_permission_is_needed_to_start_the_session_and_display_the_map),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.open_settings))
        }
    }
}
