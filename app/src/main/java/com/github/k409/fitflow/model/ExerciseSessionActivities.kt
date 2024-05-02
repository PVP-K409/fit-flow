package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

enum class ExerciseSessionActivities(
    val exerciseSessionActivity: ExerciseSessionActivity,
) {
    Walking(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Walking",
            icon = R.drawable.walk,
            validExerciseTypes = setOf(79),
            locationUpdateInterval = 10000L,
            fastestLocationUpdateInterval = 5000L,
        ),
    ),
    Running(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Running",
            icon = R.drawable.run,
            validExerciseTypes = setOf(56, 57),
            locationUpdateInterval = 5000L,
            fastestLocationUpdateInterval = 3000L,
        ),
    ),
    Biking(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Biking",
            icon = R.drawable.bike,
            validExerciseTypes = setOf(8, 9),
            locationUpdateInterval = 4000L,
            fastestLocationUpdateInterval = 2000L,
        ),
    ),
}

data class ExerciseSessionActivity(
    val type: String,
    val icon: Int = R.drawable.walk,
    val validExerciseTypes: Set<Int>,
    val locationUpdateInterval: Long,
    val fastestLocationUpdateInterval: Long,
)

fun getAllExerciseSessionActivitiesTypes(): List<String> {
    return ExerciseSessionActivities.entries.map { it.exerciseSessionActivity.type }
}

fun getExerciseSessionActivityByType(type: String): ExerciseSessionActivity {
    return ExerciseSessionActivities.valueOf(type).exerciseSessionActivity
}
