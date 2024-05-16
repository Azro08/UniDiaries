package com.ivkorshak.el_diaries.presentation.common.calendar.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Notes
import com.ivkorshak.el_diaries.data.repository.NotesRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<ScreenState<List<Notes>>>(ScreenState.Loading())
    val notes = _notes

    private val _deleteNoteState = MutableStateFlow<ScreenState<String>>(ScreenState.Loading())
//    val deleteNoteState = _deleteNoteState

    fun getNotes(classRoomId: String, date : String) = viewModelScope.launch {
        notesRepository.getNotes(classRoomId, date).let {
            if (it.isNotEmpty()) _notes.value = ScreenState.Success(it)
            else _notes.value = ScreenState.Error("No notes found")
        }
    }

    fun deleteNote(noteId: String) = viewModelScope.launch {
        notesRepository.deleteNote(noteId).let {
            if (it == "Done") _deleteNoteState.value = ScreenState.Success("Deleted")
            else _deleteNoteState.value = ScreenState.Error(it)
        }
    }

}