package com.ivkorshak.el_diaries.presentation.admin.classes_list

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
class EditClassesListViewModel @Inject constructor(
    private val repository: ClassRoomRepository
) : ViewModel() {

    private val _classRooms = MutableStateFlow<ScreenState<List<ClassRoom>?>>(ScreenState.Loading())
    val classRooms: MutableStateFlow<ScreenState<List<ClassRoom>?>> = _classRooms

    private val _classRoomDeleted = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val classRoomDeleted: MutableStateFlow<ScreenState<String?>> = _classRoomDeleted

    init {
        getClassRooms()
    }

    fun refresh() {
        getClassRooms()
    }

    private fun getClassRooms() = viewModelScope.launch {
        try {
            repository.getClassRooms("ПН").let {
                if (it.isNotEmpty()) {
                    _classRooms.value = ScreenState.Success(it)
                } else _classRooms.value = ScreenState.Error("No Classes Available")
            }
        } catch (e: HttpException) {
            _classRooms.value = ScreenState.Error(e.message.toString())
        }
    }

    suspend fun deleteClassRoom(id: String) = viewModelScope.launch {
        try {
            repository.deleteClassRoom(id).let {
                if (it == "Done") {
                    _classRoomDeleted.value = ScreenState.Success(it)
                    getClassRooms()
                } else _classRoomDeleted.value = ScreenState.Error(it)
            }
        } catch (e: HttpException) {
            _classRoomDeleted.value = ScreenState.Error(e.message.toString())
        }
    }

}