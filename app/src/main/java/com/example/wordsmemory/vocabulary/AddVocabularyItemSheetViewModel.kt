package com.example.wordsmemory.vocabulary

import android.os.StrictMode
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.EnVocabularyDao
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.*

class AddVocabularyItemSheetViewModel(
    private val dbDao: EnVocabularyDao,
    credentials: InputStream
) : ViewModel() {

    val enText = MutableLiveData<String>()
    val itText = MutableLiveData<String>()
    private var translate: Translate? = null

    init {
        getTranslateService(credentials)
    }

    fun saveVocabularyItem() {
        viewModelScope.launch {
            dbDao.insert(
                EnVocabulary(
                    enText.value!!.toLowerCase(Locale.getDefault()), itText.value!!.toLowerCase(
                        Locale.getDefault()
                    )
                )
            )
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
                translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    fun translate() {
        if (translate == null) return

        //Get input text to be translated:
        val textToTranslate = enText.value
        val translation = translate?.translate(
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

class AddVocabularyItemSheetViewModelFactory(
    private val dataSource: EnVocabularyDao,
    private val credentials: InputStream
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVocabularyItemSheetViewModel::class.java)) {
            return AddVocabularyItemSheetViewModel(dataSource, credentials) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}