package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

enum class ExerciseSessionActivities(
    val exerciseSessionActivity: ExerciseSessionActivity,
) {
    Walking(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Walking",
            icon = R.drawable.walk,
            validExerciseType = 79,
            locationUpdateInterval = 15000L,
            fastestLocationUpdateInterval = 10000L,
            met = 3.0,
        ),
    ),
    Running(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Running",
            icon = R.drawable.run,
            validExerciseType = 56,
            locationUpdateInterval = 10000L,
            fastestLocationUpdateInterval = 6000L,
            met = 8.8,
        ),
    ),
    Biking(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Biking",
            icon = R.drawable.bike,
            validExerciseType = 8,
            locationUpdateInterval = 8000L,
            fastestLocationUpdateInterval = 5000L,
            met = 6.0,
        ),
    ),
}

data class ExerciseSessionActivity(
    val type: String,
    val icon: Int = R.drawable.walk,
    val validExerciseType: Int,
    val locationUpdateInterval: Long,
    val fastestLocationUpdateInterval: Long,
    val met: Double,
)

fun getAllExerciseSessionActivitiesTypes(): List<String> {
    return ExerciseSessionActivities.entries.map { it.exerciseSessionActivity.type }
}

fun getExerciseSessionActivityByType(type: String): ExerciseSessionActivity {
    return ExerciseSessionActivities.valueOf(type).exerciseSessionActivity
}
