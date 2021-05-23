package com.example.wordsmemory.ui.vocabulary.addoreditcategory

import androidx.lifecycle.*
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddCategorySheetViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao
) : ViewModel() {

    val categoryItem = MutableLiveData<Category>()
    var isEdit = false

    init {
        viewModelScope.launch {
            val selectedCategoryId = _savedStateHandle.get<Int>("selectedCategoryId")
            if (selectedCategoryId != null && selectedCategoryId != -1) {
                isEdit = true
                categoryItem.value = _dbDao.getCategoryById(selectedCategoryId)
            } else {
                categoryItem.value = Category("")
            }
        }
    }

    fun insertOrUpdateCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryItem.value!!.category =
                categoryItem.value!!.category.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            if (isEdit) {
                _dbDao.updateCategory(categoryItem.value!!)
            } else {
                _dbDao.insertCategory(categoryItem.value!!)
            }
        }
    }
}