package com.example.wordsmemory.ui.vocabulary.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.wordsmemory.framework.room.CategoryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    _categoryDao: CategoryDao
) : ViewModel() {

    val categoryId = _savedStateHandle.get<Int>("categoryId") ?: 0
    val categoryName = _categoryDao.getCategoryNameAsLiveData(categoryId)
}