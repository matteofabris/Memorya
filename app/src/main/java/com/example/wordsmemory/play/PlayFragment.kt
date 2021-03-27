package com.example.wordsmemory.play

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.*
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wordsmemory.R
import com.example.wordsmemory.VocabularyDatabase
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

        setupTranslationObserver()
        setupResultObserver()

        return binding.root
    }

    private fun setupTranslationObserver() {
        viewModel.translationText.observe(
            viewLifecycleOwner,
            {
                viewModel.buttonEnabled.value = !it.isNullOrEmpty()
            }
        )
    }

    private fun setupResultObserver() {
        viewModel.isTranslationOk.observe(
            viewLifecycleOwner,
            {
                val text: String = if (it) "Risposta esatta!!" else "Hai sbagliato, riprova!"
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

    @InternalCoroutinesApi
    private fun createViewModel() {
        val application = requireNotNull(this.activity).application
        val dbDao = VocabularyDatabase.getInstance(application).enVocabularyDao()

        val factory = PlayFragmentViewModelFactory(dbDao)
        viewModel = ViewModelProvider(this, factory).get(PlayFragmentViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}

internal class TranslateInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        if (source == null) return ""

        for (i in start until end) {
            if (!Character.isLetter(source[i]) ||
                Character.isSpaceChar(source[i])
            ) {
                return ""
            }
        }
        return source
    }
}