package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.UserManager

class RemoveAllUsers(private val _userManager: UserManager) {
    suspend operator fun invoke() = _userManager.removeAll()
}