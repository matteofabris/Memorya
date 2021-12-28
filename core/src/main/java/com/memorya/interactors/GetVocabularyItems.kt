package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager

class GetVocabularyItems(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke() = _vocabularyManager.getVocabularyItems()
}