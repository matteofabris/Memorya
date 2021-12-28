package com.memorya.presentation.fragment.vocabulary.words

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.memorya.R
import com.memorya.presentation.adpater.VocabularyItemAdapter
import com.memorya.databinding.VocabularyWordsFragmentBinding
import com.memorya.presentation.helper.SwipeToDeleteCallback
import com.memorya.presentation.fragment.vocabulary.VocabularyFragment
import com.memorya.presentation.fragment.vocabulary.VocabularyFragmentDirections
import com.memorya.presentation.fragment.vocabulary.category.CategoryFragmentDirections
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class VocabularyWordsFragment(private val _categoryId: Int = -1) : Fragment() {

    private val _viewModel: VocabularyWordsViewModel by viewModels()
    private lateinit var _binding: VocabularyWordsFragmentBinding
    private var _addClicked = false
    private val _showAddOrEditVocabularyItemSheet: (Int) -> Boolean = {
        if (!_addClicked) {
            _addClicked = true

            val navDirection =
                if (parentFragment is VocabularyFragment)
                    VocabularyFragmentDirections.actionVocabularyFragmentToAddOrEditVocabularyItemSheet(
                        it
                    )
                else CategoryFragmentDirections.actionCategoryFragmentToAddOrEditVocabularyItemSheet(
                    it
                )

            findNavController().navigate(navDirection)

            lifecycleScope.launch(Dispatchers.IO) {
                delay(500)
                _addClicked = false
            }
            true
        } else false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewModel.initVocabularyList(_categoryId)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VocabularyWordsFragmentBinding.inflate(inflater)
        _binding.viewModel = _viewModel

        setupVocabularyList()
        setupAddButtonListener()

        return _binding.root
    }

    private fun setupVocabularyList() {
        val vocabularyAdapter = VocabularyItemAdapter(
            VocabularyItemAdapter.OnLongClickListener { _showAddOrEditVocabularyItemSheet.invoke(it.id) }
        )
        _binding.vocabularyList.adapter = vocabularyAdapter
        vocabularyAdapter.addHeaderAndSubmitList(_viewModel.vocabularyList.value)

        setListDivider()
        setSwipeGesture()

        _viewModel.vocabularyList.observe(
            viewLifecycleOwner,
            { it?.let { vocabularyAdapter.addHeaderAndSubmitList(it) } })
    }

    private fun setupAddButtonListener() {
        _binding.addButton.setOnClickListener {
            _showAddOrEditVocabularyItemSheet.invoke(-1)
        }
    }

    private fun setListDivider() {
        if (context == null) return

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val listDivider =
            ContextCompat.getDrawable(requireContext(), R.drawable.vocabulary_list_divider)
                ?: return

        itemDecoration.setDrawable(listDivider)
        _binding.vocabularyList.addItemDecoration(itemDecoration)
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
                                _viewModel.deleteItem((viewHolder as VocabularyItemAdapter.VocabularyItemViewHolder).itemId)
                            } else {
                                _binding.vocabularyList.adapter?.notifyItemChanged(viewHolder.layoutPosition)
                            }
                        }
                    })
                    .show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(_binding.vocabularyList)
    }
}