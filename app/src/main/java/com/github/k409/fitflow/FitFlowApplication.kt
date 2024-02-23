package com.github.k409.fitflow

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.k409.fitflow.ui.step_counter.StepCounterWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
@RequiresApi(Build.VERSION_CODES.S)
class FitFlowApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        val myWork = PeriodicWorkRequestBuilder<StepCounterWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "UpdateStepsWorker",
                ExistingPeriodicWorkPolicy.UPDATE, myWork
            )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

}