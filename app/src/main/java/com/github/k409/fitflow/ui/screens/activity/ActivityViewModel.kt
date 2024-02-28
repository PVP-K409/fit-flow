package com.github.k409.fitflow.ui.screens.activity

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.DataModels.Step
import com.github.k409.fitflow.ui.step_counter.StepCounter
import com.github.k409.fitflow.ui.step_counter.StepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class  ActivityViewModel @Inject constructor(
    private val stepRepository: StepRepository,
    private val stepCounter: StepCounter,
    private val prefs : SharedPreferences
): ViewModel(){
    private val _todaySteps = MutableLiveData<Step?>()
    val todaySteps: LiveData<Step?> = _todaySteps
    init {
        loadTodaySteps()
    }
    fun loadTodaySteps(){
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val step = stepRepository.loadTodaySteps(today)
            _todaySteps.value = step
        }
    }
    suspend fun updateTodayStepsManually() {
        val hasRebooted = prefs.getBoolean("rebooted", false) // boolean if reboot has happened
        val today = LocalDate.now().toString()
        val step: Step? = stepRepository.loadTodaySteps(today)
        val currentSteps = stepCounter.steps()
        val newStep: Step
        if (step == null) { // if new day
            newStep = Step(
                current = 0,
                initial = currentSteps,
                date = today,
                temp = 0
            )
        } else if (hasRebooted || currentSteps <=1){ // we update current day step log
            newStep = Step(
                current = step.current + currentSteps,
                initial = 0,
                date = today,
                temp = step.current
            )
        }else{
            newStep = Step(
                current = currentSteps - step.initial + step.temp,
                initial = step.initial,
                date = today,
                temp = step.temp
            )
        }
        if (hasRebooted) { // we have handled the reboot
            prefs.edit().putBoolean("rebooted", false).apply()
        }
        stepRepository.updateSteps(newStep)
        _todaySteps.value = newStep
    }


}