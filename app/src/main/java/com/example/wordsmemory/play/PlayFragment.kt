package com.example.wordsmemory.play

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wordsmemory.R
import com.example.wordsmemory.TranslateInputFilter
import com.example.wordsmemory.VocabularyDatabase
import com.example.wordsmemory.afterTextChanged
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

        val filter = TranslateInputFilter()
        binding.translationEditText.filters = arrayOf(filter)
        binding.translationEditText.afterTextChanged { s ->
            binding.acceptTranslationButton.isEnabled = s.isNotEmpty()
        }

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

    private fun setupResultObserver() {
        viewModel.isTranslationOk.observe(
            viewLifecycleOwner,
            {
                val text: String =
                    if (it) getString(R.string.right_translation) else getString(R.string.wrong_translation)
                Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()

                changeBackgroundColor(!it)
            })
    }

    private fun changeBackgroundColor(showBackground: Boolean) {
        if (showBackground) {
            lifecycleScope.launch {
                binding.container.setBackgroundColor(
                    ColorUtils.setAlphaComponent(
                        Color.RED,
                        200
                    )
                )
                delay(170)
                binding.container.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (NavigationUI.onNavDestinationSelected(item, requireView().findNavController())) {
            val view = activity?.currentFocus
            if (view != null) {
                val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}

