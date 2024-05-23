package com.github.k409.fitflow.di

import android.content.Context
import android.content.SharedPreferences
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.AuthRepository
import com.github.k409.fitflow.data.GoalsRepository
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.data.ItemRepository
import com.github.k409.fitflow.data.ProfileRepository
import com.github.k409.fitflow.data.StepsRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.service.HydrationNotificationService
import com.github.k409.fitflow.service.StepCounterService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        db: FirebaseFirestore,
        hydrationRepository: HydrationRepository,
    ): ProfileRepository = ProfileRepository(
        db,
        hydrationRepository,
    )

    @Provides
    @Singleton
    fun provideUserRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
        stepsRepository: StepsRepository,
    ): UserRepository {
        return UserRepository(db, auth, stepsRepository)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        userRepository: UserRepository,
        itemRepository: ItemRepository,
        aquariumRepository: AquariumRepository,
        credentialManager: CredentialManager,
        getCredentialRequest: GetCredentialRequest,
        @ApplicationContext context: Context,
    ): AuthRepository {
        return AuthRepository(
            auth,
            userRepository,
            itemRepository,
            aquariumRepository,
            credentialManager,
            getCredentialRequest,
            context,
        )
    }

    @Provides
    @Singleton
    fun provideGoalRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): GoalsRepository {
        return GoalsRepository(db, auth)
    }

    @Provides
    @Singleton
    fun provideHydrationRepository(
        userRepository: UserRepository,
        db: FirebaseFirestore,
        auth: FirebaseAuth,
        preferencesRepository: PreferencesRepository,
        hydrationNotificationService: HydrationNotificationService,
        aquariumRepository: AquariumRepository,
    ): HydrationRepository {
        return HydrationRepository(
            userRepository,
            db,
            auth,
            preferencesRepository,
            hydrationNotificationService,
            aquariumRepository,
        )
    }

    @Provides
    @Singleton
    fun provideStepsRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
        stepCounterService: StepCounterService,
        prefs: SharedPreferences,
    ): StepsRepository {
        return StepsRepository(db, auth, stepCounterService, prefs)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideAquariumRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
    ): AquariumRepository {
        return AquariumRepository(db, auth)
    }
}
