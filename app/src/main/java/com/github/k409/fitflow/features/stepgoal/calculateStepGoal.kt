// File for describing base idea and formula for daily step goal calculation
package com.github.k409.fitflow.features.stepgoal

fun calculateDailyStepGoal(
    defaultValue: Int = 5000,
    multiplier: Double = 1.05,
    daysToCheck: Int = 7,
): Int {
    // TODO: Replace stepHistory with actual values of user's last daysToCheck step values
    val stepHistory = listOf(5000, 5500, 6000, 5000, 5500, 6000, 10000)
    // TODO: Replace completedLastGoal with actual user value
    val completedLastGoal = true
    return if (stepHistory != null && stepHistory.average() >= defaultValue) {
        if (completedLastGoal) {
            (stepHistory.average() * multiplier).toInt()
        } else {
            (stepHistory.average()).toInt()
        }
    }
    else {
        defaultValue
    }
}