package com.example.wordsmemory.play

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.TranslateInputFilter
import com.example.wordsmemory.afterTextChanged
import com.example.wordsmemory.databinding.PlayFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val _viewModel: PlayFragmentViewModel by viewModels()
    private lateinit var _binding: PlayFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayFragmentBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.playViewModel = _viewModel

        setStyles()
        setupEditText()
        setupVocabularyButtonListener()
        setupObservers()
        setCategoriesSpinner()

        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        _viewModel.setPlayWord()
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            _binding.acceptTranslationButton.style(R.style.buttonStyleTablet)
            _binding.vocabularyButton.setImageResource(R.drawable.outline_library_books_white_36)

            _binding.randomWordTitleTextView.style(R.style.wm_labelStyleTablet)
            _binding.randomWordTextView.style(R.style.wm_labelStyleTablet)
            _binding.translationEditTextTitle.style(R.style.wm_labelStyleTablet)
            _binding.translationEditText.style(R.style.wm_labelStyleTablet)
            _binding.recentAttemptsTextView.style(R.style.wm_recentAttemptsLabelStyleTablet)

            _binding.topBar.style(R.style.topBarStyleTablet)
            _binding.topBarTitle.style(R.style.topBarTitleTablet)
        }
    }

    private fun setupEditText() {
        val filter = TranslateInputFilter()
        _binding.translationEditText.filters = arrayOf(filter)
        _binding.translationEditText.afterTextChanged { s ->
            _binding.acceptTranslationButton.isEnabled =
                _binding.randomWordTextView.text.isNotEmpty() && s.isNotEmpty()
        }
    }

    private fun setupVocabularyButtonListener() {
        _binding.vocabularyButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_playFragment_to_vocabularyFragment)

            val view = activity?.currentFocus
            if (view != null) {
                val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun setupObservers() {
        _viewModel.isTranslationOk.observe(
            viewLifecycleOwner,
            {
                val text: String =
                    if (it) getString(R.string.right_translation) else getString(R.string.wrong_translation)
                val toast = Toast.makeText(this.context, text, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()

                changeBackgroundColor(it)
            })

        _viewModel.vocabularyList.observe(
            viewLifecycleOwner,
            {
                _viewModel.setPlayWord()
            })
    }

    private fun setCategoriesSpinner() {
        _viewModel.categories.observe(viewLifecycleOwner, {
            it.let {
                val categories = it.map { c -> c.category }.toTypedArray()

                it.forEach { c ->
                    Log.i(
                        "categories",
                        "Category: id - ${c.id}, name - ${c.category}"
                    )
                }

                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
                )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                _binding.categorySpinner.adapter = arrayAdapter
            }
        })

        _binding.categorySpinner.onItemSelectedListener = this
    }

    private fun changeBackgroundColor(rightAnswer: Boolean) {
        val color = if (rightAnswer) resources.getColor(
            R.color.wm_primaryVariant,
            context?.theme
        ) else Color.RED

        lifecycleScope.launch {
            _binding.container.setBackgroundColor(
                ColorUtils.setAlphaComponent(
                    color,
                    200
                )
            )
            delay(170)
            _binding.container.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        _viewModel.resetGamePlay(parent?.getItemAtPosition(position).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        _viewModel.resetGamePlay(Constants.defaultCategory)
    }
}

