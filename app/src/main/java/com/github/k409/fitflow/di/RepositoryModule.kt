package com.github.k409.fitflow.di

import com.github.k409.fitflow.data.ProfileRepository
import com.github.k409.fitflow.data.UserRepository
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
        firestoreFirestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): UserRepository {
        return UserRepository(firestoreFirestore, auth)
    }
}
