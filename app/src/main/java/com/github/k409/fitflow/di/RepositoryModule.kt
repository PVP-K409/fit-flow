package com.github.k409.fitflow.di

import android.content.SharedPreferences
import com.github.k409.fitflow.data.AuthRepository
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.ProfileRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.features.stepcounter.StepCounter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        db: FirebaseFirestore,
    ): ProfileRepository = ProfileRepository(
        db,
    )

    @Provides
    @Singleton
    fun provideUserRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
        stepCounter: StepCounter,
        prefs: SharedPreferences,
    ): UserRepository {
        return UserRepository(db, auth, stepCounter, prefs)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        userRepository: UserRepository,
    ): AuthRepository {
        return AuthRepository(auth, userRepository)
    }

    @Provides
    @Singleton
    fun provideWaterRepository(
        userRepository: UserRepository,
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): HydrationRepository {
        return HydrationRepository(userRepository, db, auth)
    }
}
