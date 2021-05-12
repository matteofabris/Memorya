package com.example.wordsmemory.vocabulary.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wordsmemory.R
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.database.VocabularyDatabase
import com.example.wordsmemory.databinding.CategoryFragmentBinding
import com.example.wordsmemory.vocabulary.words.VocabularyWordsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var _dbDao: VocabularyDao
    private val _viewModel: CategoryViewModel by viewModels()
    private lateinit var _binding: CategoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryFragmentBinding.inflate(inflater)
        _binding.categoryViewmodel = _viewModel
        _dbDao = getDbDao()

        childFragmentManager.beginTransaction()
            .replace(R.id.category_vocabulary_container, VocabularyWordsFragment())
            .commit()

        return _binding.root
    }

    private fun getDbDao(): VocabularyDao {
        val application = requireNotNull(this.activity).application
        return VocabularyDatabase.getInstance(application).vocabularyDao()
    }
}