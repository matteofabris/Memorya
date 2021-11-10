package com.example.wordsmemory.presentation.fragment.vocabulary.addoreditvocabularyitem

import androidx.lifecycle.*
import com.example.wordsmemory.framework.Interactors
import com.example.wordsmemory.framework.room.entities.CategoryEntity
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditVocabularyItemSheetViewModel @Inject constructor(
    private val _savedStateHandle: SavedStateHandle,
    private val _interactors: Interactors
) : ViewModel() {

    val categories = _interactors.getCategoriesAsLiveData() as LiveData<List<CategoryEntity>>
    var isEdit = false
    val vocabularyItem = MutableLiveData<VocabularyItemEntity>()

    fun initVocabularyItem() {
        viewModelScope.launch {
            val selectedVocabularyItemId = _savedStateHandle.get<Int>("selectedVocabularyItemId")
            if (selectedVocabularyItemId != null && selectedVocabularyItemId != -1) {
                isEdit = true
                vocabularyItem.value = VocabularyItemEntity(
                    _interactors.getVocabularyItems()
                        .find { item -> item.id == selectedVocabularyItemId }!!
                )
            } else {
                vocabularyItem.value = VocabularyItemEntity("", "", 1)
            }
        }
    }

    fun setVocabularyItemCategory(category: String) {
        vocabularyItem.value?.category =
            categories.value?.find { c -> c.category == category }?.id ?: 0
    }

    suspend fun insertOrUpdateVocabularyItem() =
        _interactors.addVocabularyItem(vocabularyItem.value!!, isEdit)

    fun translate() {
        viewModelScope.launch(Dispatchers.Main) {
            val textToTranslate = vocabularyItem.value?.enWord
            if (!textToTranslate.isNullOrEmpty()) {
                val translatedText = _interactors.translate(textToTranslate)
                if (translatedText.isNotEmpty()) updateTranslatedText(translatedText)
            }
        }
    }

    private fun updateTranslatedText(translatedText: String) {
        val vocabularyItemTemp = vocabularyItem.value
        if (vocabularyItemTemp != null) {
            vocabularyItemTemp.itWord = translatedText
            vocabularyItem.value = vocabularyItemTemp!!
        }
    }
}