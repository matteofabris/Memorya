package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager
import com.memorya.domain.Category

class AddCategory(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(category: Category, update: Boolean = false) =
        _vocabularyManager.addOrUpdateCategory(category, update)
}