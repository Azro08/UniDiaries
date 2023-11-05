package com.ivkorshak.el_diaries.data

import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.util.firestor_service.FirestoreDeleteService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeleteUserRepository @Inject constructor(
    private val firestore : FirestoreDeleteService,
    private val api : UserService
) {

    suspend fun deleteUser(userId: String) = firestore.deleteFromFirestore("users", userId)
    suspend fun deleteAccount(uid : String) = api.deleteUser(uid)
}