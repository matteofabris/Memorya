package com.example.wordsmemory.vocabulary.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.VocabularyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class VocabularyWordsViewModel(
    private val _dbDao: VocabularyDao,
    private val _categoryId: Int = 0
) : ViewModel() {

    var vocabularyList = initVocabularyList()

    private fun initVocabularyList(): LiveData<List<VocabularyItem>> {
        return if (_categoryId > 0) _dbDao.getVocabularyItemsByCategoryAsLiveData(_categoryId) else _dbDao.getVocabularyItemsAsLiveData()
    }

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
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteVocabularyItem(vocabularyList.value!!.first { it.id == id })
        }
    }
}

class VocabularyWordsViewModelFactory(
    private val _dataSource: VocabularyDao,
    private val _categoryId: Int = 0
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyWordsViewModel::class.java)) {
            return VocabularyWordsViewModel(_dataSource, _categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}