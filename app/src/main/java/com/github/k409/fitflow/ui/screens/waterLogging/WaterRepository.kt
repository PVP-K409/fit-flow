package com.github.k409.fitflow.ui.screens.waterLogging

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val USERS_COLLECTION = "users"
private const val USER_WEIGHT = "weight"
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

fun addWaterIntake(waterIntake: Int) {

    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()

    val documentPath = "$USERS_COLLECTION/$uid"

    val todayDate = Calendar.getInstance().time
    val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(todayDate)

    // Get the existing "Water intake" array
    db.document(documentPath).get()
        .addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.data
            val waterIntakeArray = data?.get("Water intake") as? List<Map<String, Any>> ?: emptyList()

            // Check if an entry for today already exists
            val existingEntry = waterIntakeArray.firstOrNull { it["date"] == todayDateString }

            // If entry for today doesn't exist, create a new entry
            if (existingEntry == null) {
                val newEntry = mapOf("date" to todayDateString, "amount" to waterIntake)
                val newData = mapOf("Water intake" to waterIntakeArray + newEntry)
                db.document(documentPath).set(newData, SetOptions.merge())
            } else {
                // Entry for today exists, update its amount
                val totalWaterIntake = (existingEntry["amount"] as? Long ?: 0).toInt() + waterIntake
                val updatedEntry = mapOf("date" to todayDateString, "amount" to totalWaterIntake)
                val updatedArray = waterIntakeArray.map { if (it["date"] == todayDateString) updatedEntry else it }
                val newData = mapOf("Water intake" to updatedArray)
                db.document(documentPath).set(newData, SetOptions.merge())
            }
        }
}

suspend fun retrieveTotalWaterIntake(): Int {
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val documentPath = "$USERS_COLLECTION/$uid"

    return try {
        val documentSnapshot = db.document(documentPath).get().await()
        val data = documentSnapshot.data
        val waterIntakeArray = data?.get("Water intake") as? List<Map<String, Any>> ?: emptyList()

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        // Find today's entry in the "Water intake" array
        val todayEntry = waterIntakeArray.firstOrNull { it["date"] == todayDate }

        if (todayEntry != null) {
            val totalWaterIntake = (todayEntry["amount"] as? Long ?: 0).toInt()
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
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val documentPath = "$USERS_COLLECTION/$uid"

    return try {
        val documentSnapshot = db.document(documentPath).get().await()
        val data = documentSnapshot.data
        val waterIntakeArray = data?.get("Water intake") as? List<Map<String, Any>> ?: emptyList()

        val yesterdayDate = Calendar.getInstance()
        yesterdayDate.add(Calendar.DATE, -1)
        val yesterdayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterdayDate.time)

        // Find yesterday's entry in the "Water intake" array
        val yesterdayEntry = waterIntakeArray.firstOrNull { it["date"] == yesterdayDateString }

        if (yesterdayEntry != null) {
            val waterIntakeAmount = (yesterdayEntry["amount"] as? Long ?: 0).toInt()
            waterIntakeAmount
        } else {
            0 // If yesterday's entry doesn't exist, return 0
        }
    } catch (e: Exception) {
        Log.e(TAG_Retrieve_Amount, "Error retrieving water intake for yesterday", e)
        0
    }
}

suspend fun retrieveWaterIntakeThisWeek(): Int {
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val documentPath = "$USERS_COLLECTION/$uid"

    return try {
        val documentSnapshot = db.document(documentPath).get().await()
        val data = documentSnapshot.data
        val waterIntakeArray = data?.get("Water intake") as? List<Map<String, Any>> ?: emptyList()

        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter entries for the current week
        val thisWeekEntries = waterIntakeArray.filter { entry ->
            val entryDate = entry["date"] as String
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(entryDate)!!
            val entryWeek = entryCalendar.get(Calendar.WEEK_OF_YEAR)
            val entryYear = entryCalendar.get(Calendar.YEAR)
            entryWeek == currentWeek && entryYear == currentYear
        }

        // Calculate total water intake for the current week
        val totalWaterIntakeThisWeek = thisWeekEntries.sumOf { (it["amount"] as? Long ?: 0).toInt() }

        totalWaterIntakeThisWeek
    } catch (e: Exception) {
        Log.e(TAG_Retrieve_Amount, "Error retrieving water intake for this week", e)
        0
    }
}

suspend fun retrieveWaterIntakeThisMonth(): Int {
    val uid = currentUser!!.uid
    val db = FirebaseFirestore.getInstance()
    val documentPath = "$USERS_COLLECTION/$uid"

    return try {
        val documentSnapshot = db.document(documentPath).get().await()
        val data = documentSnapshot.data
        val waterIntakeArray = data?.get("Water intake") as? List<Map<String, Any>> ?: emptyList()

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter entries for the current month
        val thisMonthEntries = waterIntakeArray.filter { entry ->
            val entryDate = entry["date"] as String
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(entryDate)!!
            val entryMonth = entryCalendar.get(Calendar.MONTH)
            val entryYear = entryCalendar.get(Calendar.YEAR)
            entryMonth == currentMonth && entryYear == currentYear
        }

        // Calculate total water intake for the current month
        val totalWaterIntakeThisMonth = thisMonthEntries.sumOf { (it["amount"] as? Long ?: 0).toInt() }

        totalWaterIntakeThisMonth
    } catch (e: Exception) {
        Log.e(TAG_Retrieve_Amount, "Error retrieving water intake for this month", e)
        0
    }
}