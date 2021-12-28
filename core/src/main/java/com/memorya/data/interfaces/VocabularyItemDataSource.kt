package com.memorya.data.interfaces

import com.memorya.domain.VocabularyItem

interface VocabularyItemDataSource {
    suspend fun getVocabularyItems(): List<VocabularyItem>
    fun getVocabularyItemsAsLiveData(): Any
    suspend fun addVocabularyItem(vocabularyItem: VocabularyItem, update: Boolean = false): Int
    suspend fun removeVocabularyItem(vocabularyItem: VocabularyItem)
}