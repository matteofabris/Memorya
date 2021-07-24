package com.example.wordsmemory.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.databinding.CategoryItemBinding
import com.example.wordsmemory.model.vocabulary.Category

class CategoryItemAdapter(
    private val _onClickListener : OnClickListener,
    private val _onLongClickListener: OnLongClickListener
) : ListAdapter<Category, CategoryItemAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener { _onClickListener.onClick(item) }
        holder.itemView.setOnLongClickListener { _onLongClickListener.onLongClick(item) }
        holder.bind(item)
    }

    class CategoryViewHolder(private val _binding: CategoryItemBinding) : RecyclerView.ViewHolder(_binding.root) {

        var itemId : Int = -1

        fun bind(item: Category) {
            itemId = item.id;
            _binding.categoryTextView.text = item.category
            _binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (item: Category) -> Unit) {
        fun onClick(item: Category) = clickListener(item)
    }

    class OnLongClickListener(val longClickListener : (item: Category) -> Boolean) {
        fun onLongClick(item: Category) = longClickListener(item)
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
