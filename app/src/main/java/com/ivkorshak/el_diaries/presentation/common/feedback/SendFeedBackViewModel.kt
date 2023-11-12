package com.ivkorshak.el_diaries.presentation.common.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.repository.FeedBackRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendFeedBackViewModel @Inject constructor(
    private val feedBackRepository: FeedBackRepository
) : ViewModel() {

    private val _feedBackSent = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val feedBackSent = _feedBackSent


    fun sendFeedBack(text: String) = viewModelScope.launch {
        try {
            feedBackRepository.sendFeedback(text).let {
                if (it == "Done") _feedBackSent.value = ScreenState.Success(it)
                else _feedBackSent.value = ScreenState.Error(it)
            }
        } catch (e: Exception) {
            _feedBackSent.value = ScreenState.Error(e.message.toString())
        }
    }

}