package com.example.wordsmemory.vocabulary.addoreditcategory

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

    private lateinit var _category: Category
    private val _selectedCategoryId: Int =
        _savedStateHandle.get<Int>("selectedCategoryId")!!

    val addButtonText = MutableLiveData("Add")
    val category = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            if (_selectedCategoryId != -1) {
                _category = _dbDao.getCategoryById(_selectedCategoryId)
                category.value = _category.category
                addButtonText.value = "Update"
            }
        }
    }

    fun insertOrUpdateCategory() {
        viewModelScope.launch {
            if (_selectedCategoryId != -1) {
                _category.category = category.value!!.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                _dbDao.updateCategory(_category)
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