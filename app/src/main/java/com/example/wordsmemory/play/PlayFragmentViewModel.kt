package com.example.wordsmemory.play

import androidx.lifecycle.*
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.EnVocabularyDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlayFragmentViewModel(private val dbDao: EnVocabularyDao) : ViewModel() {

    private var vocabularyList = emptyList<EnVocabulary>()

    private val _vocabularyItem = MutableLiveData<EnVocabulary>()
    val vocabularyItem: LiveData<EnVocabulary>
        get() = _vocabularyItem

    val translationText = MutableLiveData<String>()

    private val _isTranslationOk = MutableLiveData<Boolean>()
    val isTranslationOk: LiveData<Boolean>
        get() = _isTranslationOk

    init {
        viewModelScope.launch {
            vocabularyList = dbDao.getAll()
            setPlayWord()
        }
    }

    fun setPlayWord() {
        if (vocabularyList.isNotEmpty()) {
            val randomIndex = Random.nextInt(vocabularyList.size)
            _vocabularyItem.value = vocabularyList[randomIndex]
        }
    }

    fun onCheckClicked() {
        _isTranslationOk.value =
            translationText.value!!.equals(_vocabularyItem.value!!.itWord, ignoreCase = true)

        if (_isTranslationOk.value!!) {
            setPlayWord()
            translationText.value = ""
        }
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