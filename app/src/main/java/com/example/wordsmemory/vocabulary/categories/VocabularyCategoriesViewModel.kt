package com.example.wordsmemory.vocabulary.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.Category
import com.example.wordsmemory.VocabularyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class VocabularyCategoriesViewModel @Inject constructor(private val _dbDao: VocabularyDao) :
    ViewModel() {

    var categories = _dbDao.getCategoriesAsLiveData()

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