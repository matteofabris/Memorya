package com.example.wordsmemory.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.EnVocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class EnVocabularyViewModel(private val dbDao: EnVocabularyDao) : ViewModel() {

    var vocabularyList = dbDao.getAll()

    fun onClicked() {
        val a = EnVocabulary("house", "casa")
        val b = EnVocabulary("room", "stanza")
        val c = EnVocabulary("table", "tavolo")
        val d = EnVocabulary("pen", "penna")
        val e = EnVocabulary("country", "stato")
        val f = EnVocabulary("book", "libro")
        val g = EnVocabulary("phone", "telefono")
        val h = EnVocabulary("station", "stazione")
        val i = EnVocabulary("friday", "venerdì")
        val list = listOf(a, b, c, d, e, f, g, h, i)
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