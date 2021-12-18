package com.example.wordsmemory.data.interfaces

import com.example.wordsmemory.domain.Category

interface CategoryDataSource {
    fun getCategoriesAsLiveData(): Any
    fun getNotEmptyCategoriesAsLiveData(): Any
    suspend fun getCategories(): List<Category>
    suspend fun addCategory(category: Category, update: Boolean = false) : Int
    suspend fun removeCategory(category: Category)
}