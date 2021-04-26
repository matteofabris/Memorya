package com.example.wordsmemory.vocabulary.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.Category
import com.example.wordsmemory.databinding.CategoryItemBinding

class CategoryItemAdapter(
    private val _navigateToCategoryFragment: (Int) -> Unit,
    private val _onCategoryLongClickAction: (Int) -> Boolean
) : ListAdapter<Category, RecyclerView.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as Category
                val onItemClickedAction: (View) -> Unit = {
                    _navigateToCategoryFragment.invoke(item.id)
                }
                holder.setLayout(item)
                holder.itemView.setOnClickListener(onItemClickedAction)
                holder.itemView.setOnLongClickListener {
                    _onCategoryLongClickAction.invoke(item.id)
                }
            }
        }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}

class ViewHolder private constructor(private val _binding: CategoryItemBinding) :
    RecyclerView.ViewHolder(_binding.root) {

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater =
                LayoutInflater.from(parent.context)
            val binding = CategoryItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }

    var itemId: Int = -1

    fun setLayout(item: Category) {
        itemId = item.id
        _binding.categoryTextView.text = item.category
        _binding.executePendingBindings()
    }
}