package com.example.wordsmemory.data.manager

import com.example.wordsmemory.data.interfaces.*
import com.example.wordsmemory.domain.Category
import com.example.wordsmemory.domain.Constants
import com.example.wordsmemory.domain.Result
import com.example.wordsmemory.domain.VocabularyItem

class VocabularyManager(
    private val _cloudDbService: CloudDbService,
    private val _vocabularyItemDataSource: VocabularyItemDataSource,
    private val _categoryDataSource: CategoryDataSource,
    private val _restService: RESTService,
    private val _userDataSource: UserDataSource
) {
    fun fetchCloudDb() = _cloudDbService.fetchCloudDb()
    suspend fun translate(text: String): String {
        when (val translationResult = _restService.translate(text)) {
            is Result.Error -> {
                if (translationResult.code == 401) {
                    val accessToken = _restService.refreshAccessToken()
                    if (!accessToken.isNullOrEmpty()) {
                        val user = _userDataSource.getUser()
                        user.accessToken = accessToken
                        _userDataSource.update(user)
                        _cloudDbService.add(Constants.CloudDbObjectType.User, user.id)

                        return this.translate(text)
                    }
                }

                return ""
            }
            is Result.Loading -> return ""
            is Result.Success -> return translationResult.data
        }
    }

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