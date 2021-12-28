package com.memorya.interactors

import com.memorya.data.manager.UserManager

class RemoveAllUsers(private val _userManager: UserManager) {
    suspend operator fun invoke() = _userManager.removeAll()
}