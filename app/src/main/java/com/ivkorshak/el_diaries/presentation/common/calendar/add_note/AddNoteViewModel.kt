package com.ivkorshak.el_diaries.presentation.common.calendar.add_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Notes
import com.ivkorshak.el_diaries.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    fun addNote(note: Notes) = viewModelScope.launch {
        notesRepository.addNote(note)
    }

}