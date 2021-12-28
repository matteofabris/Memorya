package com.memorya.framework

import com.memorya.interactors.*

data class Interactors(
    val fetchCloudDb: FetchCloudDb,
    val addUser: AddUser,
    val removeAllUsers: RemoveAllUsers,
    val getVocabularyItems: GetVocabularyItems,
    val getVocabularyItemsAsLiveData: GetVocabularyItemsAsLiveData,
    val getCategoriesAsLiveData: GetCategoriesAsLiveData,
    val getNotEmptyCategoriesAsLiveData: GetNotEmptyCategoriesAsLiveData,
    val getCategories: GetCategories,
    val getAuthTokens: GetAuthTokens,
    val addCategory: AddCategory,
    val addVocabularyItem: AddVocabularyItem,
    val translate: Translate,
    val removeCategory: RemoveCategory,
    val removeVocabularyItem: RemoveVocabularyItem
)