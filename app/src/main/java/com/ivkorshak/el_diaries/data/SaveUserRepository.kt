package com.ivkorshak.el_diaries.data

import com.ivkorshak.el_diaries.util.firestor_service.FirestoreAddService
import javax.inject.Inject

class SaveUserRepository @Inject constructor(
    private val firestore: FirestoreAddService
) {

    suspend fun saveUser(user: Users): String =
        firestore.saveToFirestore(user, "users", user.id)
}