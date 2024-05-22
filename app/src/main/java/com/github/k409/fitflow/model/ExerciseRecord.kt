package com.github.k409.fitflow.model

import androidx.annotation.StringRes
import androidx.health.connect.client.records.ExerciseRoute
import java.time.Instant

class ExerciseRecord(
    var id: Int = 0,
    var startTime: Instant = Instant.now(),
    var endTime: Instant = Instant.now(),
    var distance: Double = 0.0,
    var calories: Long = 0,
    var icon: Int = 0,
    var exerciseType: String? = "",
    @StringRes var title: Int? = 0,
    var exerciseRoute: ExerciseRoute? = null,
)
