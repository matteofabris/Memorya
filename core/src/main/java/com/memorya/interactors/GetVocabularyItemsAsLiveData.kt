package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager

class GetVocabularyItemsAsLiveData(private val _vocabularyManager: VocabularyManager) {
    operator fun invoke() = _vocabularyManager.getVocabularyItemsAsLiveData()
}