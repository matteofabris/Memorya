package com.example.wordsmemory.ui.vocabulary.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.wordsmemory.database.VocabularyDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    _dbDao: VocabularyDao
) : ViewModel() {

    val categoryName = _dbDao.getCategoryNameAsLiveData(_savedStateHandle.get<Int>("categoryId")!!)
}