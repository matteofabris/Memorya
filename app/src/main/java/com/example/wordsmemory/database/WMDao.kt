package com.example.wordsmemory.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordsmemory.model.Category
import com.example.wordsmemory.model.User
import com.example.wordsmemory.model.VocabularyItem

@Dao
interface WMDao {
    @Insert
    suspend fun insertVocabularyItem(item: VocabularyItem)

    @Insert
    suspend fun insertCategory(category: Category)

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateVocabularyItem(item: VocabularyItem)

    @Update
    suspend fun updateCategory(category: Category)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteVocabularyItem(item: VocabularyItem)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM user")
    fun deleteAllUsers(): Unit

    @Query("SELECT * FROM user")
    fun getUsers(): List<User>

    @Query("SELECT * FROM vocabulary_item")
    fun getVocabularyItemsAsLiveData(): LiveData<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary_item ORDER BY en_word")
    suspend fun getVocabularyItems(): List<VocabularyItem>

    @Query("SELECT * FROM vocabulary_item WHERE id == :id")
    suspend fun getVocabularyItemById(id: Int): VocabularyItem

    @Query("SELECT * FROM vocabulary_item WHERE category == :categoryId")
    fun getVocabularyItemsByCategoryAsLiveData(categoryId: Int): LiveData<List<VocabularyItem>>

    @Query("SELECT * FROM vocabulary_item WHERE category == :categoryId ORDER BY en_word")
    suspend fun getVocabularyItemsByCategory(categoryId: Int): List<VocabularyItem>

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