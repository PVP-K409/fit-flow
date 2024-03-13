package com.github.k409.fitflow.di

import android.content.Context
import android.content.SharedPreferences
import androidx.health.connect.client.HealthConnectClient
import com.github.k409.fitflow.di.services.HealthConnectService
import com.github.k409.fitflow.features.stepcounter.CaloriesAndDistanceUtil
import com.github.k409.fitflow.features.stepcounter.StepCounter
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
    fun provideStepCounter(@ApplicationContext context: Context): StepCounter {
        return StepCounter(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideHealthConnectService(client: HealthConnectClient): HealthConnectService {
        return HealthConnectService(client)
    }

    @Provides
    @Singleton
    fun provideCaloriesAndDistanceutil(healthConnectService: HealthConnectService): CaloriesAndDistanceUtil{
        return CaloriesAndDistanceUtil(healthConnectService)
    }
}
