package com.example.wordsmemory.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.EnVocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.random.Random

class PlayFragmentViewModel(dbDao: EnVocabularyDao) : ViewModel() {

    var vocabularyList = dbDao.getAllAsLiveData()
    private var _allAttempts = 0
    private var _correctAttempts = 0

    private val _vocabularyItem = MutableLiveData<EnVocabulary>()
    val vocabularyItem: LiveData<EnVocabulary>
        get() = _vocabularyItem

    val translationText = MutableLiveData<String>()

    val recentAttemptsText =
        MutableLiveData(getRecentAttemptsText())

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    companion object {
        const val correctString = " correct"
        const val recentAttemptsString = "Recent attempts: "
    }

    fun setPlayWord() {
        if (vocabularyList.value != null) {
            if (vocabularyList.value!!.isNotEmpty()) {
                val randomIndex = Random.nextInt(vocabularyList.value!!.size)
                _vocabularyItem.value = vocabularyList.value!![randomIndex]
            } else {
                _vocabularyItem.value = EnVocabulary("", "")
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
    private val _dataSource: EnVocabularyDao
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