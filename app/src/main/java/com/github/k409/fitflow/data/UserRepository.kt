package com.github.k409.fitflow.data

import android.content.SharedPreferences
import android.util.Log
import com.github.k409.fitflow.features.stepcounter.StepCounter
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.toUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

private const val USERS_COLLECTION = "users"
private const val USER_STEPS_ARRAY = "steps"

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val stepCounter: StepCounter,
    private val prefs: SharedPreferences,
) {

    private fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val getCurrentUser: Flow<User> = getAuthState().flatMapLatest { isUserLoggedOut ->
        if (isUserLoggedOut) {
            callbackFlow {
                trySend(User())
                awaitClose()
            }
        } else {
            getUser(auth.currentUser!!.uid)
        }
    }

    fun getUser(uid: String): Flow<User> = db.collection(USERS_COLLECTION).document(uid).snapshots()
        .map { it.toObject<User>() ?: User() }

    fun createUser(firebaseUser: FirebaseUser) {
        val user = firebaseUser.toUser()

        if (user.name.isEmpty()) {
            user.name = user.email
        }

        db.collection(USERS_COLLECTION).document(user.uid).set(user)

        CoroutineScope(Dispatchers.IO).launch {
            setInitialSteps(user.uid)
        }
    }

    private suspend fun setInitialSteps(uid: String) {
        val initialSteps = stepCounter.steps()
        val today = LocalDate.now().toString()

        prefs.edit().putString("lastDate", today).apply()

        val initialStepRecordMap = mapOf(
            "current" to 0L,
            "initial" to initialSteps,
            "date" to today,
            "temp" to 0L,
            "distance" to 0.0,
            "calories" to 0L,
        )

        val initialStepRecordList = mutableListOf(initialStepRecordMap)

        db.collection(USERS_COLLECTION).document(uid).update(USER_STEPS_ARRAY, initialStepRecordList)
    }

    suspend fun updateSteps(newSteps: DailyStepRecord) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            val userDocRef = db.collection(USERS_COLLECTION).document(uid)

            try {
                val snapshot = userDocRef.get().await()

                if (snapshot.exists()) {
                    val stepsList =
                        snapshot.data?.get(USER_STEPS_ARRAY) as? List<Map<String, Any>> ?: mutableListOf()
                    val existingStepMap = stepsList.firstOrNull { it["date"] == newSteps.recordDate }
                    val updatedStepMap = mapOf(
                        "current" to newSteps.totalSteps,
                        "initial" to newSteps.initialSteps,
                        "date" to newSteps.recordDate,
                        "temp" to newSteps.stepsBeforeReboot,
                        "distance" to newSteps.totalDistance,
                        "calories" to newSteps.caloriesBurned,
                    )

                    val updatedStepsList = if (existingStepMap != null) {
                        stepsList.map { if (it["date"] == newSteps.recordDate) updatedStepMap else it }
                    } else {
                        stepsList + updatedStepMap // new day
                    }

                    userDocRef.update(USER_STEPS_ARRAY, updatedStepsList).await()
                } else {
                    Log.e("User Repository", "No such document")
                }
            } catch (e: Exception) {
                Log.e("User Repository", "Error updating steps", e)
            }
        } else {
            Log.e("User Repository", "No signed-in user")
        }
    }

    suspend fun loadTodaySteps(day: String): DailyStepRecord? {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            val userDocRef = db.collection(USERS_COLLECTION).document(uid)
            val snapshot = userDocRef.get().await()

            if (snapshot.exists()) {
                val stepsList = snapshot.data?.get(USER_STEPS_ARRAY) as? List<Map<String, Any>> ?: return null
                val stepMap = stepsList.firstOrNull { it["date"] == day }

                return stepMap?.let {
                    DailyStepRecord(
                        totalSteps = it["current"] as? Long ?: 0,
                        initialSteps = it["initial"] as? Long ?: 0,
                        recordDate = it["date"] as? String ?: day,
                        stepsBeforeReboot = it["temp"] as? Long ?: 0,
                        caloriesBurned = it["calories"] as? Long ?: 0,
                        totalDistance = it["distance"] as? Double ?: 0.0,
                    )
                }
            } else {
                return null
            }
        } else {
            return null
        }
    }

    suspend fun getUser(): User? {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                val documentSnapshot = db.collection(USERS_COLLECTION).document(uid).get().await()

                User(
                    uid = documentSnapshot.getLong("uid")?.toString() ?: "",
                    name = documentSnapshot.getString("name") ?: "",
                    photoUrl = documentSnapshot.getString("photoUrl") ?: "",
                    email = documentSnapshot.getString("email") ?: "",
                    points = documentSnapshot.getLong("points")?.toInt() ?: 0,
                    xp = documentSnapshot.getLong("xp")?.toInt() ?: 0,
                    dateOfBirth = documentSnapshot.getString("dateOfBirth") ?: "",
                    height = documentSnapshot.getDouble("height") ?: 0.0,
                    weight = documentSnapshot.getDouble("weight") ?: 0.0,
                    gender = documentSnapshot.getString("gender") ?: "",
                    fitnessLevel = documentSnapshot.getString("fitnessLevel") ?: "",
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
