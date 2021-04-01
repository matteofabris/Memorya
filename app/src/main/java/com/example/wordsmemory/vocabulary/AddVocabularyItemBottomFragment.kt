package com.example.wordsmemory.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wordsmemory.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddVocabularyItemBottomFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.add_vocabulary_item_dialog,
            container,
            false
        )
    }

    companion object {
        fun newInstance(): AddVocabularyItemBottomFragment =
            AddVocabularyItemBottomFragment().apply {}
    }
}