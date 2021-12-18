package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager

class GetNotEmptyCategoriesAsLiveData(private val _vocabularyManager: VocabularyManager) {
    operator fun invoke() = _vocabularyManager.getNotEmptyCategoriesAsLiveData()
}