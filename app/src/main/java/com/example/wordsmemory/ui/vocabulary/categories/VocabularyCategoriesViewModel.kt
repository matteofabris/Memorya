package com.example.wordsmemory.ui.vocabulary.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordsmemory.database.CloudDbSyncHelper
import com.example.wordsmemory.database.WMDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyCategoriesViewModel @Inject constructor(
    private val _dbDao: WMDao,
    private val _firestoreDb: FirebaseFirestore
) :
    ViewModel() {

    val categories = _dbDao.getCategoriesAsLiveData()

    fun removeItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _dbDao.deleteCategory(categories.value!!.first { it.id == id })
            CloudDbSyncHelper.deleteCategory(_dbDao, _firestoreDb, id)
        }
    }
}