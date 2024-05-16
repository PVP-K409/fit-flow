package com.github.k409.fitflow.ui.screen.activity.exerciseSession

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.model.getAllExerciseSessionActivitiesTypes

class ExerciseSessionViewModel : ViewModel() {

    fun getValidExerciseSessionActivitiesTypes(): List<String> {
        return getAllExerciseSessionActivitiesTypes()
    }
}
