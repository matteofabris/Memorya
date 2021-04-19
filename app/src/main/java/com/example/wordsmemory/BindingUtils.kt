package com.example.wordsmemory

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("enWord")
fun TextView.setDataToEnWord(item: VocabularyItem) {
    text = item.enWord
}

@BindingAdapter("itWord")
fun TextView.setDataToItWord(item: VocabularyItem) {
    text = item.itWord
}