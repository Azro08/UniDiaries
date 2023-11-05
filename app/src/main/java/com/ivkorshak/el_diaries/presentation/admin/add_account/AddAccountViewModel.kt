package com.ivkorshak.el_diaries.presentation.admin.add_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivkorshak.el_diaries.data.model.Users
import com.ivkorshak.el_diaries.data.repository.UsersRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _userCreated = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val userCreated: MutableStateFlow<ScreenState<String?>> = _userCreated


    fun saveUser(user: Users) = viewModelScope.launch {
        usersRepository.saveUser(user).let {
            if (it == "Done") {
                _userCreated.value = ScreenState.Success(it)
            } else {
                _userCreated.value = ScreenState.Error(it)
            }
        }
    }

}