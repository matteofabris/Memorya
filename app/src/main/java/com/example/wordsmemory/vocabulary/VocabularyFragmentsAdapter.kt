package com.example.wordsmemory.vocabulary

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wordsmemory.vocabulary.allwords.EnVocabularyFragment
import com.example.wordsmemory.vocabulary.categories.VocabularyCategoriesFragment
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class VocabularyFragmentsAdapter(fragment: Fragment, private val itemsCount: Int) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return itemsCount // Use itemsCount??
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return EnVocabularyFragment()
            1 -> return VocabularyCategoriesFragment()
        }

        return Fragment()
    }
}