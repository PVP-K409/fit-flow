package com.github.k409.fitflow.features.stepcounter

import android.icu.lang.UCharacter.toLowerCase
import com.github.k409.fitflow.model.User
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.round


fun calculateDistanceFromSteps(
    steps: Long,
    user: User?
): Double? {
    if (user == null) return 0.0
    return try {
        val stepFactor = if (toLowerCase(user.gender) == "male") 0.415 else 0.413

        BigDecimal(steps * (user.height * stepFactor) / 10.0.pow(5.0)).setScale(
            2, RoundingMode.CEILING
        ).toDouble()
    } catch (e: Exception) {
        null
    }
}

fun calculateCaloriesFromSteps(
    steps: Long,
    user: User?
): Long? {
    if (user == null) return 0

    return try {
        val stepFactor = if (toLowerCase(user.gender) == "male") 0.415 else 0.413
        val stepLength = user.height * stepFactor / 100
        val distance = steps * stepLength
        val speed = 5.0 // TODO fitness levels
        val fitnessFactor = 1.2
        val time = distance / 1000 / speed
        val met = 3.5 // Metabolic Equivalent of Task

        round(met * user.weight * time * fitnessFactor).toLong()
    } catch (e: Exception) {
        null
    }
}