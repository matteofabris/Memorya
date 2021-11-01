package com.example.wordsmemory.framework.implementations

import android.util.Log
import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.VocabularyItemDataSource
import com.example.wordsmemory.domain.VocabularyItem
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import com.example.wordsmemory.framework.worker.CloudDbSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VocabularyItemDataSourceImpl @Inject constructor(
    private val _vocabularyItemDao: VocabularyItemDao,
    private val _workManager: WorkManager
) : VocabularyItemDataSource {

    override suspend fun getVocabularyItems() = _vocabularyItemDao.getVocabularyItems()

    override fun getVocabularyItemsAsLiveData() = _vocabularyItemDao.getVocabularyItemsAsLiveData()

    override suspend fun addVocabularyItem(vocabularyItem: VocabularyItem, update: Boolean) =
        withContext(Dispatchers.IO) {
            Log.i(
                Constants.packageName,
                "Save vocabulary item: en - ${vocabularyItem.enWord}, " +
                        "it - ${vocabularyItem.itWord}, " +
                        "category - ${vocabularyItem.category}"
            )

            val itemId = if (update) {
                _vocabularyItemDao.updateVocabularyItem(VocabularyItemEntity(vocabularyItem))
                vocabularyItem.id
            } else {
                _vocabularyItemDao.insertVocabularyItem(VocabularyItemEntity(vocabularyItem))
                    .toInt()
            }

            updateCloudDbVocabularyItem(itemId)
        }

    override suspend fun removeVocabularyItem(vocabularyItem: VocabularyItem) {
        _vocabularyItemDao.deleteVocabularyItem(VocabularyItemEntity(vocabularyItem))

        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.DeleteVocabularyItem.name,
                        Constants.ITEM_ID to vocabularyItem.id
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }

    private fun updateCloudDbVocabularyItem(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.InsertVocabularyItem.name,
                        Constants.ITEM_ID to itemId
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }
}