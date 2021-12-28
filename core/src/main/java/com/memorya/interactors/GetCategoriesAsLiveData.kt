package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager

class GetCategoriesAsLiveData(private val _vocabularyManager: VocabularyManager) {
    operator fun invoke() = _vocabularyManager.getCategoriesAsLiveData()
}