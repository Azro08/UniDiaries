package com.ivkorshak.el_diaries.presentation.common.calendar.add_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Notes
import com.ivkorshak.el_diaries.databinding.FragmentAddNoteBinding
import com.ivkorshak.el_diaries.presentation.common.calendar.notes.NotePriority
import com.ivkorshak.el_diaries.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddNoteViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val classRoomId = arguments?.getString(Constants.CLASS_ID)
        val date = arguments?.getString(Constants.DATE_KEY)
        if (classRoomId == null || date == null) Toast.makeText(
            requireContext(),
            getString(R.string.cant_load_class_details), Toast.LENGTH_SHORT
        ).show()
        else {
            setSpinner()
            binding.buttonAddNote.setOnClickListener {
                if (binding.editTextNoteText.text!!.isNotBlank() && binding.editTextNoteTitle.text!!.isNotBlank()) {
                    addNote(classRoomId, date)
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.fillup_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setSpinner() {
        val priorities = listOf(
            NotePriority.LOW.name,
            NotePriority.HIGH.name,
            NotePriority.MEDIUM.name
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            priorities
        )
        binding.spinnerPriorities.adapter = adapter
    }

    private fun addNote(roomId: String, date: String) = with(binding) {
        val title = editTextNoteTitle.text.toString()
        val text = editTextNoteText.text.toString()
        val noteId = Constants.generateRandomId()
        val priority = binding.spinnerPriorities.selectedItem.toString()
        val note = Notes(
            id = noteId,
            title = title,
            text = text,
            date = date,
            classRoomId = roomId,
            priority = priority
        )
        viewModel.addNote(note)
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}