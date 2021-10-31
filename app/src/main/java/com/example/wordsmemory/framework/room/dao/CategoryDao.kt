package com.example.wordsmemory.framework.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordsmemory.framework.room.entities.CategoryEntity

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(categoryEntity: CategoryEntity): Long

    @Update
    suspend fun updateCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM category ORDER BY category")
    fun getCategoriesAsLiveData(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category ORDER BY category")
    suspend fun getCategories(): List<CategoryEntity>

    @Query("SELECT category FROM category WHERE id == :id")
    suspend fun getCategoryName(id: Int): String

    @Query("SELECT category FROM category WHERE id == :id")
    fun getCategoryNameAsLiveData(id: Int): LiveData<String>

    @Query("SELECT * FROM category WHERE id == :id")
    suspend fun getCategoryById(id: Int): CategoryEntity
}