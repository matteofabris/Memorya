package com.memorya.interactors

import com.memorya.data.manager.UserManager
import com.memorya.domain.User

class AddUser(private val _userManager: UserManager) {
    suspend operator fun invoke(user: User) = _userManager.add(user)
}