package com.example.wordsmemory.framework.implementations

import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.framework.worker.CloudDbSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoryDataSourceImpl @Inject constructor(
    private val _categoryDao: CategoryDao,
    private val _workManager: WorkManager
) : CategoryDataSource {

    override fun getCategoriesAsLiveData() = _categoryDao.getCategoriesAsLiveData()

    override suspend fun getCategories() = _categoryDao.getCategories()

    override suspend fun addCategory(category: Category, update: Boolean) =
        withContext(Dispatchers.IO) {
            val categoryId = if (update) {
                _categoryDao.updateCategory(CategoryEntity(category))
                category.id
            } else {
                _categoryDao.insertCategory(CategoryEntity(category)).toInt()
            }

            updateCloudDbCategory(categoryId)
        }

    override suspend fun removeCategory(category: Category) {
        _categoryDao.deleteCategory(CategoryEntity(category))

        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.DeleteCategory.name,
                        Constants.ITEM_ID to category.id
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }

    private fun updateCloudDbCategory(itemId: Int) {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CloudDbSyncWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        Constants.WORK_TYPE to Constants.CloudDbSyncWorkType.InsertCategory.name,
                        Constants.ITEM_ID to itemId
                    )
                )
                .build()
        _workManager.enqueue(workRequest)
    }
}