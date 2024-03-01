package com.github.k409.fitflow.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    fun submitProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Int,
        height: Int
    ) {
        profileRepository.submitProfile(
            name, age, gender, weight, height
        )
    }
}