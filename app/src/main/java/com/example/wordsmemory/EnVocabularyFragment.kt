package com.example.wordsmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wordsmemory.databinding.FragmentEnVocabularyBinding
import kotlinx.coroutines.InternalCoroutinesApi

class EnVocabularyFragment : Fragment() {
    @InternalCoroutinesApi
    private lateinit var viewModel: EnVocabularyViewModel

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEnVocabularyBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).enVocabularyDao()

        val factory = EnVocabularyViewModelFactory(dbDao)
        viewModel = ViewModelProvider(this, factory).get(EnVocabularyViewModel::class.java)

        binding.enVocabularyViewModel = viewModel

        val vocabularyAdapter = VocabularyItemAdapter()
        binding.vocabularyList.adapter = vocabularyAdapter

        viewModel.vocabularyList.observe(
            viewLifecycleOwner,
            { it?.let { vocabularyAdapter.data = it } })

        return binding.root
    }
}