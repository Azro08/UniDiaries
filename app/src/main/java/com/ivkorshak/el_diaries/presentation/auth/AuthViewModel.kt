package com.ivkorshak.el_diaries.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.ivkorshak.el_diaries.data.repository.UsersRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    private val _userRole = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val userRole get() =  _userRole

    fun getUserRole() = viewModelScope.launch {
        try {
            repository.getUserRole().let {role->
                try {
                    _userRole.value = ScreenState.Success(role)
                } catch (e : FirebaseException){
                    _userRole.value = ScreenState.Error(e.message!!)
                }
            }
        } catch (e : UnknownHostException){
            _userRole.value = ScreenState.Error(e.message!!)
        }
    }

}