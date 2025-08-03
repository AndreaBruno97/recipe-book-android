package com.example.recipebook.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.recipebook.constants.FileConstants
import com.example.recipebook.data.utility.DbFunc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "PeriodicBackupWorker"

class PeriodicBackupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private val _context = context

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                DbFunc.updateBackupInAppLocalStorage(_context)
                Result.success()
            } catch (throwable: Throwable) {
                Result.failure()
            }
        }
    }
}

class PeriodicBackupWorkerManager(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun startPeriodicBackup() {
        val workerRequest = PeriodicWorkRequestBuilder<PeriodicBackupWorker>(
            FileConstants.BACKUP_INTERVAL_HOURS, TimeUnit.HOURS
        )
            // It's useless to start the backup when the app is opened the first time
            // Adding a delay equal to the interval
            .setInitialDelay(FileConstants.BACKUP_INTERVAL_HOURS, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            workerRequest
        )
    }
}