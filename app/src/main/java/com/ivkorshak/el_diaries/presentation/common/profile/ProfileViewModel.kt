package com.ivkorshak.el_diaries.presentation.common.profile

import android.net.Uri
import android.util.Log
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
class ProfileViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _user = MutableStateFlow<ScreenState<Users?>>(ScreenState.Loading())
    val users: MutableStateFlow<ScreenState<Users?>> = _user

    private val _userSaved = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val userSaved: MutableStateFlow<ScreenState<String?>> = _userSaved

    private val _imageUploaded = MutableStateFlow<ScreenState<Uri?>>(ScreenState.Loading())
    val imageUploaded: MutableStateFlow<ScreenState<Uri?>> = _imageUploaded

    fun getUser(id: String) = viewModelScope.launch {
        try {
            usersRepository.getUser(id).let {
                if (it != null) _user.value = ScreenState.Success(it)
                else _user.value = ScreenState.Error("User details not Found")
            }
        } catch (e: Exception) {
            _user.value = ScreenState.Error(e.message.toString())
        }
    }

    fun saveUser(userID: String, updatedFields : Map<String, Any>, password:String, oldPassword : String, email : String) = viewModelScope.launch {
        try {
            usersRepository.updateUserFields(userID, updatedFields,  password, oldPassword, email).let {
                if (it == "Done") {
                    _userSaved.value = ScreenState.Success("User details Saved successfully")
                    getUser(userID)
                }
                else _userSaved.value = ScreenState.Error(it)
                Log.d("UserNotUpdated", it)
            }
        } catch (e: Exception) {
            _userSaved.value = ScreenState.Error(e.message.toString())
        }
    }

    fun uploadImageAndGetUri(userId: String, imageUri: Uri) = viewModelScope.launch {
        try {
            usersRepository.uploadImageAndGetUri(userId, imageUri).let {
                if (it != null) _imageUploaded.value = ScreenState.Success(it)
                else _imageUploaded.value = ScreenState.Error("Error Loading image")
            }
        } catch (e: Exception) {
            _imageUploaded.value = ScreenState.Error(e.message.toString())
        }
    }

}