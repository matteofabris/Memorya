package com.memorya.framework.implementations

import androidx.work.*
import com.memorya.data.interfaces.RESTService
import com.memorya.domain.Result
import com.memorya.framework.api.ResultProvider
import com.memorya.framework.api.translate.TranslateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RESTServiceImpl @Inject constructor() : RESTService, ResultProvider() {

    override suspend fun translate(text: String): Result<String> = withContext(Dispatchers.IO) {
        Timber.i("Translate - $text")
        val translationResult = getResult {
            TranslateService.create().translate(text)
        }

        when (translationResult) {
            is Result.Error -> {
                return@withContext Result.Error<String>(
                    translationResult.code,
                    translationResult.message
                )
            }
            is Result.Loading -> return@withContext Result.Loading(null)
            is Result.Success -> {
                Timber.i("Translation is successful")
                val translatedText =
                    translationResult.data.data?.translations?.first()?.translatedText
                if (!translatedText.isNullOrEmpty()) {
                    Timber.i("Translated text - $translatedText")
                    return@withContext Result.Success(translatedText)
                }

                val errorMessage = "Translated text is null or empty"
                Timber.e(errorMessage)
                return@withContext Result.Error<String>(null, errorMessage)
            }
        }
    }
}