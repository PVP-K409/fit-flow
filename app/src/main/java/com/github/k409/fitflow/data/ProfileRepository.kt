package com.github.k409.fitflow.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val hydrationRepository: HydrationRepository,
    var success: Boolean = true,
) {
    suspend fun submitProfile(
        uid: String,
        name: String,
        dateOfBirth: String,
        gender: String,
        weight: Int,
        height: Int,
    ): Boolean {
        try {
            val updatedData = hashMapOf<String, Any>(
                "name" to name,
                "dateOfBirth" to dateOfBirth,
                "gender" to gender,
                "weight" to weight,
                "height" to height,
            )

            val userDocRef = db.collection("users").document(uid)

            userDocRef.update(updatedData).await()

            hydrationRepository.updateWaterIntakeGoal()
        } catch (e: Exception) {
            e.printStackTrace()
            success = false
        }
        return success
    }
}
