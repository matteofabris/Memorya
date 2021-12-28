package com.memorya.interactors

import com.memorya.data.manager.VocabularyManager

class Translate(private val _vocabularyManager: VocabularyManager) {
    suspend operator fun invoke(text: String) = _vocabularyManager.translate(text)
}