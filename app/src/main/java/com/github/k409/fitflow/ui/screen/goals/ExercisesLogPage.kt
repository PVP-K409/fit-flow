package com.github.k409.fitflow.ui.screen.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.model.ExerciseRecord
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun ExercisesLogPage(
    exerciseLogViewModel: ExerciseLogViewModel = hiltViewModel(),
){
    val exerciseRecords  by exerciseLogViewModel.exerciseRecords.collectAsState()
    val loading by exerciseLogViewModel.loading.collectAsState()

    LaunchedEffect(key1 = Unit) {
        exerciseLogViewModel.loadExerciseRecords()
    }

    if (loading) {
       FitFlowCircularProgressIndicator()
    } else {
        LazyColumn {
            items(exerciseRecords) { record ->
                ExerciseRecordCard(record)
            }
        }
    }

}

@Composable
fun ExerciseRecordCard(record: ExerciseRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        ExerciseRecordView(record)
    }
}

@Composable
fun ExerciseRecordView(record: ExerciseRecord) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val startLocalDateTime = record.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val endLocalDateTime = record.endTime.atZone(ZoneId.systemDefault()).toLocalDateTime()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Start Time: ${formatter.format(startLocalDateTime)}")
        Text("End Time: ${formatter.format(endLocalDateTime)}")
        Text("Type: ${record.exerciseType ?: "N/A"}")
        Text("Distance: ${record.distance} km")
        Text("Distance: ${record.calories} Cal")
    }
}

