package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.manager.VocabularyManager

class Translate(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(text: String) = _vocabularyManager.translate(text)
}