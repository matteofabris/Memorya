package com.example.wordsmemory.vocabulary.addoreditvocabularyitem

import android.os.StrictMode
import android.util.Log
import androidx.lifecycle.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.VocabularyItem
import com.example.wordsmemory.VocabularyDao
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.*

class AddOrEditVocabularyItemSheetViewModel(
    private val _dbDao: VocabularyDao,
    credentials: InputStream,
    private val _selectedVocabularyItemId: Int? = null
) : ViewModel() {

    private var _translate: Translate? = null
    private lateinit var _vocabularyItem: VocabularyItem

    val addButtonText = MutableLiveData("Add")
    val enText = MutableLiveData<String>()
    val itText = MutableLiveData<String>()
    val categories = _dbDao.getCategoriesAsLiveData()
    var selectedCategoryId = MutableLiveData(1)
    var category = Constants.defaultCategory

    init {
        viewModelScope.launch {
            getTranslateService(credentials)

            if (_selectedVocabularyItemId != null) {
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

            if (_selectedVocabularyItemId != null) {
                _vocabularyItem.enWord = enText.value!!.toLowerCase(Locale.getDefault())
                _vocabularyItem.itWord = itText.value!!.toLowerCase(Locale.getDefault())
                _vocabularyItem.category = categoryId
                _dbDao.updateVocabularyItem(_vocabularyItem)
            } else {
                val itemToInsert = VocabularyItem(
                    enText.value!!.toLowerCase(Locale.getDefault()),
                    itText.value!!.toLowerCase(Locale.getDefault()),
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
        if (_selectedVocabularyItemId != null) {
            selectedCategoryId.value = _vocabularyItem.category
        }
    }
}

class AddVocabularyItemSheetViewModelFactory(
    private val _dataSource: VocabularyDao,
    private val _credentials: InputStream,
    private val _vocabularyItemId: Int? = null
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddOrEditVocabularyItemSheetViewModel::class.java)) {
            return AddOrEditVocabularyItemSheetViewModel(
                _dataSource,
                _credentials,
                _vocabularyItemId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}