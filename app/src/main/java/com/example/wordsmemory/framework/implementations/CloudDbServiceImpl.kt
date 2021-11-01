package com.example.wordsmemory.framework.implementations

import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.framework.worker.CloudDbSyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CloudDbServiceImpl @Inject constructor(private val _workManager: WorkManager) :
    CloudDbService {
    override fun fetchCloudDb() {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(workDataOf(Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.Fetch.name))
                .build()
        _workManager.enqueue(workRequest)
    }
}