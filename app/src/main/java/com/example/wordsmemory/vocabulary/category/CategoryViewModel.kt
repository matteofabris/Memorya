package com.example.wordsmemory.vocabulary.category

import androidx.lifecycle.*
import com.example.wordsmemory.database.VocabularyDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao
) : ViewModel() {

    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val cat = _dbDao.getCategoryName(_savedStateHandle.get<Int>("categoryId")!!)
            viewModelScope.launch(Dispatchers.Main) {
                _categoryName.value = cat
            }
        }
    }
}