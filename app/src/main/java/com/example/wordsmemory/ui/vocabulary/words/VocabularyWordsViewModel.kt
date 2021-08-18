package com.example.wordsmemory.ui.vocabulary.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.wordsmemory.Constants
import com.example.wordsmemory.database.WMDao
import com.example.wordsmemory.model.vocabulary.VocabularyItem
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyWordsViewModel @Inject constructor(
    private val _dbDao: WMDao,
    private val _workManager: WorkManager
) : ViewModel() {
    lateinit var vocabularyList: LiveData<List<VocabularyItem>>

    fun deleteItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteVocabularyItem(vocabularyList.value!!.first { it.id == id })
            deleteVocabularyItem(id)
        }
    }

    private fun deleteVocabularyItem(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
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
            vocabularyList =
                if (categoryId > 0) _dbDao.getVocabularyItemsByCategoryAsLiveData(categoryId)
                else _dbDao.getVocabularyItemsAsLiveData()
        }
    }
}