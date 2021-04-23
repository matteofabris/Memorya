package com.example.wordsmemory.vocabulary.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.InternalCoroutinesApi

class CategoryViewModel : ViewModel()

class CategoryViewModelFactory : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}