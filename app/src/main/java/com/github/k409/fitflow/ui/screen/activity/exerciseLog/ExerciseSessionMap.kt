package com.github.k409.fitflow.ui.screen.activity.exerciseLog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.service.RouteTrackingService
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun ExerciseSessionMap(
    exerciseSessionMapViewModel: ExerciseSessionMapViewModel = hiltViewModel(),
) {
    val loading = exerciseSessionMapViewModel.loading.collectAsState()
    val exerciseRecord = exerciseSessionMapViewModel.exerciseRecord.collectAsState()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startLocalDateTime = exerciseRecord.value.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val endLocalDateTime = exerciseRecord.value.endTime.atZone(ZoneId.systemDefault()).toLocalDateTime()

    if (loading.value || exerciseRecord.value.exerciseRoute == null) {
        FitFlowCircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.65f)
                    .fillMaxWidth(),
            ) {
                GoogleMap(
                    properties = RouteTrackingService.mapProperties,
                    uiSettings = RouteTrackingService.mapUiSettings,
                ){
                    MapEffect { map ->
                        exerciseSessionMapViewModel.setGoogleMap(map)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = exerciseRecord.value.title ?: R.string.exercise_session),
                    modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Light,
                )
                Text(
                    text = "${timeFormatter.format(startLocalDateTime)} - ${
                        timeFormatter.format(
                            endLocalDateTime,
                        )
                    }",
                    style = MaterialTheme.typography.displayMedium,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                val duration = Duration.between(exerciseRecord.value.startTime, exerciseRecord.value.endTime)
                val hours = duration.toHours().toString()
                val minutes = (duration.toMinutes() % 60).toString()
                val calories = "${exerciseRecord.value.calories}"
                val distance = "${exerciseRecord.value.distance}"

                MetricColumn(icon = R.drawable.mode_heat_24px, text = "$calories Cal")
                MetricColumn(icon = R.drawable.distance_24px, text = "$distance km")
                MetricColumn(icon = R.drawable.clock, text = "$hours h $minutes min")
            }
        }
    }
}
