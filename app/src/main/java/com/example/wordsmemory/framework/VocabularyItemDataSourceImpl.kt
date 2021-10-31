package com.example.wordsmemory.framework

import androidx.lifecycle.LiveData
import com.example.wordsmemory.data.interfaces.VocabularyItemDataSource
import com.example.wordsmemory.framework.room.dao.VocabularyItemDao
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import javax.inject.Inject

class VocabularyItemDataSourceImpl @Inject constructor(
    private val _vocabularyItemDao: VocabularyItemDao
) : VocabularyItemDataSource {

    override fun getVocabularyItems(): LiveData<List<VocabularyItemEntity>> {
        return _vocabularyItemDao.getVocabularyItems()
    }
}