package com.example.wordsmemory.vocabulary.addoreditcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.*
import com.example.wordsmemory.database.VocabularyDatabase
import com.example.wordsmemory.databinding.AddCategorySheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class AddOrEditCategorySheet(private val _selectedCategoryId: Int? = null) : BottomSheetDialogFragment() {

    private lateinit var _viewModel: AddCategorySheetViewModel
    private lateinit var _binding: AddCategorySheetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddCategorySheetFragmentBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner

        createViewModel()
        setStyles()
        setEditTextsFilter()
        setupButtons()

        return _binding.root
    }

    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).vocabularyDao()

        val factory = AddCategorySheetViewModelFactory(dbDao, _selectedCategoryId)
        _viewModel =
            ViewModelProvider(this, factory).get(AddCategorySheetViewModel::class.java)
        _binding.addCategoryViewModel = _viewModel
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
            dismiss()
        }

        _binding.categoryEditText.afterTextChanged { s ->
            _binding.addButton.isEnabled = s.isNotEmpty()
        }
    }
}