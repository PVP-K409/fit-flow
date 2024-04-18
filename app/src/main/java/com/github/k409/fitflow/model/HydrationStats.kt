package com.github.k409.fitflow.model

data class HydrationStats(
    val yesterdayTotalAmount: Int = 0,
    val thisWeekTotalAmount: Int = 0,
    val thisMonthTotalAmount: Int = 0,
    val maxIntake: Pair<String, Int> = Pair("", 0),
)
