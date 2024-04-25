package com.github.k409.fitflow.ui.screen.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.model.HydrationRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val hydrationRepository: HydrationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState = _uiState.asStateFlow()

    val hydrationLogsUiState: StateFlow<HydrationLogsUiState> = combine(
        hydrationRepository.getHydrationRecordsGroupedByWeek(),
        hydrationRepository.getWaterIntakeGoal(),
    ) { records, goal ->
        HydrationLogsUiState.Success(records, goal)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HydrationLogsUiState.Loading,
    )

    init {
        getTodayWaterIntake()
        getCupSize()
        getWaterIntakeGoal()
        getHistory()
    }

    fun addWaterCup() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository.addWaterIntake(_uiState.value.cupSize)
            }
        }
    }

    fun setCupSize(size: Int) {
        viewModelScope.launch {
            hydrationRepository.setCupSize(size)

            _uiState.update {
                it.copy(cupSize = size)
            }
        }
    }

    private fun getTodayWaterIntake() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository.getTodayWaterIntake()
                    .collect { record ->
                        _uiState.update {
                            it.copy(today = record.waterIntake)
                        }
                    }
            }
        }
    }

    private fun getWaterIntakeGoal() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository
                    .getWaterIntakeGoal()
                    .collect { goal ->
                        _uiState.update {
                            it.copy(dailyGoal = goal)
                        }
                    }
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository.getThisMonthStats().collect { stats ->
                    _uiState.update {
                        it.copy(
                            stats = stats,
                        )
                    }
                }
            }
        }
    }

    private fun getCupSize() {
        viewModelScope.launch {
            hydrationRepository.getCupSize().collect { cupSize ->
                _uiState.update {
                    it.copy(cupSize = cupSize)
                }
            }
        }
    }
}

sealed interface HydrationLogsUiState {
    data object Loading : HydrationLogsUiState

    data class Success(
        val groupedRecords: Map<String, List<HydrationRecord>> = emptyMap(),
        val goal: Int = 0,
    ) : HydrationLogsUiState
}
