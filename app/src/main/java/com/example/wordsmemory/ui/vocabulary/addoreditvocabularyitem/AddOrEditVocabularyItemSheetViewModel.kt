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
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao,
    private val _credentials: InputStream
) : ViewModel() {

    private var _translate: Translate? = null
    private lateinit var _vocabularyItem: VocabularyItem
    private val _selectedVocabularyItemId: Int =
        _savedStateHandle.get<Int>("selectedVocabularyItemId")!!

    val addButtonText = MutableLiveData("Add")
    val enText = MutableLiveData<String>()
    val itText = MutableLiveData<String>()
    val categories = _dbDao.getCategoriesAsLiveData()
    var selectedCategoryId = MutableLiveData(1)
    var category = Constants.defaultCategory

    init {
        viewModelScope.launch {
            getTranslateService(_credentials)

            if (_selectedVocabularyItemId != -1) {
                _vocabularyItem = _dbDao.getVocabularyItemById(_selectedVocabularyItemId)
                enText.value = _vocabularyItem.enWord
                itText.value = _vocabularyItem.itWord
                addButtonText.value = "Update"
            }
        }
    }

    fun insertOrUpdateVocabularyItem() {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryId = _dbDao.getCategoryId(category)
            Log.i(
                "save_item",
                "Save vocabulary item: en - ${enText.value}, it - ${itText.value}, cat - $categoryId"
            )

            if (_selectedVocabularyItemId != -1) {
                _vocabularyItem.enWord = enText.value!!.lowercase(Locale.getDefault())
                _vocabularyItem.itWord = itText.value!!.lowercase(Locale.getDefault())
                _vocabularyItem.category = categoryId
                _dbDao.updateVocabularyItem(_vocabularyItem)
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

    private fun getTranslateService(credentials: InputStream) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            credentials.use { `is` ->
                //Get credentials:
                val myCredentials = GoogleCredentials.fromStream(`is`)

                //Set credentials and get translate service:
                val translateOptions =
                    TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                _translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
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

    fun setCategory() {
        if (_selectedVocabularyItemId != -1) {
            selectedCategoryId.value = _vocabularyItem.category
        }
    }
}