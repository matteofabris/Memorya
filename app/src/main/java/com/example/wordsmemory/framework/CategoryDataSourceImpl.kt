package com.example.wordsmemory.framework

import androidx.lifecycle.LiveData
import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.framework.room.dao.CategoryDao
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import javax.inject.Inject

class CategoryDataSourceImpl @Inject constructor(
    private val _categoryDao: CategoryDao
) : CategoryDataSource {

    override fun getCategories(): LiveData<List<CategoryEntity>> {
        return _categoryDao.getCategories()
    }
}