package com.ivkorshak.el_diaries.presentation.admin.add_class

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.model.Students
import com.ivkorshak.el_diaries.data.model.Teacher
import com.ivkorshak.el_diaries.data.repository.ClassRoomRepository
import com.ivkorshak.el_diaries.domain.GetStudentsUseCase
import com.ivkorshak.el_diaries.domain.GetTeachersUseCase
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AddClassRoomViewModel @Inject constructor(
    private val studentsUseCase: GetStudentsUseCase,
    private val teachersUseCase: GetTeachersUseCase,
    private val classRoomRepository: ClassRoomRepository
) : ViewModel() {

    private val _classRoomCreated = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val classRoomCreated: MutableStateFlow<ScreenState<String?>> = _classRoomCreated

    private val _students = MutableStateFlow<ScreenState<List<Students>?>>(ScreenState.Loading())
    val students: MutableStateFlow<ScreenState<List<Students>?>> = _students

    private val _teachers = MutableStateFlow<ScreenState<List<Teacher>?>>(ScreenState.Loading())
    val teachers: MutableStateFlow<ScreenState<List<Teacher>?>> = _teachers

    init {
        getStudents()
    }

    fun createClassRoom(classRoom: ClassRoom) = viewModelScope.launch {
        try {
            classRoomRepository.addClassRoom(classRoom).let {
                if (it == "Done") _classRoomCreated.value = ScreenState.Success(it)
                else _classRoomCreated.value = ScreenState.Error(it)
            }
        } catch (e: HttpException) {
            _classRoomCreated.value = ScreenState.Error(e.message.toString())
        }

    }

    private fun getStudents() = viewModelScope.launch {
        _students.value = ScreenState.Loading()
        try {
            studentsUseCase.invoke().let {
                _students.value = it
            }
        } catch (e: HttpException) {
            _students.value = ScreenState.Error(e.message.toString())
        }
    }

    private fun getTeachers() = viewModelScope.launch {
        _teachers.value = ScreenState.Loading()
        try {
            teachersUseCase.invoke().let {
                _teachers.value = it
            }
        } catch (e: HttpException) {
            _teachers.value = ScreenState.Error(e.message.toString())
        }
    }

}