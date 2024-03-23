package com.github.k409.fitflow.ui.screens.hydration

import com.github.k409.fitflow.model.HydrationStats

data class HydrationUiState(
    val dailyGoal: Int = 0,
    val today: Int = 0,
    val cupSize: Int = 250,
    val stats: HydrationStats = HydrationStats()
)
