package com.example.wordsmemory.framework

import androidx.work.*
import com.example.wordsmemory.BuildConfig
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.framework.api.auth.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RESTServiceImpl :
    RESTService {
    override suspend fun getAccessToken(authCode: String): String {
        return withContext(Dispatchers.IO) {
            val authResult = AuthService.create().auth(
                Constants.webClientId,
                BuildConfig.CLIENT_SECRET,
                authCode
            )
            if (authResult.isSuccessful) {
                return@withContext authResult.body()?.accessToken ?: ""
            } else return@withContext ""
        }
    }
}