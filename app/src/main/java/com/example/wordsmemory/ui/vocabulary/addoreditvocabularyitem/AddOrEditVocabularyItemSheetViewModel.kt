package com.example.wordsmemory.ui.vocabulary.addoreditvocabularyitem

import android.os.StrictMode
import android.util.Log
import androidx.lifecycle.*
import com.example.wordsmemory.Constants
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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddOrEditVocabularyItemSheetViewModel @Inject constructor(
    private val _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao,
    _credentials: InputStream
) : ViewModel() {

    private var _translate = getTranslateService(_credentials)

    val enText = MutableLiveData<String>()
    val itText = MutableLiveData<String>()
    val categories = _dbDao.getCategoriesAsLiveData()
    var category = Constants.defaultCategory

    private val _vocabularyItem = MutableLiveData<VocabularyItem>()
    val vocabularyItem: LiveData<VocabularyItem>
        get() = _vocabularyItem

    fun getVocabularyItem() {
        viewModelScope.launch {
            val selectedVocabularyItemId = _savedStateHandle.get<Int>("selectedVocabularyItemId")
            if (selectedVocabularyItemId != null && selectedVocabularyItemId != -1) {
                _vocabularyItem.value = _dbDao.getVocabularyItemById(selectedVocabularyItemId)
                enText.value = _vocabularyItem.value?.enWord
                itText.value = _vocabularyItem.value?.itWord
            }
        }
    }

    fun insertOrUpdateVocabularyItem() {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryId = _dbDao.getCategoryId(category)
            Log.i(
                "save_item",
                "Save vocabulary item: en - ${enText.value}, it - ${itText.value}, category - $categoryId"
            )

            if (_vocabularyItem.value != null) {
                _vocabularyItem.value!!.enWord = enText.value!!.lowercase(Locale.getDefault())
                _vocabularyItem.value!!.itWord = itText.value!!.lowercase(Locale.getDefault())
                _vocabularyItem.value!!.category = categoryId
                _dbDao.updateVocabularyItem(_vocabularyItem.value!!)
            } else {
                val itemToInsert = VocabularyItem(
                    enText.value!!.lowercase(Locale.getDefault()),
                    itText.value!!.lowercase(Locale.getDefault()),
                    categoryId
                )
                _dbDao.insertVocabularyItem(itemToInsert)
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
        val textToTranslate = enText.value
        val translation = _translate?.translate(
            textToTranslate,
            Translate.TranslateOption.targetLanguage("it"),
            Translate.TranslateOption.model("nmt")
        )

        if (translation != null) {
            val translatedText = translation.translatedText
            itText.value = translatedText
            //Translated text and original text are set to TextViews:
            Log.d("Translation", "TEO: $translatedText")
        }
    }
}