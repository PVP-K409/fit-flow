package com.github.k409.fitflow.model

data class DailyStepRecord(
    var totalSteps: Long = 0,
    var stepCounterSteps: Long = 0,
    var initialSteps: Long = 0,
    var recordDate: String = "",
    var stepsBeforeReboot: Long = 0,
    var caloriesBurned: Long? = 0,
    var totalDistance: Double? = 0.0,
    var stepGoal: Long = 0L,
)
