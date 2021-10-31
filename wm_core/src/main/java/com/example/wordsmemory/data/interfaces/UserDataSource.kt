package com.example.wordsmemory.data.interfaces

import com.example.wordsmemory.domain.User

interface UserDataSource {
    suspend fun add(user: User)
}