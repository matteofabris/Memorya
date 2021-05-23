package com.example.wordsmemory.ui.vocabulary.addoreditcategory

import androidx.lifecycle.*
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddCategorySheetViewModel @Inject constructor(
    _savedStateHandle: SavedStateHandle,
    private val _dbDao: VocabularyDao
) : ViewModel() {

    val addButtonText = MutableLiveData("Add")
    val category = MutableLiveData<String>()

    private val _categoryItem = MutableLiveData<Category>()
    val categoryItem: LiveData<Category>
        get() = _categoryItem

    init {
        viewModelScope.launch {
            val selectedCategoryId = _savedStateHandle.get<Int>("selectedCategoryId")
            if (selectedCategoryId != null && selectedCategoryId != -1) {
                _categoryItem.value = _dbDao.getCategoryById(selectedCategoryId)
                category.value = _categoryItem.value?.category
            }
        }
    }

    fun insertOrUpdateCategory() {
        viewModelScope.launch {
            if (_categoryItem.value != null) {
                _categoryItem.value!!.category = category.value!!.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                _dbDao.updateCategory(_categoryItem.value!!)
            } else {
                _dbDao.insertCategory(
                    Category(
                        category.value!!.lowercase(Locale.getDefault())
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    )
                )
            }
        }
    }
}