package com.github.k409.fitflow.model

import androidx.health.connect.client.records.ExerciseRoute
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

data class ExerciseSession(
    var exerciseType: Int = 0,
    var startTime: Instant = Instant.now(),
    var startZoneOffset: ZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now()),
    var endTime: Instant = Instant.now(),
    var endZoneOffset: ZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now()),
    var distance: Float = 0.0f,
    var calories: Long = 0L,
    var route: List<ExerciseRoute.Location> = listOf(),
)
