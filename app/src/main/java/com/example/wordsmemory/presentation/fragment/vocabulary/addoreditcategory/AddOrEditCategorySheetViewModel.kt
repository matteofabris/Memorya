package com.example.wordsmemory.presentation.fragment.vocabulary.addoreditcategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.framework.Interactors
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategorySheetViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _interactors: Interactors
) : ViewModel() {

    val categoryItem = MutableLiveData<CategoryEntity>()
    var isEdit = false

    init {
        viewModelScope.launch {
            val selectedCategoryId = _savedStateHandle.get<Int>("selectedCategoryId")
            if (selectedCategoryId != null && selectedCategoryId != -1) {
                isEdit = true
                categoryItem.value = CategoryEntity(
                    _interactors.getCategories().find { c -> c.id == selectedCategoryId }!!
                )
            } else {
                categoryItem.value = CategoryEntity("")
            }
        }
    }

    suspend fun insertOrUpdateCategory() = _interactors.addCategory(categoryItem.value!!, isEdit)
}