package com.ivkorshak.el_diaries.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getUsers(): List<Users> {
        val usersCollection = firestore.collection("users")
        try {
            val querySnapshot = usersCollection.get().await()
            val usersList = mutableListOf<Users>()
            for (document in querySnapshot) {
                val user = document.toObject(Users::class.java)
                if (user.role != "admin") {
                    usersList.add(user)
                }
            }
            return usersList
        } catch (e: Exception) {
            // Handle any errors or exceptions here
            throw e
        }
    }

    suspend fun deleteUser(userId: String) {
        val userDocument = firestore.collection("users").document(userId)
        try {
            userDocument.delete().await()
        } catch (e: Exception) {
            // Handle any errors or exceptions here
            throw e
        }
    }

}
