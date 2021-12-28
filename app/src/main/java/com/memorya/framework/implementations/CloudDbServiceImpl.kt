package com.memorya.framework.implementations

import androidx.work.WorkManager
import com.memorya.Constants
import com.memorya.data.interfaces.CloudDbService
import com.memorya.domain.Constants.CloudDbObjectType
import com.memorya.domain.Constants.CloudDbObjectType.*
import com.memorya.framework.worker.WorkRequestBuilder
import javax.inject.Inject

class CloudDbServiceImpl @Inject constructor(private val _workManager: WorkManager) :
    CloudDbService {
    override fun fetchCloudDb() {
        _workManager.enqueue(WorkRequestBuilder.build(Constants.CloudDbSyncWorkType.Fetch))
    }

    override fun add(type: CloudDbObjectType, id: Int) {
        val workType = when (type) {
            User -> Constants.CloudDbSyncWorkType.InsertUser
            VocabularyItem -> Constants.CloudDbSyncWorkType.InsertVocabularyItem
            Category -> Constants.CloudDbSyncWorkType.InsertCategory
        }

        _workManager.enqueue(WorkRequestBuilder.build(workType, id))
    }

    override fun remove(type: CloudDbObjectType, id: Int) {
        val workType = when (type) {
            VocabularyItem -> Constants.CloudDbSyncWorkType.DeleteVocabularyItem
            Category -> Constants.CloudDbSyncWorkType.DeleteCategory
            User -> return
        }

        _workManager.enqueue(WorkRequestBuilder.build(workType, id))
    }
}