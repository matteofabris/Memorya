package com.example.wordsmemory.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordsmemory.VocabularyItem
import com.example.wordsmemory.VocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.random.Random

class PlayFragmentViewModel(dbDao: VocabularyDao) : ViewModel() {

    companion object {
        const val correctString = " correct"
        const val recentAttemptsString = "Recent attempts: "
    }

    private var _allAttempts = 0
    private var _correctAttempts = 0

    var vocabularyList = dbDao.getVocabularyItems()
    val translationText = MutableLiveData<String>()
    val recentAttemptsText = MutableLiveData(getRecentAttemptsText())

    private val _vocabularyItem = MutableLiveData<VocabularyItem>()
    val vocabularyItemItem: LiveData<VocabularyItem>
        get() = _vocabularyItem

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    fun setPlayWord() {
        if (vocabularyList.value != null) {
            if (vocabularyList.value!!.isNotEmpty()) {
                val randomIndex = Random.nextInt(vocabularyList.value!!.size)
                _vocabularyItem.value = vocabularyList.value!![randomIndex]
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
}

class PlayFragmentViewModelFactory(
    private val _dataSource: VocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayFragmentViewModel::class.java)) {
            return PlayFragmentViewModel(_dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}