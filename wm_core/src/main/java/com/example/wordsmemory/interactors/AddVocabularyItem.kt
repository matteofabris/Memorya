package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager
import com.example.wordsmemory.domain.VocabularyItem

class AddVocabularyItem(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(vocabularyItem: VocabularyItem, update: Boolean = false) =
        _vocabularyManager.addOrUpdateVocabularyItem(vocabularyItem, update)
}