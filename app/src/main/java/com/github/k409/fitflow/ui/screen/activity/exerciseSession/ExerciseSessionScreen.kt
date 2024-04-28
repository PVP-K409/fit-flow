package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
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
import androidx.compose.material3.Switch
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExerciseSessionScreen(
    exerciseSessionViewModel: ExerciseSessionViewModel = hiltViewModel(),
) {
    val fineLocationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
        ),
    )
    val showMap = exerciseSessionViewModel.showMap.collectAsState().value
    val distance = exerciseSessionViewModel.distance.collectAsState().value
    val time = exerciseSessionViewModel.time.collectAsState().value
    val location = exerciseSessionViewModel.liveLocation.collectAsState().value


    LaunchedEffect(key1 = Unit) {
        fineLocationPermissionState.launchMultiplePermissionRequest()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))

        if (showMap && fineLocationPermissionState.allPermissionsGranted) {
            exerciseSessionViewModel.getUserLocation()

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 14f)
            }
            val marker = MarkerState(position = location)
            var uiSettings by remember { mutableStateOf(MapUiSettings()) }
            val properties by remember {
                mutableStateOf( MapProperties(mapType = MapType.SATELLITE))
            }

            Log.d("ExerciseSessionScreen", "location: $location")
            Box(modifier = Modifier.height(600.dp)) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                ) {
                    Marker(
                        state = marker,
                        title = "Current Location",
                    )
                }
                Switch(
                    checked = uiSettings.zoomControlsEnabled,
                    onCheckedChange = {
                        uiSettings = uiSettings.copy(zoomControlsEnabled = it)
                    })

                Spacer(modifier = Modifier.height(16.dp))
            }

        }
        Row(
            modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if(!showMap) {
                ActionButton(
                    text = "Start",
                    onClick = { exerciseSessionViewModel.startSession() },
                    modifier = Modifier.weight(1f),
                )
            }
            if (showMap && fineLocationPermissionState.allPermissionsGranted){
                ActionButton(
                    text = "Stop",
                    onClick = { exerciseSessionViewModel.stopSession() },
                    modifier = Modifier.weight(1f)
                )
            }
            if (showMap && !fineLocationPermissionState.allPermissionsGranted) {
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