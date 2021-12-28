package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager
import com.memorya.domain.Category

class RemoveCategory(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(category: Category) = _vocabularyManager.removeCategory(category)
}