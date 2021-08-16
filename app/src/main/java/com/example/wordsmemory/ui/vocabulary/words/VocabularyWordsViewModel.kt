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
    lateinit var vocabularyList: LiveData<List<VocabularyItem>>

    fun removeItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteVocabularyItem(vocabularyList.value!!.first { it.id == id })
            CloudDbSyncHelper.deleteVocabularyItem(_dbDao, _firestoreDb, id)
        }
    }

    fun initVocabularyList(categoryId: Int) {
        viewModelScope.launch {
            vocabularyList =
                if (categoryId > 0) _dbDao.getVocabularyItemsByCategoryAsLiveData(categoryId)
                else _dbDao.getVocabularyItemsAsLiveData()
        }
    }
}