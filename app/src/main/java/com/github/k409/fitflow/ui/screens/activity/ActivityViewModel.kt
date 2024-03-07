package com.github.k409.fitflow.ui.screens.activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.features.stepcounter.StepCounter
import com.github.k409.fitflow.features.stepcounter.calculateCaloriesFromSteps
import com.github.k409.fitflow.features.stepcounter.calculateDistanceFromSteps
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stepCounter: StepCounter,
    private val prefs: SharedPreferences,
) : ViewModel() {
    private val _todaySteps = MutableLiveData<DailyStepRecord?>()
    val todaySteps: LiveData<DailyStepRecord?> = _todaySteps

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
        val today = LocalDate.now().toString()
        val user: User? = userRepository.getUser()
        val dailyStepRecord: DailyStepRecord? = userRepository.loadTodaySteps(today)
        val currentSteps = stepCounter.steps()
        val newDailyStepRecord: DailyStepRecord

        if (dailyStepRecord == null) { // if new day
            newDailyStepRecord = DailyStepRecord(
                totalSteps = 0,
                initialSteps = currentSteps,
                recordDate = today,
                stepsBeforeReboot = 0,
                caloriesBurned = 0,
                totalDistance = 0.0,

                )
        } else if (hasRebooted || currentSteps <= 1) { // if current day and reboot has happened
            newDailyStepRecord = DailyStepRecord(
                totalSteps = dailyStepRecord.totalSteps + currentSteps,
                initialSteps = 0,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.totalSteps,
                caloriesBurned = calculateCaloriesFromSteps(
                    (dailyStepRecord.totalSteps + currentSteps),
                    user,
                ),
                totalDistance = calculateDistanceFromSteps(
                    (dailyStepRecord.totalSteps + currentSteps),
                    user,
                ),
            )

            prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
        } else {
            // if current day and no reboot
            newDailyStepRecord = DailyStepRecord(
                totalSteps = currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot,
                initialSteps = dailyStepRecord.initialSteps,
                recordDate = today,
                stepsBeforeReboot = dailyStepRecord.stepsBeforeReboot,
                caloriesBurned = calculateCaloriesFromSteps(
                    (currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot),
                    user,
                ),
                totalDistance = calculateDistanceFromSteps(
                    (currentSteps - dailyStepRecord.initialSteps + dailyStepRecord.stepsBeforeReboot),
                    user,
                ),
            )
        }

        userRepository.updateSteps(newDailyStepRecord)

        _todaySteps.value = newDailyStepRecord
    }

    suspend fun getStepRecord(date: LocalDate): DailyStepRecord? {
        return userRepository.loadTodaySteps(date.toString())
    }
}
