package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager

class GetNotEmptyCategoriesAsLiveData(private val _vocabularyManager: VocabularyManager) {
    operator fun invoke() = _vocabularyManager.getNotEmptyCategoriesAsLiveData()
}