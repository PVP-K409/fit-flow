package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExerciseSessionScreen(
    exerciseSessionViewModel: ExerciseSessionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val fineLocationPermissionState = rememberMultiplePermissionsState(
        RouteTrackingService.fineLocationPermissions
    )
    val sessionPaused = RouteTrackingService.sessionPaused.collectAsState()
    val sessionActive = RouteTrackingService.sessionActive.collectAsState()
    val exercise = RouteTrackingService.selectedExercise.collectAsState()

    var map: GoogleMap? by remember { mutableStateOf(null) }

    var selectedExercise by remember { mutableStateOf("") }
    val expandedDropdown by remember { mutableStateOf(ExpandedDropdown.NONE) }
    var showInlineError by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        fineLocationPermissionState.launchMultiplePermissionRequest()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))
        if (fineLocationPermissionState.allPermissionsGranted) {
            if (sessionActive.value) {
                Box(modifier = Modifier.height(600.dp)) {
                    AndroidView({ MapView(it).apply { onCreate(null) } }, modifier = Modifier.fillMaxSize()) { mapView ->
                        mapView.getMapAsync { googleMap ->
                            map = googleMap
                            exerciseSessionViewModel.setupMap(map!!)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            else { // show dropdown
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ControlButtons(
                    sessionActive = sessionActive.value,
                    sessionPaused = sessionPaused.value,
                    onStart = {
                        if (selectedExercise.isNotEmpty()) {
                            showConfirmationDialog = true
                        }
                        else {
                            showInlineError = true
                        }
                    },
                    onStop = {
                        exerciseSessionViewModel.stopSession()
                        Intent(context, RouteTrackingService::class.java).also {
                            it.action = RouteTrackingService.Actions.STOP.toString()
                            context.startService(it)
                        }
                    },
                    onPause = {
                        Intent(context, RouteTrackingService::class.java).also {
                            it.action = RouteTrackingService.Actions.PAUSE.toString()
                            context.startService(it)
                        }
                    },
                    onResume = {
                        Intent(context, RouteTrackingService::class.java).also {
                            it.action = RouteTrackingService.Actions.RESUME.toString()
                            context.startService(it)
                        }
                    },
                )
            }
        }
        if (!fineLocationPermissionState.allPermissionsGranted) {
            ToSettings()
        }
        if (showConfirmationDialog) {
            ConfirmDialog(
                dialogTitle = "Are you sure you want start this exercise session?",
                dialogText = "Exercise: $selectedExercise",
                onDismiss = { showConfirmationDialog = false },
                onConfirm = {
                    showConfirmationDialog = false
                    RouteTrackingService.selectedExercise.value = selectedExercise
                    Intent(context, RouteTrackingService::class.java).also {
                        it.action = RouteTrackingService.Actions.START.toString()
                        context.startService(it)
                    }
                },
            )
        }

    }
}

@Composable
fun ControlButtons(
    sessionActive: Boolean,
    sessionPaused: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if(!sessionActive) {
            ActionButton(
                text = "Start",
                onClick = onStart,
                modifier = Modifier.weight(1f),
            )
        }
        if (sessionActive){
            ActionButton(
                text = "Stop",
                onClick = onStop,
                modifier = Modifier.weight(1f)
            )
        }
        if (sessionActive && sessionPaused) {
            ActionButton(
                text = "Resume",
                onClick = onResume,
                modifier = Modifier.weight(1f)
            )
        }
        if (sessionActive && !sessionPaused) {
            ActionButton(
                text = "Pause",
                onClick = onPause,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Location permission is needed to start the session and display the map.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Settings")
        }
    }
}