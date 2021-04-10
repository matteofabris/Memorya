package com.example.wordsmemory.play

import androidx.lifecycle.*
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.EnVocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlayFragmentViewModel(private val dbDao: EnVocabularyDao) : ViewModel() {

    private var vocabularyList = emptyList<EnVocabulary>()
    private var allAttempts = 0
    private var correctAttempts = 0

    private val _vocabularyItem = MutableLiveData<EnVocabulary>()
    val vocabularyItem: LiveData<EnVocabulary>
        get() = _vocabularyItem

    val translationText = MutableLiveData<String>()

    val recentAttemptsText =
        MutableLiveData(getRecentAttemptsText())

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    init {
        viewModelScope.launch {
            vocabularyList = dbDao.getAll()
            setPlayWord()
        }
    }

    companion object {
        const val correctString = " correct"
        const val recentAttemptsString = "Recent attempts: "
    }

    fun setPlayWord() {
        if (vocabularyList.isNotEmpty()) {
            val randomIndex = Random.nextInt(vocabularyList.size)
            _vocabularyItem.value = vocabularyList[randomIndex]
        }
    }

    fun onCheckClicked() {
        allAttempts++
        _isTranslationOk.value =
            translationText.value!!.equals(_vocabularyItem.value!!.itWord, ignoreCase = true)

        if (_isTranslationOk.value!!) {
            correctAttempts++
            setPlayWord()
            translationText.value = ""
        }

        recentAttemptsText.value = getRecentAttemptsText()
    }

    private fun getRecentAttemptsText(): String {
        return "$recentAttemptsString$correctAttempts/$allAttempts$correctString"
    }
}

class PlayFragmentViewModelFactory(
    private val dataSource: EnVocabularyDao
) : ViewModelProvider.Factory {
    @InternalCoroutinesApi
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayFragmentViewModel::class.java)) {
            return PlayFragmentViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}