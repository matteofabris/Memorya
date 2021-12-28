package com.memorya.data.interfaces

import com.memorya.domain.AuthTokens
import com.memorya.domain.Result

interface RESTService {
    suspend fun getAuthTokens(authCode: String): AuthTokens?
    suspend fun refreshAccessToken(): String?
    suspend fun translate(text: String): Result<String>
}