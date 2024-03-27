package com.github.k409.fitflow.ui.screens.goals

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.di.healthConnect.HealthStatsManager
import com.github.k409.fitflow.features.stepgoal.calculatePoints
import com.github.k409.fitflow.features.stepgoal.calculateStepGoal
import com.github.k409.fitflow.features.stepgoal.calculateXp
import com.github.k409.fitflow.model.GoalRecord
import com.github.k409.fitflow.model.getGoalByType
import com.github.k409.fitflow.model.getGoalTypes
import com.github.k409.fitflow.model.getValidExerciseTypesByType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository,
    private val client: HealthConnectClient,
    private val stepsRepository: StepsRepository,
    private val healthStatsManager: HealthStatsManager,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _todayGoals = MutableStateFlow<MutableMap<String, GoalRecord>?>(mutableMapOf())
    private val _weeklyGoals = MutableStateFlow<MutableMap<String, GoalRecord>?>(mutableMapOf())
    val todayGoals: StateFlow<MutableMap<String, GoalRecord>?> = _todayGoals.asStateFlow()
    val weeklyGoals: StateFlow<MutableMap<String, GoalRecord>?> = _weeklyGoals.asStateFlow()

    private val weekly = "Weekly"
    private val daily = "Daily"
    private val walking = "Walking"

    init {
        loadGoals(weekly)
        loadGoals(daily)
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
    )

    suspend fun permissionsGranted(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()

        return granted.containsAll(permissions)
    }

     fun loadGoals(type: String) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedToday = today.format(dateFormatter)
            val goals: MutableMap<String, GoalRecord>? = when(type) {
                daily -> goalsRepository.getDailyGoals(formattedToday)
                weekly -> goalsRepository.getWeeklyGoals(formattedToday)
                else -> null
            }

            if (goals == null || !goals.keys.contains(walking)) {
                val goal = calculateStepGoal(
                    stepsRepository = stepsRepository,
                    description = "$type $walking",
                    type = walking,
                    startDate = formattedToday,
                    endDate = if (type == weekly) today.plusDays(7).format(dateFormatter) else today.plusDays(1).format(dateFormatter),
                )
                Log.d("goals", "${goals?.keys?.contains(walking).toString()} - $type")
                addGoal(type, goal)
            } else {
                when (type) {
                    daily -> _todayGoals.value = goals
                    weekly -> _weeklyGoals.value = goals
                }
            }
            updateGoals(type)
        }
    }

    private suspend fun addGoal(type: String, goal: GoalRecord) {
        val currentGoals = when (type) {
            daily -> _todayGoals.value ?: mutableMapOf()
            weekly -> _weeklyGoals.value ?: mutableMapOf()
            else -> return
        }
        currentGoals[goal.type] = goal

        when (type) {
            daily -> _todayGoals.value = currentGoals
            weekly -> _weeklyGoals.value = currentGoals
        }

        goalsRepository.updateGoals(currentGoals, LocalDate.now().toString(), type)
    }

    fun submitGoalAsync(goalType: String, exerciseType: String, distance: Double) {
        viewModelScope.launch {
            val healthConnectGoal = getGoalByType(exerciseType)

            val startDate = LocalDate.now()
            val endDate = if (goalType == weekly) startDate.plusDays(7) else startDate.plusDays(1)

            val goal = GoalRecord(
                description = "$goalType $exerciseType",
                type = exerciseType,
                target = distance,
                currentProgress = 0.0,
                points = calculatePoints(distance, healthConnectGoal?.boost?: 1.0),
                xp = calculateXp(distance, healthConnectGoal?.boost?: 1.0),
                startDate = startDate.toString(),
                endDate = endDate.toString(),
                completed = false
            )

            addGoal(goalType, goal)
        }
    }

    fun getValidExerciseTypes(goalType: String) : List<String> {
        val allGoalTypes = getGoalTypes()

        val goalsInUseMap: Map<String, GoalRecord>? = when (goalType) {
            daily -> _todayGoals.value
            weekly -> _weeklyGoals.value
            else -> return emptyList()
        }

        val goalsInUse = goalsInUseMap?.keys ?: emptySet()

        return allGoalTypes.filter { it !in goalsInUse }
    }


    private suspend fun updateGoals(type: String) {

        val date = LocalDate.now()
        Log.d("date", date.toString())

        val goalsToUpdate: MutableMap<String, GoalRecord>? = when (type) {
            daily -> _todayGoals.value
            weekly -> _weeklyGoals.value
            else -> return
        }

        if (!goalsToUpdate.isNullOrEmpty()) {
            for (key in goalsToUpdate.keys ) {
                val goal = goalsToUpdate[key]

                if (key == walking) {
                    goalsToUpdate[key]?.currentProgress = healthStatsManager.getTotalSteps(
                        startDateString = goal?.startDate ?: "",
                        endDateString = goal?.endDate ?: "",
                    )
                } else {
                    val validExerciseTypes = getValidExerciseTypesByType(key)
                    goalsToUpdate[key]?.currentProgress = healthStatsManager.getTotalExerciseDistance(
                        validExerciseTypes = validExerciseTypes,
                        startDateString = goal?.startDate ?: "",
                        endDateString = goal?.endDate ?: "",
                    )
                }

                val updatedGoal = goalsToUpdate[key]

                if (updatedGoal?.completed == false && updatedGoal.currentProgress > updatedGoal.target) {
                    goalsToUpdate[key]?.completed = true
                    userRepository.addCoinsAndXp(updatedGoal.points, updatedGoal.xp)
                }
            }

            goalsRepository.updateGoals(goalsToUpdate,LocalDate.now().toString(), type)
        }

        when (type) {
            daily -> _todayGoals.value = goalsToUpdate
            weekly -> _weeklyGoals.value = goalsToUpdate
        }
    }
}