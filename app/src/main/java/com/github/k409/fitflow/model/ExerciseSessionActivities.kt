package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

enum class ExerciseSessionActivities (
    val exerciseSessionActivity: ExerciseSessionActivity,
) {
    Walking(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Walking",
            icon = R.drawable.walk,
            validExerciseTypes = setOf(79),
        ),
    ),
    Running(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Running",
            icon = R.drawable.run,
            validExerciseTypes = setOf(56, 57),
        ),
    ),
    Biking(
        exerciseSessionActivity = ExerciseSessionActivity(
            type = "Biking",
            icon = R.drawable.bike,
            validExerciseTypes = setOf(8, 9),
        ),
    ),

}

data class ExerciseSessionActivity(
    val type: String,
    val icon: Int,
    val validExerciseTypes: Set<Int>,
)