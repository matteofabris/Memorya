package com.example.wordsmemory.ui.play

import androidx.lifecycle.*
import com.example.wordsmemory.Constants
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.model.VocabularyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayFragmentViewModel @Inject constructor(private val _dbDao: VocabularyDao) : ViewModel() {

    private var _categoryId = Constants.defaultCategoryId

    val vocabularyList = _dbDao.getVocabularyItemsAsLiveData()
    val translationText = MutableLiveData<String>()
    val categories = _dbDao.getCategoriesAsLiveData()

    private var _correctAttempts = 0
    val correctAttempts: Int
        get() = _correctAttempts

    private val _allAttempts = MutableLiveData(0)
    val allAttempts: LiveData<Int>
        get() = _allAttempts

    private val _vocabularyItem = MutableLiveData<VocabularyItem>()
    val vocabularyItem: LiveData<VocabularyItem>
        get() = _vocabularyItem

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    fun setPlayWord() {
        if (vocabularyList.value != null) {
            if (vocabularyList.value!!.isNotEmpty()) {
                val filteredList = if (_categoryId == Constants.defaultCategoryId) {
                    vocabularyList.value!!
                } else {
                    vocabularyList.value!!.filter {
                        it.category == _categoryId
                    }
                }

                val randomIndex = Random.nextInt(filteredList.size)
                _vocabularyItem.value = filteredList[randomIndex]
            } else {
                _vocabularyItem.value = VocabularyItem("", "")
            }
        }
    }

    fun onCheckTranslationButtonClicked() {
        _isTranslationOk.value =
            translationText.value!!.equals(_vocabularyItem.value!!.itWord, ignoreCase = true)

        if (_isTranslationOk.value!!) {
            _correctAttempts++
            setPlayWord()
            translationText.value = ""
        }

        _allAttempts.value = _allAttempts.value?.plus(1)
    }

    fun resetGamePlay(categoryName: String) {
        viewModelScope.launch {
            setCategoryId(categoryName)
            resetRecentAttempts()
            setPlayWord()
        }
    }

    private suspend fun setCategoryId(categoryName: String) {
        return withContext(Dispatchers.IO) {
            _categoryId = _dbDao.getCategoryId(categoryName)
        }
    }

    private fun resetRecentAttempts() {
        _correctAttempts = 0
        _allAttempts.value = 0
    }
}