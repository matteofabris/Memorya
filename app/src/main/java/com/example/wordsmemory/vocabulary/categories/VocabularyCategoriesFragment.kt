package com.example.wordsmemory.vocabulary.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.VocabularyDatabase
import com.example.wordsmemory.databinding.VocabularyCategoriesFragmentBinding
import com.example.wordsmemory.vocabulary.VocabularyFragmentDirections
import com.example.wordsmemory.vocabulary.addcategory.AddCategorySheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class VocabularyCategoriesFragment : Fragment() {

    private lateinit var _viewModel: VocabularyCategoriesViewModel
    private lateinit var _binding: VocabularyCategoriesFragmentBinding
    private var _addClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VocabularyCategoriesFragmentBinding.inflate(inflater)

        createViewModel()
        setupVocabularyList()
        setStyles()
        setupAddButtonListener()

        return _binding.root
    }

    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).vocabularyDao()

        val factory = VocabularyCategoriesViewModelFactory(dbDao)
        _viewModel = ViewModelProvider(this, factory).get(VocabularyCategoriesViewModel::class.java)
        _binding.enVocabularyViewModel = _viewModel
    }

    private fun setupVocabularyList() {
        val categoryAdapter =
            CategoryItemAdapter {
                val action =
                    VocabularyFragmentDirections.actionVocabularyFragmentToCategoryFragment(it)
                findNavController().navigate(action)
            }
        _binding.categoriesList.adapter = categoryAdapter

        _viewModel.categories.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    val filteredList = it.toMutableList().filter { c -> c.id != 1 }
                    categoryAdapter.submitList(filteredList)
                }
            })
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.addButton.style(R.style.buttonStyleTablet)
        }
    }

    private fun setupAddButtonListener() {
        _binding.addButton.setOnClickListener {
            if (!_addClicked) {
                _addClicked = true

                val addCategoryBottomFragment = AddCategorySheet.newInstance()
                addCategoryBottomFragment.show(parentFragmentManager, "add_category")

                lifecycleScope.launch(Dispatchers.IO) {
                    delay(500)
                    _addClicked = false
                }
            }
        }
    }
}