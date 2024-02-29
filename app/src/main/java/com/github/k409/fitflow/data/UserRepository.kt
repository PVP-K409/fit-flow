package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.Step
import com.github.k409.fitflow.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val userid = "ohxyZCvlrIt0JaQQH5RF"

    private val db = FirebaseFirestore.getInstance()

    suspend fun updateSteps(newSteps: Step){
        val userDocRef = db.collection("users").document(userid)

        try {
            val snapshot = userDocRef.get().await()

            if (snapshot.exists()) {
                val stepsList = snapshot.data?.get("steps") as? List<Map<String, Any>> ?: mutableListOf()
                val existingStepMap = stepsList.firstOrNull { it["date"] == newSteps.date }
                val updatedStepMap = mapOf(
                    "current" to newSteps.current,
                    "initial" to newSteps.initial,
                    "date" to newSteps.date,
                    "temp" to newSteps.temp,
                    "distance" to newSteps.distance,
                    "calories" to newSteps.calories
                )
                val updatedStepsList = if (existingStepMap != null) {
                    stepsList.map { if (it["date"] == newSteps.date) updatedStepMap else it }
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

    suspend fun loadTodaySteps(day : String): Step? {
        val userDocRef = db.collection("users").document(userid)
        val snapshot = userDocRef.get().await()

        if (snapshot.exists()) {
            val stepsList = snapshot.data?.get("steps") as? List<Map<String, Any>> ?: return null
            val stepMap = stepsList.firstOrNull { it["date"] == day }
            return stepMap?.let {
                Step(
                    current = it["current"] as? Long ?: 0,
                    initial = it["initial"] as? Long ?: 0,
                    date = it["date"] as? String ?: day,
                    temp =  it["temp"] as? Long ?: 0,
                    calories = it["calories"] as? Long ?: 0,
                    distance = it["distance"] as? Double ?: 0.0
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