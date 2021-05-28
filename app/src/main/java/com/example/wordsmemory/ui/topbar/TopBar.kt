package com.example.wordsmemory.ui.topbar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import com.example.wordsmemory.R
import com.google.android.material.appbar.AppBarLayout

class TopBar(context: Context, attrs: AttributeSet) : AppBarLayout(context, attrs) {

    private var _isVocabularyButtonVisible = false

    var title: String = ""
        set(value) {
            findViewById<TextView>(R.id.topBarTitle).text = value
            field = value
        }

    init {
        inflate(context, R.layout.top_bar, this)

        obtainStyledAttributes(context, attrs)

        findViewById<ImageButton>(R.id.vocabularyButton).visibility =
            if (_isVocabularyButtonVisible) VISIBLE else INVISIBLE
    }

    private fun obtainStyledAttributes(context: Context, attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TopBar, 0, 0).apply {
            try {
                _isVocabularyButtonVisible =
                    getBoolean(R.styleable.TopBar_isVocabularyButtonVisible, false)
                title = getString(R.styleable.TopBar_title) ?: ""
            } finally {
                recycle()
            }
        }
    }

    fun setVocabularyButtonAction(action: () -> Unit) {
        findViewById<ImageButton>(R.id.vocabularyButton).setOnClickListener {
            action.invoke()
        }
    }
}