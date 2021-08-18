package com.example.wordsmemory.ui.vocabulary.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.wordsmemory.Constants
import com.example.wordsmemory.database.WMDao
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyCategoriesViewModel @Inject constructor(
    private val _dbDao: WMDao,
    private val _workManager: WorkManager
) :
    ViewModel() {

    val categories = _dbDao.getCategoriesAsLiveData()

    fun deleteItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteCategory(categories.value!!.first { it.id == id })
            deleteCategory(id)
        }
    }

    private fun deleteCategory(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setInputData(
                    workDataOf(
                        Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.DeleteCategory.name,
                        Constants.ITEM_ID to itemId
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }
}