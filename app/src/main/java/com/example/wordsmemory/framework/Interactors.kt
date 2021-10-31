package com.example.wordsmemory.framework

import com.example.wordsmemory.interactors.*

data class Interactors(
    val fetchCloudDb: FetchCloudDb,
    val addUser: AddUser,
    val removeAllUsers: RemoveAllUsers,
    val getVocabularyItems: GetVocabularyItems,
    val getCategoriesAsLiveData: GetCategoriesAsLiveData,
    val getCategories: GetCategories,
    val getAccessToken: GetAccessToken,
    val addCategory: AddCategory
)