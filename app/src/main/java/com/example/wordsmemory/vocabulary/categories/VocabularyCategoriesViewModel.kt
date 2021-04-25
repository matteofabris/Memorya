package com.example.wordsmemory.vocabulary.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.Category
import com.example.wordsmemory.VocabularyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class VocabularyCategoriesViewModel(private val _dbDao: VocabularyDao) : ViewModel() {

    var categories = _dbDao.getCategories()

    fun removeItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteCategory(categories.value!!.first { it.id == id })
        }
    }

    //init {
    //mockCategories()
    //}

    fun mockCategories() {
        val a = Category("house")
        val b = Category("room")
        val c = Category("table")
        val d = Category("pen")
        val list = listOf(a, b, c, d)
        viewModelScope.launch {
            list.forEach { _dbDao.insertCategory(it) }
        }
    }
}

class VocabularyCategoriesViewModelFactory(
    private val _dataSource: VocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyCategoriesViewModel::class.java)) {
            return VocabularyCategoriesViewModel(_dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}