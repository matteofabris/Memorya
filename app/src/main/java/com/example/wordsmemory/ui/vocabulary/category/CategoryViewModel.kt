package com.example.wordsmemory.ui.vocabulary.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.wordsmemory.database.WMDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    _dbDao: WMDao
) : ViewModel() {

    val categoryId = _savedStateHandle.get<Int>("categoryId") ?: 0
    val categoryName = _dbDao.getCategoryNameAsLiveData(categoryId)
}