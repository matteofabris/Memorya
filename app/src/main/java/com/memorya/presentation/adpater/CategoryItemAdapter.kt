package com.memorya.presentation.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.memorya.databinding.CategoryItemBinding
import com.memorya.framework.room.entities.CategoryEntity

class CategoryItemAdapter(
    private val _onClickListener : OnClickListener,
    private val _onLongClickListener: OnLongClickListener
) : ListAdapter<CategoryEntity, CategoryItemAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

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

        fun bind(item: CategoryEntity) {
            itemId = item.id;
            _binding.categoryTextView.text = item.category
            _binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (item: CategoryEntity) -> Unit) {
        fun onClick(item: CategoryEntity) = clickListener(item)
    }

    class OnLongClickListener(val longClickListener : (item: CategoryEntity) -> Boolean) {
        fun onLongClick(item: CategoryEntity) = longClickListener(item)
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}
