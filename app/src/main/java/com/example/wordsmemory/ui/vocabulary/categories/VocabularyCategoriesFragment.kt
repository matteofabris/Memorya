package com.example.wordsmemory.ui.vocabulary.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.adpater.CategoryItemAdapter
import com.example.wordsmemory.databinding.VocabularyCategoriesFragmentBinding
import com.example.wordsmemory.ui.vocabulary.SwipeToDeleteCallback
import com.example.wordsmemory.ui.vocabulary.VocabularyFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class VocabularyCategoriesFragment : Fragment() {

    private val _viewModel: VocabularyCategoriesViewModel by viewModels()
    private lateinit var _binding: VocabularyCategoriesFragmentBinding
    private lateinit var _categoryItemAdapter : CategoryItemAdapter

    private var _addClicked = false
    private val _showAddOrEditCategorySheet: (Int) -> Boolean = {
        if (!_addClicked) {
            _addClicked = true

            findNavController().navigate(
                VocabularyFragmentDirections.actionVocabularyFragmentToAddOrEditCategorySheet(
                    it
                )
            )

            lifecycleScope.launch(Dispatchers.IO) {
                delay(500)
                _addClicked = false
            }
            true
        } else false
    }
    private val _showSelectedCategoryFragment: (Int) -> Unit = {
        val action =
            VocabularyFragmentDirections.actionVocabularyFragmentToCategoryFragment(it)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VocabularyCategoriesFragmentBinding.inflate(inflater)
        _binding.enVocabularyViewModel = _viewModel

        setupCategoriesList()
        setupAddButtonListener()

        return _binding.root
    }

    private fun setupCategoriesList() {

        _categoryItemAdapter = CategoryItemAdapter(
            CategoryItemAdapter.OnClickListener {
                _showSelectedCategoryFragment.invoke(it.id)
            },
            CategoryItemAdapter.OnLongClickListener {
                _showAddOrEditCategorySheet.invoke(it.id)
            }
        )
        _binding.categoriesList.adapter = _categoryItemAdapter

        setSwipeGesture()

        _viewModel.categories.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    val filteredList = it.toMutableList().filter { c -> c.id != 1 }
                    _categoryItemAdapter.submitList(filteredList)
                }
            })
    }

    private fun setupAddButtonListener() {
        _binding.addButton.setOnClickListener {
            _showAddOrEditCategorySheet.invoke(-1)
        }
    }

    private fun setSwipeGesture() {
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                _viewModel.removeItem((viewHolder as CategoryItemAdapter.CategoryViewHolder).itemId)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(_binding.categoriesList)
    }
}