package com.github.k409.fitflow.model

import com.google.firebase.auth.FirebaseUser

data class User(
    var uid: String = "",
    var name: String = "",
    val photoUrl: String = "",
    var email: String = "",
    var points: Int = 0,
    var xp: Int = 0,
    var dateOfBirth: String = "",
    var height: Double = 0.0,
    var weight: Double = 0.0,
    var gender: String = "",
)

fun FirebaseUser.toUser(): User {
    val name = (
        if (displayName.isNullOrEmpty()) {
            email
        } else {
            displayName
        }
        ) ?: ""

    return User(
        uid = uid,
        name = name,
        email = email ?: "",
        photoUrl = photoUrl.toString(),
    )
}
