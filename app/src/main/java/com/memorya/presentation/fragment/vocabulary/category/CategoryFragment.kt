package com.memorya.presentation.fragment.vocabulary.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.memorya.R
import com.memorya.databinding.CategoryFragmentBinding
import com.memorya.presentation.fragment.vocabulary.words.VocabularyWordsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private val _viewModel: CategoryViewModel by viewModels()
    private lateinit var _binding: CategoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryFragmentBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.viewModel = _viewModel

        childFragmentManager.beginTransaction()
            .replace(
                R.id.category_vocabulary_container,
                VocabularyWordsFragment(_viewModel.categoryId)
            )
            .commit()

        return _binding.root
    }
}