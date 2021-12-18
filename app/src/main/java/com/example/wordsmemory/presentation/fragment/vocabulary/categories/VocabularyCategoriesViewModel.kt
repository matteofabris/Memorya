package com.example.wordsmemory.presentation.fragment.vocabulary.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.framework.Interactors
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyCategoriesViewModel @Inject constructor(
    private val _interactors: Interactors
) :
    ViewModel() {

    val categories = _interactors.getCategoriesAsLiveData() as LiveData<List<CategoryEntity>>

    fun deleteCategory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _interactors.removeCategory(categories.value!!.first { it.id == id })
        }
    }
}