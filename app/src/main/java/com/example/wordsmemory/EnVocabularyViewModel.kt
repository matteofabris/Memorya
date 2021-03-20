package com.example.wordsmemory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class EnVocabularyViewModel(private val dbDao: EnVocabularyDao) : ViewModel() {

    var vocabularyList = dbDao.getAll()

    fun onClicked() {
        val a = EnVocabulary("aaarr", "bbbtt")
        val b = EnVocabulary("aaayy", "bbbuu")
        val c = EnVocabulary("aaaii", "bbboo")
        val list = listOf(a, b, c)
        viewModelScope.launch {
            list.forEach { dbDao.insert(it) }
        }
    }
}

class EnVocabularyViewModelFactory(
    private val dataSource: EnVocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnVocabularyViewModel::class.java)) {
            return EnVocabularyViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}