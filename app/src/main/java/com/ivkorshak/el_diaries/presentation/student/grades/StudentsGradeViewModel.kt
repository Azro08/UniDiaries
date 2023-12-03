package com.ivkorshak.el_diaries.presentation.student.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Grades
import com.ivkorshak.el_diaries.data.repository.StudentsWorkRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentsGradeViewModel @Inject constructor(
    private val repository: StudentsWorkRepository
) : ViewModel(){

    private val _studentGrades = MutableStateFlow<ScreenState<List<Grades>?>>(ScreenState.Loading())
    val studentGrades = _studentGrades

    fun refresh(classRoomId : String, studentId : String) {
        getStudentGrades(classRoomId, studentId)
    }

    fun getStudentGrades(classRoomId : String, studentId : String) = viewModelScope.launch {
        try {
            repository.getGradesForStudent(classRoomId, studentId).let {
                _studentGrades.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _studentGrades.value = ScreenState.Error(e.message.toString())
        }
    }

}