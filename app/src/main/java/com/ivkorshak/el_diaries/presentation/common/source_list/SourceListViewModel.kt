package com.ivkorshak.el_diaries.presentation.common.source_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.repository.SourcesRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val repository: SourcesRepository
) : ViewModel() {

    private val _sourceAdded = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val sourceAdded = _sourceAdded

    private val _sourceDeleted = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val sourceDeleted = _sourceDeleted

    private val _sourceList = MutableStateFlow<ScreenState<List<String>?>>(ScreenState.Loading())
    val sourceList = _sourceList

    fun refresh(classRoomId: String){
        getSourceList(classRoomId)
    }

    fun getSourceList(classRoomId: String) = viewModelScope.launch {
        try {
            repository.getSourcesList(classRoomId).let {
                _sourceList.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _sourceList.value = ScreenState.Error(e.message.toString())
        }
    }

    fun addSource(classRoomId: String, source: String) = viewModelScope.launch {
        try {
            repository.addSource(classRoomId, source).let {
                if (it == "Done") {
                    _sourceAdded.value = ScreenState.Success(it)
                    getSourceList(classRoomId)
                }
                else _sourceAdded.value = ScreenState.Error(it)
            }
        } catch (e: Exception) {
            _sourceAdded.value = ScreenState.Error(e.message.toString())
        }
    }

    fun deleteSource(classRoomId: String, source: String) = viewModelScope.launch {
        try {
            repository.deleteSource(classRoomId, source).let {
                if (it == "Done"){
                    _sourceAdded.value = ScreenState.Success(it)
                    getSourceList(classRoomId)
                }
                else _sourceAdded.value = ScreenState.Error(it)
            }
        } catch (e: Exception) {
            _sourceDeleted.value = ScreenState.Error(e.message.toString())
        }

    }

}