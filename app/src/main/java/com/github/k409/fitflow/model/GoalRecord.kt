package com.github.k409.fitflow.model

data class GoalRecord(
    var description: String = "",
    var type: String = "",
    var target: Double = 0.0,
    var currentProgress: Double = 0.0,
    var points: Long = 0,
    var xp: Long = 0,
    var startDate: String = "",
    var endDate: String = "",
    var completed: Boolean = false,
    var mandatory: Boolean = false,
)
