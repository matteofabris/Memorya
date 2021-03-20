package com.example.wordsmemory

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI

class PlayFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        /*
       val db = Room.databaseBuilder(
           applicationContext,
           AppDatabase::class.java, "database-name"
       ).build()
       val userDao = db.userDao()
       val users: List<User> = userDao.getAll()
       */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
//        return when (item.itemId) {
//            R.id.enVocabularyFragment -> {
//                findNavController().navigate(R.id.action_playFragment_to_enVocabularyFragment)
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
    }
}