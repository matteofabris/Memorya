package com.example.wordsmemory

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("enWord")
fun TextView.setDataToEnWord(item: EnVocabulary) {
    text = item.enWord
}

@BindingAdapter("itWord")
fun TextView.setDataToItWord(item: EnVocabulary) {
    text = item.itWord
}