package com.example.wordsmemory.vocabulary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.EnVocabulary
import com.example.wordsmemory.R
import com.example.wordsmemory.databinding.VocabularyItemBinding

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class VocabularyItemAdapter :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(EnVocabularyDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addHeaderAndSubmitList(list: List<EnVocabulary>?) {
        val items = when (list) {
            null -> listOf(DataItem.Header)
            else -> listOf(DataItem.Header) + list.map { DataItem.EnVocabularyItem(it) }
        }
        submitList(items)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.EnVocabularyItem
                holder.bind(item.vocabulary)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.EnVocabularyItem -> ITEM_VIEW_TYPE_ITEM
        }
    }
}

class EnVocabularyDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
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

class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.vocabulary_header, parent, false)
            return HeaderViewHolder(view)
        }
    }
}

sealed class DataItem {
    abstract val id: Int

    data class EnVocabularyItem(val vocabulary: EnVocabulary) : DataItem() {
        override val id = vocabulary.id
    }

    object Header : DataItem() {
        override val id = Int.MIN_VALUE
    }
}