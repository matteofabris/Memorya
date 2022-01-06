package com.memorya

object Constants {
    const val appName = "Memorya"
    const val defaultCategory = "None"
    const val defaultCategoryId = 1
    const val translateBaseUrl = "https://translation.googleapis.com/"
    const val users = "users"
    const val vocabularyItems = "vocabulary_items"
    const val categories = "categories"
    const val WORK_TYPE = "WORK_TYPE"
    const val ITEM_ID = "ITEM_ID"

    enum class CloudDbSyncWorkType {
        InsertUser, Fetch, InsertVocabularyItem, InsertCategory, DeleteVocabularyItem, DeleteCategory
    }
}