package com.github.k409.fitflow.ui.screens.activity

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
    private val stepCounter: StepCounter
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
        val today = LocalDate.now().toString()
        val step: Step? = stepRepository.loadTodaySteps(today)
        val newStep: Step
        if (step == null) { // new day
            newStep = Step(
                current = 0,
                initial = stepCounter.steps(),
                date = today
            )
            stepRepository.updateSteps(newStep) // create new day step log
        } else { // we update current day step log
            newStep = Step(
                current = (stepCounter.steps().toInt() - step.initial.toInt()).toLong(),
                initial = step.initial,
                date = today
            )
            stepRepository.updateSteps(newStep)
        }
        _todaySteps.value = newStep
    }


}