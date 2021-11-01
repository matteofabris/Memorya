package com.example.wordsmemory.data.interfaces

import com.example.wordsmemory.domain.VocabularyItem

interface VocabularyItemDataSource {
    suspend fun getVocabularyItems(): List<VocabularyItem>
    fun getVocabularyItemsAsLiveData(): Any
    suspend fun addVocabularyItem(vocabularyItem: VocabularyItem, update: Boolean = false)
    suspend fun removeVocabularyItem(vocabularyItem: VocabularyItem)
}