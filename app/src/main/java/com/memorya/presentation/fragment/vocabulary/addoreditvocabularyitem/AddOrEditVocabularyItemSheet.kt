package com.memorya.presentation.fragment.vocabulary.addoreditvocabularyitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.memorya.*
import com.memorya.databinding.AddOrEditVocabularyItemSheetBinding
import com.memorya.presentation.helper.EnWordInputFilter
import com.memorya.presentation.helper.ItWordInputFilter
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

        setEditTextsFilters()
        setButtonsOnClickListeners()
        setupObservers()

        _binding.categorySpinner.onItemSelectedListener = this

        return _binding.root
    }

    private fun setEditTextsFilters() {
        val enFilter = EnWordInputFilter()
        val itFilter = ItWordInputFilter()
        _binding.enWord.filters = arrayOf(enFilter)
        _binding.itWord.filters = arrayOf(itFilter)
    }

    private fun setButtonsOnClickListeners() {
        _binding.addButton.setOnClickListener {
            lifecycleScope.launch {
                _viewModel.insertOrUpdateVocabularyItem()
                findNavController().popBackStack()
            }
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
        _binding.enWord.afterTextChanged { s ->
            _binding.addButton.isEnabled =
                s.isNotEmpty() && _binding.itWord.text.isNotEmpty()
            _binding.googleTranslateButton.isEnabled = s.isNotEmpty()
        }

        _binding.itWord.afterTextChanged { s ->
            _binding.addButton.isEnabled =
                s.isNotEmpty() && _binding.enWord.text.isNotEmpty()
        }

        _viewModel.categories.observe(viewLifecycleOwner, {
            val categories = it.map { c -> c.category }.toTypedArray()

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                categories
            )
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item)

            _binding.categorySpinner.adapter = arrayAdapter
            _viewModel.initVocabularyItem()
        })

        _viewModel.vocabularyItem.observe(viewLifecycleOwner, {
            if (_viewModel.isEdit) {
                _binding.addButton.text = getString(R.string.update)

                val categories = _viewModel.categories.value
                if (categories != null) {
                    val selectedCategory = categories.first { c -> c.id == it.category }
                    val index = categories.indexOf(selectedCategory)
                    _binding.categorySpinner.setSelection(index)
                }
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        _viewModel.setVocabularyItemCategory(parent?.getItemAtPosition(position).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        _viewModel.setVocabularyItemCategory(Constants.defaultCategory)
    }
}