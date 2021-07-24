package com.example.wordsmemory.ui.vocabulary.words

import androidx.lifecycle.*
import com.example.wordsmemory.database.CloudDbSyncHelper
import com.example.wordsmemory.database.WMDao
import com.example.wordsmemory.model.vocabulary.VocabularyItem
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyWordsViewModel @Inject constructor(
    private val _dbDao: WMDao,
    private val _firestoreDb: FirebaseFirestore
) : ViewModel() {

    private val _vocabularyList = MutableLiveData<List<VocabularyItem>>()
    val vocabularyList: LiveData<List<VocabularyItem>>
        get() = _vocabularyList

    fun removeItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteVocabularyItem(vocabularyList.value!!.first { it.id == id })
            CloudDbSyncHelper.deleteVocabularyItem(_dbDao, _firestoreDb, id)
        }
    }

    fun initVocabularyList(categoryId: Int) {
        viewModelScope.launch {
            _vocabularyList.value =
                if (categoryId > 0) _dbDao.getVocabularyItemsByCategory(categoryId)
                else _dbDao.getVocabularyItems()
        }
    }
}