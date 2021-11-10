package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.UserManager

class GetAccessToken(private val _userManager: UserManager) {
    suspend operator fun invoke(authCode: String) = _userManager.getAccessToken(authCode)
}