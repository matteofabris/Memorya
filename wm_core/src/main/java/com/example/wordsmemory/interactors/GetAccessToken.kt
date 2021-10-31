package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.AuthenticationManager

class GetAccessToken(private val _authenticationManager: AuthenticationManager) {
    suspend operator fun invoke(authCode: String) = _authenticationManager.getAccessToken(authCode)
}