package com.github.k409.fitflow.di

import android.content.Context
import android.content.SharedPreferences
import com.github.k409.fitflow.ui.step_counter.StepCounter
import com.github.k409.fitflow.Database.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideStepCounter(@ApplicationContext context: Context) : StepCounter{
        return StepCounter(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    }

}