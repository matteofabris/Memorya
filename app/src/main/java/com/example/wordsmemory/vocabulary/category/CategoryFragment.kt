package com.example.wordsmemory.vocabulary.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.wordsmemory.R
import com.example.wordsmemory.VocabularyDao
import com.example.wordsmemory.VocabularyDatabase
import com.example.wordsmemory.databinding.CategoryFragmentBinding
import com.example.wordsmemory.vocabulary.words.VocabularyWordsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class CategoryFragment : Fragment() {

    private lateinit var _dbDao: VocabularyDao
    private lateinit var _viewModel: CategoryViewModel
    private lateinit var _binding: CategoryFragmentBinding
    private val args: CategoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryFragmentBinding.inflate(inflater)
        _dbDao = getDbDao()

        createViewModel()
        setTopBarTitle()

        childFragmentManager.beginTransaction()
            .replace(R.id.category_vocabulary_container, VocabularyWordsFragment())
            .commit()

        return _binding.root
    }

    private fun createViewModel() {
        val factory = CategoryViewModelFactory()
        _viewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)
        _binding.categoryViewmodel = _viewModel
    }

    private fun setTopBarTitle() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cat = _dbDao.getCategoryName(args.categoryId)
            lifecycleScope.launch(Dispatchers.Main) { _binding.topBarTitle.text = cat }
        }
    }

    private fun getDbDao(): VocabularyDao {
        val application = requireNotNull(this.activity).application
        return VocabularyDatabase.getInstance(application).vocabularyDao()
    }
}