package com.ivkorshak.el_diaries.presentation.student.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.SkippedTimes
import com.ivkorshak.el_diaries.data.repository.StudentsWorkRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentAttendanceViewModel @Inject constructor(
    private val repository: StudentsWorkRepository
) : ViewModel(){

    private val _skippedTimes = MutableStateFlow<ScreenState<List<SkippedTimes>?>>(ScreenState.Loading())
    val skippedTimes = _skippedTimes

    fun refresh(classId : String, studentId :  String){
        getSkippedTimes(classId, studentId)
    }

    fun getSkippedTimes (classId : String, studentId :  String) = viewModelScope.launch {
        try {
            repository.getAttendanceForStudent(classId, studentId).let {
                _skippedTimes.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _skippedTimes.value = ScreenState.Error(e.message.toString())
        }
    }

}