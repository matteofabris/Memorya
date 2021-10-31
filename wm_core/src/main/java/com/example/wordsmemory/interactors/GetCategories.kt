package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager

class GetCategories(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke() = _vocabularyManager.getCategories()
}