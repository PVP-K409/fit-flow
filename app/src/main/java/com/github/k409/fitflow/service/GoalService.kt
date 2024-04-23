package com.github.k409.fitflow.service

import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.model.GoalRecord
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.round

class GoalService @Inject constructor(
    private val stepsRepository: StepsRepository,
) {
    suspend fun calculateStepGoal(
        description: String,
        type: String,
        startDate: String,
        endDate: String,
        boost: Double = 0.005,
    ): GoalRecord {
        val target = calculateStepTarget(startDate, endDate, stepsRepository)
        val currentProgress = stepsRepository.getSteps(startDate)?.totalSteps?.toDouble() ?: 0.0

        return GoalRecord(
            description = description,
            type = type,
            target = target,
            currentProgress = currentProgress,
            points = calculatePoints(target, boost),
            xp = calculateXp(target, boost),
            startDate = startDate,
            endDate = endDate,
            completed = false,
            mandatory = true
        )
    }

    fun calculatePoints(
        distance: Double,
        boost: Double,
    ): Long {
        return round(distance * boost).toLong()
    }

    fun calculateXp(
        distance: Double,
        boost: Double,
    ): Long {
        return round(distance * boost).toLong()
    }

    suspend fun calculateStepTarget(
        startDate: String,
        endDate: String,
        stepsRepository: StepsRepository,
        defaultValue: Double = 3000.0,
        multiplier: Double = 1.05,
        daysToCheck: Long = 7,
        roundToNearest: Int = 250,
    ): Double {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val endDateToCheck = LocalDate.parse(startDate, dateFormatter).minusDays(1)
        val startDateToCheck = endDateToCheck.minusDays(daysToCheck)

        val (totalStepsSum, recordsCounted) = stepsRepository.stepSumAndCountInPeriod(
            startDateToCheck,
            endDateToCheck,
        )

        val startLocalDate = LocalDate.parse(startDate, dateFormatter)
        val endLocalDate = LocalDate.parse(endDate, dateFormatter)
        var dayMultiplication = ChronoUnit.DAYS.between(startLocalDate, endLocalDate).toInt()

        if (dayMultiplication == 0) dayMultiplication = 1

        if (recordsCounted == 0) return defaultValue * dayMultiplication

        var averageSteps = totalStepsSum.toDouble() / recordsCounted

        var stepMultiplier = multiplier

        if (averageSteps < defaultValue) {
            averageSteps = defaultValue
            stepMultiplier = 1.0
        }

        val result = BigDecimal(averageSteps * dayMultiplication * stepMultiplier)
        val roundNumber = BigDecimal(roundToNearest)

        val rounded = result.divide(roundNumber, 0, RoundingMode.CEILING).multiply(roundNumber)

        return rounded.toDouble()
    }
}
