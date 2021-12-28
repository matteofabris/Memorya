package com.memorya.data.interfaces

import com.memorya.domain.User

interface UserDataSource {
    suspend fun add(user: User)
    suspend fun update(user: User)
    suspend fun getUser(): User
    suspend fun removeAll()
}