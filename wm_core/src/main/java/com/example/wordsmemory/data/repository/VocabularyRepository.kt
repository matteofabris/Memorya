package com.example.wordsmemory.data.repository

import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.data.interfaces.VocabularyItemDataSource

class VocabularyRepository(
    private val _cloudDbService: CloudDbService,
    private val _vocabularyItemDataSource: VocabularyItemDataSource,
    private val _categoryDataSource: CategoryDataSource
) {
    fun fetchCloudDb() = _cloudDbService.fetchCloudDb()
    fun getVocabularyItems(): Any = _vocabularyItemDataSource.getVocabularyItems()
    fun getCategories(): Any = _categoryDataSource.getCategories()
}