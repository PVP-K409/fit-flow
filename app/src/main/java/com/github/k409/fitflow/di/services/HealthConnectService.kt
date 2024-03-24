package com.github.k409.fitflow.di.services

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.records.ExerciseSessionRecord
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import javax.inject.Inject
import kotlin.math.round

class HealthConnectService @Inject constructor(
    private val client: HealthConnectClient,
) {
    suspend fun aggregateTotalDistance(
        startTime: Instant,
        endTime: Instant,
    ): Double {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                ),
            )
            val distance = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
            BigDecimal(distance / 1000).setScale(
                2,
                RoundingMode.CEILING,
            ).toDouble()
        } catch (e: Exception) {
            Log.d("Aggregate Distance", "Distance could not be read")
            0.0
        }
    }

    suspend fun aggregateTotalCalories(
        startTime: Instant,
        endTime: Instant,
    ): Long {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                ),
            )
            val calories = response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inCalories?.toLong() ?: 0L
            round(calories.toDouble() / 1000).toLong()
        } catch (e: Exception) {
            Log.d("Aggregate Calories", "Calories could not be read")
            0L
        }
    }

    suspend fun aggregateRunningDistance(
        startTime: Instant,
        endTime: Instant,
    ): Double {
        val exerciseTypeRunning = 56
        val exerciseTypeRunningTreadmill = 57
        return aggregateDistanceByExerciseTypes(startTime, endTime, setOf(exerciseTypeRunning, exerciseTypeRunningTreadmill))
    }

    suspend fun aggregateBikingDistance(
        startTime: Instant,
        endTime: Instant,
    ): Double {
        val exerciseTypeBiking = 8
        val exerciseTypeBikingStationary = 9
        return aggregateDistanceByExerciseTypes(startTime, endTime, setOf(exerciseTypeBiking, exerciseTypeBikingStationary))
    }

    private suspend fun aggregateDistanceByExerciseTypes(
        startTime: Instant,
        endTime: Instant,
        validExerciseTypes: Set<Int>,
    ): Double {
        return try {
            val exerciseSessions = client.readRecords(
                ReadRecordsRequest<ExerciseSessionRecord>(
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            var totalDistance = 0.0
            exerciseSessions.records.forEach { exerciseRecord ->
                if (validExerciseTypes.contains(exerciseRecord.exerciseType)) {
                    val distanceRecords = client.readRecords(
                        ReadRecordsRequest<DistanceRecord>(
                            timeRangeFilter = TimeRangeFilter.between(
                                exerciseRecord.startTime,
                                exerciseRecord.endTime
                            )
                        )
                    )
                    totalDistance += distanceRecords.records.sumOf { distanceRecord ->
                        distanceRecord.distance.inMeters
                    }
                }
            }

            BigDecimal(totalDistance / 1000).setScale(
                2,
                RoundingMode.CEILING,
            ).toDouble()

        } catch (e: Exception) {
            Log.e("HealthConnectService", "Failed to aggregate exercise distance")
            0.0
        }
    }
}
