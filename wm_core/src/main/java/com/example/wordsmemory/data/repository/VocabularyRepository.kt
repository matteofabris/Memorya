package com.example.wordsmemory.data.repository

import com.example.wordsmemory.data.interfaces.CloudDbService

class VocabularyRepository(private val _cloudDbService: CloudDbService) {
    fun fetchCloudDb() = _cloudDbService.fetchCloudDb()
}