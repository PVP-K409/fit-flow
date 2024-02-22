package com.github.k409.fitflow.di

import android.content.Context
import com.github.k409.fitflow.ui.step_counter.StepCounter
import com.github.k409.fitflow.ui.step_counter.StepRepository
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
    fun provideStepRepository(): StepRepository {
        return StepRepository()
    }

    @Provides
    @Singleton
    fun provideStepCounter(@ApplicationContext context: Context) : StepCounter{
        return StepCounter(context)
    }

}