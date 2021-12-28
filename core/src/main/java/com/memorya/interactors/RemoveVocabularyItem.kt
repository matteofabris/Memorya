package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager
import com.memorya.domain.VocabularyItem

class RemoveVocabularyItem(private val vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(vocabularyItem: VocabularyItem) =
        vocabularyManager.removeVocabularyItem(vocabularyItem)
}