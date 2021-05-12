package com.example.wordsmemory.vocabulary.words

import androidx.lifecycle.*
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.VocabularyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyWordsViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao
) : ViewModel() {

    private val _categoryId: Int =
        _savedStateHandle.get<Int>("categoryId") ?: 0

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