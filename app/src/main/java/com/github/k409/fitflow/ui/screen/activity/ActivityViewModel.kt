package com.github.k409.fitflow.ui.screen.activity

import android.content.SharedPreferences
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.service.StepCounterService
import com.github.k409.fitflow.model.DailyStepRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val stepsRepository: StepsRepository,
    private val stepCounterService: StepCounterService,
    private val prefs: SharedPreferences,
    private val client: HealthConnectClient,
    private val healthStatsManager: HealthStatsManager,
) : ViewModel() {

    private val _todaySteps = MutableStateFlow<DailyStepRecord?>(null)
    val todaySteps: StateFlow<DailyStepRecord?> = _todaySteps

    val progressUiState: StateFlow<ProgressUiState> = combine(
        stepsRepository.getStepRecordCurrentWeek(),
        stepsRepository.getStepRecordLastWeeks(12),
    ) { currentWeek, lastWeeks ->
        ProgressUiState.Success(
            currentWeek = currentWeek,
            lastWeeks = lastWeeks,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState.Loading,
    )

    val permissions = setOf(
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
    )

    init {
        _todaySteps.value = DailyStepRecord(
            recordDate = LocalDate.now().toString(),
        )
        loadTodaySteps()
    }

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(permissions)
    }

    private fun loadTodaySteps() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val step = stepsRepository.getSteps(today)

            if (step == null) { // new day
                viewModelScope.launch {
                    updateTodayStepsManually()
                    _todaySteps.value =
                        stepsRepository.getSteps(today) // Update step after the suspend function completes
                }
            } else {
                _todaySteps.value = step
            }
        }
    }

    suspend fun updateTodayStepsManually() {
        val hasRebooted = prefs.getBoolean("rebooted", false) // boolean if reboot has happened
        val lastDate = prefs.getString("lastDate", "") // last update day
        val today = LocalDate.now().toString()
        val dailyStepRecord: DailyStepRecord? = stepsRepository.getSteps(today)
        val currentSteps = stepCounterService.steps()
        val newDailyStepRecord: DailyStepRecord
        var calories = 0L
        var distance = 0.0

        if (permissionsGranted()) {
            calories = healthStatsManager.getTotalCalories()
            distance = healthStatsManager.getTotalDistance()
        }

        if (dailyStepRecord == null) { // if new day
            newDailyStepRecord = DailyStepRecord(
                totalSteps = 0,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = 0,
                caloriesBurned = calories,
                totalDistance = distance,

            )
        } else if (hasRebooted || currentSteps <= 1) { // if current day and reboot has happened
            newDailyStepRecord = DailyStepRecord(
                totalSteps = dailyStepRecord.totalSteps + currentSteps,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.totalSteps + currentSteps,
                caloriesBurned = calories,
                totalDistance = distance,
            )

            prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
        } else if (today != lastDate) {
            newDailyStepRecord = DailyStepRecord(
                totalSteps = dailyStepRecord.totalSteps,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.totalSteps,
                caloriesBurned = if (calories > dailyStepRecord.caloriesBurned!!) calories else dailyStepRecord.caloriesBurned,
                totalDistance = distance,
            )
        } else {
            // if current day and no reboot
            newDailyStepRecord = DailyStepRecord(
                totalSteps = currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                initialSteps = dailyStepRecord.initialSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.stepsBeforeReboot,
                caloriesBurned = calories,
                totalDistance = distance,
            )
        }
        prefs.edit().putString("lastDate", today).apply() // saving last update day

        stepsRepository.updateSteps(newDailyStepRecord)

        _todaySteps.value = newDailyStepRecord
    }

    suspend fun getStepRecord(date: LocalDate): DailyStepRecord? {
        return stepsRepository.getSteps(date.toString())
    }
}

sealed interface ProgressUiState {
    data object Loading : ProgressUiState
    data class Success(
        val currentWeek: Map<String, DailyStepRecord> = emptyMap(),
        val lastWeeks: Map<String, DailyStepRecord> = emptyMap(),
    ) : ProgressUiState
}
