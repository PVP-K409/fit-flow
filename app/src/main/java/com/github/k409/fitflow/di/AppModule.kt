package com.github.k409.fitflow.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.health.connect.client.HealthConnectClient
import androidx.work.WorkManager
import com.github.k409.fitflow.data.HealthStatsManager
import com.github.k409.fitflow.service.HealthConnectService
import com.github.k409.fitflow.service.StepCounterService
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
    fun provideStepCounter(@ApplicationContext context: Context): StepCounterService {
        return StepCounterService(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("FitFlowPrefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideHealthConnectService(client: HealthConnectClient): HealthConnectService {
        return HealthConnectService(client)
    }

    @Provides
    @Singleton
    fun provideHealthStatsManager(healthConnectService: HealthConnectService): HealthStatsManager {
        return HealthStatsManager(healthConnectService)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext
        context: Context,
    ): NotificationManager {
        return (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext
        context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}
