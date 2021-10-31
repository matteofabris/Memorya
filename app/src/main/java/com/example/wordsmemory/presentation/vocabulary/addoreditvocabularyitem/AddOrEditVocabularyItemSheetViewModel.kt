package com.example.wordsmemory.presentation.vocabulary.addoreditvocabularyitem

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.framework.api.translate.TranslateService
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import com.example.wordsmemory.worker.CloudDbSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AddOrEditVocabularyItemSheetViewModel @Inject constructor(
    private val _savedStateHandle: SavedStateHandle,
    private val _vocabularyItemDao: VocabularyItemDao,
    categoryDao: CategoryDao,
    private val _userDao: UserDao,
    private val _workManager: WorkManager
) : ViewModel() {

    val categories = categoryDao.getCategoriesAsLiveData()
    var isEdit = false
    val vocabularyItem = MutableLiveData<VocabularyItemEntity>()

    fun initVocabularyItem() {
        viewModelScope.launch {
            val selectedVocabularyItemId = _savedStateHandle.get<Int>("selectedVocabularyItemId")
            if (selectedVocabularyItemId != null && selectedVocabularyItemId != -1) {
                isEdit = true
                vocabularyItem.value =
                    _vocabularyItemDao.getVocabularyItemById(selectedVocabularyItemId)
            } else {
                vocabularyItem.value = VocabularyItemEntity("", "", 1)
            }
        }
    }

    fun setVocabularyItemCategory(category: String) {
        vocabularyItem.value?.category =
            categories.value?.find { c -> c.category == category }?.id ?: 0
    }

    suspend fun insertOrUpdateVocabularyItem() {
        return withContext(Dispatchers.IO) {
            Log.i(
                "save_item",
                "Save vocabulary item: en - ${vocabularyItem.value?.enWord}, " +
                        "it - ${vocabularyItem.value?.itWord}, " +
                        "category - ${vocabularyItem.value?.category}"
            )

            val itemId: Int = if (isEdit) {
                _vocabularyItemDao.updateVocabularyItem(vocabularyItem.value!!)
                vocabularyItem.value!!.id
            } else {
                _vocabularyItemDao.insertVocabularyItem(vocabularyItem.value!!).toInt()
            }

            updateCloudDbVocabularyItem(itemId)
        }
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

    fun translate() {
        viewModelScope.launch(Dispatchers.IO) {
            val textToTranslate = vocabularyItem.value?.enWord ?: ""
            val accessToken = _userDao.getUsers().first().accessToken

            val response = TranslateService.create()
                .translate("Bearer $accessToken", textToTranslate)

            if (response.isSuccessful) {
                val translatedText = response.body()?.data?.translations?.first()?.translatedText
                if (!translatedText.isNullOrEmpty()) updateTranslatedText(translatedText)

                Log.d("Translation", "TEO: $translatedText")
            }
        }
    }

    private suspend fun updateTranslatedText(translatedText: String) {
        return withContext(Dispatchers.Main) {
            val vocabularyItemTemp = vocabularyItem.value
            if (vocabularyItemTemp != null) {
                vocabularyItemTemp.itWord = translatedText
                vocabularyItem.value = vocabularyItemTemp!!
            }
        }
    }
}