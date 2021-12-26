package com.example.wordsmemory.presentation.fragment.play

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordsmemory.Constants
import com.example.wordsmemory.R
import com.example.wordsmemory.databinding.PlayFragmentBinding
import com.example.wordsmemory.presentation.helper.EnWordInputFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PlayFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val _viewModel: PlayFragmentViewModel by viewModels()
    private lateinit var _binding: PlayFragmentBinding
    private val _activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            _viewModel.manageAuthResult(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayFragmentBinding.inflate(inflater)
        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.viewModel = _viewModel
        _binding.translation.filters = arrayOf(EnWordInputFilter())

        setTopBarButtonsListeners()
        setupObservers()
        setCategoriesSpinner()

        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        _viewModel.setPlayWord()
    }

    private fun authenticate() {
        if (_viewModel.isAuthenticated()) {
            _viewModel.onAuthenticationOk()
        } else {
            _viewModel.signInClient.signInIntent.apply {
                _activityResultLauncher.launch(this)
            }
        }
    }

    private fun setTopBarButtonsListeners() {
        _binding.topBar.setVocabularyButtonAction {
            findNavController().navigate(R.id.action_playFragment_to_vocabularyFragment)

            val view = activity?.currentFocus
            if (view != null) {
                val inputMethodManager =
                    activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        _binding.topBar.setLogoutButtonAction {
            _viewModel.signOut()
        }
    }

    private fun setupObservers() {
        _viewModel.translationText.observe(viewLifecycleOwner, { s ->
            _binding.checkTranslationButton.isEnabled =
                _binding.randomWord.text.isNotEmpty() && s.isNotEmpty()
        })

        _viewModel.allAttempts.observe(viewLifecycleOwner, { allAttempts ->
            val recentAttempts = getString(R.string.recent_attempts)
            val correctAttempts = _viewModel.correctAttempts
            val correct = getString(R.string.correct)

            ("$recentAttempts $correctAttempts/$allAttempts $correct")
                .also { _binding.recentAttemptsTextView.text = it }
        })

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

        _viewModel.isAuthenticated.observe(viewLifecycleOwner,
            {
                setPlayBoardVisible(it)
                if (!it) authenticate()
            })
    }

    private fun setCategoriesSpinner() {
        _viewModel.categories.observe(viewLifecycleOwner, {
            it.let {
                val categories = it.map { c -> c.category }.toTypedArray()

                it.forEach { c ->
                    Timber.i("Category: id - " + c.id + ", name - " + c.category)
                }

                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item,
                    categories
                )
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item)

                _binding.categorySpinner.adapter = arrayAdapter
            }
        })

        _binding.categorySpinner.onItemSelectedListener = this
    }

    private fun changeBackgroundColor(rightAnswer: Boolean) {
        val color = if (rightAnswer) resources.getColor(
            R.color.palette_color_3,
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

    private fun setPlayBoardVisible(visible: Boolean) {
        _binding.playBoard.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        _binding.recentAttemptsTextView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        _binding.checkTranslationButton.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        _binding.categoryTextView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        _binding.categorySpinner.visibility = if (visible) View.VISIBLE else View.INVISIBLE

        _binding.topBar.isButtonsVisible = visible
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        _viewModel.resetGamePlay(parent?.getItemAtPosition(position).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        _viewModel.resetGamePlay(Constants.defaultCategory)
    }
}