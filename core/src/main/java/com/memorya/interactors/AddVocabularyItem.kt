package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager
import com.memorya.domain.VocabularyItem

class AddVocabularyItem(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(vocabularyItem: VocabularyItem, update: Boolean = false) =
        _vocabularyManager.addOrUpdateVocabularyItem(vocabularyItem, update)
}