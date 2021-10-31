package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.UserManager
import com.example.wordsmemory.domain.User

class AddUser(private val _userManager: UserManager) {
    suspend operator fun invoke(user: User) = _userManager.add(user)
}