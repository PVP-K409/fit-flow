package com.github.k409.fitflow.ui.screen.activity.exerciseLog

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.ExerciseRecord
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.navigation.NavRoutes
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ExercisesLogPage(
    exerciseLogViewModel: ExercisesLogViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val exerciseRecords by exerciseLogViewModel.exerciseRecords.collectAsState()
    val loading by exerciseLogViewModel.loading.collectAsState()

    LaunchedEffect(key1 = Unit) {
        exerciseLogViewModel.loadExerciseRecords()
    }

    if (loading) {
        FitFlowCircularProgressIndicator()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            if (exerciseRecords.isEmpty()) {
                NoExerciseLogsFound()
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn {
                        items(exerciseRecords) { record ->
                            ExerciseRecordCard(record)
                        }
                    }
                }
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
            text = "It seems that you have not been active recently",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
        )
        Text(
            text = "Try manually logging the exercises",
            fontSize = 10.sp,
        )
    }
}
