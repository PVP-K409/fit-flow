package com.github.k409.fitflow.ui.screen.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.model.ExerciseRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseLogViewModel @Inject constructor(
    private val healthStatsManager: HealthStatsManager
): ViewModel() {
    private val _exerciseRecords = MutableStateFlow<List<ExerciseRecord>>(mutableListOf())
    val exerciseRecords: StateFlow<List<ExerciseRecord>> = _exerciseRecords

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadExerciseRecords() {
        _loading.value = true
        val today = LocalDate.now().plusMonths(1)

        val monthAgo = today.minusMonths(2)
        viewModelScope.launch {
            _exerciseRecords.value = healthStatsManager.getExerciseRecords(monthAgo.toString(), today.toString())
            _loading.value = false
        }
    }

}