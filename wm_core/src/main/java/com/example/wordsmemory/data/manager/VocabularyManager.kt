package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.data.interfaces.VocabularyItemDataSource
import com.example.wordsmemory.domain.Category

class VocabularyManager(
    private val _cloudDbService: CloudDbService,
    private val _vocabularyItemDataSource: VocabularyItemDataSource,
    private val _categoryDataSource: CategoryDataSource
) {
    fun fetchCloudDb() = _cloudDbService.fetchCloudDb()
    fun getVocabularyItems() = _vocabularyItemDataSource.getVocabularyItems()
    fun getCategoriesAsLiveData() = _categoryDataSource.getCategoriesAsLiveData()
    suspend fun getCategories() = _categoryDataSource.getCategories()
    suspend fun addOrUpdateCategory(category: Category, update: Boolean = false) =
        _categoryDataSource.addCategory(category, update)
}