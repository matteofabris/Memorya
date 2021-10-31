package com.example.wordsmemory.framework

import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.worker.CloudDbSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoryDataSourceImpl @Inject constructor(
    private val _categoryDao: CategoryDao,
    private val _workManager: WorkManager
) : CategoryDataSource {

    override fun getCategoriesAsLiveData(): LiveData<List<CategoryEntity>> {
        return _categoryDao.getCategoriesAsLiveData()
    }

    override suspend fun getCategories(): List<Category> {
        return  _categoryDao.getCategories()
    }

    override suspend fun addCategory(category: Category, update: Boolean) {
        return withContext(Dispatchers.IO) {
            val categoryId: Int = if (update) {
                _categoryDao.updateCategory(CategoryEntity(category))
                category.id
            } else {
                _categoryDao.insertCategory(CategoryEntity(category)).toInt()
            }

            updateCloudDbCategory(categoryId)
        }
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