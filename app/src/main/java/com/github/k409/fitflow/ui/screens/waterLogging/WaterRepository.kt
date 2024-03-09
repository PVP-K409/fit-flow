package com.github.k409.fitflow.ui.screens.waterLogging

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val USERS_COLLECTION = "users"
private const val UUID_DOC = "user-uid"
private const val JOURNAL_COLLECTION = "journal"
private const val HYDRATION_COLLECTION = "hydration"
private const val USER_WEIGHT = "weight"
private const val TAG_Hydration = "FirebaseHydration"
private const val TAG_Goal = "DailyGoal"
private const val TAG_Retrieve_Amount = "RetrieveAmount"
private val currentUser = FirebaseAuth.getInstance().currentUser

@Composable
fun getWaterIntakeGoal(): Int {
    val usersRef = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    var userWeight by remember { mutableIntStateOf(0) }

    if (currentUser != null) {
        val uid = currentUser.uid
        usersRef.document(uid).get().addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val weight = document.getLong(USER_WEIGHT)
                    if (weight != null) {
                        userWeight = weight.toInt()
                    }
                } else{
                    Log.e(TAG_Goal, "The document doesn't exist.")
                }
            } else {
                task.exception?.message?.let {
                    Log.e(TAG_Goal, it)
                }
            }
        }
    }

    return userWeight * 30
}

// Function to add water intake data to Firestore
fun addWaterIntake(waterIntake: Int) {

    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    val documentPath = "$JOURNAL_COLLECTION/$UUID_DOC/$HYDRATION_COLLECTION/$uid"

    val todayDate = Calendar.getInstance().time
    val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(todayDate)

    // Get the existing array for today's date
    db.document(documentPath).get()
        .addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.data
            val todayArray = data?.get(todayDateString) as? List<Map<String, Any>> ?: emptyList()

            // Calculate the total water intake for today
            val totalWaterIntake = todayArray.sumOf { (it["amount"] as? Long ?: 0).toInt() } + waterIntake

            // Update or create the array field for today's date with the new total
            val newData = mapOf(todayDateString to listOf(mapOf("date" to todayDateString, "amount" to totalWaterIntake)))
            db.document(documentPath).update(newData)
        }
}

suspend fun retrieveTotalWaterIntake(): Int {
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val documentPath = "$JOURNAL_COLLECTION/$UUID_DOC/$HYDRATION_COLLECTION/$uid"

    return try {
        val documentSnapshot = db.document(documentPath).get().await()
        val data = documentSnapshot.data
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        val todayArray = data?.get(todayDate) as? List<Map<String, Any>> ?: emptyList()

        // Check if the date in the array is today's date
        val isToday = todayArray.any { it["date"] == todayDate }

        if (isToday) {
            val totalWaterIntake = todayArray.sumOf { (it["amount"] as? Long ?: 0).toInt() }
            totalWaterIntake
        } else {
            0// If the date in the array is not today's date, return 0
        }
    } catch (e: Exception) {
        Log.e(TAG_Retrieve_Amount, "Error retrieving total water intake", e)
        0
    }
}


fun createFirebaseDocHydration(uid: String){
    val docRef = Firebase.firestore
        .collection(JOURNAL_COLLECTION)
        .document(UUID_DOC)
        .collection(HYDRATION_COLLECTION)
        .document(uid)

    docRef.get()
        .addOnSuccessListener { documentSnapshot ->
            if(documentSnapshot.exists()){
                Log.d(TAG_Hydration, "The user document exists for UID: $uid" )
            } else {
                docRef.set(hashMapOf<String, Any>())
                    .addOnSuccessListener {
                        Log.d(TAG_Hydration, "Document created for UID: $uid" )
                    }
                    .addOnFailureListener{e ->
                        Log.e(TAG_Hydration, "Error:$e creating document for UID: $uid" )
                    }
            }
        }
        .addOnFailureListener{exception ->
            Log.e(TAG_Hydration, exception.message ?:
            "Error getting documents in hydration collection : $exception")
        }
}