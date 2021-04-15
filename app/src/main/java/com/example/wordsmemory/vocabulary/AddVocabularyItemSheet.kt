package com.example.wordsmemory.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.*
import com.example.wordsmemory.databinding.AddVocabularyItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class AddVocabularyItemSheet : BottomSheetDialogFragment() {

    private lateinit var viewModel: AddVocabularyItemSheetViewModel
    private lateinit var binding: AddVocabularyItemSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddVocabularyItemSheetBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        createViewModel()
        binding.addItemViewModel = viewModel
        setStyles()
        setEditTextsFilter()
        setupButtons()

        return binding.root
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
        viewModel =
            ViewModelProvider(this, factory).get(AddVocabularyItemSheetViewModel::class.java)
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            binding.addButton.style(R.style.buttonStyleTablet)
            binding.googleTranslateButton.style(R.style.buttonStyleTablet)
            binding.googleTranslateButton.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_g_translate_white_36,
                0,
                0,
                0
            )

            binding.enWordTitleTextView.style(R.style.wm_labelStyleTablet)
            binding.enWordEditText.style(R.style.wm_labelStyleTablet)
            binding.itWordTitleTextView.style(R.style.wm_labelStyleTablet)
            binding.itWordEditText.style(R.style.wm_labelStyleTablet)
        }
    }

    private fun setEditTextsFilter() {
        val filter = TranslateInputFilter()
        binding.enWordEditText.filters = arrayOf(filter)
        binding.itWordEditText.filters = arrayOf(filter)
    }

    private fun setupButtons() {
        binding.addButton.setOnClickListener {
            viewModel.saveVocabularyItem()
            dismiss()
        }
        binding.googleTranslateButton.setOnClickListener {
            if (activity != null) {
                lifecycleScope.launch {
                    if (checkInternetConnection(requireActivity())) {
                        viewModel.translate()
                    }
                }
            }
        }

        binding.enWordEditText.afterTextChanged { s ->
            binding.addButton.isEnabled = s.isNotEmpty() && binding.itWordEditText.text.isNotEmpty()
            binding.googleTranslateButton.isEnabled = s.isNotEmpty()
        }
        binding.itWordEditText.afterTextChanged { s ->
            binding.addButton.isEnabled = s.isNotEmpty() && binding.enWordEditText.text.isNotEmpty()
        }
    }
}