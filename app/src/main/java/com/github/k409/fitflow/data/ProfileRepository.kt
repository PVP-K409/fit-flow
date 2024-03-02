package com.github.k409.fitflow.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,

) {
    fun submitProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Int,
        height: Int,
    ) {
        try {
            val updatedData = hashMapOf<String, Any>(
                "name" to name,
                "age" to age,
                "gender" to gender,
                "weight" to weight,
                "height" to height,
            )
            val userid = "ohxyZCvlrIt0JaQQH5RF"
            val userDocRef = db.collection("users").document(userid)

            userDocRef.update(updatedData).addOnSuccessListener {
            }.addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Error updating document", e)
            }

            Log.d("profileRepository", "DB stuff")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
