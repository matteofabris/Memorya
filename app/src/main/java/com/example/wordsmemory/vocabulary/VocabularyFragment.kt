package com.example.wordsmemory.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.databinding.FragmentVocabularyBinding
import com.example.wordsmemory.vocabulary.addvocabularyitem.AddVocabularyItemSheet
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@InternalCoroutinesApi
class VocabularyFragment : Fragment() {
    private lateinit var _binding: FragmentVocabularyBinding
    private var _addClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVocabularyBinding.inflate(inflater)

        _binding.viewPager.adapter = VocabularyFragmentsAdapter(this, 2)
        setStyles()
        setupAddButtonListener()

        TabLayoutMediator(_binding.tabLayout, _binding.viewPager) { tab: TabLayout.Tab, i: Int ->
            tab.text = when (i) {
                0 -> "Vocabulary"
                1 -> "Categories"
                else -> "Empty"
            }
        }.attach()

        return _binding.root
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.addButton.style(R.style.buttonStyleTablet)
            _binding.addButton.setImageResource(R.drawable.outline_add_white_36)

            _binding.topBar.style(R.style.topBarStyleTablet)
            _binding.topBarTitle.style(R.style.topBarTitleTablet)
        }
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