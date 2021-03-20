package com.example.wordsmemory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VocabularyItemAdapter : RecyclerView.Adapter<ViewHolder>() {
    var data = listOf<EnVocabulary>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size
}

class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val enWord: TextView = itemView.findViewById(R.id.enWordTextView)
    private val itWord: TextView = itemView.findViewById(R.id.itWordTextView)

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater =
                LayoutInflater.from(parent.context)
            val view = layoutInflater
                .inflate(
                    R.layout.vocabulary_item,
                    parent, false
                )
            return ViewHolder(view)
        }
    }

    fun bind(item: EnVocabulary) {
        enWord.text = item.enWord
        itWord.text = item.itWord
    }
}