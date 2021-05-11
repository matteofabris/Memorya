package com.example.wordsmemory.vocabulary.addoreditvocabularyitem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.*
import com.example.wordsmemory.databinding.AddOrEditVocabularyItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class AddOrEditVocabularyItemSheet :
    BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private val _viewModel: AddOrEditVocabularyItemSheetViewModel by viewModels()
    private lateinit var _binding: AddOrEditVocabularyItemSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddOrEditVocabularyItemSheetBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.addOrEditItemViewModel = _viewModel

        setStyles()
        setEditTextsFilter()
        setupButtons()
        setCategoriesSpinner()

        return _binding.root
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
            _viewModel.insertOrUpdateVocabularyItem()
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
            _binding.addButton.isEnabled =
                s.isNotEmpty() && _binding.itWordEditText.text.isNotEmpty()
            _binding.googleTranslateButton.isEnabled = s.isNotEmpty()
        }
        _binding.itWordEditText.afterTextChanged { s ->
            _binding.addButton.isEnabled =
                s.isNotEmpty() && _binding.enWordEditText.text.isNotEmpty()
        }
    }

    private fun setCategoriesSpinner() {
        _viewModel.categories.observe(viewLifecycleOwner, {
            val categories = it.map { c -> c.category }.toTypedArray()

            it.forEach { c ->
                Log.i(
                    "categories",
                    "Category: id - ${c.id}, name - ${c.category}"
                )
            }

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            _binding.categorySpinner.adapter = arrayAdapter
            _viewModel.setCategory()
        })

        _viewModel.selectedCategoryId.observe(viewLifecycleOwner, {
            val categories = _viewModel.categories.value
            if (categories != null) {
                val selectedCategory = categories.first { c -> c.id == it }
                val index = categories.indexOf(selectedCategory)
                _binding.categorySpinner.setSelection(index)
            }
        })

        _binding.categorySpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        _viewModel.category = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        _viewModel.category = Constants.defaultCategory
    }
}