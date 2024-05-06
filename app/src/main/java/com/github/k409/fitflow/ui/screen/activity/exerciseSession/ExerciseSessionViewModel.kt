package com.github.k409.fitflow.ui.screen.activity.exerciseSession


import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.model.getAllExerciseSessionActivitiesTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExerciseSessionViewModel() : ViewModel() {

    fun getValidExerciseSessionActivitiesTypes(): List<String> {
        return getAllExerciseSessionActivitiesTypes()
    }
}
