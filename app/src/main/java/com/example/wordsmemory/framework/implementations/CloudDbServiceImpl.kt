package com.example.wordsmemory.framework.implementations

import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.domain.Constants.CloudDbObjectType
import com.example.wordsmemory.domain.Constants.CloudDbObjectType.*
import com.example.wordsmemory.framework.worker.CloudDbSyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CloudDbServiceImpl @Inject constructor(private val _workManager: WorkManager) :
    CloudDbService {
    override fun fetchCloudDb() = createWorkRequest(Constants.CloudDbSyncWorkType.Fetch.name)

    override fun add(type: CloudDbObjectType, id: Int) {
        val workType = when (type) {
            User -> Constants.CloudDbSyncWorkType.InsertUser.name
            VocabularyItem -> Constants.CloudDbSyncWorkType.InsertVocabularyItem.name
            Category -> Constants.CloudDbSyncWorkType.InsertCategory.name
        }

        createWorkRequest(workType, id)
    }

    override fun remove(type: CloudDbObjectType, id: Int) {
        val workType = when (type) {
            VocabularyItem -> Constants.CloudDbSyncWorkType.DeleteVocabularyItem.name
            Category -> Constants.CloudDbSyncWorkType.DeleteCategory.name
            User -> return
        }

        createWorkRequest(workType, id)
    }

    private fun createWorkRequest(workType: String, id: Int = -1) {
        val workData = getWorkData(workType, id)
        val workRequest = OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).setInputData(workData).build()
        _workManager.enqueue(workRequest)
    }

    private fun getWorkData(workType: String, id: Int = -1): Data {
        return when (workType) {
            Constants.CloudDbSyncWorkType.InsertUser.name, Constants.CloudDbSyncWorkType.Fetch.name -> {
                workDataOf(
                    Constants.WORK_TYPE to workType
                )
            }
            else -> {
                workDataOf(
                    Constants.WORK_TYPE to workType,
                    Constants.ITEM_ID to id
                )
            }
        }
    }
}