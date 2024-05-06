package com.github.k409.fitflow.ui.screen.activity.exerciseLog

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.model.ExerciseRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExercisesLogViewModel @Inject constructor(
    private val healthStatsManager: HealthStatsManager,
    private val client: HealthConnectClient,
) : ViewModel() {
    private val _exerciseRecords = MutableStateFlow<List<ExerciseRecord>>(mutableListOf())
    val exerciseRecords: StateFlow<List<ExerciseRecord>> = _exerciseRecords

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val permissions = setOf(
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE,
    )

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(permissions)
    }

    fun loadExerciseRecords() {
        _loading.value = true
        val today = LocalDate.now().plusMonths(1)

        val monthAgo = today.minusMonths(2)
        viewModelScope.launch {
            _exerciseRecords.value = healthStatsManager.getExerciseRecords(monthAgo.toString(), today.toString())
            _loading.value = false
        }
    }
}
