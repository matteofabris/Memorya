package com.example.wordsmemory.framework.implementations

import androidx.work.*
import com.example.wordsmemory.BuildConfig
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.domain.AuthTokens
import com.example.wordsmemory.domain.Result
import com.example.wordsmemory.framework.api.ResultProvider
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
    RESTService, ResultProvider() {
    override suspend fun getAuthTokens(authCode: String) = withContext(Dispatchers.IO) {
        val authResult = getResult {
            AuthService.create().auth(
                Constants.webClientId,
                BuildConfig.CLIENT_SECRET,
                authCode
            )
        }

        when (authResult) {
            is Result.Error -> return@withContext null
            is Result.Loading -> return@withContext null
            is Result.Success -> {
                Timber.i("Access token request is successful")
                val response = authResult.data
                return@withContext AuthTokens(response.accessToken, response.refreshToken)
            }
        }
    }

    override suspend fun translate(text: String): Result<String> = withContext(Dispatchers.IO) {
        Timber.i("Translate - $text")
        val accessToken = _userDao.getUsers().first().accessToken
        val translationResult = getResult {
            TranslateService.create().translate("Bearer $accessToken", text)
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

    override suspend fun refreshAccessToken() = withContext(Dispatchers.IO) {
        val user = _userDao.getUsers().first()
        val refreshToken = user.refreshToken

        Timber.i("Access token refresh. Refresh token - $refreshToken")

        val refreshTokenResult = getResult {
            AuthService.create().refresh(
                Constants.webClientId,
                BuildConfig.CLIENT_SECRET,
                refreshToken = refreshToken
            )
        }

        when (refreshTokenResult) {
            is Result.Error -> return@withContext null
            is Result.Loading -> return@withContext null
            is Result.Success -> {
                Timber.i("Access token refresh is successful")
                return@withContext refreshTokenResult.data.accessToken
            }
        }
    }
}