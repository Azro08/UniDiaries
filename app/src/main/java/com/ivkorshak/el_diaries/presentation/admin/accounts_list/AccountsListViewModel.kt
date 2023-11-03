package com.ivkorshak.el_diaries.presentation.admin.accounts_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.ivkorshak.el_diaries.data.Users
import com.ivkorshak.el_diaries.data.UsersRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {
    private val _users = MutableStateFlow<ScreenState<List<Users>?>>(ScreenState.Loading())
    val users: MutableStateFlow<ScreenState<List<Users>?>> = _users

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        _users.value = ScreenState.Loading()
        try {
            repository.getUsers().let {
                try {
                    _users.value = ScreenState.Success(it)
                } catch (e: FirebaseException) {
                    _users.value = ScreenState.Error(e.message.toString())
                }
            }
        } catch (e: UnknownHostException) {
            _users.value = ScreenState.Error(e.message.toString())
        }
    }

    fun deleteUser(uid: String) = viewModelScope.launch {
        repository.deleteUser(uid)
        getUsers()
    }

}