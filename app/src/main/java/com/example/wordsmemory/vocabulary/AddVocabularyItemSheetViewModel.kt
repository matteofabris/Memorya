package com.example.wordsmemory.vocabulary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.EnVocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

class AddVocabularyItemSheetViewModel(private val dbDao: EnVocabularyDao) : ViewModel() {

    val enText = MutableLiveData<String>()
    val itText = MutableLiveData<String>()

    fun saveVocabularyItem() {
        viewModelScope.launch {
            dbDao.insert(
                EnVocabulary(
                    enText.value!!.toLowerCase(Locale.getDefault()), itText.value!!.toLowerCase(
                        Locale.getDefault()
                    )
                )
            )
        }
    }
}

class AddVocabularyItemSheetViewModelFactory(
    private val dataSource: EnVocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVocabularyItemSheetViewModel::class.java)) {
            return AddVocabularyItemSheetViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}