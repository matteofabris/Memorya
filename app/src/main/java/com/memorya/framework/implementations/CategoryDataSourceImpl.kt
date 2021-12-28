package com.memorya.framework.implementations

import com.memorya.data.interfaces.CategoryDataSource
import com.memorya.domain.Category
import com.memorya.framework.room.dao.CategoryDao
import com.memorya.framework.room.entities.CategoryEntity
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