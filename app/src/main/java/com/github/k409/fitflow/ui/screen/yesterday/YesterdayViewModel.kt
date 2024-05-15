package com.github.k409.fitflow.ui.screen.yesterday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.GoalRecord
import com.github.k409.fitflow.model.HydrationRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class YesterdayViewModel @Inject constructor(
    private val stepsRepository: StepsRepository,
    private val goalsRepository: GoalsRepository,
    private val hydrationRepository: HydrationRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val yesterday = LocalDate.now().minusDays(1)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val formattedYesterday = yesterday.format(dateFormatter)

    private val _yesterdayUiState: MutableStateFlow<YesterdayUiState> =
        MutableStateFlow(YesterdayUiState.NotStarted)
    val yesterdayUiState: StateFlow<YesterdayUiState> = _yesterdayUiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadYesterdayData()
        }
    }
    private fun loadYesterdayData() {
        viewModelScope.launch {
            _yesterdayUiState.value = YesterdayUiState.Loading

            val stepRecord = stepsRepository.getSteps(formattedYesterday)
            val dailyGoals = goalsRepository.getDailyGoals(formattedYesterday)
            val weeklyGoals = goalsRepository.getWeeklyGoals(formattedYesterday)
            val hydration = hydrationRepository.getWaterIntake(formattedYesterday)

            _yesterdayUiState.value = YesterdayUiState.Success(
                stepRecord = stepRecord,
                dailyGoals = dailyGoals,
                weeklyGoals = weeklyGoals,
                hydration = hydration,
            )
        }
    }
    suspend fun updateYesterdayPreference() {
        preferencesRepository.updateYesterdayPreference(LocalDate.now().format(dateFormatter))
    }
}

sealed interface YesterdayUiState {
    data object NotStarted : YesterdayUiState
    data object Loading : YesterdayUiState
    data class Success(
        val stepRecord: DailyStepRecord?,
        val dailyGoals: MutableMap<String, GoalRecord>?,
        val weeklyGoals: MutableMap<String, GoalRecord>?,
        val hydration: Flow<HydrationRecord>,
    ) : YesterdayUiState
}
