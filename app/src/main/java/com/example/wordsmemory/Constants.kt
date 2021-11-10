package com.example.wordsmemory

object Constants {
    const val packageName = "wordsmemory"
    const val defaultCategory = "None"
    const val defaultCategoryId = 1
    const val translateBaseUrl = "https://translation.googleapis.com/"
    const val authBaseUrl = "https://oauth2.googleapis.com/"
    const val webClientId = "139830564645-8uo7v2isus2djk2jcqmkbgd0qeig3orm.apps.googleusercontent.com"
    const val users = "users"
    const val vocabularyItems = "vocabulary_items"
    const val categories = "categories"
    const val WORK_TYPE = "WORK_TYPE"
    const val ITEM_ID = "ITEM_ID"

    enum class CloudDbSyncWorkType {
        InsertUser, Fetch, InsertVocabularyItem, InsertCategory, DeleteVocabularyItem, DeleteCategory
    }
}