package com.github.k409.fitflow.model

import com.google.firebase.auth.FirebaseUser

data class User(
    var uid : String = "",
    var name: String = "",
    val photoUrl: String = "",
    var email: String = "",
    var points: Int = 0,
    var xp: Int = 0,
    var age: Int = 0,
    var height: Double = 0.0,
    var weight: Double = 0.0,
    var gender: String = "",
)

fun FirebaseUser.toUser() = User(
    uid = uid,
    name = displayName ?: "",
    email = email ?: "",
    photoUrl = photoUrl.toString(),
)