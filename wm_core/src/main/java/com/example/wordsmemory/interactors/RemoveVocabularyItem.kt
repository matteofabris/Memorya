package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager
import com.example.wordsmemory.domain.VocabularyItem

class RemoveVocabularyItem(private val vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(vocabularyItem: VocabularyItem) =
        vocabularyManager.removeVocabularyItem(vocabularyItem)
}