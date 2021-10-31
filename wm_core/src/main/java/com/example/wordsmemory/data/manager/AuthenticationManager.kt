package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.RESTService

class AuthenticationManager(private val restService: RESTService) {
    suspend fun getAccessToken(authCode: String) = restService.getAccessToken(authCode)
}