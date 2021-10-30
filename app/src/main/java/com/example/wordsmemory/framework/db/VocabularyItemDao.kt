package com.example.wordsmemory.framework.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordsmemory.model.vocabulary.VocabularyItem

@Dao
interface VocabularyItemDao {
    @Insert
    suspend fun insertVocabularyItem(item: VocabularyItem): Long

    @Update
    suspend fun updateVocabularyItem(item: VocabularyItem)

    @Delete
    suspend fun deleteVocabularyItem(item: VocabularyItem)

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
}