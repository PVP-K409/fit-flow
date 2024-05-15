package com.github.k409.fitflow.model

import androidx.annotation.StringRes
import com.github.k409.fitflow.R
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
    var gender: Gender = Gender.Unspecified,
    var fitnessLevel: String = "",
    var fcmToken: String = "",
    var rank: Int = 0,
    var hasLeveledUp: Boolean = false,
)

enum class Gender(@StringRes val title: Int) {
    Male(R.string.male),
    Female(R.string.female),
    Unspecified(R.string.unspecified),
}

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

fun User.isProfileComplete(): Boolean {
    return gender != Gender.Unspecified && dateOfBirth.isNotEmpty()
}
