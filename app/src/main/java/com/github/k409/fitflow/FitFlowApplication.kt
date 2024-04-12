package com.github.k409.fitflow

import android.app.Application
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.github.k409.fitflow.worker.GoalUpdaterWorker
import com.github.k409.fitflow.worker.StepCounterWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
@RequiresApi(Build.VERSION_CODES.S)
class FitFlowApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    // ImageLoader has its own memory cache, disk cache, and OkHttpClient so it should be initialized only once in app
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // workers for StepCounter
        scheduleWork<StepCounterWorker>("PeriodicStepWorker", 180, TimeUnit.MINUTES)
        scheduleWork<StepCounterWorker>("MidnightStepWorker", 24, TimeUnit.HOURS, calculateInitialDelayUntilMidnight())
        scheduleWork<StepCounterWorker>("BeforeMidnightStepWorker", 24, TimeUnit.HOURS, calculateInitialDelayBeforeMidnight())

        // workers for GoalUpdater
        scheduleWork<GoalUpdaterWorker>("PeriodicGoalUpdater", 180, TimeUnit.MINUTES)
        scheduleWork<GoalUpdaterWorker>("MidnightGoalUpdater", 24, TimeUnit.HOURS, calculateInitialDelayUntilMidnight())
        scheduleWork<GoalUpdaterWorker>("BeforeMidnightGoalUpdater", 24, TimeUnit.HOURS, calculateInitialDelayBeforeMidnight())
    }

    private inline fun <reified T : ListenableWorker> scheduleWork(workerName: String, repeatInterval: Long, timeUnit: TimeUnit, initialDelay: Long = 0L) {
        val workRequest = PeriodicWorkRequestBuilder<T>(repeatInterval, timeUnit)
            .apply {
                if (initialDelay > 0) {
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }
            }.build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            workerName,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }

    private fun calculateInitialDelayUntilMidnight(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis - now
    }

    private fun calculateInitialDelayBeforeMidnight(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 58)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis - now
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}
