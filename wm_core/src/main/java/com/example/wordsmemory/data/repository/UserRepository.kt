package com.example.wordsmemory.data.repository

import com.example.wordsmemory.data.interfaces.UserDataSource
import com.example.wordsmemory.domain.User

class UserRepository(private val _userDataSource: UserDataSource) {
    suspend fun add(user: User) = _userDataSource.add(user)
}