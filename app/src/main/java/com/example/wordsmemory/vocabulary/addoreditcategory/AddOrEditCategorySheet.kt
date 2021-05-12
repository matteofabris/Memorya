package com.example.wordsmemory.vocabulary.addoreditcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.AddCategoryInputFilter
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.afterTextChanged
import com.example.wordsmemory.databinding.AddCategorySheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

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
        _binding.addCategoryViewModel = _viewModel

        setStyles()
        setEditTextsFilter()
        setupButtons()

        return _binding.root
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.addButton.style(R.style.buttonStyleTablet)

            _binding.categoryTitleTextView.style(R.style.wm_labelStyleTablet)
            _binding.categoryEditText.style(R.style.wm_labelStyleTablet)
        }
    }

    private fun setEditTextsFilter() {
        val filter = AddCategoryInputFilter()
        _binding.categoryEditText.filters = arrayOf(filter)
    }

    private fun setupButtons() {
        _binding.addButton.setOnClickListener {
            _viewModel.insertOrUpdateCategory()
            findNavController().popBackStack()
        }

        _binding.categoryEditText.afterTextChanged { s ->
            _binding.addButton.isEnabled = s.isNotEmpty()
        }
    }
}