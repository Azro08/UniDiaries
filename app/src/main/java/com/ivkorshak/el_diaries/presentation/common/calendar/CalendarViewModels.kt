package com.ivkorshak.el_diaries.presentation.common.calendar

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
class CalendarViewModels @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<ScreenState<List<Notes>>>(ScreenState.Loading())
    val notes = _notes

    fun getNotes(classRoomId: String) = viewModelScope.launch {
        notesRepository.getNotes(classRoomId, "").let {
            if (it.isNotEmpty()) _notes.value = ScreenState.Success(it)
            else _notes.value = ScreenState.Error("No notes found")
        }
    }


}