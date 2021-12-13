package com.example.wordsmemory.data.interfaces

import com.example.wordsmemory.domain.AuthTokens
import com.example.wordsmemory.domain.Result

interface RESTService {
    suspend fun getAuthTokens(authCode: String): AuthTokens?
    suspend fun refreshAccessToken(): String?
    suspend fun translate(text: String): Result<String>
}