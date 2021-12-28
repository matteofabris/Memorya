package com.memorya.framework.implementations

import com.memorya.data.interfaces.UserDataSource
import com.memorya.domain.User
import com.memorya.framework.room.dao.UserDao
import com.memorya.framework.room.entities.UserEntity
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