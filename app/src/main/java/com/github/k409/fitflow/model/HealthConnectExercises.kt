package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

enum class HealthConnectExercises(
    val healthConnectExercise: HealthConnectExercise,
) {
    Running(
        healthConnectExercise = HealthConnectExercise(
            type = "Running",
            icon = R.drawable.run,
            validExerciseTypes = setOf(56, 57),
        ),
    ),
    Biking(
        healthConnectExercise = HealthConnectExercise(
            type = "Biking",
            icon = R.drawable.bike,
            validExerciseTypes = setOf(8, 9),
        ),
    ),
    Walking(
        healthConnectExercise = HealthConnectExercise(
            type = "Walking",
            icon = R.drawable.walk,
            validExerciseTypes = setOf(79),
        )
    );
    companion object {
        fun findByExerciseType(exerciseType: Int): HealthConnectExercise? {
            return entries.firstOrNull { exerciseType in it.healthConnectExercise.validExerciseTypes }?.healthConnectExercise
        }
        fun getIconByType(type: String): Int {
            return HealthConnectExercises.entries.find { it.healthConnectExercise.type == type }?.healthConnectExercise?.icon ?: R.drawable.ecg_heart_24px
        }
    }
}

data class HealthConnectExercise(
    val type: String,
    val icon: Int,
    val validExerciseTypes: Set<Int>,
)


