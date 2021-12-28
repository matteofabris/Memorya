package com.memorya.presentation.fragment.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.memorya.R
import com.memorya.presentation.adpater.VocabularyFragmentsAdapter
import com.memorya.databinding.VocabularyFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@AndroidEntryPoint
class VocabularyFragment : Fragment() {

    private val _viewModel: VocabularyViewModel by viewModels()
    private lateinit var _binding: VocabularyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VocabularyFragmentBinding.inflate(inflater)
        _binding.viewModel = _viewModel
        _binding.viewPager.adapter = VocabularyFragmentsAdapter(this, 2)

        setTabLayoutMediator()

        return _binding.root
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