package com.ivkorshak.el_diaries.presentation.teacher.students_list.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Grade
import com.ivkorshak.el_diaries.data.model.SkippedTime
import com.ivkorshak.el_diaries.data.repository.ClassRoomRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditStudentViewModel @Inject constructor(
    private val classRoomRepository: ClassRoomRepository
) : ViewModel() {
    private val _gradeIsSet = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val gradeIsSet: MutableStateFlow<ScreenState<String?>> = _gradeIsSet

    private val _skippedTimeIsSet = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val skippedTimeIsSet: MutableStateFlow<ScreenState<String?>> = _skippedTimeIsSet

    suspend fun setGradeAndSkippedTime(
        classRoomId: String,
        studentId: String,
        grade: Grade,
        skippedTime: SkippedTime
    ) {
        try {
            classRoomRepository.setGrade(classRoomId, studentId, grade)
            classRoomRepository.setSkippedTime(classRoomId, studentId, skippedTime)
        } catch (e: Exception) {
            Log.d("SetDataErro", e.message.toString())
        }
    }


    fun setGrade(classRoomId: String, studentId: String, grade: Grade) = viewModelScope.launch {

        try {
            classRoomRepository.setGrade(classRoomId, studentId, grade).let {
                _gradeIsSet.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _gradeIsSet.value = ScreenState.Error(e.message ?: "Unknown error")
        }
    }

    fun setSkippedTime(classRoomId: String, studentId: String, skippedTime: SkippedTime) =
        viewModelScope.launch {


            try {
                classRoomRepository.setSkippedTime(classRoomId, studentId, skippedTime).let {
                    _skippedTimeIsSet.value = ScreenState.Success(it)
                }
            } catch (e: Exception) {
                _skippedTimeIsSet.value = ScreenState.Error(e.message ?: "Unknown error")
            }


        }

}