package com.example.wordsmemory.ui.vocabulary.addoreditcategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.wordsmemory.Constants
import com.example.wordsmemory.database.WMDao
import com.example.wordsmemory.model.vocabulary.Category
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddCategorySheetViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: WMDao,
    private val _workManager: WorkManager
) : ViewModel() {

    val categoryItem = MutableLiveData<Category>()
    var isEdit = false

    init {
        viewModelScope.launch {
            val selectedCategoryId = _savedStateHandle.get<Int>("selectedCategoryId")
            if (selectedCategoryId != null && selectedCategoryId != -1) {
                isEdit = true
                categoryItem.value = _dbDao.getCategoryById(selectedCategoryId)
            } else {
                categoryItem.value = Category("")
            }
        }
    }

    suspend fun insertOrUpdateCategory() {
        return withContext(Dispatchers.IO) {
            categoryItem.value!!.category =
                categoryItem.value!!.category.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            val categoryId: Int = if (isEdit) {
                _dbDao.updateCategory(categoryItem.value!!)
                categoryItem.value!!.id
            } else {
                _dbDao.insertCategory(categoryItem.value!!).toInt()
            }

            updateCloudDbCategory(categoryId)
        }
    }

    private fun updateCloudDbCategory(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
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