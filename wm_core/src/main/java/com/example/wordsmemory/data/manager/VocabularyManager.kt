package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.CategoryDataSource
import com.example.wordsmemory.data.interfaces.CloudDbService
import com.example.wordsmemory.data.interfaces.RESTService
import com.example.wordsmemory.data.interfaces.VocabularyItemDataSource
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.domain.Constants
import com.example.wordsmemory.domain.VocabularyItem

class VocabularyManager(
    private val _cloudDbService: CloudDbService,
    private val _vocabularyItemDataSource: VocabularyItemDataSource,
    private val _categoryDataSource: CategoryDataSource,
    private val _restService: RESTService
) {
    fun fetchCloudDb() = _cloudDbService.fetchCloudDb()
    suspend fun translate(text: String) = _restService.translate(text)

    // Category
    suspend fun getCategories() = _categoryDataSource.getCategories()
    fun getCategoriesAsLiveData() = _categoryDataSource.getCategoriesAsLiveData()
    suspend fun addOrUpdateCategory(category: Category, update: Boolean = false) {
        val id = _categoryDataSource.addCategory(category, update)
        _cloudDbService.add(Constants.CloudDbObjectType.Category, id)
    }

    suspend fun removeCategory(category: Category) {
        _categoryDataSource.removeCategory(category)
        _cloudDbService.remove(Constants.CloudDbObjectType.Category, category.id)
    }


    // VocabularyItem
    suspend fun getVocabularyItems() = _vocabularyItemDataSource.getVocabularyItems()
    fun getVocabularyItemsAsLiveData() = _vocabularyItemDataSource.getVocabularyItemsAsLiveData()
    suspend fun addOrUpdateVocabularyItem(vocabularyItem: VocabularyItem, update: Boolean = false) {
        val id = _vocabularyItemDataSource.addVocabularyItem(vocabularyItem, update)
        _cloudDbService.add(Constants.CloudDbObjectType.VocabularyItem, id)
    }

    suspend fun removeVocabularyItem(vocabularyItem: VocabularyItem) {
        _vocabularyItemDataSource.removeVocabularyItem(vocabularyItem)
        _cloudDbService.remove(Constants.CloudDbObjectType.VocabularyItem, vocabularyItem.id)
    }
}