package com.example.wordsmemory.vocabulary.categories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wordsmemory.R

class VocabularyCategoriesFragment : Fragment() {

    companion object {
        fun newInstance() = VocabularyCategoriesFragment()
    }

    private lateinit var viewModel: VocabularyCategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vocabulary_categories_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VocabularyCategoriesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}