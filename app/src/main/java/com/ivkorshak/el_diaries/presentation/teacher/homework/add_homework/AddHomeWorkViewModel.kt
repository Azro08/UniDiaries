package com.ivkorshak.el_diaries.presentation.teacher.homework.add_homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.repository.HomeWorkRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHomeWorkViewModel @Inject constructor(
    private val repository: HomeWorkRepository
) : ViewModel() {

    private val _homeWorkAdded = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val homeWorkAdded = _homeWorkAdded

    fun addHomeWork(classId: String, homework: String) = viewModelScope.launch {
        try {
            repository.addHomework(classId, homework).let {
                if (it == "Done") _homeWorkAdded.value = ScreenState.Success(it)
                else _homeWorkAdded.value = ScreenState.Error(it)
            }
        } catch (e: Exception) {
            _homeWorkAdded.value = ScreenState.Error(e.message.toString())
        }
    }

}