package com.github.k409.fitflow.ui.screens.hydration

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.HydrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val hydrationRepository: HydrationRepository,
    private val prefs: SharedPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState = _uiState.asStateFlow()

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
        prefs.edit()
            .putInt("cupSize", size)
            .apply()

        _uiState.update {
            it.copy(cupSize = size)
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
                hydrationRepository.getLastMonthStats().collect { stats ->
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
        _uiState.update {
            it.copy(cupSize = prefs.getInt("cupSize", 250))
        }
    }
}
