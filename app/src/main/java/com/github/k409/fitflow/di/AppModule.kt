package com.github.k409.fitflow.di

import android.content.Context
import android.content.SharedPreferences
import com.github.k409.fitflow.ui.step_counter.StepCounter
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
    fun provideStepCounter(@ApplicationContext appContext: Context): StepCounter {
        return StepCounter(appContext)
    }

}