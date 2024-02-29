package com.github.k409.fitflow.ui.screens.activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.Step
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.features.step_counter.DistanceAndCaloriesUtil
import com.github.k409.fitflow.features.step_counter.StepCounter
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
    private val distanceAndCaloriesUtil = DistanceAndCaloriesUtil()
    private val _todaySteps = MutableLiveData<Step?>()
    val todaySteps: LiveData<Step?> = _todaySteps

    init {
        _todaySteps.value = Step(
            current = 0,
            initial = 0,
            date = LocalDate.now().toString(),
            temp = 0,
            calories = 0,
            distance = 0.0
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
        val step: Step? = userRepository.loadTodaySteps(today)
        val currentSteps = stepCounter.steps()
        val newStep: Step
        if (step == null) { // if new day
            newStep = Step(
                current = 0,
                initial = currentSteps,
                date = today,
                temp = 0,
                calories = 0,
                distance = 0.0

            )
        } else if (hasRebooted || currentSteps <= 1) { //if current day and reboot has happened
            newStep = Step(
                current = step.current + currentSteps,
                initial = 0,
                date = today,
                temp = step.current,
                calories = distanceAndCaloriesUtil.calculateCaloriesFromSteps(
                    (step.current + currentSteps),
                    user
                ),
                distance = distanceAndCaloriesUtil.calculateDistanceFromSteps(
                    (step.current + currentSteps),
                    user
                )
            )
            prefs.edit().putBoolean("rebooted", false).apply() // we have handled reboot
        } else {
            // if current day and no reboot
            newStep = Step(
                current = currentSteps - step.initial + step.temp,
                initial = step.initial,
                date = today,
                temp = step.temp,
                calories = distanceAndCaloriesUtil.calculateCaloriesFromSteps(
                    (currentSteps - step.initial + step.temp),
                    user
                ),
                distance = distanceAndCaloriesUtil.calculateDistanceFromSteps(
                    (currentSteps - step.initial + step.temp),
                    user
                )
            )
        }
        userRepository.updateSteps(newStep)
        _todaySteps.value = newStep
    }
}