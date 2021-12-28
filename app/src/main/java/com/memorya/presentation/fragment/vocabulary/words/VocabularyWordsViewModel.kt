package com.memorya.presentation.fragment.vocabulary.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memorya.framework.Interactors
import com.memorya.framework.room.entities.VocabularyItemEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyWordsViewModel @Inject constructor(
    private val _interactors: Interactors
) : ViewModel() {
    lateinit var vocabularyList: LiveData<List<VocabularyItemEntity>>

    fun deleteItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _interactors.removeVocabularyItem(vocabularyList.value!!.first { it.id == id })
        }
    }

    fun initVocabularyList(categoryId: Int) {
        viewModelScope.launch {
            val vocabularyItems =
                _interactors.getVocabularyItemsAsLiveData() as LiveData<List<VocabularyItemEntity>>

            vocabularyList =
                if (categoryId > 0) {
                    Transformations.map(vocabularyItems) { items ->
                        items.filter { item -> item.category == categoryId }
                    }
                } else vocabularyItems
        }
    }
}