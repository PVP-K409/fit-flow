package com.github.k409.fitflow.ui.screens.activity

import android.content.SharedPreferences
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.di.healthConnect.HealthStatsManager
import com.github.k409.fitflow.features.stepcounter.StepCounter
import com.github.k409.fitflow.model.DailyStepRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stepCounter: StepCounter,
    private val prefs: SharedPreferences,
    private val client: HealthConnectClient,
    private val healthStatsManager: HealthStatsManager,
) : ViewModel() {
    private val _todaySteps = MutableLiveData<DailyStepRecord?>()
    val todaySteps: LiveData<DailyStepRecord?> = _todaySteps
    val permissions = setOf(
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
    )

    init {
        _todaySteps.value = DailyStepRecord(
            totalSteps = 0,
            initialSteps = 0,
            recordDate = LocalDate.now().toString(),
            stepsBeforeReboot = 0,
            caloriesBurned = 0,
            totalDistance = 0.0,
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
            val step = userRepository.loadTodaySteps(today)

            if (step == null) { // new day
                viewModelScope.launch {
                    updateTodayStepsManually()
                    _todaySteps.value =
                        userRepository.loadTodaySteps(today) // Update step after the suspend function completes
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
        val dailyStepRecord: DailyStepRecord? = userRepository.loadTodaySteps(today)
        val currentSteps = stepCounter.steps()
        val newDailyStepRecord: DailyStepRecord
        var calories = 0L
        var distance = 0.0

        if(permissionsGranted()){
            calories = healthStatsManager.getCalories()
            distance = healthStatsManager.getDistance()
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

        userRepository.updateSteps(newDailyStepRecord)

        _todaySteps.value = newDailyStepRecord
    }

    suspend fun getStepRecord(date: LocalDate): DailyStepRecord? {
        return userRepository.loadTodaySteps(date.toString())
    }
}
