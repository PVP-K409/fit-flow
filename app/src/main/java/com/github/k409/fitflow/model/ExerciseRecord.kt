package com.github.k409.fitflow.model

import android.health.connect.datatypes.ExerciseRoute
import androidx.health.connect.client.records.ExerciseRouteResult
import java.time.Instant

class ExerciseRecord (
    var startTime: Instant,
    var endTime: Instant,
    var distance: Double,
    var calories: Long,
    var icon: Int,
    var exerciseType: String?,
    var exerciseRoute: androidx.health.connect.client.records.ExerciseRoute?,

)
