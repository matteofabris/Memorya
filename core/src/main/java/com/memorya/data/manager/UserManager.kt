package com.memorya.data.manager

import com.memorya.data.interfaces.CloudDbService
import com.memorya.data.interfaces.RESTService
import com.memorya.data.interfaces.UserDataSource
import com.memorya.domain.Constants
import com.memorya.domain.User

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