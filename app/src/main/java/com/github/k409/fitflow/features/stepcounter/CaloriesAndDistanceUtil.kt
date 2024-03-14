package com.github.k409.fitflow.features.stepcounter

import android.util.Log
import com.github.k409.fitflow.di.services.HealthConnectService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CaloriesAndDistanceUtil @Inject constructor(
    private val healthConnectService: HealthConnectService,
) {
    suspend fun getCalories(): Long {
        val end = Instant.now()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            healthConnectService.aggregateCalories(
                startTime = start,
                endTime = end,
            )
        } catch (e: Exception) {
            Log.d("get Calories", "Unable to get Calories")
            0L
        }
    }

    suspend fun getDistance(): Double {
        val end = Instant.now()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            healthConnectService.aggregateDistance(
                startTime = start,
                endTime = end,
            )
        } catch (e: Exception) {
            Log.d("get Distance", "Unable to get Distance")
            0.0
        }
    }
}
