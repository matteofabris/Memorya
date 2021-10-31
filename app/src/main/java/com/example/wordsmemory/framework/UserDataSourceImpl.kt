package com.example.wordsmemory.framework

import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.UserDataSource
import com.example.wordsmemory.domain.User
import com.example.wordsmemory.framework.room.UserDao
import com.example.wordsmemory.model.UserEntity
import com.example.wordsmemory.worker.CloudDbSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val _userDao: UserDao,
    private val _workManager: WorkManager
) : UserDataSource {
    override suspend fun add(user: User) {
        return withContext(Dispatchers.IO) {
            _userDao.insertUser(UserEntity(user))

            val workRequest: WorkRequest =
                OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                    .setInputData(workDataOf(Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.InsertUser.name))
                    .build()
            _workManager.enqueue(workRequest)
        }
    }

    override suspend fun removeAll() {
        _userDao.deleteAllUsers()
    }
}