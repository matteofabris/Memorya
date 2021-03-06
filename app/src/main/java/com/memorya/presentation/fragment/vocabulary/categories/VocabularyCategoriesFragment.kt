package com.memorya.presentation.fragment.vocabulary.categories

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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.memorya.R
import com.memorya.databinding.VocabularyCategoriesFragmentBinding
import com.memorya.presentation.adpater.CategoryItemAdapter
import com.memorya.presentation.fragment.vocabulary.VocabularyFragmentDirections
import com.memorya.presentation.helper.SwipeToDeleteCallback
import com.memorya.utils.createBalloon
import com.memorya.utils.getBooleanPref
import com.memorya.utils.setBooleanPref
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import com.skydoves.balloon.showAlignTop
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
        _binding.viewModel = _viewModel

        setupCategoriesList()
        setupAddButtonListener()

        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        // Workaround to be sure that the add button is positioned correctly
        showTips()
    }

    private fun setupCategoriesList() {
        val categoryItemAdapter = CategoryItemAdapter(
            CategoryItemAdapter.OnClickListener {
                _showSelectedCategoryFragment.invoke(it.id)
            },
            CategoryItemAdapter.OnLongClickListener {
                _showAddOrEditCategorySheet.invoke(it.id)
            }
        )
        _binding.categoriesList.adapter = categoryItemAdapter

        setSwipeGesture()

        _viewModel.categories.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val filteredList = it.toMutableList().filter { c -> c.id != 1 }
                categoryItemAdapter.submitList(filteredList)
            }
        }
    }

    private fun setupAddButtonListener() {
        _binding.addButton.setOnClickListener {
            _showAddOrEditCategorySheet.invoke(-1)
        }
    }

    private fun setSwipeGesture() {
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var delete = true
                Snackbar.make(_binding.coordinatorLayout, R.string.deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { delete = false }
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (delete) {
                                _viewModel.deleteCategory((viewHolder as CategoryItemAdapter.CategoryViewHolder).itemId)
                            } else {
                                _binding.categoriesList.adapter?.notifyItemChanged(viewHolder.layoutPosition)
                            }
                        }
                    })
                    .show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(_binding.categoriesList)
    }

    private fun showTips() {
        if (requireActivity().getBooleanPref(getString(R.string.vocabulary_categories_fragment_tips_shown_key))) return

        val categorySectionBalloon = createBalloon(
            getString(R.string.category_section_balloon_text),
            BalloonOverlayRoundRect(12f, 12f)
        )

        _binding.addButton.showAlignTop(categorySectionBalloon)

        requireActivity().setBooleanPref(
            getString(R.string.vocabulary_categories_fragment_tips_shown_key),
            true
        )
    }
}