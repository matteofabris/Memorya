package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.UserDataSource
import com.example.wordsmemory.domain.User

class UserManager(private val _userDataSource: UserDataSource) {
    suspend fun add(user: User) = _userDataSource.add(user)
    suspend fun removeAll() = _userDataSource.removeAll()
}