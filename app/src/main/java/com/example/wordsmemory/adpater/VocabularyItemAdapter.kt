package com.example.wordsmemory.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.databinding.VocabularyHeaderBinding
import com.example.wordsmemory.databinding.VocabularyItemBinding
import com.example.wordsmemory.framework.room.entities.VocabularyItemEntity

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class VocabularyItemAdapter(
    private val _onLongClickListener: OnLongClickListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(VocabularyItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> VocabularyItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addHeaderAndSubmitList(list: List<VocabularyItemEntity>?) {
        val items = when (list) {
            null -> listOf(DataItem.Header)
            else -> listOf(DataItem.Header) + list.map { DataItem.EnVocabularyItem(it) }
        }
        submitList(items)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VocabularyItemViewHolder -> {
                val item = getItem(position) as DataItem.EnVocabularyItem
                holder.bind(item.vocabularyItemEntity)
                holder.itemView.setOnLongClickListener {
                    _onLongClickListener.onLongClick(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.EnVocabularyItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class VocabularyItemViewHolder private constructor(private val _binding: VocabularyItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {

        companion object {
            fun from(parent: ViewGroup): VocabularyItemViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding = VocabularyItemBinding.inflate(layoutInflater, parent, false)
                return VocabularyItemViewHolder(binding)
            }
        }

        var itemId: Int = -1

        fun bind(itemEntity: VocabularyItemEntity) {
            itemId = itemEntity.id
            _binding.vocabularyItem = itemEntity
            _binding.executePendingBindings()
        }
    }

    class OnLongClickListener(val longClickListener: (item: DataItem.EnVocabularyItem) -> Boolean) {
        fun onLongClick(item: DataItem.EnVocabularyItem) = longClickListener(item)
    }

    class VocabularyItemDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    class HeaderViewHolder private constructor(_binding: VocabularyHeaderBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VocabularyHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }
}

sealed class DataItem {
    abstract val id: Int

    data class EnVocabularyItem(val vocabularyItemEntity: VocabularyItemEntity) : DataItem() {
        override val id = vocabularyItemEntity.id
    }

    object Header : DataItem() {
        override val id = Int.MIN_VALUE
    }
}

