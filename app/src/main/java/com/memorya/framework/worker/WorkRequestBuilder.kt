package com.memorya.framework.worker

import androidx.work.*
import com.memorya.Constants
import java.util.concurrent.TimeUnit

object WorkRequestBuilder {
    fun build(workType: Constants.CloudDbSyncWorkType, id: Int = -1): WorkRequest {
        val workData = getWorkData(workType, id)
        return OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).setInputData(workData).build()
    }

    private fun getWorkData(workType: Constants.CloudDbSyncWorkType, id: Int = -1): Data {
        return when (workType) {
            Constants.CloudDbSyncWorkType.InsertUser, Constants.CloudDbSyncWorkType.Fetch -> {
                workDataOf(
                    Constants.WORK_TYPE to workType.name
                )
            }
            else -> {
                workDataOf(
                    Constants.WORK_TYPE to workType.name,
                    Constants.ITEM_ID to id
                )
            }
        }
    }
}