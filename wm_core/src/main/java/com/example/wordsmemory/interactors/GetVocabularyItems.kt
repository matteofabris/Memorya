package com.example.wordsmemory.interactors

import com.example.wordsmemory.data.repository.VocabularyRepository

class GetVocabularyItems(private val _vocabularyRepository: VocabularyRepository) {
    operator fun invoke() = _vocabularyRepository.getVocabularyItems()
}