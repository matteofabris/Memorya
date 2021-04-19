package com.example.wordsmemory.vocabulary.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.Category
import com.example.wordsmemory.databinding.CategoryItemBinding

class CategoryItemAdapter(private val _itemClickListener: (View) -> Unit) :
    ListAdapter<Category, RecyclerView.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as Category
                holder.setLayout(item)
                holder.itemView.setOnClickListener(_itemClickListener)
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

    fun setLayout(item: Category) {
        _binding.categoryTextView.text = item.category
        _binding.executePendingBindings()
    }
}