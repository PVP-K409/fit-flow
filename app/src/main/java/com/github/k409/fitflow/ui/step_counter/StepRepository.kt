package com.github.k409.fitflow.ui.step_counter

import android.util.Log
import com.github.k409.fitflow.DataModels.Step
import com.github.k409.fitflow.DataModels.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StepRepository {

    private val userid = "ohxyZCvlrIt0JaQQH5RF"

    private val db = FirebaseFirestore.getInstance()

    suspend fun updateSteps(newSteps: Step){

        val userDocRef = db.collection("users").document(userid)
        try{
            val snapshot = userDocRef.get().await()
            val user = snapshot.toObject(User::class.java)?: return

            val existingStepIndex = user.steps.indexOfFirst { it.date == newSteps.date }
            if (existingStepIndex != -1) {
                // update existing day
                user.steps[existingStepIndex] = newSteps
            } else {
                // new day
                user.steps.add(newSteps)
            }
            val updatedStepsList = user.steps.map { step ->
                mapOf(
                    "current" to step.current,
                    "initial" to step.initial,
                    "date" to step.date
                )
            }
            // Update the steps field in Firestore
            userDocRef.update("steps", updatedStepsList).await()
            Log.d("Step Repository", "Updated steps")

        }catch (e: Exception){
            e.printStackTrace()
        }


    }

    suspend fun loadTodaySteps(day : String): Step? {
        val userDocRef = db.collection("users").document(userid)
        val snapshot = userDocRef.get().await()
        val user = snapshot.toObject(User::class.java)?: return null
        val existingStepIndex = user.steps.indexOfFirst { it.date == day }
        if(existingStepIndex != -1){
            return user.steps[existingStepIndex]
        }
        return null

    }

}