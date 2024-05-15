package com.ivkorshak.el_diaries.presentation.common.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.repository.ClassRoomRepository
import com.ivkorshak.el_diaries.domain.GetStudentsClassesUseCase
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ClassesListViewModel @Inject constructor(
    private val repository: ClassRoomRepository,
    private val useCase: GetStudentsClassesUseCase
) : ViewModel() {

    private val _classRooms = MutableStateFlow<ScreenState<List<ClassRoom>?>>(ScreenState.Loading())
    val classRooms get()  = _classRooms

    fun refresh(weekDay: Int) {
        getClassRooms(weekDay)
    }

    fun getClassRooms(weekDay: Int) = viewModelScope.launch {
        try {
            repository.getClassRooms(weekDay).let {
                if (it.isNotEmpty()) {
                    _classRooms.value = ScreenState.Success(it)
                } else _classRooms.value = ScreenState.Error("No Classes Available")
            }
        } catch (e: HttpException) {
            _classRooms.value = ScreenState.Error(e.message.toString())
        }
    }

    fun getStudentsClasses(weekDay: Int) = viewModelScope.launch {
        try {
            useCase.invoke(weekDay).let {
                if (!it.isNullOrEmpty()) {
                    _classRooms.value = ScreenState.Success(it)
                } else _classRooms.value = ScreenState.Error("No Classes Available")
            }
        } catch (e: HttpException) {
            _classRooms.value = ScreenState.Error(e.message.toString())
        }
    }


}