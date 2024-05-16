package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.ExerciseSessionActivities
import com.github.k409.fitflow.model.getExerciseSessionActivityByType
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
fun ExerciseSessionScreen() {
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

    var selectedExercise: ExerciseSessionActivities? by remember { mutableStateOf(null) }
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
                Box(modifier = Modifier.fillMaxHeight(0.65f)) {
                    AndroidView({ MapView(it).apply { onCreate(null) } }) { mapView ->
                        mapView.getMapAsync { googleMap ->
                            RouteTrackingService.setGoogleMap(googleMap)
                        }
                    }
                }
            } else { // show dropdown
                Spacer(modifier = Modifier.height(24.dp))
                ExerciseDropdownMenu(
                    options = ExerciseSessionActivities.entries.map { stringResource(id = it.exerciseSessionActivity.title) },
                    selectedOption = selectedExercise?.exerciseSessionActivity?.title?.let {
                        stringResource(
                            id = it,
                        )
                    } ?: "",
                    label = stringResource(R.string.exercise),
                    onOptionSelected = { value ->
                        selectedExercise = ExerciseSessionActivities.entries.firstOrNull {
                            value == context.getString(it.exerciseSessionActivity.title)
                        }
                    },
                    expandedState = expandedDropdown == ExpandedDropdown.EXERCISE,
                )

                InlineError(selectedExercise == null && showInlineError)
            }
            if (sessionActive.value) {
                ExerciseParametric(
                    exercise = stringResource(id = getExerciseSessionActivityByType(exercise.value).title),
                    timeInSeconds = timeInSecond.value,
                    distance = distance.value,
                    avgSpeed = avgSpeed.value,
                    calories = calories.value,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ControlButtons(
                sessionActive = sessionActive.value,
                onStart = {
                    if (selectedExercise != null) {
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
        if (!fineLocationPermissionState.allPermissionsGranted) {
            Spacer(modifier = Modifier.weight(1f))
            ToSettings()
        }
        if (showConfirmationDialogStart) {
            ConfirmDialog(
                dialogTitle = stringResource(R.string.are_you_sure_you_want_start_this_exercise_session),
                dialogText = stringResource(id = R.string.exercise) + " : ${selectedExercise?.exerciseSessionActivity?.title?.let { stringResource(id = it) }}",
                onDismiss = { showConfirmationDialogStart = false },
                onConfirm = {
                    showConfirmationDialogStart = false
                    RouteTrackingService.selectedExercise.value = selectedExercise?.exerciseSessionActivity?.type ?: ""
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
                dialogText = if (timeInSecond.value < 60) {
                    stringResource(R.string.this_exercise_session_is_shorter_than_one_minute_and_will_not_be_saved)
                } else {
                    """
                        ${stringResource(id = R.string.distance)}: ${String.format(Locale.US, "%.2f", distance.value)} km
                        ${stringResource(id = R.string.time)}: ${formatTimeFromSeconds(timeInSecond.value)}
                        ${stringResource(id = R.string.calories)}: ${calories.value} cal
                        ${stringResource(id = R.string.average_speed)}: ${String.format(Locale.US, "%.2f", avgSpeed.value)} km/h
                    """.trimIndent()
                },
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
fun ExerciseParametric(
    exercise: String,
    timeInSeconds: Long,
    distance: Float,
    avgSpeed: Float,
    calories: Long,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(
            text = exercise,
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
            fontWeight = FontWeight.Light
        )

        val formattedTime = formatTimeFromSeconds(timeInSeconds)
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.displayLarge,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val modifier = Modifier.size(36.dp)
        Parametric(
            modifier = modifier,
            title = "km",
            value = String.format(Locale.US, "%.2f", distance),
            icon = R.drawable.distance_24px
        )
        Parametric(
            modifier = modifier,
            title = "km/h",
            value = String.format(Locale.US, "%.2f", avgSpeed),
            icon = R.drawable.speed_24
        )
        Parametric(
            modifier = modifier,
            title = "cal",
            value = "$calories",
            icon = R.drawable.mode_heat_24px
        )
    }
}

@Composable
fun Parametric(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: Int,
    fontSize : TextUnit = 26.sp,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier,
        )
        Text(
            text = value,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontWeight = fontWeight,
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        )
    }
}

@Composable
fun ControlButtons(
    sessionActive: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.primary

    val actionText = if (!sessionActive) {
        stringResource(R.string.start)
    } else {
        stringResource(R.string.stop)
    }

    val onClickAction = if (!sessionActive) {
        { onStart() }
    } else {
        { onStop() }
    }

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClickAction)
            .height(52.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = actionText,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 18.sp,
        )
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
