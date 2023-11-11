package com.ivkorshak.el_diaries.presentation.teacher.students_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.data.repository.ClassRoomRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassStudentsViewModel @Inject constructor(
    private val classRoomRepository: ClassRoomRepository
) : ViewModel() {

    private val _students = MutableStateFlow<ScreenState<List<Students>?>>(ScreenState.Loading())
    val students : MutableStateFlow<ScreenState<List<Students>?>>  = _students

    fun getAttachedStudents(classId: String) = viewModelScope.launch {
        try {
            classRoomRepository.getAttachedStudents(classId).let {
                _students.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _students.value = ScreenState.Error(e.message.toString())
        }
    }
}