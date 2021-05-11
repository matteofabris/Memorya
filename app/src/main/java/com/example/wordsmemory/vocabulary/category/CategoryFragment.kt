package com.example.wordsmemory.vocabulary.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.wordsmemory.R
import com.example.wordsmemory.database.VocabularyDao
import com.example.wordsmemory.database.VocabularyDatabase
import com.example.wordsmemory.databinding.CategoryFragmentBinding
import com.example.wordsmemory.vocabulary.words.VocabularyWordsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var _dbDao: VocabularyDao
    private val _viewModel: CategoryViewModel by viewModels()
    private lateinit var _binding: CategoryFragmentBinding
    private val _args: CategoryFragmentArgs by navArgs()
    private var _categoryId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _categoryId = _args.categoryId
        _binding = CategoryFragmentBinding.inflate(inflater)
        _binding.categoryViewmodel = _viewModel
        _dbDao = getDbDao()

        setTopBarTitle()

        childFragmentManager.beginTransaction()
            .replace(R.id.category_vocabulary_container, VocabularyWordsFragment(_categoryId))
            .commit()

        return _binding.root
    }

    private fun setTopBarTitle() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cat = _dbDao.getCategoryName(_categoryId)
            lifecycleScope.launch(Dispatchers.Main) { _binding.topBarTitle.text = cat }
        }
    }

    private fun getDbDao(): VocabularyDao {
        val application = requireNotNull(this.activity).application
        return VocabularyDatabase.getInstance(application).vocabularyDao()
    }
}