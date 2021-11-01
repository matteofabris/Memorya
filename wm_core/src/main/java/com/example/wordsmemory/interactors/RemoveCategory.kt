package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager
import com.example.wordsmemory.domain.Category

class RemoveCategory(private val vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(category: Category) = vocabularyManager.removeCategory(category)
}