package com.example.wordsmemory.ui.vocabulary.words

import androidx.lifecycle.*
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.VocabularyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyWordsViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao
) : ViewModel() {

    private val _categoryId: Int =
        _savedStateHandle.get<Int>("categoryId") ?: 0

    val vocabularyList = initVocabularyList()

    private fun initVocabularyList(): LiveData<List<VocabularyItem>> {
        return if (_categoryId > 0) _dbDao.getVocabularyItemsByCategoryAsLiveData(_categoryId) else
            _dbDao.getVocabularyItemsAsLiveData()
    }

    fun removeItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteVocabularyItem(vocabularyList.value!!.first { it.id == id })
        }
    }
}