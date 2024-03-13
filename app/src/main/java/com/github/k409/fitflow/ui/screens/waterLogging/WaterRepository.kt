package com.github.k409.fitflow.ui.screens.waterLogging

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val USERS_COLLECTION = "users"
private const val JOURNAL_COLLECTION = "journal"
private const val HYDRATION_COLLECTION = "hydration"
private const val USER_WEIGHT = "weight"
private const val TAG_Goal = "DailyGoal"
private const val TAG_Retrieve_Amount = "RetrieveAmount"
private const val TAG_Doc_Creation = "DocCreation"
private const val TAG_Collection_Creation = "CollectionCreation"
private const val TAG_Water_Intake = "WaterIntake"
private const val TAG_Week_Intake= "WeekIntake"
private const val TAG_Month_Intake= "MonthIntake"

@Composable
fun getWaterIntakeGoal(): Int {

    val currentUser = FirebaseAuth.getInstance().currentUser
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

fun createFirebaseDoc(uid: String) {
    val docRef = Firebase.firestore
        .collection(JOURNAL_COLLECTION)
        .document(uid)

    docRef.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                Log.d(TAG_Doc_Creation, "The user document exists for UID: $uid")
            } else {
                docRef.set(hashMapOf<String, Any>()) // Create an empty document
                    .addOnSuccessListener {
                        Log.d(TAG_Doc_Creation, "Document created for UID: $uid")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG_Doc_Creation, "Error creating document for UID: $uid", e)
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e(TAG_Doc_Creation, "Error getting document for UID: $uid", exception)
        }

    createHydrationDocument()
}

fun createHydrationDocument() {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser!!.uid

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate = dateFormat.format(Date())

    val docRef = Firebase.firestore
        .collection(JOURNAL_COLLECTION)
        .document(uid)

    val hydrationCollectionRef = docRef.collection("hydration")

    hydrationCollectionRef.get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                // If "hydration" collection doesn't exist, create it
                hydrationCollectionRef.document(todayDate).set(hashMapOf<String, Any>())
                    .addOnSuccessListener {
                        Log.d(TAG_Collection_Creation, "Hydration collection created for UID: $uid")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG_Collection_Creation, "Error creating hydration collection for UID: $uid", e)
                    }
            } else {
                // If "hydration" collection exists, check if document for today exists
                val hydrationDocumentRef = hydrationCollectionRef.document(todayDate)
                hydrationDocumentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            Log.d(TAG_Doc_Creation, "Hydration document for $todayDate already exists for UID: $uid")
                        } else {
                            // Create document for today if it doesn't exist yet
                            hydrationDocumentRef.set(hashMapOf<String, Any>())
                                .addOnSuccessListener {
                                    Log.d(TAG_Doc_Creation, "Hydration document created for $todayDate and UID: $uid")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG_Doc_Creation, "Error creating hydration document for $todayDate and UID: $uid", e)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG_Doc_Creation, "Error checking hydration document for $todayDate and UID: $uid", exception)
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.e(TAG_Collection_Creation, "Error checking hydration collection for UID: $uid", exception)
        }
}

fun addWaterIntake(waterIntake: Int) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser!!.uid

    val db = FirebaseFirestore.getInstance()

    val todayDate = Calendar.getInstance().time
    val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(todayDate)

    val hydrationDocumentRef = db.collection(JOURNAL_COLLECTION)
        .document(uid)
        .collection(HYDRATION_COLLECTION)
        .document(todayDateString)

    // Update the document with water intake data
    hydrationDocumentRef.update("waterIntake", FieldValue.increment(waterIntake.toLong()))
        .addOnSuccessListener {
            Log.d(TAG_Water_Intake, "Water intake updated for $todayDateString and UID: $uid")
        }
        .addOnFailureListener { e ->
            Log.e(TAG_Water_Intake, "Error updating water intake for $todayDateString and UID: $uid", e)
        }
}


suspend fun retrieveTotalWaterIntake(): Int {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    val hydrationDocumentRef = db.collection(JOURNAL_COLLECTION)
        .document(uid)
        .collection(HYDRATION_COLLECTION)
        .document(todayDate)

    return try {
        val documentSnapshot = hydrationDocumentRef.get().await()

        if (documentSnapshot.exists()) {
            val totalWaterIntake = documentSnapshot.getLong("waterIntake")?.toInt() ?: 0
            totalWaterIntake
        } else {
            0 // If today's entry doesn't exist, return 0
        }
    } catch (e: Exception) {
        Log.e(TAG_Retrieve_Amount, "Error retrieving total water intake", e)
        0
    }
}

suspend fun retrieveWaterIntakeYesterday(): Int {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    return try {
        val yesterdayDate = Calendar.getInstance()
        yesterdayDate.add(Calendar.DATE, -1)
        val yesterdayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterdayDate.time)

        val hydrationDocumentRef = db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(HYDRATION_COLLECTION)
            .document(yesterdayDateString)

        val documentSnapshot = hydrationDocumentRef.get().await()

        if (documentSnapshot.exists()) {
            val waterIntakeYesterday = documentSnapshot.getLong("waterIntake")?.toInt() ?: 0
            waterIntakeYesterday
        } else {
            0 // If yesterday's entry doesn't exist, return 0
        }
    } catch (e: Exception) {
        Log.e(TAG_Retrieve_Amount, "Error retrieving water intake for yesterday", e)
        0
    }
}

suspend fun retrieveWaterIntakeThisWeek(): Int {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    return try {
        val calendar = Calendar.getInstance()
        var totalWaterIntakeThisWeek = 0

        for (dayOfWeek in Calendar.SUNDAY..Calendar.SATURDAY) {
            // Get the date for the current day of the week
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Path to the document for the current day
            val documentPath = "$JOURNAL_COLLECTION/$uid/$HYDRATION_COLLECTION/$currentDate"

            // Retrieve the document for the current day
            val documentSnapshot = db.document(documentPath).get().await()
            val data = documentSnapshot.data

            // If document exists for the current day, calculate total water intake
            if (documentSnapshot.exists()) {
                val waterIntake = data?.get("waterIntake") as? Long ?: 0
                totalWaterIntakeThisWeek += waterIntake.toInt()
                Log.d(TAG_Week_Intake, "Date: $currentDate, Water Intake: $waterIntake")
            }
        }

        totalWaterIntakeThisWeek
    } catch (e: Exception) {
        Log.e(TAG_Week_Intake, "Error retrieving water intake for this week", e)
        0
    }
}

suspend fun retrieveWaterIntakeThisMonth(): Int {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    return try {
        val calendar = Calendar.getInstance()
        var totalWaterIntakeThisMonth = 0

        for (dayOfMonth in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val documentPath = "$JOURNAL_COLLECTION/$uid/$HYDRATION_COLLECTION/$currentDate"
            val documentSnapshot = db.document(documentPath).get().await()

            if (documentSnapshot.exists()) {
                val waterIntake = documentSnapshot.getLong("waterIntake") ?: 0
                totalWaterIntakeThisMonth += waterIntake.toInt()
                Log.d("Month", "Date: $currentDate, Water Intake: $waterIntake")
            }
        }

        totalWaterIntakeThisMonth
    } catch (e: Exception) {
        Log.e(TAG_Month_Intake, "Error retrieving water intake for this month", e)
        0
    }
}