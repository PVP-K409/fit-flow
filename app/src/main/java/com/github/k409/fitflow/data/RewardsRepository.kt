package com.github.k409.fitflow.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val USERS_COLLECTION = "users"
private const val POINTS_FIELD = "points"
private const val XP_FIELD = "xp"

fun getPoints(onSuccess: (Int) -> Unit){
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    db.collection(USERS_COLLECTION).document(uid)
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("PointsListener", "Error getting points", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val points = snapshot.getLong(POINTS_FIELD)?.toInt() ?: 0
                Log.d("PointsListener", "User's points: $points")
                onSuccess(points)
            } else {
                Log.e("PointsListener", "Document does not exist or has no data")
            }
        }
}

fun getXp(onSuccess: (Int) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    db.collection(USERS_COLLECTION).document(uid)
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("XpListener", "Error getting xp", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val xp = snapshot.getLong(XP_FIELD)?.toInt() ?: 0
                Log.d("XpListener", "User's xp: $xp")
                onSuccess(xp)
            } else {
                Log.e("XpListener", "Document does not exist or has no data")
            }
        }
}