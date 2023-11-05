package com.ivkorshak.el_diaries.presentation.admin.accounts_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.ivkorshak.el_diaries.data.DeleteUserRepository
import com.ivkorshak.el_diaries.data.Users
import com.ivkorshak.el_diaries.data.GetUsersRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
    private val getUsersRepository: GetUsersRepository,
    private val deleteUserRepository: DeleteUserRepository
) : ViewModel() {
    private val _users = MutableStateFlow<ScreenState<List<Users>?>>(ScreenState.Loading())
    val users: MutableStateFlow<ScreenState<List<Users>?>> = _users

    private val _userDeleted = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val userDeleted: MutableStateFlow<ScreenState<String?>> = _userDeleted

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        _users.value = ScreenState.Loading()
        try {
            getUsersRepository.getUsers().let {
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
        try {
            deleteUserRepository.deleteAccount(uid).let {
                if (it == "Done") {
                    deleteUserRepository.deleteUser(uid).let {response ->
                        if (response == "Done") _userDeleted.value = ScreenState.Success(response)
                        else _userDeleted.value = ScreenState.Error(response)
                    }
                    getUsers()
                } else {
                    _userDeleted.value = ScreenState.Error(it)
                }
            }
        } catch (e : HttpException) {
            Log.d("AccountListViewModel", e.message().toString())
            _userDeleted.value = ScreenState.Error(e.message())
        }
    }

}