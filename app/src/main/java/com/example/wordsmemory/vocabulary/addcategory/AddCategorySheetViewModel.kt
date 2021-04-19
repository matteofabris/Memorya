package com.example.wordsmemory.vocabulary.addcategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.Category
import com.example.wordsmemory.VocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

class AddCategorySheetViewModel(private val _dbDao: VocabularyDao) : ViewModel() {

    val category = MutableLiveData<String>()

    fun saveCategory() {
        viewModelScope.launch {
            _dbDao.insertCategory(
                Category(
                    category.value!!.toLowerCase(Locale.getDefault())
                        .capitalize(Locale.getDefault())
                )
            )
        }
    }
}

class AddCategorySheetViewModelFactory(
    private val _dataSource: VocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCategorySheetViewModel::class.java)) {
            return AddCategorySheetViewModel(_dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}