package com.example.wordsmemory.ui.vocabulary.addoreditvocabularyitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.TranslateInputFilter
import com.example.wordsmemory.checkInternetConnection
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
        _binding.viewModel = _viewModel

        setEditTextsFilter()
        setButtonsOnClickListeners()
        setupObservers()

        _binding.categorySpinner.onItemSelectedListener = this

        return _binding.root
    }

    private fun setEditTextsFilter() {
        val filter = TranslateInputFilter()
        _binding.enWord.filters = arrayOf(filter)
        _binding.itWord.filters = arrayOf(filter)
    }

    private fun setButtonsOnClickListeners() {
        _binding.addButton.setOnClickListener {
            _viewModel.insertOrUpdateVocabularyItem()
            findNavController().popBackStack()
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
    }

    private fun setupObservers() {
        _viewModel.enText.observe(viewLifecycleOwner, { s ->
            _binding.addButton.isEnabled =
                s.isNotEmpty() && _binding.itWord.text.isNotEmpty()
            _binding.googleTranslateButton.isEnabled = s.isNotEmpty()
        })

        _viewModel.itText.observe(viewLifecycleOwner, { s ->
            _binding.addButton.isEnabled =
                s.isNotEmpty() && _binding.enWord.text.isNotEmpty()
        })

        _viewModel.categories.observe(viewLifecycleOwner, {
            val categories = it.map { c -> c.category }.toTypedArray()

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            _binding.categorySpinner.adapter = arrayAdapter
            _viewModel.getVocabularyItem()
        })

        _viewModel.vocabularyItem.observe(viewLifecycleOwner, {
            _binding.addButton.text = getString(R.string.update)

            val categories = _viewModel.categories.value
            if (categories != null) {
                val selectedCategory = categories.first { c -> c.id == it.category }
                val index = categories.indexOf(selectedCategory)
                _binding.categorySpinner.setSelection(index)
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        _viewModel.category = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        _viewModel.category = Constants.defaultCategory
    }
}