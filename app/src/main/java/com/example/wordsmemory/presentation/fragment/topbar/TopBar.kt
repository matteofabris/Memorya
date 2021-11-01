package com.example.wordsmemory.presentation.fragment.topbar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import com.example.wordsmemory.R
import com.google.android.material.appbar.AppBarLayout

class TopBar(context: Context, attrs: AttributeSet) : AppBarLayout(context, attrs) {

    var isButtonsVisible: Boolean = false
        set(value) {
            field = value
            findViewById<ImageButton>(R.id.vocabularyButton).visibility =
                if (value) VISIBLE else INVISIBLE
            findViewById<ImageButton>(R.id.logoutButton).visibility =
                if (value) VISIBLE else INVISIBLE
        }

    var title: String = ""
        set(value) {
            field = value
            findViewById<TextView>(R.id.topBarTitle).text = value
        }

    init {
        inflate(context, R.layout.top_bar, this)
        obtainStyledAttributes(context, attrs)
    }

    private fun obtainStyledAttributes(context: Context, attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TopBar, 0, 0).apply {
            try {
                isButtonsVisible =
                    getBoolean(R.styleable.TopBar_isButtonsVisible, false)
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

    fun setLogoutButtonAction(action: () -> Unit) {
        findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            action.invoke()
        }
    }
}