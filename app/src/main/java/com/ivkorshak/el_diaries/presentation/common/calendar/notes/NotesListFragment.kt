package com.ivkorshak.el_diaries.presentation.common.calendar.notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivkorshak.el_diaries.R
import com.ivkorshak.el_diaries.data.model.Notes
import com.ivkorshak.el_diaries.databinding.FragmentNotesListBinding
import com.ivkorshak.el_diaries.util.AuthManager
import com.ivkorshak.el_diaries.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesListFragment : Fragment() {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private var classRoomId = ""
    private var date : String? = null
    private val viewModel: NotesViewModel by viewModels()
    private var notesRvAdapter: NotesRvAdapter? = null

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        classRoomId = arguments?.getString(Constants.CLASS_ID) ?: ""
        arguments?.getString(Constants.DATE_KEY)?.let {
            date = it
            getNotesList()
        }
        if (authManager.getRole() == Constants.TEACHER) binding.buttonAddNote.visibility =
            View.VISIBLE
        val screenTitle = getString(R.string.notes) + " " + date
        binding.textViewTitle.text = screenTitle
        binding.buttonAddNote.setOnClickListener {
            val bundle =
                bundleOf(Pair(Constants.CLASS_ID, classRoomId), Pair(Constants.DATE_KEY, date))
            findNavController().navigate(R.id.nav_notes_list_add_note, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
//        getNotesList()
    }

    private fun getNotesList() {
        lifecycleScope.launch {
            viewModel.getNotes(classRoomId = classRoomId, date = date!!)
            viewModel.notes.collect {
                collectViewModelOutputs(it)
            }
        }
    }

    private fun collectViewModelOutputs(state: ScreenState<List<Notes>>) {
        when (state) {
            is ScreenState.Loading -> {
                //loading........
            }

            is ScreenState.Error -> {
                //show nothing
            }

            is ScreenState.Success -> {
                displayNotes(state.data!!)
            }
        }
    }

    private fun displayNotes(notesList: List<Notes>) = with(binding) {
        notesRvAdapter = NotesRvAdapter(notesList, authManager.getRole()) {
            viewModel.deleteNote(it)
//            getNotesList()
        }
        recyclerViewNotes.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNotes.setHasFixedSize(true)
        recyclerViewNotes.adapter = notesRvAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        notesRvAdapter = null
    }

}