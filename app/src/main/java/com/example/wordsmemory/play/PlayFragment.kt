package com.example.wordsmemory.play

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.airbnb.paris.extensions.style
import com.example.wordsmemory.*
import com.example.wordsmemory.databinding.FragmentPlayBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayFragment : Fragment() {

    private lateinit var viewModel: PlayFragmentViewModel
    private lateinit var binding: FragmentPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayBinding.inflate(inflater)

        createViewModel()

        binding.playViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setStyles()
        setupEditText()
        setupVocabularyButtonListener()
        setupResultObserver()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPlayWord()
    }

    @InternalCoroutinesApi
    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).enVocabularyDao()

        val factory = PlayFragmentViewModelFactory(dbDao)
        viewModel = ViewModelProvider(this, factory).get(PlayFragmentViewModel::class.java)
    }

    private fun setStyles() {
        if (Constants.isTablet) {
            binding.acceptTranslationButton.style(R.style.buttonStyleTablet)
            binding.vocabularyButton.setImageResource(R.drawable.outline_library_books_white_36)

            binding.randomWordTitleTextView.style(R.style.wm_labelStyleTablet)
            binding.randomWordTextView.style(R.style.wm_labelStyleTablet)
            binding.translationEditTextTitle.style(R.style.wm_labelStyleTablet)
            binding.translationEditText.style(R.style.wm_labelStyleTablet)
            binding.recentAttemptsTextView.style(R.style.wm_recentAttemptsLabelStyleTablet)

            binding.topBar.style(R.style.topBarStyleTablet)
            binding.topBarTitle.style(R.style.topBarTitleTablet)
        }
    }

    private fun setupEditText() {
        val filter = TranslateInputFilter()
        binding.translationEditText.filters = arrayOf(filter)
        binding.translationEditText.afterTextChanged { s ->
            binding.acceptTranslationButton.isEnabled = s.isNotEmpty()
        }
    }

    private fun setupVocabularyButtonListener() {
        binding.vocabularyButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_playFragment_to_enVocabularyFragment)

            val view = activity?.currentFocus
            if (view != null) {
                val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun setupResultObserver() {
        viewModel.isTranslationOk.observe(
            viewLifecycleOwner,
            {
                val text: String =
                    if (it) getString(R.string.right_translation) else getString(R.string.wrong_translation)
                val toast = Toast.makeText(this.context, text, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()

                changeBackgroundColor(it)
            })
    }

    private fun changeBackgroundColor(rightAnswer: Boolean) {
        val color = if (rightAnswer) resources.getColor(
            R.color.wm_primaryVariant,
            context?.theme
        ) else Color.RED

        lifecycleScope.launch {
            binding.container.setBackgroundColor(
                ColorUtils.setAlphaComponent(
                    color,
                    200
                )
            )
            delay(170)
            binding.container.setBackgroundColor(Color.TRANSPARENT)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.main_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (NavigationUI.onNavDestinationSelected(item, requireView().findNavController())) {
//            val view = activity?.currentFocus
//            if (view != null) {
//                val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(view.windowToken, 0)
//            }
//
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
}

