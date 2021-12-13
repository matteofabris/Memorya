package com.example.wordsmemory.framework.implementations

import com.example.wordsmemory.data.interfaces.UserDataSource
import com.example.wordsmemory.domain.User
import com.example.wordsmemory.framework.room.dao.UserDao
import com.example.wordsmemory.framework.room.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val _userDao: UserDao
) : UserDataSource {
    override suspend fun add(user: User) {
        return withContext(Dispatchers.IO) {
            _userDao.insertUser(UserEntity(user))
        }
    }

    override suspend fun update(user: User) {
        return withContext(Dispatchers.IO) {
            _userDao.updateUser(UserEntity(user))
        }
    }

    override suspend fun getUser(): User {
        return withContext(Dispatchers.IO) {
            _userDao.getUsers().first()
        }
    }

    override suspend fun removeAll() = _userDao.deleteAllUsers()
}