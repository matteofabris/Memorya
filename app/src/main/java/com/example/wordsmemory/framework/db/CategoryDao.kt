package com.example.wordsmemory.framework.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordsmemory.model.vocabulary.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY category")
    fun getCategoriesAsLiveData(): LiveData<List<Category>>

    @Query("SELECT * FROM category")
    suspend fun getCategories(): List<Category>

    @Query("SELECT id FROM category WHERE category == :category")
    suspend fun getCategoryId(category: String): Int

    @Query("SELECT category FROM category WHERE id == :id")
    suspend fun getCategoryName(id: Int): String

    @Query("SELECT category FROM category WHERE id == :id")
    fun getCategoryNameAsLiveData(id: Int): LiveData<String>

    @Query("SELECT * FROM category WHERE id == :id")
    suspend fun getCategoryById(id: Int): Category
}