package com.ivkorshak.el_diaries.presentation.common.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.ClassRoom
import com.ivkorshak.el_diaries.data.repository.ClassRoomRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ClassesListViewModel @Inject constructor(
    private val repository: ClassRoomRepository
) : ViewModel(){

    private val _classRooms = MutableStateFlow<ScreenState<List<ClassRoom>?>>(ScreenState.Loading())
    val classRooms: MutableStateFlow<ScreenState<List<ClassRoom>?>> = _classRooms

    fun refresh() {
        getClassRooms("Monday")
    }

    fun getClassRooms(weekDay : String) = viewModelScope.launch {
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


}