package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

enum class HealthConnectGoals(
    val healthConnectGoal: HealthConnectGoal,
    ) {
    Running(
        healthConnectGoal = HealthConnectGoal(
            type = "Running",
            icon = R.drawable.run,
            boost = 20.0,
            validExerciseTypes =  setOf(56, 57)
        ),
    ),
    Biking(
        healthConnectGoal = HealthConnectGoal(
            type = "Biking",
            icon = R.drawable.bike,
            boost = 10.0,
            validExerciseTypes = setOf(8, 9)
        ),
    ),

}
data class HealthConnectGoal(
    val type: String,
    val icon: Int,
    val boost: Double,
    val validExerciseTypes: Set<Int>,
)

fun getGoalTypes(): List<String> {
    return HealthConnectGoals.entries.map { it.healthConnectGoal.type }
}

fun getGoalByType(type: String): HealthConnectGoal? {
    return HealthConnectGoals.entries.find { it.healthConnectGoal.type == type }?.healthConnectGoal
}

fun getValidExerciseTypesByType(type: String): Set<Int> {
    return HealthConnectGoals.entries.find { it.healthConnectGoal.type == type }?.healthConnectGoal?.validExerciseTypes ?: emptySet()
}

