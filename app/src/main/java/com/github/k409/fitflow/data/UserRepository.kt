package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val userid = "ohxyZCvlrIt0JaQQH5RF"

    suspend fun updateSteps(newSteps: DailyStepRecord) {
        val userDocRef = db.collection("users").document(userid)

        try {
            val snapshot = userDocRef.get().await()

            if (snapshot.exists()) {
                val stepsList =
                    snapshot.data?.get("steps") as? List<Map<String, Any>> ?: mutableListOf()
                val existingStepMap = stepsList.firstOrNull { it["date"] == newSteps.recordDate }
                val updatedStepMap = mapOf(
                    "current" to newSteps.totalSteps,
                    "initial" to newSteps.initialSteps,
                    "date" to newSteps.recordDate,
                    "temp" to newSteps.stepsBeforeReboot,
                    "distance" to newSteps.totalDistance,
                    "calories" to newSteps.caloriesBurned
                )

                val updatedStepsList = if (existingStepMap != null) {
                    stepsList.map { if (it["date"] == newSteps.recordDate) updatedStepMap else it }
                } else {
                    stepsList + updatedStepMap // new day
                }

                userDocRef.update("steps", updatedStepsList).await()
            } else {
                Log.e("Step Repository", "No such document")
            }

        } catch (e: Exception) {
            Log.e("Step Repository", "Error updating steps", e)
        }


    }

    suspend fun loadTodaySteps(day: String): DailyStepRecord? {
        val userDocRef = db.collection("users").document(userid)
        val snapshot = userDocRef.get().await()

        if (snapshot.exists()) {
            val stepsList = snapshot.data?.get("steps") as? List<Map<String, Any>> ?: return null
            val stepMap = stepsList.firstOrNull { it["date"] == day }

            return stepMap?.let {
                DailyStepRecord(
                    totalSteps = it["current"] as? Long ?: 0,
                    initialSteps = it["initial"] as? Long ?: 0,
                    recordDate = it["date"] as? String ?: day,
                    stepsBeforeReboot = it["temp"] as? Long ?: 0,
                    caloriesBurned = it["calories"] as? Long ?: 0,
                    totalDistance = it["distance"] as? Double ?: 0.0
                )
            }
        } else {
            return null
        }

    }

    suspend fun getUser(): User? {
        return try {
            val documentSnapshot = db.collection("users").document(userid).get().await()

            documentSnapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

}