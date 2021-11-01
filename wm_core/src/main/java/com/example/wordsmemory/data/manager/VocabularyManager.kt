package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.data.interfaces.VocabularyItemDataSource
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.domain.VocabularyItem

class VocabularyManager(
    private val _cloudDbService: CloudDbService,
    private val _vocabularyItemDataSource: VocabularyItemDataSource,
    private val _categoryDataSource: CategoryDataSource,
    private val _restService: RESTService
) {
    fun fetchCloudDb() = _cloudDbService.fetchCloudDb()

    suspend fun getCategories() = _categoryDataSource.getCategories()
    fun getCategoriesAsLiveData() = _categoryDataSource.getCategoriesAsLiveData()
    suspend fun addOrUpdateCategory(category: Category, update: Boolean = false) =
        _categoryDataSource.addCategory(category, update)

    suspend fun removeCategory(category: Category) = _categoryDataSource.removeCategory(category)

    suspend fun getVocabularyItems() = _vocabularyItemDataSource.getVocabularyItems()
    fun getVocabularyItemsAsLiveData() = _vocabularyItemDataSource.getVocabularyItemsAsLiveData()
    suspend fun addOrUpdateVocabularyItem(vocabularyItem: VocabularyItem, update: Boolean = false) =
        _vocabularyItemDataSource.addVocabularyItem(vocabularyItem, update)

    suspend fun removeVocabularyItem(vocabularyItem: VocabularyItem) =
        _vocabularyItemDataSource.removeVocabularyItem(vocabularyItem)

    suspend fun translate(text: String) = _restService.translate(text)
}