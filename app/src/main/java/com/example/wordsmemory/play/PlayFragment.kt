package com.example.wordsmemory.play

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wordsmemory.R
import com.example.wordsmemory.VocabularyDatabase
import com.example.wordsmemory.databinding.FragmentPlayBinding
import kotlinx.coroutines.InternalCoroutinesApi

class PlayFragment : Fragment() {

    private lateinit var viewModel: PlayFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlayBinding.inflate(inflater)

        createViewModel()

        binding.playViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.isTranslationOk.observe(
            viewLifecycleOwner,
            { Toast.makeText(this.context, it.toString(), Toast.LENGTH_SHORT).show() })

        return binding.root
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