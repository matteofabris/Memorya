package com.example.wordsmemory.framework.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity

@Dao
interface VocabularyItemDao {
    @Insert
    suspend fun insertVocabularyItem(itemEntity: VocabularyItemEntity): Long

    @Update
    suspend fun updateVocabularyItem(itemEntity: VocabularyItemEntity)

    @Delete
    suspend fun deleteVocabularyItem(itemEntity: VocabularyItemEntity)

    @Query("SELECT * FROM vocabulary_item")
    fun getVocabularyItems(): LiveData<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_item WHERE id == :id")
    suspend fun getVocabularyItemById(id: Int): VocabularyItemEntity

    @Query("SELECT * FROM vocabulary_item WHERE category == :categoryId")
    fun getVocabularyItemsByCategoryAsLiveData(categoryId: Int): LiveData<List<VocabularyItemEntity>>
}