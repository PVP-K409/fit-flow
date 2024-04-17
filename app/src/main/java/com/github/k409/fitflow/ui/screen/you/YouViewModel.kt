package com.github.k409.fitflow.ui.screen.you

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.HydrationStats
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.theme.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class YouViewModel @Inject constructor(
    userRepository: UserRepository,
    preferencesRepository: PreferencesRepository,
    stepsRepository: StepsRepository,
    hydrationRepository: HydrationRepository,
) : ViewModel() {

    val youUiState: StateFlow<YouUiState> = combine(
        userRepository.currentUser,
        preferencesRepository.themeColourPreferences,
    ) { user, themePreferences ->
        YouUiState.Success(
            user = user,
            themePreferences = themePreferences,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = YouUiState.Loading,
    )

    val progressUiState: StateFlow<ProgressUiState> = combine(
        stepsRepository.getStepRecordCurrentWeek(),
        stepsRepository.getStepRecordLastWeeks(12),
        stepsRepository.getStepRecordThisMonth(),
        hydrationRepository.getLastMonthStats(),
    ) { currentWeek, lastWeeks, thisMonth, hydrationStats ->
        ProgressUiState.Success(
            currentWeek = currentWeek,
            lastWeeks = lastWeeks,
            thisMonth = thisMonth,
            hydrationStats = hydrationStats,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState.Loading,
    )


}

sealed interface YouUiState {
    data object Loading : YouUiState
    data class Success(
        val user: User,
        val themePreferences: ThemePreferences,
    ) : YouUiState
}

sealed interface ProgressUiState {
    data object Loading : ProgressUiState
    data class Success(
        val currentWeek: Map<String, DailyStepRecord> = emptyMap(),
        val thisMonth: Map<String, DailyStepRecord> = emptyMap(),
        val lastWeeks: Map<String, DailyStepRecord> = emptyMap(),
        val hydrationStats: HydrationStats = HydrationStats(),
    ) : ProgressUiState
}
