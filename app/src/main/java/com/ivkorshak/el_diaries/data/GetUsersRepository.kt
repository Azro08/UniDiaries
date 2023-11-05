package com.ivkorshak.el_diaries.data

import com.ivkorshak.el_diaries.util.firestor_service.FirestoreGetDataService
import javax.inject.Inject

class GetUsersRepository @Inject constructor(
    private val firestore: FirestoreGetDataService
) {
    suspend fun getUsers(): List<Users> = firestore.getUsers()
}
