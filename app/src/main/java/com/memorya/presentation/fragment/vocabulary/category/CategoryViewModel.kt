package com.memorya.presentation.fragment.vocabulary.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memorya.framework.Interactors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    _interactors: Interactors
) : ViewModel() {

    val categoryId = _savedStateHandle.get<Int>("categoryId") ?: 0
    var categoryName = MutableLiveData("")

    init {
        viewModelScope.launch {
            categoryName.value = _interactors.getCategories()
                .first { category -> category.id == categoryId }.category
        }
    }
}