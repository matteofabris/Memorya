package com.memorya.presentation.fragment.vocabulary.addoreditcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.memorya.R
import com.memorya.databinding.AddOrEditCategorySheetFragmentBinding
import com.memorya.presentation.helper.CategoryInputFilter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.memorya.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class AddOrEditCategorySheet : BottomSheetDialogFragment() {

    private val _viewModel: AddCategorySheetViewModel by viewModels()
    private lateinit var _binding: AddOrEditCategorySheetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddOrEditCategorySheetFragmentBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.viewModel = _viewModel
        _binding.category.filters = arrayOf(CategoryInputFilter())

        setupAddButtonOnClickListener()
        setupObservers()

        return _binding.root
    }

    private fun setupAddButtonOnClickListener() {
        _binding.addButton.setOnClickListener {
            lifecycleScope.launch {
                _viewModel.insertOrUpdateCategory()
                findNavController().popBackStack()
            }
        }
    }

    private fun setupObservers() {
        _binding.category.afterTextChanged { s ->
            _binding.addButton.isEnabled = s.isNotEmpty()
        }

        _viewModel.categoryItem.observe(viewLifecycleOwner) {
            if (_viewModel.isEdit) _binding.addButton.text = getString(R.string.update)
        }
    }
}