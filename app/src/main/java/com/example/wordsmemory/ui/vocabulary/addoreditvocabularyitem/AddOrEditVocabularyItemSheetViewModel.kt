package com.example.wordsmemory.ui.vocabulary.addoreditvocabularyitem

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.api.translate.TranslateService
import com.example.wordsmemory.database.CloudDbSyncHelper
import com.example.wordsmemory.database.WMDao
import com.example.wordsmemory.model.vocabulary.VocabularyItem
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddOrEditVocabularyItemSheetViewModel @Inject constructor(
    private val _savedStateHandle: SavedStateHandle,
    private val _dbDao: WMDao,
    private val _firestoreDb: FirebaseFirestore
) : ViewModel() {

    val categories = _dbDao.getCategoriesAsLiveData()
    var isEdit = false
    val vocabularyItem = MutableLiveData<VocabularyItem>()

    fun initVocabularyItem() {
        viewModelScope.launch {
            val selectedVocabularyItemId = _savedStateHandle.get<Int>("selectedVocabularyItemId")
            if (selectedVocabularyItemId != null && selectedVocabularyItemId != -1) {
                isEdit = true
                vocabularyItem.value = _dbDao.getVocabularyItemById(selectedVocabularyItemId)
            } else {
                vocabularyItem.value = VocabularyItem("", "", 1)
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
                _dbDao.updateVocabularyItem(vocabularyItem.value!!)
                vocabularyItem.value!!.id
            } else {
                _dbDao.insertVocabularyItem(vocabularyItem.value!!).toInt()
            }

            CloudDbSyncHelper.updateCloudDbVocabularyItem(_dbDao, _firestoreDb, itemId)
        }
    }

    fun translate() {
        viewModelScope.launch(Dispatchers.IO) {
            val textToTranslate = vocabularyItem.value?.enWord ?: ""
            val accessToken = _dbDao.getUsers().first().accessToken

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