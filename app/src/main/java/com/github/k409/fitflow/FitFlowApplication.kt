package com.github.k409.fitflow

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
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
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.worker.AquariumMetricsRemindersWorker
import com.github.k409.fitflow.worker.AquariumMetricsUpdaterWorker
import com.github.k409.fitflow.worker.DrinkReminderWorker
import com.github.k409.fitflow.worker.GoalAndStepUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
@RequiresApi(Build.VERSION_CODES.S)
class FitFlowApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManager: WorkManager

    override val workManagerConfiguration: Configuration
        get() = Configuration
            .Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()

        scheduleAquariumMetricsReminderWorker()
        scheduleGoalAndStepUpdateWorkers()
        scheduleHydrationReminderWorker()
        scheduleAquariumMetricsUpdaterWorker()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .respectCacheHeaders(false)
            .crossfade(true)
            .components {
                add(SvgDecoder.Factory())
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.03)
                    .directory(cacheDir)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }

    private fun scheduleGoalAndStepUpdateWorkers() {
        val start = GoalAndStepUpdateWorker.startTimeToSend
        val end = GoalAndStepUpdateWorker.endTimeToSend

        val times = (end - start) / 3
        val period = times * 60

        scheduleWork<GoalAndStepUpdateWorker>(
            "PeriodicGoalAndStepUpdateWorker",
            period.toLong(),
            TimeUnit.MINUTES,
            calculateInitialDelayUntil(start, 0),
        )
        scheduleWork<GoalAndStepUpdateWorker>(
            "MidnightGoalAndStepUpdateWorker",
            24,
            TimeUnit.HOURS,
            calculateInitialDelayUntilMidnight(),
        )
        scheduleWork<GoalAndStepUpdateWorker>(
            "BeforeGoalAndStepUpdateWorker",
            24,
            TimeUnit.HOURS,
            calculateInitialDelayBeforeMidnight(),
        )
    }

    private fun scheduleAquariumMetricsReminderWorker() {
        val initialDelay = calculateInitialDelayUntil(12, 0)

        val workerRequest = PeriodicWorkRequestBuilder<AquariumMetricsRemindersWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            AquariumMetricsRemindersWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workerRequest,
        )
    }

    private fun scheduleHydrationReminderWorker() {
        val drinkWorkerRequest = PeriodicWorkRequestBuilder<DrinkReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            DrinkReminderWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            drinkWorkerRequest,
        )
    }

    private fun scheduleAquariumMetricsUpdaterWorker() {
        val aquariumWorkerRequest = PeriodicWorkRequestBuilder<AquariumMetricsUpdaterWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).apply {
            val initialDelay = calculateInitialDelayUntil(0, 15)

            setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        }.build()

        workManager.enqueueUniquePeriodicWork(
            AquariumMetricsUpdaterWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            aquariumWorkerRequest,
        )
    }

    private inline fun <reified T : ListenableWorker> scheduleWork(
        workerName: String,
        repeatInterval: Long,
        timeUnit: TimeUnit,
        initialDelay: Long = 0L,
    ) {
        val workRequest = PeriodicWorkRequestBuilder<T>(repeatInterval, timeUnit)
            .apply {
                if (initialDelay > 0) {
                    setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                }
            }.build()

        workManager.enqueueUniquePeriodicWork(
            workerName,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }

    private fun calculateInitialDelayUntilMidnight(): Long {
        return calculateInitialDelayUntil(0, 0)
    }

    private fun calculateInitialDelayBeforeMidnight(): Long {
        return calculateInitialDelayUntil(23, 45)
    }

    private fun calculateInitialDelayUntil(
        targetHour: Int,
        targetMinute: Int,
    ): Long {
        val now = LocalDateTime.now()
        var targetDate = now.toLocalDate()

        if (now.hour > targetHour || (now.hour == targetHour && now.minute >= targetMinute)) {
            targetDate = targetDate.plusDays(1)
        }

        val targetTimeNextDay = LocalDateTime.of(targetDate, LocalTime.of(targetHour, targetMinute))

        return Duration.between(now, targetTimeNextDay).toMillis()
    }

    private fun createNotificationChannels() {
        val manager = NotificationManagerCompat.from(this)

        NotificationChannel.entries.forEach {
            if (it != null) {
                val notificationChannel = android.app.NotificationChannel(
                    it.channelId,
                    it.channelId,
                    NotificationManager.IMPORTANCE_DEFAULT,
                )

                manager.createNotificationChannel(notificationChannel)
            }
        }
    }
}
