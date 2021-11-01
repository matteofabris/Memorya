package com.example.wordsmemory.data.interfaces

interface RESTService {
    suspend fun getAccessToken(authCode: String): String
    suspend fun translate(text: String): String
}