package com.example.wordsmemory.vocabulary.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wordsmemory.R
import com.example.wordsmemory.databinding.CategoryFragmentBinding
import com.example.wordsmemory.vocabulary.words.VocabularyWordsFragment
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class CategoryFragment : Fragment() {

    private lateinit var _viewModel: CategoryViewModel
    private lateinit var _binding: CategoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryFragmentBinding.inflate(inflater)
        _viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        childFragmentManager.beginTransaction()
            .replace(R.id.category_vocabulary_container, VocabularyWordsFragment())
            .commit()

        return _binding.root
    }
}