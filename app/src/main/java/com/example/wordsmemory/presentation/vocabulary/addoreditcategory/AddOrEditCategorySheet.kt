package com.example.wordsmemory.presentation.vocabulary.addoreditcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordsmemory.R
import com.example.wordsmemory.TranslateInputFilter
import com.example.wordsmemory.afterTextChanged
import com.example.wordsmemory.databinding.AddCategorySheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class AddOrEditCategorySheet : BottomSheetDialogFragment() {

    private val _viewModel: AddCategorySheetViewModel by viewModels()
    private lateinit var _binding: AddCategorySheetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCategorySheetFragmentBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.viewModel = _viewModel
        _binding.category.filters = arrayOf(TranslateInputFilter())

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

        _viewModel.categoryItem.observe(viewLifecycleOwner, {
            if (_viewModel.isEdit) _binding.addButton.text = getString(R.string.update)
        })
    }
}