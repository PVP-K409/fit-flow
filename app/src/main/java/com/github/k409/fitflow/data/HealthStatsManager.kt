package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.ExerciseRecord
import com.github.k409.fitflow.service.HealthConnectService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class HealthStatsManager @Inject constructor(
    private val healthConnectService: HealthConnectService,
) {
    private suspend fun <T> fetchData(fetchFunction: suspend () -> T, default: T, logTag: String): T {
        return try {
            fetchFunction()
        } catch (e: Exception) {
            Log.d(logTag, "Unable to get $logTag")
            default
        }
    }

    suspend fun getTotalCalories(): Long = fetchData(
        fetchFunction = {
            healthConnectService.aggregateTotalCalories(
                startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                endTime = Instant.now(),
            )
        },
        default = 0L,
        logTag = "Total Calories",
    )

    suspend fun getTotalDistance(): Double = fetchData(
        fetchFunction = {
            healthConnectService.aggregateTotalDistance(
                startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                endTime = Instant.now(),
            )
        },
        default = 0.0,
        logTag = "Total Distance",
    )

    suspend fun getTotalSteps(
        startDateString: String,
        endDateString: String,
    ): Double {
        val zoneId = ZoneId.systemDefault()
        val startDate = LocalDate.parse(startDateString).atStartOfDay(zoneId).toInstant()

        val endDate = if (LocalDate.parse(endDateString).isEqual(LocalDate.now())) {
            Instant.now()
        } else {
            LocalDate.parse(endDateString).atTime(23, 59, 59).atZone(zoneId).toInstant()
        }

        return healthConnectService.aggregateTotalSteps(
            startTime = startDate,
            endTime = endDate,
        )
    }

    suspend fun getTotalExerciseDistance(
        validExerciseTypes: Set<Int>,
        startDateString: String,
        endDateString: String,
    ): Double {
        val zoneId = ZoneId.systemDefault()
        val startDate = LocalDate.parse(startDateString).atStartOfDay(zoneId).toInstant()

        val endDate = if (LocalDate.parse(endDateString).isEqual(LocalDate.now())) { // if same day
            Instant.now()
        } else {
            LocalDate.parse(endDateString).atStartOfDay(zoneId).toInstant() // if not same day
        }

        return healthConnectService.aggregateDistanceByExerciseTypes(
            startTime = startDate,
            endTime = endDate,
            validExerciseTypes = validExerciseTypes,
        )
    }

    suspend fun getExerciseRecords(
        startDateString: String,
        endDateString: String,
    ): List<ExerciseRecord> {
        val zoneId = ZoneId.systemDefault()
        val startDate = LocalDate.parse(startDateString).atStartOfDay(zoneId).toInstant()

        val endDate = LocalDate.parse(endDateString).atTime(23, 59, 59).atZone(zoneId).toInstant()


        Log.d("start", startDate.toString())
        Log.d("end", endDate.toString())

        return healthConnectService.getExerciseRecords(startDate, endDate)
    }
}
