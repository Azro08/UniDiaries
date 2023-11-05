package com.ivkorshak.el_diaries.data

import com.ivkorshak.el_diaries.util.firestor_service.FirestoreGetDataService
import javax.inject.Inject

class UserRoleRepository @Inject constructor(
    private val firestore: FirestoreGetDataService
) {
    suspend fun getUserRole(): String? = firestore.getUserRole()
}
