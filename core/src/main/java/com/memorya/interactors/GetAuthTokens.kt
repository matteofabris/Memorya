package com.memorya.interactors

import com.memorya.data.manager.UserManager

class GetAuthTokens(private val _userManager: UserManager) {
    suspend operator fun invoke(authCode: String) = _userManager.getAuthTokens(authCode)
}