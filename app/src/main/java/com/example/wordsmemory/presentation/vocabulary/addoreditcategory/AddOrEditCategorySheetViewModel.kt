package com.example.wordsmemory.presentation.vocabulary.addoreditcategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AddCategorySheetViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _cateogryDao: CategoryDao,
    private val _workManager: WorkManager
) : ViewModel() {

    val categoryItem = MutableLiveData<CategoryEntity>()
    var isEdit = false

    init {
        viewModelScope.launch {
            val selectedCategoryId = _savedStateHandle.get<Int>("selectedCategoryId")
            if (selectedCategoryId != null && selectedCategoryId != -1) {
                isEdit = true
                categoryItem.value = _cateogryDao.getCategoryById(selectedCategoryId)
            } else {
                categoryItem.value = CategoryEntity("")
            }
        }
    }

    suspend fun insertOrUpdateCategory() {
        return withContext(Dispatchers.IO) {
            categoryItem.value!!.category =
                categoryItem.value!!.category.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            val categoryId: Int = if (isEdit) {
                _cateogryDao.updateCategory(categoryItem.value!!)
                categoryItem.value!!.id
            } else {
                _cateogryDao.insertCategory(categoryItem.value!!).toInt()
            }

            updateCloudDbCategory(categoryId)
        }
    }

    private fun updateCloudDbCategory(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.InsertCategory.name,
                        Constants.ITEM_ID to itemId
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }
}