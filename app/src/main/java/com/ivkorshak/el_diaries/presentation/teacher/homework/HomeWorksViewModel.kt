package com.ivkorshak.el_diaries.presentation.teacher.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.repository.HomeWorkRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeWorksViewModel @Inject constructor(
    private val repository: HomeWorkRepository
) : ViewModel() {

    private val _homeWorks = MutableStateFlow<ScreenState<List<String>?>>(ScreenState.Loading())
    val homeWorks = _homeWorks

    private val _homeworkDeleted = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val homeworkDeleted = _homeworkDeleted

    fun refresh(classRoomId: String) {
        getHomeWorks(classRoomId)
    }

    fun getHomeWorks(classId: String) = viewModelScope.launch {
        try {
            repository.getHomeworks(classId).let {
                _homeWorks.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _homeWorks.value = ScreenState.Error(e.message.toString())
        }
    }

    fun deleteHomework(classRoomId: String, homeworkId: String) = viewModelScope.launch {
        try {
            repository.deleteHomework(classRoomId, homeworkId).let {
                if (it == "Done") {
                    _homeworkDeleted.value = ScreenState.Success(it)
                    getHomeWorks(classRoomId)
                } else _homeworkDeleted.value = ScreenState.Error(it)
            }
        } catch (e: Exception) {
            _homeworkDeleted.value = ScreenState.Error(e.message.toString())
        }
    }

}