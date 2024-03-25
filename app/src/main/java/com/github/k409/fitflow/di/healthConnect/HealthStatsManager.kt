package com.github.k409.fitflow.di.healthConnect

import android.util.Log
import com.github.k409.fitflow.di.services.HealthConnectService
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

    suspend fun getTotalBikingDistance(): Double = fetchData(
        fetchFunction = {
            healthConnectService.aggregateBikingDistance(
                startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                endTime = Instant.now(),
            )
        },
        default = 0.0,
        logTag = "Biking Distance",
    )

    suspend fun getTotalRunningDistance(): Double = fetchData(
        fetchFunction = {
            healthConnectService.aggregateRunningDistance(
                startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                endTime = Instant.now(),
            )
        },
        default = 0.0,
        logTag = "Running Distance",
    )
}
