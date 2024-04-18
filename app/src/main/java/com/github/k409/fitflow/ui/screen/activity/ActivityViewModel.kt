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
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.service.GoalService
import com.github.k409.fitflow.service.StepCounterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

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
        var calories = dailyStepRecord?.caloriesBurned ?: 0L
        var distance = dailyStepRecord?.totalDistance ?: 0.0
        val permissionsGranted = permissionsGranted()
        val healthConnectSteps = healthStatsManager.getTotalSteps(today, today).toLong()

        if (permissionsGranted) {
            calories = healthStatsManager.getTotalCalories()
            distance = healthStatsManager.getTotalDistance()
        }

        val stepGoal = if (dailyStepRecord == null || dailyStepRecord.stepGoal == 0L) {
            val goalService = GoalService(stepsRepository)
            _loading.value = true
            goalService.calculateStepTarget(today, today, stepsRepository).toLong()
        } else {
            dailyStepRecord.stepGoal
        }

        if (dailyStepRecord == null) { // if new day
            newDailyStepRecord = DailyStepRecord(
                totalSteps = if (permissionsGranted) healthConnectSteps else 0L,
                stepCounterSteps = 0,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = 0,
                caloriesBurned = calories,
                totalDistance = distance,
                stepGoal = stepGoal,

            )
        } else if (hasRebooted || currentSteps <= 1) { // if current day and reboot has happened
            newDailyStepRecord = DailyStepRecord(
                totalSteps = if (permissionsGranted) healthConnectSteps else dailyStepRecord.stepCounterSteps + currentSteps,
                stepCounterSteps = dailyStepRecord.stepCounterSteps + currentSteps,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.stepCounterSteps + currentSteps,
                caloriesBurned = calories,
                totalDistance = distance,
                stepGoal = stepGoal,
            )

            prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
        } else if (today != lastDate) {
            newDailyStepRecord = DailyStepRecord(
                totalSteps = if (permissionsGranted) healthConnectSteps else dailyStepRecord.stepCounterSteps,
                stepCounterSteps = dailyStepRecord.stepCounterSteps,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.stepCounterSteps,
                caloriesBurned = if (calories > dailyStepRecord.caloriesBurned!!) calories else dailyStepRecord.caloriesBurned,
                totalDistance = distance,
                stepGoal = stepGoal,
            )
        } else {
            // if current day and no reboot
            newDailyStepRecord = DailyStepRecord(
                totalSteps = if (permissionsGranted) healthConnectSteps else currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                stepCounterSteps = currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                initialSteps = dailyStepRecord.initialSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.stepsBeforeReboot,
                caloriesBurned = calories,
                totalDistance = distance,
                stepGoal = stepGoal,
            )
        }
        prefs.edit().putString("lastDate", today).apply() // saving last update day

        _todaySteps.value = newDailyStepRecord

        stepsRepository.updateSteps(newDailyStepRecord)
        _loading.value = false
    }

    suspend fun getStepRecord(date: LocalDate): DailyStepRecord? {
        return stepsRepository.getSteps(date.toString())
    }
}
