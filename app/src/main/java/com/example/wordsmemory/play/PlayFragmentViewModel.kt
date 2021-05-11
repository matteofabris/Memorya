package com.example.wordsmemory.play

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

    companion object {
        const val correctString = " correct"
        const val recentAttemptsString = "Recent attempts: "
    }

    private var _allAttempts = 0
    private var _correctAttempts = 0
    private var categoryId = Constants.defaultCategoryId

    var vocabularyList = _dbDao.getVocabularyItemsAsLiveData()
    val translationText = MutableLiveData<String>()
    val recentAttemptsText = MutableLiveData(getRecentAttemptsText())
    val categories = _dbDao.getCategoriesAsLiveData()


    private val _vocabularyItem = MutableLiveData<VocabularyItem>()
    val vocabularyItem: LiveData<VocabularyItem>
        get() = _vocabularyItem

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    fun setPlayWord() {
        if (vocabularyList.value != null) {
            if (vocabularyList.value!!.isNotEmpty()) {
                val filteredList = if (categoryId == Constants.defaultCategoryId) {
                    vocabularyList.value!!
                } else {
                    vocabularyList.value!!.filter {
                        it.category == categoryId
                    }
                }

                val randomIndex = Random.nextInt(filteredList.size)
                _vocabularyItem.value = filteredList[randomIndex]
            } else {
                _vocabularyItem.value = VocabularyItem("", "")
            }
        }
    }

    fun onCheckClicked() {
        _allAttempts++
        _isTranslationOk.value =
            translationText.value!!.equals(_vocabularyItem.value!!.itWord, ignoreCase = true)

        if (_isTranslationOk.value!!) {
            _correctAttempts++
            setPlayWord()
            translationText.value = ""
        }

        recentAttemptsText.value = getRecentAttemptsText()
    }

    private fun getRecentAttemptsText(): String {
        return "$recentAttemptsString$_correctAttempts/$_allAttempts$correctString"
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
            categoryId = _dbDao.getCategoryId(categoryName)
        }
    }

    private fun resetRecentAttempts() {
        _correctAttempts = 0
        _allAttempts = 0
        recentAttemptsText.value = getRecentAttemptsText()
    }
}