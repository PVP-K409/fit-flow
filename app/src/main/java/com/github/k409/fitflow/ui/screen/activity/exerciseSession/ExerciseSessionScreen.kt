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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.service.RouteTrackingService
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
    val isTracking = exerciseSessionViewModel.isTracking.collectAsState().value
    var map: GoogleMap? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = Unit) {
        fineLocationPermissionState.launchMultiplePermissionRequest()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))

        if (fineLocationPermissionState.allPermissionsGranted) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if(!isTracking && fineLocationPermissionState.allPermissionsGranted) {
                ActionButton(
                    text = "Start",
                    onClick = {
                        exerciseSessionViewModel.startSession()
                        Intent(context, RouteTrackingService::class.java).also {
                            it.action = RouteTrackingService.Actions.START.toString()
                            context.startService(it)
                        }
                              },
                    modifier = Modifier.weight(1f),
                )
            }
            if (isTracking && fineLocationPermissionState.allPermissionsGranted){
                ActionButton(
                    text = "Stop",
                    onClick = {
                        exerciseSessionViewModel.stopSession()
                        Intent(context, RouteTrackingService::class.java).also {
                            it.action = RouteTrackingService.Actions.STOP.toString()
                            context.startService(it)
                        }
                              },
                    modifier = Modifier.weight(1f)
                )
            }
            if (!fineLocationPermissionState.allPermissionsGranted) {
                ToSettings()
            }
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