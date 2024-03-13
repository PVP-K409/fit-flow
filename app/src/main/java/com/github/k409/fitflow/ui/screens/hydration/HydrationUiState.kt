package com.github.k409.fitflow.ui.screens.hydration

data class HydrationUiState(
    val dailyGoal: Int = 0,
    val today: Int = 0,
    val yesterday: Int = 0,
    val thisWeek: Int = 0,
    val thisMonth: Int = 0,
    val cupSize: Int = 250,
)