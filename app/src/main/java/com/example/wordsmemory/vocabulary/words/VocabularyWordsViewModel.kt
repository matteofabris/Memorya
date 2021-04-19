package com.example.wordsmemory.vocabulary.words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.VocabularyItem
import com.example.wordsmemory.VocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class VocabularyWordsViewModel(private val _dbDao: VocabularyDao) : ViewModel() {

    var vocabularyList = _dbDao.getVocabularyItems()

    fun mockWords() {
        val a = VocabularyItem("house", "casa")
        val b = VocabularyItem("room", "stanza")
        val c = VocabularyItem("table", "tavolo")
        val d = VocabularyItem("pen", "penna")
        val e = VocabularyItem("country", "stato")
        val f = VocabularyItem("book", "libro")
        val g = VocabularyItem("phone", "telefono")
        val h = VocabularyItem("station", "stazione")
        val i = VocabularyItem("friday", "venerdì")
        val list = listOf(a, b, c, d, e, f, g, h, i)
        viewModelScope.launch {
            list.forEach { _dbDao.insertVocabularyItem(it) }
        }
    }

    fun removeItem(id: Int) {
        viewModelScope.launch {
            _dbDao.delete(vocabularyList.value!!.first { it.id == id })
        }
    }
}

class EnVocabularyViewModelFactory(
    private val _dataSource: VocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyWordsViewModel::class.java)) {
            return VocabularyWordsViewModel(_dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}