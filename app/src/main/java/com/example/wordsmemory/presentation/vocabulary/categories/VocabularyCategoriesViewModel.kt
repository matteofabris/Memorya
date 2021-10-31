package com.example.wordsmemory.presentation.vocabulary.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VocabularyCategoriesViewModel @Inject constructor(
    private val _categoryDao: CategoryDao,
    private val _workManager: WorkManager
) :
    ViewModel() {

    val categories = _categoryDao.getCategories()

    fun deleteItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryDao.deleteCategory(categories.value!!.first { it.id == id })
            deleteCategory(id)
        }
    }

    private fun deleteCategory(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
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