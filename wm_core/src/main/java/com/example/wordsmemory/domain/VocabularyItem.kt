package com.example.wordsmemory.domain

interface VocabularyItem : IItem {
    var enWord: String
    var itWord: String
    var category: Int
}