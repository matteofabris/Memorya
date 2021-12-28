package com.memorya.framework.implementations

import androidx.work.*
import com.memorya.data.interfaces.VocabularyItemDataSource
import com.memorya.domain.VocabularyItem
import com.memorya.framework.room.dao.VocabularyItemDao
import com.memorya.framework.room.entities.VocabularyItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class VocabularyItemDataSourceImpl @Inject constructor(
    private val _vocabularyItemDao: VocabularyItemDao,
) : VocabularyItemDataSource {

    override suspend fun getVocabularyItems() = _vocabularyItemDao.getVocabularyItems()

    override fun getVocabularyItemsAsLiveData() = _vocabularyItemDao.getVocabularyItemsAsLiveData()

    override suspend fun addVocabularyItem(vocabularyItem: VocabularyItem, update: Boolean) =
        withContext(Dispatchers.IO) {
            Timber.i("Save vocabulary item: en - " + vocabularyItem.enWord + ", " +
                    "it - " + vocabularyItem.itWord + ", " + "category - " + vocabularyItem.category)

            val itemId = if (update) {
                _vocabularyItemDao.updateVocabularyItem(VocabularyItemEntity(vocabularyItem))
                vocabularyItem.id
            } else {
                _vocabularyItemDao.insertVocabularyItem(VocabularyItemEntity(vocabularyItem))
                    .toInt()
            }

            return@withContext itemId
        }

    override suspend fun removeVocabularyItem(vocabularyItem: VocabularyItem) =
        _vocabularyItemDao.deleteVocabularyItem(VocabularyItemEntity(vocabularyItem))
}