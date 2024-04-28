package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView


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

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        if (showMap && fineLocationPermissionState.allPermissionsGranted) {
            exerciseSessionViewModel.getUserLocation()
            AndroidView(factory = { context ->
                MapView(context).apply {
                    onCreate(null)
                    getMapAsync { googleMap ->
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
                        googleMap.uiSettings.isZoomControlsEnabled = true
                    }
                }
            }, modifier = Modifier
                .weight(1f)
                .fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                // Additional UI elements here
            }
        }
        if(!showMap) {
            Button(
                onClick = { exerciseSessionViewModel.startSession() }
            )
            {
                Text("Start")
            }
        }
        if (showMap && fineLocationPermissionState.allPermissionsGranted){
            Button(
                onClick = { exerciseSessionViewModel.stopSession() }
            )
            {
                Text("Stop")
            }
        }
        if (showMap && !fineLocationPermissionState.allPermissionsGranted) {
            ToSettings()
        }
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