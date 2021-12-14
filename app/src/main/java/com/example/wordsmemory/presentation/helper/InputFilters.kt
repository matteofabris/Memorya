package com.example.wordsmemory.presentation.helper

import android.text.InputFilter
import android.text.Spanned

class EnWordInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source == null) return dest?.subSequence(dstart, dend) ?: ""

        val regex1 = Regex("[\\w&&\\D]*")
        val regex2 = Regex("to ")
        if (regex1.matches(source) || (dstart == 0 && regex2.matches(source)))
            return null

        return dest?.subSequence(dstart, dend) ?: ""
    }
}

class ItWordInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source == null) return dest?.subSequence(dstart, dend) ?: ""

        val regex1 = Regex("[\\w&&\\D]*")
        if (regex1.matches(source))
            return null

        return dest?.subSequence(dstart, dend) ?: ""
    }
}

class CategoryInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source == null) return dest?.subSequence(dstart, dend) ?: ""

        val regex1 = Regex("[\\w]*")
        if (regex1.matches(source))
            return null

        return dest?.subSequence(dstart, dend) ?: ""
    }
}