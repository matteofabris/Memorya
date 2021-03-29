package com.example.wordsmemory.vocabulary

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.wordsmemory.R

class AddVocabularyItemFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        //val activity = activity
        //return activity?.let {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)

        builder.setView(inflater.inflate(R.layout.add_vocabulary_item_dialog, null))
            .setPositiveButton(
                getString(R.string.add)
            ) { dialog, id ->
                // add word
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
        //} ?: throw IllegalStateException("Activity cannot be null")
    }
}