package com.example.wordsmemory.framework.implementations

import androidx.work.*
import com.example.wordsmemory.BuildConfig
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.framework.api.auth.AuthService
import com.example.wordsmemory.framework.api.translate.TranslateService
import com.example.wordsmemory.framework.room.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RESTServiceImpl @Inject constructor(
    private val _userDao: UserDao
) :
    RESTService {
    override suspend fun getAccessToken(authCode: String) = withContext(Dispatchers.IO) {
        val authResult = AuthService.create().auth(
            Constants.webClientId,
            BuildConfig.CLIENT_SECRET,
            authCode
        )
        if (authResult.isSuccessful) {
            Timber.d("Access token request is successful")
            return@withContext authResult.body()?.accessToken ?: ""
        } else {
            Timber.d("ERROR! CODE: " + authResult.code() + ", MESSAGE: " + authResult.message())
            return@withContext ""
        }
    }

    override suspend fun translate(text: String) = withContext(Dispatchers.IO) {
        val accessToken = _userDao.getUsers().first().accessToken
        val response = TranslateService.create()
            .translate("Bearer $accessToken", text)

        if (response.isSuccessful) {
            Timber.d("Translation is successful")
            val translatedText =
                response.body()?.data?.translations?.first()?.translatedText ?: ""
            if (translatedText.isNotEmpty()) {
                Timber.d("Translated text - $translatedText")
                return@withContext translatedText
            }
        } else {
            Timber.d("ERROR! CODE: " + response.code() + ", MESSAGE: " + response.message())
        }

        return@withContext ""
    }
}