package com.memorya.framework.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.memorya.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CloudDbSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val _cloudDbSyncWorkerManager: CloudDbSyncWorkerManager
) :
    CoroutineWorker(appContext, workerParams) {
    @Suppress("MoveVariableDeclarationIntoWhen")
    override suspend fun doWork(): Result {
        val itemId = inputData.getInt(Constants.ITEM_ID, -1)
        val workType =
            Constants.CloudDbSyncWorkType.valueOf(inputData.getString(Constants.WORK_TYPE)!!)

        when (workType) {
            Constants.CloudDbSyncWorkType.Fetch -> return _cloudDbSyncWorkerManager.fetchCloudDb()
            Constants.CloudDbSyncWorkType.InsertVocabularyItem -> return _cloudDbSyncWorkerManager.updateCloudDbVocabularyItem(
                itemId
            )
            Constants.CloudDbSyncWorkType.InsertCategory -> return _cloudDbSyncWorkerManager.updateCloudDbCategory(
                itemId
            )
            Constants.CloudDbSyncWorkType.DeleteVocabularyItem -> return _cloudDbSyncWorkerManager.deleteVocabularyItem(
                itemId
            )
            Constants.CloudDbSyncWorkType.DeleteCategory -> return _cloudDbSyncWorkerManager.deleteCategory(
                itemId
            )
            Constants.CloudDbSyncWorkType.InsertUser -> return _cloudDbSyncWorkerManager.insertCloudDbUser()
        }
    }
}