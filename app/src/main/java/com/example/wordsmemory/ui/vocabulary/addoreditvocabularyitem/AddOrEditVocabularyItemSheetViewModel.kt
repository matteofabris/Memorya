package com.example.wordsmemory.ui.vocabulary.addoreditvocabularyitem

import android.os.StrictMode
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.VocabularyItem
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class AddOrEditVocabularyItemSheetViewModel @Inject constructor(
    private val _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao,
    _credentials: InputStream
) : ViewModel() {

    private var _translate = getTranslateService(_credentials)

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

    fun insertOrUpdateVocabularyItem() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(
                "save_item",
                "Save vocabulary item: en - ${vocabularyItem.value?.enWord}, " +
                        "it - ${vocabularyItem.value?.itWord}, " +
                        "category - ${vocabularyItem.value?.category}"
            )

            if (isEdit) {
                _dbDao.updateVocabularyItem(vocabularyItem.value!!)
            } else {
                _dbDao.insertVocabularyItem(vocabularyItem.value!!)
            }
        }
    }

    private fun getTranslateService(credentials: InputStream): Translate? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            credentials.use { `is` ->
                //Get credentials:
                val myCredentials = GoogleCredentials.fromStream(`is`)

                //Set credentials and get translate service:
                val translateOptions =
                    TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                return translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

        return null
    }

    fun translate() {
        if (_translate == null) return

        //Get input text to be translated:
        val textToTranslate = vocabularyItem.value?.enWord
        val translation = _translate?.translate(
            textToTranslate,
            Translate.TranslateOption.targetLanguage("it"),
            Translate.TranslateOption.model("nmt")
        )

        if (translation != null) {
            val translatedText = translation.translatedText
            vocabularyItem.value?.itWord = translatedText
            //Translated text and original text are set to TextViews:
            Log.d("Translation", "TEO: $translatedText")
        }
    }
}