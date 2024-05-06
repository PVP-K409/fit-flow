package com.github.k409.fitflow.service

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseRouteResult
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.github.k409.fitflow.model.ExerciseRecord
import com.github.k409.fitflow.model.HealthConnectExercises
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneOffset
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
            Log.e("Aggregate Distance", "Distance could not be read")
            0.0
        }
    }

    suspend fun aggregateTotalSteps(
        startTime: Instant,
        endTime: Instant,
    ): Double {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.Companion.between(startTime, endTime),
                ),
            )
            response[StepsRecord.COUNT_TOTAL]?.toDouble() ?: 0.0
        } catch (e: Exception) {
            Log.e("Aggregate Steps", "Steps could not be read", e)
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
            Log.d("Aggregate Calories", "Calories could not be read", e)
            0L
        }
    }

    suspend fun aggregateDistanceByExerciseTypes(
        startTime: Instant,
        endTime: Instant,
        validExerciseTypes: Set<Int>,
    ): Double {
        return try {
            val exerciseSessions = client.readRecords(
                ReadRecordsRequest<ExerciseSessionRecord>(
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                ),
            )

            var totalDistance = 0.0
            exerciseSessions.records.forEach { exerciseRecord ->
                if (validExerciseTypes.contains(exerciseRecord.exerciseType)) {
                    val response = client.aggregate(
                        AggregateRequest(
                            metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                            timeRangeFilter = TimeRangeFilter.between(exerciseRecord.startTime, exerciseRecord.endTime),
                        ),
                    )
                    val distance = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
                    totalDistance += distance
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

    suspend fun getExerciseRecords(
        startTime: Instant,
        endTime: Instant,
    ): MutableList<ExerciseRecord> {
        try {
            val exercisesList: MutableList<ExerciseRecord> = mutableListOf()

            val exerciseSessions = client.readRecords(
                ReadRecordsRequest<ExerciseSessionRecord>(
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                ),
            )

            exerciseSessions.records.forEach { record ->
                val exerciseTypeInt = record.exerciseType

                val exerciseType = HealthConnectExercises.findTypeByExerciseType(exerciseTypeInt) ?: "Unknown Exercise Type"

                var exerciseRoute: ExerciseRoute? = null

                when (val exerciseRouteResult = record.exerciseRouteResult) {
                    is ExerciseRouteResult.Data ->
                        exerciseRoute = exerciseRouteResult.exerciseRoute
                }

                val exerciseRecord = ExerciseRecord(
                    startTime = record.startTime,
                    endTime = record.endTime,
                    exerciseType = exerciseType,
                    calories = aggregateTotalCalories(record.startTime, record.endTime),
                    distance = aggregateTotalDistance(record.startTime, record.endTime),
                    icon = HealthConnectExercises.getIconByType(exerciseType),
                    exerciseRoute = exerciseRoute,
                )

                exercisesList.add(exerciseRecord)
            }

            return exercisesList.sortedByDescending { it.startTime }.toMutableList()
        } catch (e: Exception) {
            Log.e("Read Exercises", "Unable to read Exercise sessions")
            return mutableListOf()
        }
    }

    suspend fun writeExerciseSession(
        exerciseType: Int,
        startTime: Instant,
        startZoneOffset: ZoneOffset,
        endTime: Instant,
        endZoneOffset: ZoneOffset,
        distanceInKm: Float,
        calories: Long,
        route: List<ExerciseRoute.Location>,
    ) {
        val distanceInMeters = (distanceInKm * 1000).toDouble()
        val length = Length.meters(distanceInMeters)
        val energy = Energy.calories(calories.toDouble())
        Log.d("Write exercise", "energy: ${energy.inCalories}")

        try {
            val records = mutableListOf<Record>()
            records.add(
                ExerciseSessionRecord(
                    exerciseType = exerciseType,
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    exerciseRoute = ExerciseRoute(route),
                )
            )

            records.add(
                DistanceRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    distance = length,
                )
            )

            records.add(
                TotalCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    energy = energy,
                )
            )

            client.insertRecords(records)
        } catch (e: Exception) {
            Log.e("Write Exercise", "Failed to write exercise", e)
        }

    }


}
