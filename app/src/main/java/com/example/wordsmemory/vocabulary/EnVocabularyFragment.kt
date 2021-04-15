package com.example.wordsmemory.vocabulary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.VocabularyDatabase
import com.example.wordsmemory.databinding.FragmentEnVocabularyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class EnVocabularyFragment : Fragment() {

    private lateinit var _viewModel: EnVocabularyViewModel
    private lateinit var _binding: FragmentEnVocabularyBinding
    private var _addClicked = false

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnVocabularyBinding.inflate(inflater)

        createViewModel()
        setStyles()
        setupVocabularyList()
        setupAddButtonListener()

        return _binding.root
    }

    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).enVocabularyDao()

        val factory = EnVocabularyViewModelFactory(dbDao)
        _viewModel = ViewModelProvider(this, factory).get(EnVocabularyViewModel::class.java)

        _binding.enVocabularyViewModel = _viewModel
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.addButton.style(R.style.buttonStyleTablet)
            _binding.addButton.setImageResource(R.drawable.outline_add_white_36)

            _binding.topBar.style(R.style.topBarStyleTablet)
            _binding.topBarTitle.style(R.style.topBarTitleTablet)
        }
    }

    private fun setupVocabularyList() {
        val vocabularyAdapter = VocabularyItemAdapter()
        _binding.vocabularyList.adapter = vocabularyAdapter

        setListDivider()
        setSwipeGesture()

        _viewModel.vocabularyList.observe(
            viewLifecycleOwner,
            { it?.let { vocabularyAdapter.addHeaderAndSubmitList(it) } })
    }

    private fun setListDivider() {
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            getDrawable(
                requireContext(),
                R.drawable.vocabulary_list_divider
            )!!
        )
        _binding.vocabularyList.addItemDecoration(itemDecoration)
    }

    private fun setSwipeGesture() {
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                _viewModel.removeItem((viewHolder as ViewHolder).itemId)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(_binding.vocabularyList)
    }

    private fun setupAddButtonListener() {
        _binding.addButton.setOnClickListener {
            if (!_addClicked) {
                _addClicked = true

                val addVocabularyItemBottomFragment = AddVocabularyItemSheet.newInstance()
                addVocabularyItemBottomFragment.show(parentFragmentManager, "add_word")

                lifecycleScope.launch(Dispatchers.IO) {
                    delay(500)
                    _addClicked = false
                }
            }
        }
    }
}