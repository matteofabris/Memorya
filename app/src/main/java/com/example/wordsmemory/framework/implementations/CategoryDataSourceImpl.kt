package com.example.wordsmemory.framework.implementations

import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryDataSourceImpl @Inject constructor(
    private val _categoryDao: CategoryDao
) : CategoryDataSource {

    override fun getCategoriesAsLiveData() = _categoryDao.getCategoriesAsLiveData()

    override fun getNotEmptyCategoriesAsLiveData() = _categoryDao.getNotEmptyCategoriesAsLiveData()

    override suspend fun getCategories() = _categoryDao.getCategories()

    override suspend fun addCategory(category: Category, update: Boolean) =
        withContext(Dispatchers.IO) {
            val categoryId = if (update) {
                _categoryDao.updateCategory(CategoryEntity(category))
                category.id
            } else {
                _categoryDao.insertCategory(CategoryEntity(category)).toInt()
            }

            return@withContext categoryId
        }

    override suspend fun removeCategory(category: Category) =
        _categoryDao.deleteCategory(CategoryEntity(category))
}