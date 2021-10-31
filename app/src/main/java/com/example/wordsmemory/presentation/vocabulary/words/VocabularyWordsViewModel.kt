package com.example.wordsmemory.presentation.vocabulary.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VocabularyWordsViewModel @Inject constructor(
    private val _vocabularyItemDao: VocabularyItemDao,
    private val _workManager: WorkManager
) : ViewModel() {
    lateinit var vocabularyListEntity: LiveData<List<VocabularyItemEntity>>

    fun deleteItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _vocabularyItemDao.deleteVocabularyItem(vocabularyListEntity.value!!.first { it.id == id })
            deleteVocabularyItem(id)
        }
    }

    private fun deleteVocabularyItem(itemId: Int) {
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
                        Constants.ITEM_ID to itemId
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }

    fun initVocabularyList(categoryId: Int) {
        viewModelScope.launch {
            vocabularyListEntity =
                if (categoryId > 0) _vocabularyItemDao.getVocabularyItemsByCategoryAsLiveData(
                    categoryId
                )
                else _vocabularyItemDao.getVocabularyItemsAsLiveData()
        }
    }
}