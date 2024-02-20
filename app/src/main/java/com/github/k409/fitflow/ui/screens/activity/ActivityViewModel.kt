package com.github.k409.fitflow.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.ui.step_counter.StepCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(private val stepCounter: StepCounter) : ViewModel() {

    val steps = stepCounter.todayStepsLiveData

    override fun onCleared() {
        super.onCleared()
        stepCounter.unregisterListener()
    }

}