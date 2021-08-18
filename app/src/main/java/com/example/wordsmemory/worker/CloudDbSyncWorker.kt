package com.example.wordsmemory.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wordsmemory.Constants
import com.example.wordsmemory.database.CloudDbSyncHelper
import com.example.wordsmemory.database.WMDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CloudDbSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val _dbDao: WMDao,
    private val _firestoreDb: FirebaseFirestore
) :
    CoroutineWorker(appContext, workerParams) {
    @Suppress("MoveVariableDeclarationIntoWhen")
    override suspend fun doWork(): Result {
        val itemId = inputData.getInt(Constants.ITEM_ID, -1)

        val workType =
            Constants.CloudDbSyncWorkType.valueOf(inputData.getString(Constants.WORK_TYPE)!!)
        when (workType) {
            Constants.CloudDbSyncWorkType.Fetch -> CloudDbSyncHelper.fetchDataFromCloud(
                _dbDao,
                _firestoreDb
            )
            Constants.CloudDbSyncWorkType.InsertVocabularyItem -> CloudDbSyncHelper.updateCloudDbVocabularyItem(
                _dbDao,
                _firestoreDb,
                itemId
            )
            Constants.CloudDbSyncWorkType.InsertCategory -> CloudDbSyncHelper.updateCloudDbCategory(
                _dbDao,
                _firestoreDb,
                itemId
            )
            Constants.CloudDbSyncWorkType.DeleteVocabularyItem -> CloudDbSyncHelper.deleteVocabularyItem(
                _dbDao,
                _firestoreDb,
                itemId
            )
            Constants.CloudDbSyncWorkType.DeleteCategory -> CloudDbSyncHelper.deleteCategory(
                _dbDao,
                _firestoreDb,
                itemId
            )
        }

        return Result.success()
    }
}