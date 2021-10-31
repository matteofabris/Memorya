package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager
import com.example.wordsmemory.domain.Category

class AddCategory(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(category: Category, update: Boolean = false) =
        _vocabularyManager.addOrUpdateCategory(category, update)
}