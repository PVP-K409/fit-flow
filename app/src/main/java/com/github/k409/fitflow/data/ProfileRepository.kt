package com.github.k409.fitflow.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository {
    fun submitProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Int,
        height: Int
    ) {
        try {
            // Create a map to represent the updated data
            val updatedData = hashMapOf<String, Any>(
                "name" to name,
                "age" to age,
                "gender" to gender,
                "weight" to weight,
                "height" to height
            )
            val userid = "ohxyZCvlrIt0JaQQH5RF" // replace with non-constant userid later
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(userid)
            userDocRef.update(updatedData).addOnSuccessListener {
                // Handle success, e.g., document successfully updated
            }.addOnFailureListener { e ->
                // Handle failure, e.g., there was an error updating the document
                Log.e("FirestoreUpdate", "Error updating document", e)
            }
            Log.d("profileRepository", "DB stuff")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}