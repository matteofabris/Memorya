package com.example.wordsmemory.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.databinding.VocabularyFragmentBinding
import com.example.wordsmemory.vocabulary.categories.VocabularyCategoriesViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject


@InternalCoroutinesApi
@AndroidEntryPoint
class VocabularyFragment : Fragment() {

    @Inject
    lateinit var viewModel: VocabularyViewModel
    private lateinit var _binding: VocabularyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VocabularyFragmentBinding.inflate(inflater)
        _binding.vocabularyViewmodel = viewModel

        _binding.viewPager.adapter = VocabularyFragmentsAdapter(this, 2)
        setStyles()
        setTabLayoutMediator()

        return _binding.root
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.topBar.style(R.style.topBarStyleTablet)
            _binding.topBarTitle.style(R.style.topBarTitleTablet)
        }
    }

    private fun setTabLayoutMediator() {
        TabLayoutMediator(_binding.tabLayout, _binding.viewPager) { tab: TabLayout.Tab, i: Int ->
            tab.text = when (i) {
                0 -> getString(R.string.all_words)
                1 -> getString(R.string.categories)
                else -> "Empty"
            }
        }.attach()
    }
}