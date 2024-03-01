package com.github.k409.fitflow.ui.screens.registration

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
) : ViewModel()