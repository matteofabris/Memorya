package com.example.wordsmemory.vocabulary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.databinding.VocabularyItemBinding

class VocabularyItemAdapter : ListAdapter<EnVocabulary, ViewHolder>(EnVocabularyDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class EnVocabularyDiffCallback : DiffUtil.ItemCallback<EnVocabulary>() {
    override fun areItemsTheSame(oldItem: EnVocabulary, newItem: EnVocabulary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EnVocabulary, newItem: EnVocabulary): Boolean {
        return oldItem == newItem
    }

}

class ViewHolder private constructor(private val binding: VocabularyItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater =
                LayoutInflater.from(parent.context)
            val binding = VocabularyItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }

    fun bind(item: EnVocabulary) {
        binding.vocabularyItem = item
        binding.executePendingBindings()
    }
}