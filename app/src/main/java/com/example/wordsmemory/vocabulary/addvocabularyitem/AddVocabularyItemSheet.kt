package com.example.wordsmemory.vocabulary.addvocabularyitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.*
import com.example.wordsmemory.databinding.AddVocabularyItemSheetBinding
import com.example.wordsmemory.vocabulary.AddVocabularyItemSheetViewModel
import com.example.wordsmemory.vocabulary.AddVocabularyItemSheetViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class AddVocabularyItemSheet : BottomSheetDialogFragment() {

    private lateinit var _viewModel: AddVocabularyItemSheetViewModel
    private lateinit var _binding: AddVocabularyItemSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddVocabularyItemSheetBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner

        createViewModel()
        _binding.addItemViewModel = _viewModel
        setStyles()
        setEditTextsFilter()
        setupButtons()

        return _binding.root
    }

    companion object {
        fun newInstance(): AddVocabularyItemSheet =
            AddVocabularyItemSheet().apply {}
    }

    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).enVocabularyDao()

        val factory = AddVocabularyItemSheetViewModelFactory(
            dbDao,
            resources.openRawResource(R.raw.wordstranslationcredentials)
        )
        _viewModel =
            ViewModelProvider(this, factory).get(AddVocabularyItemSheetViewModel::class.java)
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.addButton.style(R.style.buttonStyleTablet)
            _binding.googleTranslateButton.style(R.style.buttonStyleTablet)
            _binding.googleTranslateButton.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_g_translate_white_36,
                0,
                0,
                0
            )

            _binding.enWordTitleTextView.style(R.style.wm_labelStyleTablet)
            _binding.enWordEditText.style(R.style.wm_labelStyleTablet)
            _binding.itWordTitleTextView.style(R.style.wm_labelStyleTablet)
            _binding.itWordEditText.style(R.style.wm_labelStyleTablet)
        }
    }

    private fun setEditTextsFilter() {
        val filter = TranslateInputFilter()
        _binding.enWordEditText.filters = arrayOf(filter)
        _binding.itWordEditText.filters = arrayOf(filter)
    }

    private fun setupButtons() {
        _binding.addButton.setOnClickListener {
            _viewModel.saveVocabularyItem()
            dismiss()
        }
        _binding.googleTranslateButton.setOnClickListener {
            if (activity != null) {
                lifecycleScope.launch {
                    if (checkInternetConnection(requireActivity())) {
                        _viewModel.translate()
                    }
                }
            }
        }

        _binding.enWordEditText.afterTextChanged { s ->
            _binding.addButton.isEnabled = s.isNotEmpty() && _binding.itWordEditText.text.isNotEmpty()
            _binding.googleTranslateButton.isEnabled = s.isNotEmpty()
        }
        _binding.itWordEditText.afterTextChanged { s ->
            _binding.addButton.isEnabled = s.isNotEmpty() && _binding.enWordEditText.text.isNotEmpty()
        }
    }
}