package com.ivkorshak.el_diaries.presentation.teacher.students_list.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Grades
import com.ivkorshak.el_diaries.data.model.SkippedTimes
import com.ivkorshak.el_diaries.data.repository.StudentsWorkRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditStudentViewModel @Inject constructor(
    private val repository: StudentsWorkRepository
) : ViewModel() {
    private val _gradeIsSet = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val gradeIsSet: MutableStateFlow<ScreenState<String?>> = _gradeIsSet

    private val _skippedTimeIsSet = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val skippedTimeIsSet: MutableStateFlow<ScreenState<String?>> = _skippedTimeIsSet

    suspend fun setGradeAndSkippedTime(
        classRoomId: String,
        studentId: String,
        grades: Grades,
        skippedTimes: SkippedTimes
    ) {
        try {
            repository.setGrade(classRoomId, studentId, grades)
            repository.setSkippedTime(classRoomId, studentId, skippedTimes)
        } catch (e: Exception) {
            Log.d("SetDataError", e.message.toString())
        }
    }


    fun setGrade(classRoomId: String, studentId: String, grades: Grades) = viewModelScope.launch {

        try {
            repository.setGrade(classRoomId, studentId, grades).let {
                _gradeIsSet.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _gradeIsSet.value = ScreenState.Error(e.message ?: "Unknown error")
        }
    }

    fun setSkippedTime(classRoomId: String, studentId: String, skippedTimes: SkippedTimes) =
        viewModelScope.launch {


            try {
                repository.setSkippedTime(classRoomId, studentId, skippedTimes).let {
                    _skippedTimeIsSet.value = ScreenState.Success(it)
                }
            } catch (e: Exception) {
                _skippedTimeIsSet.value = ScreenState.Error(e.message ?: "Unknown error")
            }


        }

}