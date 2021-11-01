package com.example.wordsmemory.framework.implementations

import android.util.Log
import androidx.work.*
import com.example.wordsmemory.BuildConfig
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.framework.api.auth.AuthService
import com.example.wordsmemory.framework.api.translate.TranslateService
import com.example.wordsmemory.framework.room.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            return@withContext authResult.body()?.accessToken ?: ""
        } else return@withContext ""
    }

    override suspend fun translate(text: String) = withContext(Dispatchers.IO) {
        val accessToken = _userDao.getUsers().first().accessToken
        val response = TranslateService.create()
            .translate("Bearer $accessToken", text)

        if (response.isSuccessful) {
            Log.d(Constants.packageName, "Translation is successful")
            val translatedText =
                response.body()?.data?.translations?.first()?.translatedText ?: ""
            if (translatedText.isNotEmpty()) {
                Log.d(Constants.packageName, "Translated text - $translatedText")
                return@withContext translatedText
            }
        } else {
            Log.d(Constants.packageName, "ERROR: ${response.errorBody()}")
        }

        return@withContext ""
    }
}