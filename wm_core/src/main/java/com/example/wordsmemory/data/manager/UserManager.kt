package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.data.interfaces.UserDataSource
import com.example.wordsmemory.domain.Constants
import com.example.wordsmemory.domain.User

class UserManager(
    private val _userDataSource: UserDataSource,
    private val _restService: RESTService,
    private val _cloudDbService: CloudDbService
) {
    suspend fun add(user: User) {
        _userDataSource.add(user)
        _cloudDbService.add(Constants.CloudDbObjectType.User, user.id)
    }
    suspend fun removeAll() = _userDataSource.removeAll()
    suspend fun getAuthTokens(authCode: String) = _restService.getAuthTokens(authCode)
}