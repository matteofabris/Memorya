package com.example.wordsmemory.vocabulary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.VocabularyDatabase
import com.example.wordsmemory.databinding.FragmentEnVocabularyBinding
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class EnVocabularyFragment : Fragment() {

    private lateinit var viewModel: EnVocabularyViewModel
    private lateinit var binding: FragmentEnVocabularyBinding

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnVocabularyBinding.inflate(inflater)

        createViewModel()
        setStyles()
        setupVocabularyList()
        setupAddButtonListener()

        return binding.root
    }

    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).enVocabularyDao()

        val factory = EnVocabularyViewModelFactory(dbDao)
        viewModel = ViewModelProvider(this, factory).get(EnVocabularyViewModel::class.java)

        binding.enVocabularyViewModel = viewModel
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            binding.addButton.style(R.style.buttonStyleTablet)
            binding.addButton.setImageResource(R.drawable.outline_add_white_36)

            binding.topBar.style(R.style.topBarStyleTablet)
            binding.topBarTitle.style(R.style.topBarTitleTablet)
        }
    }

    private fun setupVocabularyList() {
        val vocabularyAdapter = VocabularyItemAdapter()
        binding.vocabularyList.adapter = vocabularyAdapter

        setListDivider()
        setSwipeGesture()

        viewModel.vocabularyList.observe(
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
        binding.vocabularyList.addItemDecoration(itemDecoration)
    }

    private fun setSwipeGesture() {
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.removeItem((viewHolder as ViewHolder).itemId)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.vocabularyList)
    }

    private fun setupAddButtonListener() {
        binding.addButton.setOnClickListener {
            val addVocabularyItemBottomFragment = AddVocabularyItemSheet.newInstance()
            addVocabularyItemBottomFragment.show(parentFragmentManager, "add_word")
        }
    }
}